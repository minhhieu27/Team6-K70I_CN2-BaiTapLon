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
import com.app.common.enums.UserStatus;
import com.app.dto.request.ChangePasswordRequest;
import com.app.dto.request.ResetPasswordRequest;
import com.app.dto.request.UpdateProfileRequest;
import com.app.dto.response.LoginResponse;
import com.app.dto.response.MessageResponse;
import com.app.dto.response.UserResponse;
import com.app.repository.UserRepository;
import com.app.security.JWTUtil;

import jakarta.persistence.EntityNotFoundException;

import com.app.entity.UserEntity;
import com.app.mapper.AuthMapper;
import com.app.mapper.UserMapper;
import com.app.exception.security.EmailAlreadyExistxException;
import com.app.exception.security.OldPasswordNotMatchesException;
import com.app.exception.security.PasswordNotMatchesException;
import com.app.exception.security.PhoneAlreadyExistsException;
import com.app.exception.security.UsernameAlreadyExistException;
import com.app.exception.user.AccountBannedException;
import com.app.exception.user.AccountLockedException;
import com.app.exception.user.UserNotFoundException;

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

        UserEntity userEntity = userRepository.findByUsernameOrUserProfile_EmailOrUserProfile_Phone(identifier, identifier, identifier).orElseThrow(()-> new UsernameNotFoundException("Không tìm thấy user"));

        List<GrantedAuthority> authorities = userEntity.getRoles().stream().map(role -> (GrantedAuthority) new SimpleGrantedAuthority(role.name())).toList();

        return User.builder()
                .username(userEntity.getUserId())
                .password(userEntity.getPassword())
                .authorities(authorities)
                .build();

    }

    // ====== REGISTER ======
    public UserResponse register(String username, String email, String phone, String password){

        if (userRepository.existsByUsername(username)) {
            throw new UsernameAlreadyExistException("Tên đăng nhập đã tồn tại");
        }

        if (userRepository.existsByUserProfile_Email(email)) {
            throw new EmailAlreadyExistxException("Email đã được đăng ký");
        }

        if (userRepository.existsByUserProfile_Phone(phone)){
            throw new PhoneAlreadyExistsException("SĐT đã được đăng ký");
        }
        
        UserEntity userEntity = new UserEntity(username, email, phone, passwordEncoder.encode(password));

        userEntity.addRole(Role.ROLE_USER);

        UserEntity saveUser = userRepository.save(userEntity);

        return userMapper.toResponse(saveUser);
    }

    // ====== LOGIN ======
    public LoginResponse login (String identifier, String password) throws Exception{

        AuthenticationManager authenticationManager = authenticationConfiguration.getAuthenticationManager();

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(identifier, password));

        UserEntity user = userRepository.findByUsernameOrUserProfile_EmailOrUserProfile_Phone(identifier, identifier, identifier).orElseThrow();

        if (user.getStatus() == UserStatus.BANNED){
            throw new AccountBannedException("Tài khoản đã bị cấm");
        }
        
        if (user.getStatus() == UserStatus.LOCKED){

            user.setStatus(UserStatus.ACTIVE);

            userRepository.save(user);
        }

        String token = JWTUtil.generateToken(user.getUserId(), user.getRoles());

        return authMapper.toLoginResponse(user, token);
    }

    // ====== SELLER ======
    public LoginResponse becomeSeller(String userId){

        UserEntity userEntity = userRepository.findByUserId(userId).orElseThrow(() -> new EntityNotFoundException("Không tìm thấy người dùng"));

        // ====== CHECK STATUS ======
        if (userEntity.getStatus() == UserStatus.BANNED){
            throw new AccountLockedException("Tài khoản đã bị ban");
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
    public MessageResponse lockUser(String userId){

        UserEntity user = userRepository.findByUserId(userId).orElseThrow(() -> new UserNotFoundException("Không tìm thấy người dùng"));

        user.lockAccount();

        userRepository.save(user);

        return new MessageResponse("Đã khóa tài khoản");
    }

    // ====== GET USER ======
    public UserResponse getByUserId(String userId){

        UserEntity user = userRepository.findByUserId(userId).orElseThrow(() -> new UserNotFoundException("Không tìm thấy người dùng"));

        return userMapper.toResponse(user);
    }

    public List<UserResponse> getAll(){
        return userMapper.toResponseList(userRepository.findAll());
    }

    public UserEntity getEntityByUserId(String userID){
        
        return userRepository.findByUserId(userID).orElseThrow(() -> new UserNotFoundException("Không tìm thấy người dùng"));
    }

    // ====== CHANGE PASSWORD ======
    public MessageResponse changePassword(String userId, ChangePasswordRequest req){

        UserEntity user = userRepository.findByUserId(userId).orElseThrow(() -> new UserNotFoundException("Không tìm thấy người dùng"));

        // ====== CHECK OLD PASSWORD ======
        if (!passwordEncoder.matches(req.getOldPassword(), user.getPassword())){
            throw new OldPasswordNotMatchesException("Mật khẩu cũ không đúng");
        }

        // ====== CHECK CONFIRM PASSWORD ======
        if (!req.getNewPassword().equals(req.getConfirmPassword())){
            throw new PasswordNotMatchesException("Xác nhận mật khẩu không khớp");
        }

        user.setPassword(passwordEncoder.encode(req.getNewPassword()));

        userRepository.save(user);

        return new MessageResponse("Đổi mật khẩu thành công");
    }

    // ====== RESET PASSWORD ======
    public MessageResponse resetPassword(ResetPasswordRequest req) {

        UserEntity user = userRepository.findByUserProfile_PhoneOrUserProfile_Email(req.getPhone(), req.getEmail()).orElseThrow(() -> new UserNotFoundException("Không tìm thấy người dùng"));

        // ====== CHECK CONFIRM PASSWORD ======
        if (!req.getNewPassword().equals(req.getConfirmPassword())){
            throw new PasswordNotMatchesException("Xác nhận mật khẩu không khớp");
        }

        // ====== RESET PASSWORD ======
        user.setPassword(passwordEncoder.encode(req.getNewPassword()));

        userRepository.save(user);

        return new MessageResponse("Reset mật khẩu thành công");
    }

    // ====== UPDATE PROFILE ======
    public MessageResponse updateProfile(String userId, UpdateProfileRequest req){

        UserEntity user = userRepository.findByUserId(userId).orElseThrow(() -> new UserNotFoundException("Không tìm thấy người dùng"));

        if (req.getEmail() != null && !req.getEmail().equals(user.getUserProfile().getEmail()) && userRepository.existsByUserProfile_Email(req.getEmail())){
            throw new EmailAlreadyExistxException("Email đã tồn tại");
        }

        if (req.getPhone() != null && userRepository.existsByUserProfile_Phone(req.getPhone()) && !req.getPhone().equals(user.getUserProfile().getPhone())){
            throw new PhoneAlreadyExistsException("Số điện thoại đã tồn tại");
        }

        // ====== UPDATE ======
        if (req.getUsername() != null){
            user.setUsername(req.getUsername());
        }

        if (req.getEmail() != null){
            user.getUserProfile().setEmail(req.getEmail());
        }

        if (req.getPhone() != null){
            user.getUserProfile().setPhone(req.getPhone());
        }

        if (req.getBio() != null){
            user.getUserProfile().setBio(req.getBio());
        }

        if (req.getAvatar() != null){
            user.getUserProfile().setAvatar(req.getAvatar());
        }

        userRepository.save(user);

        return new MessageResponse("Cập nhật profile thành công");
    }
}
