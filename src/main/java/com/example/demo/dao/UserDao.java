package com.example.demo.dao;

import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserDao extends JpaRepository<User, Long> {
    /**
     * メールアドレスでユーザーを検索
     * 
     * @param email 検索するメールアドレス
     * @return Optional<User>
     */
    Optional<User> findByEmail(String email);

    /**
     * メールアドレスの存在確認
     * 
     * @param email 確認するメールアドレス
     * @return 存在する場合true、存在しない場合false
     */
    boolean existsByEmail(String email);

    /**
     * Google IDでユーザーを検索
     * 
     * @param googleId 検索するGoogle ID
     * @return Optional<User>
     */
    Optional<User> findByGoogleId(String googleId);
}
