package com.mws.back_end.account.model.entity;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import static javax.persistence.GenerationType.IDENTITY;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
public class Tenant {
    public static final String TENANT_COLUMN_NAME = "name";

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long tenantId;

    @NotNull
    @Column(unique = true)
    private String name;

    @NotNull
    @Column(nullable = false, columnDefinition = "BOOLEAN")
    private boolean active = true;

}

