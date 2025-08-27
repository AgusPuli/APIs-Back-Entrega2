package com.uade.tpo.ecommerce.service.impl;

import com.uade.tpo.ecommerce.controllers.users.UserRequest;
import com.uade.tpo.ecommerce.entity.User;
import com.uade.tpo.ecommerce.exceptions.UserEmailAlreadyExistsException;
import com.uade.tpo.ecommerce.exceptions.UserNotFoundException;
import com.uade.tpo.ecommerce.repository.UserRepository;
import com.uade.tpo.ecommerce.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository users;

    @Override
    public User create(UserRequest request) {
        String email = safe(request.getEmail());
        if (users.existsByEmail(email)) {
            throw new UserEmailAlreadyExistsException(email);
        }
        User u = User.builder()
                .firstName(safe(request.getFirstName()))
                .lastName(safe(request.getLastName()))
                .email(email)
                .password(request.getPassword())
                .build();

        return users.save(u);
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return users.findById(id).orElseThrow(() -> new UserNotFoundException(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<User> listUsers(Pageable pageable) {
        return users.findAll(pageable);
    }

    @Override
    public User update(Long id, UserRequest request) {
        User u = users.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        String newEmail = safe(request.getEmail());

        if (!u.getEmail().equalsIgnoreCase(newEmail) && users.existsByEmail(newEmail)) {
            throw new UserEmailAlreadyExistsException(newEmail);
        }

        u.setFirstName(safe(request.getFirstName()));
        u.setLastName(safe(request.getLastName()));
        u.setEmail(newEmail);
        u.setPassword(request.getPassword());
        return users.save(u);
    }

    @Override
    public void delete(Long id) {
        User u = users.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        users.delete(u);
    }

    @Override
    @Transactional(readOnly = true)
    public java.util.Optional<User> findByEmail(String email) {
        return users.findByEmail(safe(email));
    }

    private String safe(String s) {
        return s == null ? null : s.trim();
    }
}
