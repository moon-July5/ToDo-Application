package com.moon.dto;

import com.moon.entity.TodoEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TodoDTO {
    private String id;
    private String title;
    private boolean done;

    public TodoDTO(TodoEntity todoEntity){
        this.id = todoEntity.getId();
        this.title = todoEntity.getTitle();
        this.done = todoEntity.isDone();
    }

    public static TodoEntity toEntity(TodoDTO todoDTO){
        return TodoEntity.builder()
                .id(todoDTO.getId())
                .title(todoDTO.getTitle())
                .done(todoDTO.isDone())
                .build();
    }
}
