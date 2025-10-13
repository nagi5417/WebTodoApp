package com.example.demo;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.demo.app.user.UserRegistrationDto;
import com.example.demo.dao.UserDao;
import com.example.demo.entity.User;
import com.example.demo.service.UserServiceImpl;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserDao userDao;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserServiceImpl userService;

    @BeforeEach
    void setup() {
        userService = new UserServiceImpl(userDao, passwordEncoder);
    }

    @Test
    void registerUser_正常な登録DTO_ユーザーが正常に登録される() {
        UserRegistrationDto registrationDto = new UserRegistrationDto();
        registrationDto.setEmail("test@example.com");
        registrationDto.setUsername("testuser");
        registrationDto.setPassword("password123");

        String encodedPassword = "encodedPassword123";
        when(passwordEncoder.encode("password123")).thenReturn(encodedPassword);

        userService.registerUser(registrationDto);

        verify(passwordEncoder, times(1)).encode("password123");
        verify(userDao, times(1)).save(any(User.class));

        verify(userDao).save(argThat(user -> user.getEmail().equals("test@example.com") &&
                user.getUsername().equals("testuser") &&
                user.getPassword().equals(encodedPassword) &&
                user.getRole() == User.Role.USER &&
                user.getIsActive() == true));
    }

    @Test
    void registerUser_デフォルト値が正しく設定される() {
        UserRegistrationDto registrationDto = new UserRegistrationDto();
        registrationDto.setEmail("test@example.com");
        registrationDto.setUsername("testuser");
        registrationDto.setPassword("password");

        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        userService.registerUser(registrationDto);

        verify(userDao).save(argThat(user ->
            user.getRole() == User.Role.USER &&
            user.getIsActive() == true
        ));
    }

    @Test
    void existsByEmail_メールアドレスが存在する場合_trueを返す() {
        String email = "nonexistenr@example.com";
        when(userDao.existsByEmail(email)).thenReturn(true);

        boolean result = userService.existsByEmail(email);

        verify(userDao, times(1)).existsByEmail(email);
    }

    @Test
    void existsByEmail_メールアドレスが存在しない場合_falseを返す() {
        String email = "nonexistent@example.com";
        when(userDao.existsByEmail(email)).thenReturn(false);

        boolean result = userService.existsByEmail(email);

        assertFalse(result);
        verify(userDao, times(1)).existsByEmail(email);
    }

    @Test
    void existsByEmail_nullのメールアドレス_DAOのメソッドが呼ばれる() {
        String email = null;
        when(userDao.existsByEmail(email)).thenReturn(false);

        boolean result = userService.existsByEmail(email);

        assertFalse(result);
        verify(userDao, times(1)).existsByEmail(email);
    }

    @Test
    void existsByEmail_空文字のメールアドレス_DAOのメソッドが呼ばれる() {
        String email = "";
        when(userDao.existsByEmail(email)).thenReturn(false);

        boolean result = userService.existsByEmail(email);

        assertFalse(result);
        verify(userDao, times(1)).existsByEmail(email);
    }

    @Test
    void registerUser_DAOで例外が発生した場合_例外がそのまま伝搬される() {
        UserRegistrationDto registrationDto = new UserRegistrationDto();
        registrationDto.setEmail("test@example.com");
        registrationDto.setUsername("testuser");
        registrationDto.setPassword("password");

        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userDao.save(any(User.class))).thenThrow(new RuntimeException("Database error"));

        assertThrows(RuntimeException.class, () -> {
            userService.registerUser(registrationDto);
        });
    }

    @Test
    void registerUser_パスワードエンコーダーで例外が発生した場合_例外がそのまま伝搬される() {
        UserRegistrationDto registrationDto = new UserRegistrationDto();
        registrationDto.setEmail("test@example.com");
        registrationDto.setUsername("testuser");
        registrationDto.setPassword("password");

        when(passwordEncoder.encode("password")).thenThrow(new RuntimeException("Encoding error"));

        assertThrows(RuntimeException.class, () -> {
            userService.registerUser(registrationDto);
        });

        verify(userDao, never()).save(any(User.class));
    }
}
