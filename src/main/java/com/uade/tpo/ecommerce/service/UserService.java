package com.uade.tpo.ecommerce.service;

import com.uade.tpo.ecommerce.entity.User;
import com.uade.tpo.ecommerce.controllers.users.UserRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UserService {
    public User create(UserRequest request);

    public User getUserById(Long id);

    public Page<User> listUsers(Pageable pageable);

    public User update(Long id, UserRequest request);

    User makeAdmin(Long id, UserRequest request);

    User removeAdmin(Long id, UserRequest request);

    public void delete(Long id);

    public Optional<User> findByEmail(String email);

}