package com.app.service;

import java.util.HashSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.app.domain.enums.Role;
import com.app.domain.tool.IDGenerator;
import com.app.repository.UserRepository;
import com.app.security.JWTUtil;
import com.app.entity.User;

@Service
public class UserService implements UserDetailsService{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationConfiguration authenticationConfiguration;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
        User userEntity = userRepository.findByUsername(username).orElseThrow(()-> new UsernameNotFoundException("User not found"));

        return new org.springframework.security.core.userdetails.User(userEntity.getUsername(),
                userEntity.getPassword(),
                userEntity.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.name())).toList()
                
        );
    }

    // Register
    public User register(User user){
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("Username đã tồn tại");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(new HashSet<>());
        user.getRoles().add(Role.ROLE_USER);
        String id = IDGenerator.generateUserId();
        user.setUserid(id);

        return userRepository.save(user);
    }

    //Login
    public String login (User user) throws Exception{
        AuthenticationManager authenticationManager = authenticationConfiguration.getAuthenticationManager();
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));

        User userDb = userRepository.findByUsername(user.getUsername()).orElseThrow();

        return JWTUtil.generateToken(user.getUsername(),userDb.getRoles());
    }

    // Seller
    public void becomeSeller(String username){
        User userEntity = userRepository.findByUsername(username).orElseThrow();
        userEntity.getRoles().add(Role.ROLE_SELLER);
        userRepository.save(userEntity);
    }
}
