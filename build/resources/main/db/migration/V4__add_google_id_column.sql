-- PostgreSQL対応: VARCHAR、CREATE INDEXはPostgreSQLと互換性あり
-- Google IDカラムを追加
ALTER TABLE users ADD COLUMN google_id VARCHAR(255);

-- インデックスを追加（パフォーマンス向上）
CREATE INDEX idx_users_google_id ON users(google_id);
