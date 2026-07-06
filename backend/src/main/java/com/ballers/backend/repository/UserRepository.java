package com.ballers.backend.repository;

import com.ballers.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

// Extending JpaRepository<User, Long> gives us save(), findById(), findAll(), deleteById(),
// etc. for free - Spring generates a real implementation of this interface at startup
// and registers it as a bean, without us writing any implementation code.
public interface UserRepository extends JpaRepository<User, Long> {
}
