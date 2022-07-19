package com.moon.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * OncePerRequestFilter -> 한 요청당 반드시 한 번만 실행
 *
 * parseBearerToken() -> 요청의 헤더에서 Bearer 토큰을 가져온다.
 *
 * TokenProvider 를 이용해 토큰을 인증하며, UsernamePasswordAuthenticationToken 오브젝트에 사용자 인증을 저장.
 * SecurityContext 에 인증된 사용자를 등록
 *
 * 왜 등록해야 하나? 요청을 처리하는 과정에서 사용자가 인증됐는지의 여부나
 * 인증된 사용자가 누군지 알아야 할 때가 있기 때문이다.
 */
@Log4j2
@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final TokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try{
            // 요청에서 토큰 가져오기
           String token = parseBearerToken(request);
           log.info("Filter is running...");
           // 토큰 검사하기. JWT이므로 인가 서버에 요청하지 않고도 검증 가능
            if(token != null && !token.equalsIgnoreCase("null")){
                // userId 가져오기. 위조된 경우 예외 처리된다.
                String userId = tokenProvider.validateAndGetUserId(token);
                log.info("Authenticated user ID : "+userId);
                // 인증 완료. SecurityContextHolder 에 등록해야 인증된 사용자라고 생각한다.
                AbstractAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userId, // 인증된 사용자의 정보. 문자열이 아니어도 아무것이나 넣을 수 있다. 보통 UserDetails라는 오브젝트를 넣는다.
                        null // AuthorityUtils.NO_AUTHORITIES
                );

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
                securityContext.setAuthentication(authentication);

                /*
                    SecurityContextHolder는 기본적으로 ThreadLocal에 저장
                    ThreadLocal에 저장되므로 Thread 마다 하나의 컨텍스트를 관리할 수 있고
                    스레드 내라면 어디에서든 접근할 수 있다.
                 */
                SecurityContextHolder.setContext(securityContext);
            }
        } catch (Exception ex){
            logger.error("Could not set user authentication in security context", ex);
        }

        filterChain.doFilter(request, response);
    }

    private String parseBearerToken(HttpServletRequest request){
        // http 요청의 헤더를 파싱해 Bearer 토큰을 리턴한다.
        String bearerToken = request.getHeader("Authorization");

        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")){
            return bearerToken.substring(7);
        }
        return null;
    }
}
