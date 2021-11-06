package com.example.honmon.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping()
public class HomeController {
    // @CrossOrigin(origins = "http://localhost:8081")
    @GetMapping("/home")
    public String index(Model model)
    {
        return "index";
    }

    @GetMapping("/user")
    @ResponseBody
    public String user()
    {
        return "Welcome User";
    }

    @GetMapping("/admin")
    public String admin()
    {
        return "Welcome Admin";
    }
}
