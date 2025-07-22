package com.payal.ecom.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name="category")
@Data
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)   //  Uses Long for auto-increment
    private Long id;

    private String name;

    @Lob      //  For long description text (maps to LONGTEXT in MySQL)
    private String description;

}
