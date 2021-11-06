package com.example.honmon.Repo;
import com.example.honmon.Models.Settings;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface SettingsRepository extends MongoRepository<Settings, Long> {
    Settings findByKey(String key);
}