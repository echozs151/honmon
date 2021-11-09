/*  
Copyright 2021 the original author or authors.

This file is part of Honmon.

Honmon is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Honmon is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Honmon.  If not, see <https://www.gnu.org/licenses/>.
*/
package com.example.honmon.Controllers.Api;

import java.util.List;

import com.example.honmon.Models.User;
import com.example.honmon.Repo.UserRepository;

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
