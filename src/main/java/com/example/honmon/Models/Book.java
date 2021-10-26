package com.example.honmon.Models;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Getter;

@Entity
@Table(name = "books")
@Getter @Setter @NoArgsConstructor
public class Book {

    private @Id Long id;
    private String title;
    private String author;

    public Book(String title, String author) 
    {
        
    }
}
