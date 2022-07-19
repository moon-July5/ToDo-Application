package com.moon.security;

import com.moon.entity.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 *  사용자 정보를 받아 JWT를 생성하는 일
 */
@Log4j2
@Service
public class TokenProvider {
    private static final String SECRET_KEY = "NMA8JPctFuna59f5";

    // JWT 토큰 생성
    public String create(UserEntity userEntity){
        // 기한은 지금부터 1일로 설정
        Date expiryDate = Date.from(Instant.now().plus(1, ChronoUnit.DAYS));

        // JWT Token 생성
        return Jwts.builder()
                // header에 들어갈 내용 및 서명을 하기 위한 SECRET_KEY
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                // payload에 들어갈 내용
                .setSubject(userEntity.getId()) // sub
                .setIssuer("todo App") // iss
                .setIssuedAt(new Date()) // iat
                .setExpiration(expiryDate) // exp
                .compact();
    }

    // 토큰을 디코딩 및 파싱하고 토큰의 위조 여부를 확인인
   public String validateAndGetUserId(String token){
        /*
            parseClaimsJws 메서드가 Base64로 디코딩 및 파싱
            header와 payload를 setSigningKey로 넘어온 시크릿을 이용해 서명한 후 token의 서명과 비교
            위조되지 않았다면 페이로드(Claims) 리턴, 위조라면 예외
            그 중 우리는 userID가 필요하므로 getBody를 호출
         */
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }
}
