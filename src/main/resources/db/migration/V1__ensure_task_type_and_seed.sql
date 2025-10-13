CREATE TABLE users (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(50) NOT NULL,
  email VARCHAR(50) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,
  role VARCHAR(20) NOT NULL DEFAULT 'USER',
  is_active BOOLEAN NOT NULL DEFAULT TRUE,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME,
  last_login_at DATETIME
);

CREATE TABLE task_type (
  id INT NOT NULL,
  type VARCHAR(20) NOT NULL,
  comment VARCHAR(50),
  PRIMARY KEY (id)
);

-- 参照元 task は最後に
CREATE TABLE task (
  id INT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,      -- users.id と型を合わせる（BIGINT）
  type_id INT NOT NULL,         -- NOT NULL（方針A）
  title VARCHAR(50) NOT NULL,
  detail TEXT,
  deadline DATETIME NOT NULL,
  CONSTRAINT fk_task_user FOREIGN KEY (user_id) REFERENCES users(id),
  CONSTRAINT fk_task_type FOREIGN KEY (type_id) REFERENCES task_type(id)
);