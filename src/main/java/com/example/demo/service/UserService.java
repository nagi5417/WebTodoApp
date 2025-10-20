package com.example.demo.service;

import com.example.demo.app.user.UserRegistrationDto;
import com.example.demo.entity.User;

public interface UserService {

    // ユーザー情報登録処理
    void registerUser(UserRegistrationDto registrationDto);

    // メールアドレスの存在確認
    boolean existsByEmail(String email);

    // メールアドレスでユーザーIDを取得
    Long getUserIdByEmail(String email);

    // メールアドレスでユーザーを検索
    User findByEmail(String email);

    // Google IDでユーザーを検索
    User findByGoogleId(String googleId);

    // ユーザーを保存
    User save(User user);

    // ユーザー設定を更新（メール、ユーザー名、パスワード）
    void updateUserSettings(Long userId, String email, String username, String password);

    // ユーザー設定を部分的に更新（nullのフィールドは更新しない）
    void updateUserSettingsPartial(Long userId, String email, String username, String password);

    // IDでユーザーを取得
    User findById(Long userId);

}