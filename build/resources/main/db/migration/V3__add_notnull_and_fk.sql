-- PostgreSQL対応: ALTER COLUMN構文はPostgreSQLと互換性あり
-- （保険）NULL があれば埋める
UPDATE task SET type_id = 1 WHERE type_id IS NULL;

-- NOT NULL 制約だけ付与
ALTER TABLE task ALTER COLUMN type_id SET NOT NULL;
