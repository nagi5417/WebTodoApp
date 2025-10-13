package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import org.springframework.http.MediaType;

import com.example.demo.app.user.UserController;
import com.example.demo.app.user.UserRegistrationDto;
import com.example.demo.service.UserService;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    // ===============================
    // TC001: showRegistrationForm()のテスト
    // ===============================
    @Test
    void ユーザー登録画面が正常に表示される() throws Exception {
        mockMvc.perform(get("/users/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("users/register"))
                .andExpect(model().attributeExists("userRegistrationDto"));
    }

    // ===============================
    // TC002: registerUser() 正常系テスト
    // ===============================
    @Test
    void 正常なユーザー登録機能が成功する() throws Exception {
        doNothing().when(userService).registerUser(any(UserRegistrationDto.class));

        mockMvc.perform(post("/users/register")
                .param("username", "テストユーザー")
                .param("email", "test@example.com")
                .param("password", "password123")
                .param("confirmPassword", "password123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users/register"))
                .andExpect(flash().attributeExists("successMessage"))
                .andExpect(flash().attribute("successMessage", "登録が完了しました"));

        verify(userService, times(1)).registerUser(any(UserRegistrationDto.class));
    }

    // ===============================
    // TC003-009: バリデーションエラー系テスト
    // ===============================
    @Test
    void ユーザー名が空の場合バリデーションエラーになる() throws Exception {
        mockMvc.perform(post("/users/register")
                .param("username", "")
                .param("email", "test@example.com")
                .param("password", "password123")
                .param("confirmPassword", "password123"))
                .andExpect(status().isOk())
                .andExpect(view().name("users/register"))
                .andExpect(model().attributeHasFieldErrors("userRegistrationDto", "username"));

        // サービスが呼ばれていないことを確認
        verify(userService, never()).registerUser(any(UserRegistrationDto.class));
    }

    @Test
    void メールアドレスが空の場合バリデーションエラーになる() throws Exception {
        mockMvc.perform(post("/users/register")
                .param("username", "テストユーザー")
                .param("email", "")
                .param("password", "password123")
                .param("confirmPassword", "password123"))
                .andExpect(status().isOk())
                .andExpect(view().name("users/register"))
                .andExpect(model().attributeHasFieldErrors("userRegistrationDto", "email"));

        // サービスが呼ばれていないことを確認
        verify(userService, never()).registerUser(any(UserRegistrationDto.class));
    }

    @Test
    void メールアドレスが不正な形式の場合バリデーションエラーになる() throws Exception {
        mockMvc.perform(post("/users/register")
                .param("username", "テストユーザー")
                .param("email", "invalid-email")
                .param("password", "password123")
                .param("confirmPassword", "password123"))
                .andExpect(status().isOk())
                .andExpect(view().name("users/register"))
                .andExpect(model().attributeHasFieldErrors("userRegistrationDto", "email"));

        verify(userService, never()).registerUser(any(UserRegistrationDto.class));
    }

    @Test
    void パスワードが8文字未満の場合バリデーションエラーになる() throws Exception {
        mockMvc.perform(post("/users/register")
                .param("username", "テストユーザー")
                .param("email", "test@example.com")
                .param("password", "1234567")
                .param("confirmPassword", "1234567"))
                .andExpect(status().isOk())
                .andExpect(view().name("users/register"))
                .andExpect(model().attributeHasFieldErrors("userRegistrationDto", "password"));

        verify(userService, never()).registerUser(any(UserRegistrationDto.class));
    }

    @Test
    void パスワードが空の場合バリデーションエラーになる() throws Exception {
        // When & Then
        mockMvc.perform(post("/users/register")
                .param("username", "テストユーザー")
                .param("email", "test@example.com")
                .param("password", "")
                .param("confirmPassword", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("users/register"))
                .andExpect(model().attributeHasFieldErrors("userRegistrationDto", "password"));

        verify(userService, never()).registerUser(any(UserRegistrationDto.class));
    }

    @Test
    void 確認用パスワードが空の場合バリデーションエラーになる() throws Exception {
        // When & Then
        mockMvc.perform(post("/users/register")
                .param("username", "テストユーザー")
                .param("email", "test@example.com")
                .param("password", "password123")
                .param("confirmPassword", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("users/register"))
                .andExpect(model().attributeHasFieldErrors("userRegistrationDto", "confirmPassword"));

        verify(userService, never()).registerUser(any(UserRegistrationDto.class));
    }

    @Test
    void パスワードと確認用パスワードが不一致の場合バリデーションエラーになる() throws Exception {
        // When & Then
        mockMvc.perform(post("/users/register")
                .param("username", "テストユーザー")
                .param("email", "test@example.com")
                .param("password", "password123")
                .param("confirmPassword", "password456"))
                .andExpect(status().isOk())
                .andExpect(view().name("users/register"))
                .andExpect(model().attributeHasFieldErrors("userRegistrationDto", "confirmPassword"));

        verify(userService, never()).registerUser(any(UserRegistrationDto.class));
    }

    void ユーザー名が51文字以上の場合バリデーションエラーになる() throws Exception {
        // Given
        String longUsername = "a".repeat(51);

        // When & Then
        mockMvc.perform(post("/users/register")
                .param("username", longUsername)
                .param("email", "test@example.com")
                .param("password", "password123")
                .param("confirmPassword", "password123"))
                .andExpect(status().isOk())
                .andExpect(view().name("users/register"))
                .andExpect(model().attributeHasFieldErrors("userRegistrationDto", "username"));

        verify(userService, never()).registerUser(any(UserRegistrationDto.class));
    }

    // ===============================
    // TC010-011: 例外処理系テスト
    // ===============================
    @Test
    void データベース制約違反エラーの場合適切なエラーメッセージが表示される() throws Exception {
        doThrow(new DataIntegrityViolationException("duplicate key"))
                .when(userService).registerUser(any(UserRegistrationDto.class));

        mockMvc.perform(post("/users/register")
                .param("username", "テストユーザー")
                .param("email", "test@example.com")
                .param("password", "password123")
                .param("confirmPassword", "password123"))
                .andExpect(status().isOk())
                .andExpect(view().name("users/register"))
                .andExpect(model().attribute("errorMessage", "登録情報に問題があります"));

        verify(userService, times(1)).registerUser(any(UserRegistrationDto.class));
    }

    @Test
    void システムエラーの場合適切なエラーメッセージが表示される() throws Exception {
        doThrow(new RuntimeException("system error"))
                .when(userService).registerUser(any(UserRegistrationDto.class));

        mockMvc.perform(post("/users/register")
                .param("username", "テストユーザー")
                .param("email", "test@example.com")
                .param("password", "password123")
                .param("confirmPassword", "password123"))
                .andExpect(status().isOk())
                .andExpect(view().name("users/register"))
                .andExpect(model().attribute("errorMessage", "登録処理中にエラーが発生しました"));

        verify(userService, times(1)).registerUser(any(UserRegistrationDto.class));
    }

    // ===============================
    // TC012-014: checkEmailDuplicate()のテスト
    // ===============================

    @Test
    void メールアドレス重複チェックで存在する場合trueを返す() throws Exception {
        when(userService.existsByEmail("existing@example.com")).thenReturn(true);

        mockMvc.perform(get("/users/api/check-email")
                .param("email", "existing@example.com"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("true"));

        verify(userService, times(1)).existsByEmail("existing@example.com");
    }

    @Test
    void メールアドレス重複チェックで存在しない場合falseを返す() throws Exception {
        when(userService.existsByEmail("new@example.com")).thenReturn(false);

        mockMvc.perform(get("/users/api/check-email")
                .param("email", "new@example.com"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("false"));

        verify(userService, times(1)).existsByEmail("new@example.com");
    }

    @Test
    void 空のメールアドレスでの重複チェック() throws Exception {
        when(userService.existsByEmail("")).thenReturn(false);

        mockMvc.perform(get("/users/api/check-email")
                .param("email", ""))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("false"));

        verify(userService, times(1)).existsByEmail("");
    }

    // ===============================
    // 追加テスト: 境界値・特殊ケース
    // ===============================

    @Test
    void ユーザー名が50文字の場合正常処理される() throws Exception {
        // Given
        String username50 = "a".repeat(50);
        doNothing().when(userService).registerUser(any(UserRegistrationDto.class));

        // When & Then
        mockMvc.perform(post("/users/register")
                .param("username", username50)
                .param("email", "test@example.com")
                .param("password", "password123")
                .param("confirmPassword", "password123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users/register"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(userService, times(1)).registerUser(any(UserRegistrationDto.class));
    }

    @Test
    void パスワードが8文字の場合正常処理される() throws Exception {
        doNothing().when(userService).registerUser(any(UserRegistrationDto.class));

        mockMvc.perform(post("/users/register")
                .param("username", "テストユーザー")
                .param("email", "test@example.com")
                .param("password", "12345678")
                .param("confirmPassword", "12345678"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users/register"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(userService, times(1)).registerUser(any(UserRegistrationDto.class));
    }

    @Test
    void 複数のバリデーションエラーが同時に発生する場合() throws Exception {
        // When & Then（ユーザー名空、メール形式不正、パスワード短い）
        mockMvc.perform(post("/users/register")
                .param("username", "")
                .param("email", "invalid-email")
                .param("password", "123")
                .param("confirmPassword", "123"))
                .andExpect(status().isOk())
                .andExpect(view().name("users/register"))
                .andExpect(model().attributeHasErrors("userRegistrationDto"));

        verify(userService, never()).registerUser(any(UserRegistrationDto.class));
    }
}