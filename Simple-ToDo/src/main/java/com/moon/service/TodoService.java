package com.moon.service;

import com.moon.entity.TodoEntity;
import com.moon.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Log4j2
@RequiredArgsConstructor
@Service
public class TodoService {
    private final TodoRepository todoRepository;

    // Todo 아이템을 삭제하는 메서드
    public List<TodoEntity> delete(TodoEntity todoEntity){
        validate(todoEntity);

        try{
            // 엔티티를 삭제한다.
            todoRepository.delete(todoEntity);
        } catch (Exception e){
            // Exception 발생 시 id와 exception을 로깅한다.
            log.error("error deleting entity ", todoEntity.getId(), e);

            // 컨트롤러로 exception을 보낸다.
            // 데이터베이스 내부 로직을 캡슐화하려면 e를 리턴하지 않고 새 exception 오브젝트를 리턴
            throw new RuntimeException("error deleting entity "+todoEntity.getId());
        }

        // 새 todo 리스트를 가져와 리턴한다.
        return retrieve(todoEntity.getUserId());
    }

    // Todo 아이템을 수정하는 메서드
    public List<TodoEntity> update(TodoEntity todoEntity){
        // 저장할 엔티티가 유효한지 검사
        validate(todoEntity);

        // 넘겨받은 엔티티 id를 이용해 TodoEntity 를 가져온다.
        Optional<TodoEntity> original = todoRepository.findById(todoEntity.getId());

        original.ifPresent(todo -> {
            // 반환된 TodoEntity가 존재하면 값을 새 entity 값으로 덮어 씌운다.
            todo.setTitle(todoEntity.getTitle());
            todo.setDone(todoEntity.isDone());

            // 데이터베이스에 새 값을 저장한다.
            todoRepository.save(todo);
        });

        // 사용자의 모든 Todo 리스트를 리턴
        return retrieve(todoEntity.getUserId());
    }

    // Todo 리스트 검색 메서드
    public List<TodoEntity> retrieve(String userId){
        return todoRepository.findByUserId(userId);
    }

    // Todo 아이템을 생성하는 메서드
    public List<TodoEntity> create(TodoEntity todoEntity){
        validate(todoEntity);

        todoRepository.save(todoEntity);

        log.info("TodoEntity Id : {} is saved.", todoEntity.getId());

        return todoRepository.findByUserId(todoEntity.getUserId());
    }

    // 유효성 검사
    private void validate(TodoEntity todoEntity){
        // Validations
        if(todoEntity == null){
            log.warn("Entity cannot be null.");
            throw new RuntimeException("Entity cannot be null.");
        }

        if(todoEntity.getUserId() == null){
            log.warn("Unknown user.");
            throw new RuntimeException("Unknown user.");
        }
    }
}
