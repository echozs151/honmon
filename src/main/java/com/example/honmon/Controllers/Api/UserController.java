package com.example.honmon.Controllers.Api;

import java.util.List;

import com.example.honmon.Models.Book;
import com.example.honmon.Models.User;
import com.example.honmon.Repo.BookRepository;
import com.example.honmon.Repo.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/users")
public class UserController {


    private final UserRepository userRepository;

    UserController(UserRepository userRepository)
    {
        this.userRepository = userRepository;
    }

    @GetMapping()
    public List<User> all()
    {
        return (List<User>) userRepository.findAll();
    }

    @PostMapping()
    User newBook(@RequestBody User newUser) {
        return userRepository.save(newUser);
    }
}
