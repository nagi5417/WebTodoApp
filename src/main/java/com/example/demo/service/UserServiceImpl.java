package com.example.demo.service;

import com.example.demo.app.user.UserRegistrationDto;
import com.example.demo.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Optional;
import java.time.LocalDateTime;

import com.example.demo.dao.UserDao;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    /**
     * ユーザー情報登録処理
     * 
     * @param registrationDto ユーザー登録情報
     */
    public void registerUser(UserRegistrationDto registrationDto) {
        logger.debug("Starting user registration process for email: {}", registrationDto.getEmail());

        // DtoからEntityに変換
        User user = convertToEntity(registrationDto);

        // ユーザー情報を登録
        userDao.save(user);

        logger.debug("User registration process completed for email: {}", registrationDto.getEmail());
    }

    /**
     * メールアドレスの存在確認
     * 
     * @param email 確認するメールアドレス
     * @return 存在する場合true、存在しない場合false
     */
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userDao.existsByEmail(email);
    }

    /**
     * メールアドレスでユーザーを検索（ログイン認証用
     * 
     * @param email 検索するメールアドレス
     * @return 見つかったユーザー、存在しない場合はnull
     */
    @Transactional(readOnly = true)
    public Optional<User> findOptionalByEmail(String email) {
        return userDao.findByEmail(email);
    }

    /**
     * ユーザーのログイン日時を更新
     * 
     * @param email ログインしたユーザーのメールアドレス
     */
    public void updateLastLoginAt(String email) {
        logger.debug("Updating last login time for email: {}", email);

        Optional<User> result = userDao.findByEmail(email);
        if (result.isPresent()) {
            User user = result.get();
            user.setLastLoginAt(LocalDateTime.now());
            userDao.save(user);
            logger.debug("Last login time updated for email: {} (username: {})", email, user.getUsername());
        } else {
            logger.warn("User not found for login time update: {}", email);
        }
    }

    /**
     * DTOをEntityに変換する
     * 
     * @param registrationDto 変換元のDTO
     * @return 変換後のEntity
     */
    private User convertToEntity(UserRegistrationDto registrationDto) {
        User user = new User();
        user.setEmail(registrationDto.getEmail()); // メールアドレスを設定
        user.setUsername(registrationDto.getUsername()); // 表示名を設定

        // パスワードを暗号化して設定
        String encodedPassword = passwordEncoder.encode(registrationDto.getPassword());
        user.setPassword(encodedPassword);

        // デフォルト値の設定
        user.setRole(User.Role.USER); // デフォルトはUSERロール
        user.setIsActive(true); // デフォルトはアクティブ

        // lastLoginAtは初期登録時はnull

        return user;
    }

    public Long getUserIdByEmail(String email) {
        return userDao.findByEmail(email)
                .map(User::getId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    /**
     * メールアドレスでユーザーを検索
     * 
     * @param email 検索するメールアドレス
     * @return 見つかったユーザー、存在しない場合はnull
     */
    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        return userDao.findByEmail(email).orElse(null);
    }

    /**
     * Google IDでユーザーを検索
     * 
     * @param googleId 検索するGoogle ID
     * @return 見つかったユーザー、存在しない場合はnull
     */
    @Transactional(readOnly = true)
    public User findByGoogleId(String googleId) {
        return userDao.findByGoogleId(googleId).orElse(null);
    }

    /**
     * ユーザーを保存
     *
     * @param user 保存するユーザー
     * @return 保存されたユーザー
     */
    public User save(User user) {
        return userDao.save(user);
    }

    /**
     * ユーザー設定を更新
     *
     * @param userId ユーザーID
     * @param email 新しいメールアドレス
     * @param username 新しいユーザー名
     * @param password 新しいパスワード（nullの場合は変更なし）
     */
    @Transactional
    public void updateUserSettings(Long userId, String email, String username, String password) {
        logger.debug("Updating user settings for userId: {}", userId);

        User user = userDao.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        // メールアドレスとユーザー名を更新
        user.setEmail(email);
        user.setUsername(username);

        // パスワードが指定されている場合は暗号化して更新
        if (password != null && !password.isEmpty()) {
            String encodedPassword = passwordEncoder.encode(password);
            user.setPassword(encodedPassword);
            logger.debug("Password updated for userId: {}", userId);
        }

        userDao.save(user);
        logger.debug("User settings updated successfully for userId: {}", userId);
    }

    /**
     * ユーザー設定を部分的に更新
     * nullまたは空文字列のフィールドは更新しない
     *
     * @param userId ユーザーID
     * @param email 新しいメールアドレス（nullの場合は変更なし）
     * @param username 新しいユーザー名（nullの場合は変更なし）
     * @param password 新しいパスワード（nullの場合は変更なし）
     */
    @Transactional
    public void updateUserSettingsPartial(Long userId, String email, String username, String password) {
        logger.debug("Partially updating user settings for userId: {}", userId);

        User user = userDao.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        boolean updated = false;

        // メールアドレスが指定されている場合のみ更新
        if (email != null && !email.trim().isEmpty()) {
            user.setEmail(email);
            updated = true;
            logger.debug("Email updated for userId: {}", userId);
        }

        // ユーザー名が指定されている場合のみ更新
        if (username != null && !username.trim().isEmpty()) {
            user.setUsername(username);
            updated = true;
            logger.debug("Username updated for userId: {}", userId);
        }

        // パスワードが指定されている場合のみ暗号化して更新
        if (password != null && !password.trim().isEmpty()) {
            String encodedPassword = passwordEncoder.encode(password);
            user.setPassword(encodedPassword);
            updated = true;
            logger.debug("Password updated for userId: {}", userId);
        }

        if (updated) {
            userDao.save(user);
            logger.debug("User settings partially updated successfully for userId: {}", userId);
        } else {
            logger.debug("No fields to update for userId: {}", userId);
        }
    }

    /**
     * IDでユーザーを取得
     *
     * @param userId ユーザーID
     * @return 見つかったユーザー、存在しない場合はnull
     */
    @Transactional(readOnly = true)
    public User findById(Long userId) {
        return userDao.findById(userId).orElse(null);
    }
}
