package com.moon.controller;

import com.moon.dto.ResponseDTO;
import com.moon.dto.TodoDTO;
import com.moon.entity.TodoEntity;
import com.moon.service.TodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RequestMapping("todo")
@RestController
public class TodoController {
    private final TodoService todoService;

    @DeleteMapping
    public ResponseEntity<?> deleteTodo(@AuthenticationPrincipal String userId,@RequestBody TodoDTO todoDTO){
        try{
            // dto -> entity로 변환
            TodoEntity entity = TodoDTO.toEntity(todoDTO);

            // id를 userId로 초기화
            entity.setUserId(userId);

            // 서비스를 이용해 entity를 삭제
            List<TodoEntity> entities = todoService.delete(entity);

            // 자바 스트림을 이용해 리턴된 엔티티 리스트를 TodoDTO 리스트로 변환
            List<TodoDTO> dtos = entities.stream().map(TodoDTO::new).collect(Collectors.toList());

            // 변환된 TodoDTO 리스트를 이용해 ResponseDTO를 초기화
            ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().data(dtos).build();

            // ResponseDTO를 리턴
            return ResponseEntity.ok().body(response);
        } catch (Exception e){
            // 예외가 있는 경우 dto 대신 error에 메시지를 넣어 리턴한다.
            String error = e.getMessage();
            ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().error(error).build();
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping
    public ResponseEntity<?> updateTodo(@AuthenticationPrincipal String userId,@RequestBody TodoDTO todoDTO){
        // dto -> entity로 변환
        TodoEntity entity = TodoDTO.toEntity(todoDTO);

        // id를 userId로 초기화
        entity.setUserId(userId);

        // 서비스를 이용해 entity를 업데이트
        List<TodoEntity> entities = todoService.update(entity);

        // 자바 스트림을 이용해 리턴된 엔티티 리스트를 TodoDTO 리스트로 변환
        List<TodoDTO> dtos = entities.stream().map(TodoDTO::new).collect(Collectors.toList());

        // 변환된 TodoDTO 리스트를 이용해 ResponseDTO를 초기화
        ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().data(dtos).build();

        // ResponseDTO를 리턴
        return ResponseEntity.ok().body(response);
    }

    @GetMapping
    public ResponseEntity<?> retrieveTodoList(@AuthenticationPrincipal String userId) {

        // 서비스 메서드의 retrieve() 메서드를 이용해 Todo 리스트를 가져온다.
        List<TodoEntity> entities = todoService.retrieve(userId);

        // 자바 스트림을 이용해 리턴된 엔티티 리스트를 TodoDTO 리스트로 변환한다.
        List<TodoDTO> dtos = entities.stream().map(TodoDTO::new).collect(Collectors.toList());

        // 변환된 TodoDTO 리스트를 이용해 ResponseDTO를 초기화한다.
        ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().data(dtos).build();

        return ResponseEntity.ok().body(response);
    }

    @PostMapping
    public ResponseEntity<?> createTodo(@AuthenticationPrincipal String userId, @RequestBody TodoDTO todoDTO){
        try{
            // TodoEntity 로 변환
            TodoEntity entity = TodoDTO.toEntity(todoDTO);

            // id를 null로 초기화. 생성 당시에는 id가 없어야 하기 때문이다.
            entity.setId(null);

            // @AuthenticationPrincipal 에서 넘어온 userId로 설정
            entity.setUserId(userId);

            // 서비스를 이용해 Todo 엔티티를 생성한다.
            List<TodoEntity> entities = todoService.create(entity);

            // 자바 스트림을 이용해 리턴된 엔티티 리스트를 TodoDTO 리스트로 변환
            List<TodoDTO> dtos = entities.stream().map(TodoDTO::new).collect(Collectors.toList());

            // 변환된 TodoDTO 리스트를 이용해 ResponseDTO를 초기화
            ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().data(dtos).build();

            // ResponseDTO를 리턴
            return ResponseEntity.ok().body(response);
        } catch (Exception e){
            // 예외가 있는 경우 dto 대신 error에 메시지를 넣어 리턴한다.
            String error = e.getMessage();
            ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().error(error).build();
            return ResponseEntity.badRequest().body(response);
        }
    }
}
