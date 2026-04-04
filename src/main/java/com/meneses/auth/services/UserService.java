package com.meneses.auth.services;

import com.meneses.auth.entities.Role;
import com.meneses.auth.entities.User;
import com.meneses.auth.repositories.RoleRepository;
import com.meneses.auth.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;


    public void addRoleToUser(Long userId, String roleName) {

        User user = userRepository.findById(userId).orElseThrow();
        Role role = roleRepository.findByName(roleName).orElseThrow();

        user.getRoles().add(role);
        userRepository.save(user);
    }
}
