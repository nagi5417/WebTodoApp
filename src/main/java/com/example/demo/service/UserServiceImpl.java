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
}
