package com.example.demo.app.user;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * ユーザー設定変更フォーム
 */
public class UserSettingsForm {

    // メールアドレス（変更する場合のみ指定）
    @Email(message = "正しいメールアドレス形式で入力してください")
    private String email;

    // ユーザー名（変更する場合のみ指定）
    @Size(min = 2, max = 50, message = "ユーザー名は2文字以上50文字以下で入力してください")
    private String username;

    // パスワード（変更する場合のみ指定）
    @Size(min = 8, max = 100, message = "パスワードは8文字以上100文字以下で入力してください")
    private String password;

    // 確認用パスワード（パスワード変更時に指定）
    @Size(min = 8, max = 100, message = "確認用パスワードは8文字以上100文字以下で入力してください")
    private String passwordConfirm;

    // パスワード変更フラグ（パスワードを変更する場合はtrue）
    private boolean changePassword = false;

    public UserSettingsForm() {
    }

    public UserSettingsForm(String email, String username) {
        this.email = email;
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordConfirm() {
        return passwordConfirm;
    }

    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
    }

    public boolean isChangePassword() {
        return changePassword;
    }

    public void setChangePassword(boolean changePassword) {
        this.changePassword = changePassword;
    }

    @Override
    public String toString() {
        return "UserSettingsForm{" +
                "email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", changePassword=" + changePassword +
                '}';
    }
}
