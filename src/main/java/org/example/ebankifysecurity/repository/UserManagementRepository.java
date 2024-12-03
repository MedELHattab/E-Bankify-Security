package org.example.ebankifysecurity.repository;

import org.example.ebankifysecurity.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserManagementRepository extends JpaRepository<User, String> {
    boolean existsByEmail(String email);

}
