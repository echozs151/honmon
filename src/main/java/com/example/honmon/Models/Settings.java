package com.example.honmon.Models;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "settings")
public class Settings {
    private String id;
    private String description;
    private String key;
    private String value;

    public Settings() {}

    public Settings(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getDescription() {
        return description;
    }
    public String getId() {
        return id;
    }
    public String getKey() {
        return key;
    }
    public String getValue() {
        return value;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Settings(id="+getId()+", key="+getKey()+", value="+getValue()+")";
    }
}
