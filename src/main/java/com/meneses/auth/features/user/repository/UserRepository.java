package com.meneses.auth.features.user.repository;

import com.meneses.auth.features.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository  extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Page<User> findByEmailContainingIgnoreCase(String email, Pageable pageable);
}
