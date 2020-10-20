package com.satya.springbatch.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.satya.springbatch.model.User;

public interface UserRepository extends JpaRepository<User, Integer> {
}
