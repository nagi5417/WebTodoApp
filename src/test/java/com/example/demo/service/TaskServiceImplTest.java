package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import com.example.demo.entity.Task;

@SpringJUnitConfig
@SpringBootTest
@TestPropertySource(properties = {
    "spring.datasource.initialization-mode=always",
    "spring.jpa.hibernate.ddl-auto=none",
    "spring.datasource.url=jdbc:h2:mem:integrationtest;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
    "spring.security.oauth2.client.registration.google.client-id=test-client-id",
    "spring.security.oauth2.client.registration.google.client-secret=test-client-secret"
})
@DisplayName("TaskServiceImplの結合テスト")
class TaskServiceImplTest {

    @Autowired
    private TaskService taskService;

    @Test
    @DisplayName("タスクが取得できない場合のテスト")
    void testGetTaskFormReturnNull() {
        try {
            Optional<Task> task = taskService.getTask(0);
        } catch (TaskNotFoundException e) {
            Assertions.assertEquals(e.getMessage(), "指定されたタスクが存在しません");
        }
    }

    @Test
    @DisplayName("全件検索のテスト")
    void testFindAllCheckCount() {
        //全件取得（テストユーザーID=1）
        List<Task> list = taskService.findAll(1);
        //Taskテーブルに入っている2件が取得できているか確認
        assertEquals(2, list.size());
    }

    @Test
    @DisplayName("1件のタスクが取得できた場合のテスト")
    void testGetTaskFormReturnOne() {
        //idが1のTaskを取得
        Optional<Task> taskOpt = taskService.getTask(1);
        //取得できたことを確認
        assertEquals("JUnitを学習", taskOpt.get().getTitle());
    }
}