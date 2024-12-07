package com.clozex.shop.repository.user;

import com.clozex.shop.model.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);

    @EntityGraph(value = "User.roles", type = EntityGraph.EntityGraphType.LOAD)
    Optional<User> findByEmail(String email);
}
