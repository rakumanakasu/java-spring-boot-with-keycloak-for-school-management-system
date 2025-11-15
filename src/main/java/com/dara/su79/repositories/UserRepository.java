package com.dara.su79.repositories;

import com.dara.su79.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    // Check if username already exists
    boolean existsByUsername(String username);
    Optional<User> findByUsername(String username);

    Page<User> findByUsernameContainingIgnoreCase(String username, Pageable pageable);
    List<User> findByUsernameContainingIgnoreCase(String username);
    // for email checks)
    boolean existsByEmail(String email);
}
