package com.uade.tpo.ecommerce.controllers.users;

import com.uade.tpo.ecommerce.entity.User;
import com.uade.tpo.ecommerce.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UsersController {

    @Autowired
    private UserService service;

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        User user = service.getUserById(id);
        UserResponseDTO dto = new UserResponseDTO(user.getId(), user.getFirstName(), user.getLastName());
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    public Page<User> listUsers(Pageable pageable) {
        return service.listUsers(pageable);
    }

    @PutMapping("/{id}")
    public User update(@PathVariable Long id, @RequestBody UserRequest request) {
        return service.update(id, request);
    }

    @PutMapping("/removeadmin/{id}")
    public User removeAdmin(@PathVariable Long id, @RequestBody UserRequest request) {
        return service.removeAdmin(id, request);
    }

    @PutMapping("/makeadmin/{id}")
    public User makeAdmin(@PathVariable Long id, @RequestBody UserRequest request) {
        return service.makeAdmin(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}