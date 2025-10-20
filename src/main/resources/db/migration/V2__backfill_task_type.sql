-- PostgreSQL対応: DATETIME → TIMESTAMP
INSERT INTO task_type (id, type, comment) VALUES
(1,'優先度高','最優先で取り掛かるべきタスク'),
(2,'ー','期限に間に合わせるべきタスク'),
(3,'優先度低','今後やってみたいアイデア');

-- PostgreSQL/H2共通: SERIAL/BIGSERIALの場合、idは自動採番されるため省略可能
-- ただし、既存データとの互換性のため明示的にidを指定
INSERT INTO users (id, username, email, password, role, is_active, created_at, updated_at, last_login_at)
VALUES (1, 'test', 'test@example.com', 'test5417', 'USER', TRUE,
        TIMESTAMP '2020-07-07 15:00:00', TIMESTAMP '2020-07-07 15:00:00', TIMESTAMP '2020-07-07 15:00:00');

-- SERIAL の id は省略して入れるのが無難
INSERT INTO task (user_id, type_id, title, detail, deadline) VALUES
(1, 1, 'JUnitを学習', 'テストの仕方を学習する', TIMESTAMP '2020-07-07 15:00:00'),
(1, 3, 'サービスの自作', 'マイクロサービスを作ってみる', TIMESTAMP '2020-09-13 17:00:00');
