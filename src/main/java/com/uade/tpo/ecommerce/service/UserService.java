package com.uade.tpo.ecommerce.service;

import com.uade.tpo.ecommerce.entity.User;
import com.uade.tpo.ecommerce.entity.dto.UserRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UserService {
    User create(UserRequest request);

    User getUserById(Long id);

    Page<User> listUsers(Pageable pageable);

    User update(Long id, UserRequest request);

    void delete(Long id);

    Optional<User> findByEmail(String email);

}