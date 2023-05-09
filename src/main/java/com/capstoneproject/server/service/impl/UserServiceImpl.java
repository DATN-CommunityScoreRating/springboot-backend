package com.capstoneproject.server.service.impl;

import com.capstoneproject.server.domain.entity.RoleEntity;
import com.capstoneproject.server.domain.entity.UserEntity;
import com.capstoneproject.server.domain.repository.RoleRepository;
import com.capstoneproject.server.domain.repository.UserRepository;
import com.capstoneproject.server.exception.ObjectNotFoundException;
import com.capstoneproject.server.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lkadai0801
 * @since 09/05/2023
 */

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByUsername(username).orElseThrow(() ->
                new ObjectNotFoundException("username", username));
        return new User(user.getUsername(), user.getPassword(), new ArrayList<>(List.of(new SimpleGrantedAuthority(user.getRole().getRoleName()))));
    }

    @Override
    public UserEntity addUser(String username, String password, Long roleId, String fullName, String email) {
        UserEntity newUser = new UserEntity();
        newUser.setUsername(username);
        newUser.setPassword(passwordEncoder.encode(password));
        newUser.setFullName(fullName);
        newUser.setEmail(email);
        RoleEntity role = roleRepository.findById(roleId).orElseThrow(() ->
                new ObjectNotFoundException("roleId", roleId));
        newUser.setRole(role);
        return userRepository.save(newUser);
    }
}
