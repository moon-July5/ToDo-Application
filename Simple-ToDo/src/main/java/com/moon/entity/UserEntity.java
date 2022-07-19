package com.moon.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = "email")})
public class UserEntity {

    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @GeneratedValue(generator = "system-uuid")
    @Id
    private String id; // 사용자에게 부여되는 id

    @Column(nullable = false)
    private String username; // 사용자 이름

    @Column(nullable = false)
    private String email; // 사용자의 email

    @Column(nullable = false)
    private String password; // 사용자 패스워드

}
