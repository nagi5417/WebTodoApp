package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.example.demo.dao.UserDao;
import com.example.demo.entity.User;

@DataJpaTest
public class UserDaoTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserDao userDao;

    private User testUser;

    @BeforeEach
    void setup() {
        testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setUsername("testUser");
        testUser.setPassword("password");
        testUser.setIsActive(false);
        testUser.setRole(User.Role.ADMIN);

        testUser = entityManager.persistAndFlush(testUser);
    }

    @Test
    void save_新規ユーザー_登録が正常に行われる() {

        User newUser = new User();
        newUser.setEmail("new@example.com");
        newUser.setUsername("newUser");
        newUser.setPassword("encodedPassword");
        newUser.setIsActive(true);
        newUser.setRole(User.Role.USER);

        User saveUser = userDao.save(newUser);

        assertNotNull(saveUser.getId());
        assertEquals("new@example.com", saveUser.getEmail());
        assertEquals("newUser", saveUser.getUsername());
        assertEquals(User.Role.USER, saveUser.getRole());
        assertTrue(saveUser.getIsActive());
        assertNotNull(saveUser.getCreatedAt());
    }

    // =========================
    // findByEmail のテスト
    // =========================

    @Test
    void findByEmail_存在するメール_正しく取得される() {
        Optional<User> result = userDao.findByEmail("test@example.com");

        assertTrue(result.isPresent());

        User user = result.get();
        assertEquals(testUser.getId(), user.getId());
        assertEquals(testUser.getEmail(), user.getEmail());
        assertEquals(testUser.getUsername(), user.getUsername());
    }

    @Test
    void findByEmail_存在しないメール_空のOptional() {
        Optional<User> result = userDao.findByEmail("nonexistent@example.com");

        assertFalse(result.isPresent());
    }

    @Test
    void findByEmail_nullのメール_空のOptional() {
        Optional<User> result = userDao.findByEmail(null);

        assertFalse(result.isPresent());
    }

    @Test
    void findByEmail_空のメール_空のOptional() {
        Optional<User> result = userDao.findByEmail("");

        assertFalse(result.isPresent());
    }

    // =========================
    // existsByEmail のテスト
    // =========================

    @Test
    void existsByEmail_存在するメール_trueを返す() {
        boolean result = userDao.existsByEmail("test@example.com");

        assertTrue(result);
    }

    @Test
    void existsByEmail_存在しないメール_falseを返す() {
        boolean result = userDao.existsByEmail("nonexistent@example.com");

        assertFalse(result);
    }

    @Test
    void existsByEmail_nullのメール_falseを返す() {
        boolean result = userDao.existsByEmail(null);

        assertFalse(result);
    }

    @Test
    void existsByEmail_空のメール_falseを返す() {
        boolean result = userDao.existsByEmail("");

        assertFalse(result);
    }

    // =========================
    // 両メソッドの整合性テスト
    // =========================

    @Test
    void メソッドの整合性_存在するメールで両方一致() {
        Optional<User> findResult = userDao.findByEmail("test@example.com");
        boolean existsResult = userDao.existsByEmail("test@example.com");

        assertTrue(findResult.isPresent());
        assertTrue(existsResult);
    }

    @Test
    void メソッドの整合性_存在しないメールで両方不一致() {
        Optional<User> findResult = userDao.findByEmail("nonexistent@example.com");
        boolean existsResult = userDao.existsByEmail("nonexistent@example");

        assertFalse(findResult.isPresent());
        assertFalse(existsResult);
    }
}
