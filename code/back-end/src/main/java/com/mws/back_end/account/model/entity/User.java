package com.mws.back_end.account.model.entity;


import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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
    @Size(min=1)
    @Column(unique = true)
    private String email;

    @NotNull
    @Size(min=8)
    private String password;

    @NotNull
    @Size(min=1)
    private String firstName;

    @NotNull
    @Size(min=1)
    private String lastName;

}
