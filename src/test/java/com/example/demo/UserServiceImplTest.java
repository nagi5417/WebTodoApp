package com.example.demo;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;

import java.util.Optional;

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
        userService = new UserServiceImpl();
        // リフレクションでフィールドを注入
        try {
            java.lang.reflect.Field userDaoField = UserServiceImpl.class.getDeclaredField("userDao");
            userDaoField.setAccessible(true);
            userDaoField.set(userService, userDao);

            java.lang.reflect.Field passwordEncoderField = UserServiceImpl.class.getDeclaredField("passwordEncoder");
            passwordEncoderField.setAccessible(true);
            passwordEncoderField.set(userService, passwordEncoder);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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

    @Test
    void updateUserSettingsPartial_全フィールド更新_すべてが更新される() {
        // Given
        Long userId = 1L;
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setEmail("old@example.com");
        existingUser.setUsername("olduser");
        existingUser.setPassword("oldEncodedPassword");

        when(userDao.findById(userId)).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.encode("newpassword")).thenReturn("newEncodedPassword");

        // When
        userService.updateUserSettingsPartial(userId, "new@example.com", "newuser", "newpassword");

        // Then
        verify(userDao, times(1)).save(argThat(user ->
            user.getEmail().equals("new@example.com") &&
            user.getUsername().equals("newuser") &&
            user.getPassword().equals("newEncodedPassword")
        ));
    }

    @Test
    void updateUserSettingsPartial_メールアドレスのみ更新_メールアドレスだけが変更される() {
        // Given
        Long userId = 1L;
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setEmail("old@example.com");
        existingUser.setUsername("olduser");
        existingUser.setPassword("oldEncodedPassword");

        when(userDao.findById(userId)).thenReturn(Optional.of(existingUser));

        // When
        userService.updateUserSettingsPartial(userId, "new@example.com", null, null);

        // Then
        verify(userDao, times(1)).save(argThat(user ->
            user.getEmail().equals("new@example.com") &&
            user.getUsername().equals("olduser") &&
            user.getPassword().equals("oldEncodedPassword")
        ));
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void updateUserSettingsPartial_ユーザー名のみ更新_ユーザー名だけが変更される() {
        // Given
        Long userId = 1L;
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setEmail("old@example.com");
        existingUser.setUsername("olduser");
        existingUser.setPassword("oldEncodedPassword");

        when(userDao.findById(userId)).thenReturn(Optional.of(existingUser));

        // When
        userService.updateUserSettingsPartial(userId, null, "newuser", null);

        // Then
        verify(userDao, times(1)).save(argThat(user ->
            user.getEmail().equals("old@example.com") &&
            user.getUsername().equals("newuser") &&
            user.getPassword().equals("oldEncodedPassword")
        ));
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void updateUserSettingsPartial_パスワードのみ更新_パスワードだけが変更される() {
        // Given
        Long userId = 1L;
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setEmail("old@example.com");
        existingUser.setUsername("olduser");
        existingUser.setPassword("oldEncodedPassword");

        when(userDao.findById(userId)).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.encode("newpassword")).thenReturn("newEncodedPassword");

        // When
        userService.updateUserSettingsPartial(userId, null, null, "newpassword");

        // Then
        verify(userDao, times(1)).save(argThat(user ->
            user.getEmail().equals("old@example.com") &&
            user.getUsername().equals("olduser") &&
            user.getPassword().equals("newEncodedPassword")
        ));
        verify(passwordEncoder, times(1)).encode("newpassword");
    }

    @Test
    void updateUserSettingsPartial_空文字列を渡す_更新されない() {
        // Given
        Long userId = 1L;
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setEmail("old@example.com");
        existingUser.setUsername("olduser");
        existingUser.setPassword("oldEncodedPassword");

        when(userDao.findById(userId)).thenReturn(Optional.of(existingUser));

        // When
        userService.updateUserSettingsPartial(userId, "", "", "");

        // Then
        verify(userDao, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void updateUserSettingsPartial_全てnull_更新されない() {
        // Given
        Long userId = 1L;
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setEmail("old@example.com");
        existingUser.setUsername("olduser");
        existingUser.setPassword("oldEncodedPassword");

        when(userDao.findById(userId)).thenReturn(Optional.of(existingUser));

        // When
        userService.updateUserSettingsPartial(userId, null, null, null);

        // Then
        verify(userDao, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void updateUserSettingsPartial_存在しないユーザーID_例外が発生する() {
        // Given
        Long userId = 999L;
        when(userDao.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            userService.updateUserSettingsPartial(userId, "new@example.com", "newuser", "newpassword");
        });

        verify(userDao, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(anyString());
    }
}
