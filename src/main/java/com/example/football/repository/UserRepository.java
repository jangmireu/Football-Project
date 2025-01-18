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
    User findByNickname(String nickname); // 닉네임 중복 확인

	User findByUsernameAndPassword(String username, String password);

	// Username을 기반으로 Badge와 함께 User를 조회
	@Query("SELECT u FROM User u LEFT JOIN FETCH u.badge WHERE u.username = :username")
	Optional<User> findUserWithBadgeByUsername(@Param("username") String username);

	// Nickname을 기반으로 Badge와 함께 User를 조회
	@Query("SELECT u FROM User u LEFT JOIN FETCH u.badge WHERE u.nickname = :nickname")
	Optional<User> findUserWithBadgeByNickname(@Param("nickname") String nickname);
}
