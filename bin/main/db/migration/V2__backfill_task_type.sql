INSERT INTO task_type (id, type, comment) VALUES
(1,'優先度高','最優先で取り掛かるべきタスク'),
(2,'ー','期限に間に合わせるべきタスク'),
(3,'優先度低','今後やってみたいアイデア');

INSERT INTO users (id, username, email, password, role, is_active, created_at, updated_at, last_login_at)
VALUES (1, 'test', 'test@example.com', 'test5417', 'USER', TRUE,
        '2020-07-07 15:00:00', '2020-07-07 15:00:00', '2020-07-07 15:00:00');

-- AUTO_INCREMENT の id は省略して入れるのが無難
INSERT INTO task (user_id, type_id, title, detail, deadline) VALUES
(1, 1, 'JUnitを学習', 'テストの仕方を学習する', '2020-07-07 15:00:00'),
(1, 3, 'サービスの自作', 'マイクロサービスを作ってみる', '2020-09-13 17:00:00');
