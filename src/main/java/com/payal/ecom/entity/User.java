package com.payal.ecom.entity;

import com.payal.ecom.enums.UserRole;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name="users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String password;
    private String name;
    private UserRole role;


    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] img;


}
