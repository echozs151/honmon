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
package com.example.honmon.Repo;

import com.example.honmon.Models.User;

import org.springframework.data.mongodb.repository.MongoRepository;
// import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.jpa.repository.Query;
// import org.springframework.data.repository.CrudRepository;
// import org.springframework.data.repository.query.Param;

public interface UserRepository extends MongoRepository<User, String> { // extends CrudRepository<User, Long> {

    // @Query("SELECT u FROM User u WHERE u.username = :username")
    // public User getUserByUsername(@Param("username") String username);

    User findByUsername(String username);

}

