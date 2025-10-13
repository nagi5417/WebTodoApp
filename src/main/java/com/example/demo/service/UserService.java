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

}