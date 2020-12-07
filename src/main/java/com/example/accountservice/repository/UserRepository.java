package com.example.accountservice.repository;

import com.example.accountservice.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    User findByEmail(String email);

    @Query("SELECT count(u.id) FROM User u WHERE u.email=:email")
    int countUserWithSameEmail(@Param("email") String email);
}
