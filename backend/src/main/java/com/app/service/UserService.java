package com.app.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.app.common.enums.Role;
import com.app.dto.response.LoginResponse;
import com.app.dto.response.UserResponse;
import com.app.repository.UserRepository;
import com.app.security.JWTUtil;
import com.app.entity.UserEntity;
import com.app.mapper.AuthMapper;
import com.app.mapper.UserMapper;
import com.app.exception.security.EmailAlreadyExistxException;
import com.app.exception.security.UsernameAlreadyExistException;
import com.app.exception.user.AccountLockedException;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired 
    private UserMapper userMapper;

    @Autowired
    private AuthMapper authMapper;

    @Autowired
    private AuthenticationConfiguration authenticationConfiguration;

    // ====== LOAD USER ======
    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException{

        UserEntity userEntity = userRepository.findByUsernameOrEmailOrPhone(identifier, identifier, identifier).orElseThrow(()-> new UsernameNotFoundException("Không tìm thấy user"));

        List<GrantedAuthority> authorities = userEntity.getRoles().stream().map(role -> (GrantedAuthority) new SimpleGrantedAuthority(role.name())).toList();

        return User.builder()
                .username(userEntity.getUserId())
                .password(userEntity.getPassword())
                .authorities(authorities)
                .disabled(!userEntity.isActive())
                .build();

    }

    // ====== REGISTER ======
    public UserResponse register(String username, String email, String password){

        if (userRepository.existByUsername(username)) {
            throw new UsernameAlreadyExistException("Tên đăng nhập đã tồn tại");
        }

        if (userRepository.existByEmail(email)) {
            throw new EmailAlreadyExistxException("Tài khoản đã tồn tại");
        }
        
        UserEntity userEntity = new UserEntity(username, email, passwordEncoder.encode(password));

        userEntity.addRole(Role.ROLE_USER);

        UserEntity saveUser = userRepository.save(userEntity);

        return userMapper.toResponse(saveUser);
    }

    // ====== LOGIN ======
    public LoginResponse login (String identifier, String password) throws Exception{

        AuthenticationManager authenticationManager = authenticationConfiguration.getAuthenticationManager();

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(identifier, password));

        UserEntity user = userRepository.findByUsernameOrEmailOrPhone(identifier, identifier, identifier).orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng"));

        String token = JWTUtil.generateToken(user.getUserId(), user.getRoles());

        return authMapper.toLoginResponse(user, token);
    }

    // ====== SELLER ======
    public LoginResponse becomeSeller(String userId){

        UserEntity userEntity = userRepository.findByUserId(userId).orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng"));

        // ====== CHECK STATUS ======
        if (!userEntity.isActive()){
            throw new AccountLockedException("Tài khoản không hoạt động!");
        }

        if (userEntity.getRoles().contains(Role.ROLE_SELLER)){
            throw new RuntimeException("Đã trở thành seller");
        }

        userEntity.addRole(Role.ROLE_SELLER);

        userRepository.save(userEntity);

        String token = JWTUtil.generateToken(userEntity.getUserId(), userEntity.getRoles());

        return authMapper.toLoginResponse(userEntity, token);
    }

    // ====== LOCK USER ======
    public void lockUser(String userId){

        UserEntity user = userRepository.findByUserId(userId).orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng"));

        user.lockAccount();
    }

    // ====== UNLOCK USER ======
    public void unlockUser(String userId){

        UserEntity user = userRepository.findByUserId(userId).orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng"));

        user.unlockAccount();
    }

    // ====== GET USER ======
    public UserResponse getByUserId(String userId){

        UserEntity user = userRepository.findByUserId(userId).orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng"));

        return userMapper.toResponse(user);
    }

    public List<UserResponse> getAll(){
        return userMapper.toResponseList(userRepository.findAll());
    }

    public UserEntity getEntityByUserId(String userID){
        
        return userRepository.findByUserId(userID).orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng"));
    }
}
