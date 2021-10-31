package com.example.honmon.Models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Getter;

@Entity
@Table(name = "books")
@Getter @Setter @NoArgsConstructor
public class Book extends AbstractEntity {

    private String title;
    private String author;

    public Book() {}

    public Book(String title, String author) 
    {
        this.title = title;
        this.author = author;
    }

    public String getAuthor()
    {
        return author;
    }

    public void setAuthor(String author) 
    {
        this.author = author;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title) 
    {
        this.title = title;
    }

    public String toString()
    {
        return "Book(id="+getId()+", title="+title+", author="+author+")";
    }

    
}
