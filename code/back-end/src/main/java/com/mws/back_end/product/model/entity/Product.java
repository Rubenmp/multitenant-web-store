package com.mws.back_end.product.model.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import static javax.persistence.GenerationType.IDENTITY;

@Data
@Entity
@NoArgsConstructor
public class Product {
    public static final String PRODUCT_COLUMN_NAME = "name";

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @NotNull
    @Column(unique = true)
    private String name;

    @NotNull
    private String image;

    @NotNull
    //@Type(type = "true_false")
    @Column(nullable = false, columnDefinition = "BOOLEAN")
    private boolean active = true;
}
