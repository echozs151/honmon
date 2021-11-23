package com.example.honmon.Controllers.Api;

import java.io.IOException;
import java.util.Set;

import com.example.honmon.services.ImportService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/app-management")
public class AppManagement {
    
    @Autowired
    ImportService importService;

    @GetMapping("import-library")
    public Set<String> importLibrary() throws IOException
    {
        return importService.importBooks("F:\\Media\\Books");
    }
}
