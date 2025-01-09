package com.example.football.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.football.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username); // username으로 검색

	User findByUsernameAndPassword(String username, String password);
	
	@Query("SELECT u FROM User u WHERE u.username = :username")
    Optional<User> findOptionalByUsername(@Param("username") String username);
	
}
