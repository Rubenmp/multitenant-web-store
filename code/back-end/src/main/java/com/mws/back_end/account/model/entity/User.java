package com.mws.back_end.account.model.entity;


import javax.persistence.*;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.NoArgsConstructor;

import static javax.persistence.GenerationType.IDENTITY;


@Data
@Entity
@NoArgsConstructor
public class User {
    public static final String USER_COLUMN_EMAIL = "email";

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @NotNull
    @Column(unique = true)
    private String email;

    @NotNull
    private String password;

    @NotNull
    private String firstName;

    @NotNull
    private String lastName;
}

