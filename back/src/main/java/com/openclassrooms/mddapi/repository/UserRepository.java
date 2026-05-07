package com.openclassrooms.mddapi.repository;

import com.openclassrooms.mddapi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);

    boolean existsByUsername(String username);

    boolean existsByUsernameAndIdNot(String username, Long id);

    @Query("SELECT u FROM User u WHERE u.email = :identifier OR u.username = :identifier")
    Optional<User> findByEmailOrUsername(@Param("identifier") String identifier);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.subscriptions WHERE u.id = :id")
    Optional<User> findByIdWithSubscriptions(@Param("id") Long id);
}
