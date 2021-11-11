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

import java.util.List;

import com.example.honmon.Models.Book;

import org.springframework.data.mongodb.repository.MongoRepository;
// import org.springframework.data.repository.CrudRepository;

// public interface BookRepository extends CrudRepository<Book, Long> {
// }


public interface BookRepository extends MongoRepository<Book, Long> {
    Book findById(String id);
    void deleteById(String id);
}