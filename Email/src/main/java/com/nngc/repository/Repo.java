package com.nngc.repository;

import com.nngc.Email;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface Repo extends MongoRepository<Email, String> {
}
