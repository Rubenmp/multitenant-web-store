package com.mws.back_end.account.model.entity;


import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import static javax.persistence.GenerationType.IDENTITY;


@Data
@Entity
@NoArgsConstructor
public class User {
    public static final String USER_COLUMN_EMAIL = "email";
    @NotNull
    private Long tenantId;

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
