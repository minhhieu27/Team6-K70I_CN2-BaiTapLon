package com.example;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class User {
    
    @Id
    @GeneratedValue
    private long id;
}
