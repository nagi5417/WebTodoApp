# WebToDoApp API仕様書

**バージョン**: 1.0.0
**ベースURL**: `http://localhost:8080`
**認証方式**: セッション認証 (Cookie: JSESSIONID) + OAuth2 (Google Sign-In)

---

## 📋 目次

- [認証について](#認証について)
- [タスク管理API](#タスク管理api)
  - [GET /api/tasks](#get-apitasks) - タスク一覧取得
  - [POST /api/tasks](#post-apitasks) - タスク作成
  - [GET /api/tasks/{id}](#get-apitasksid) - タスク取得
  - [PUT /api/tasks/{id}](#put-apitasksid) - タスク更新
  - [DELETE /api/tasks/{id}](#delete-apitasksid) - タスク削除
- [認証API](#認証api)
  - [POST /api/auth/login](#post-apiauthlogin) - ログイン
  - [POST /api/auth/register](#post-apiauthregister) - ユーザー登録
  - [POST /api/auth/logout](#post-apiauthlogout) - ログアウト
  - [GET /api/auth/status](#get-apiauthstatus) - 認証状態確認
- [データモデル](#データモデル)
- [エラーハンドリング](#エラーハンドリング)

---

## 🔐 認証について

このAPIは以下の認証方式をサポートしています:

### 1. セッション認証（Cookie）
- Spring Securityによるセッション管理
- ログイン後、`JSESSIONID` Cookieが発行される
- **重要**: すべてのAPIリクエストにCookieを含める必要があります

### 2. OAuth2認証（Google Sign-In）
- Google OAuth2による認証
- 認証成功後、セッションが確立される

### 認証が必要なエンドポイント
以下を除く全てのエンドポイントで認証が必要です:
- `POST /api/auth/login`
- `POST /api/auth/register`
- `GET /api/auth/oauth2/success` (OAuth2コールバック)

---

## 📝 タスク管理API

### GET /api/tasks

**概要**: ログインユーザーに紐づく全タスクを取得します

**リクエスト**:
```http
GET /api/tasks HTTP/1.1
Host: localhost:8080
Cookie: JSESSIONID=xxx
```

**cURLコマンド**:
```bash
curl -X GET "http://localhost:8080/api/tasks" \
  -H "Cookie: JSESSIONID=xxx" \
  -H "Accept: application/json"
```

**レスポンス**:
- **200 OK**: タスク一覧取得成功
  ```json
  {
    "status": "success",
    "message": "タスク一覧を取得しました",
    "data": [
      {
        "id": 1,
        "userId": 1,
        "typeId": 1,
        "title": "サンプルタスク",
        "detail": "タスクの詳細説明",
        "deadline": "2025-12-31T23:59:59",
        "taskType": {
          "id": 1,
          "type": "仕事",
          "comment": "業務関連タスク"
        }
      }
    ]
  }
  ```

- **400 Bad Request**: ユーザーが見つからない
  ```json
  {
    "status": "error",
    "message": "ユーザーが見つかりません"
  }
  ```

- **401 Unauthorized**: 認証エラー

---

### POST /api/tasks

**概要**: 新しいタスクを作成します

**リクエスト**:
```http
POST /api/tasks HTTP/1.1
Host: localhost:8080
Content-Type: application/json
Cookie: JSESSIONID=xxx

{
  "typeId": 1,
  "title": "新しいタスク",
  "detail": "タスクの詳細説明",
  "deadline": "2025-12-31"
}
```

**リクエストボディ**:
| フィールド | 型 | 必須 | 説明 | 例 |
|-----------|-----|------|------|-----|
| `typeId` | integer | ✅ | タスク種別ID | `1` |
| `title` | string | ✅ | タスク名 | `"新しいタスク"` |
| `detail` | string | ❌ | タスク詳細 | `"詳細説明"` |
| `deadline` | string | ❌ | 期限（YYYY-MM-DD形式） | `"2025-12-31"` |

**cURLコマンド**:
```bash
curl -X POST "http://localhost:8080/api/tasks" \
  -H "Content-Type: application/json" \
  -H "Cookie: JSESSIONID=xxx" \
  -d '{
    "typeId": 1,
    "title": "新しいタスク",
    "detail": "タスクの詳細説明",
    "deadline": "2025-12-31"
  }'
```

**レスポンス**:
- **200 OK**: タスク作成成功
  ```json
  {
    "status": "success",
    "message": "タスクを作成しました",
    "data": {
      "id": 10,
      "userId": 1,
      "typeId": 1,
      "title": "新しいタスク",
      "detail": "タスクの詳細説明",
      "deadline": "2025-12-31T00:00:00"
    }
  }
  ```

- **400 Bad Request**: バリデーションエラー
  ```json
  {
    "status": "error",
    "message": "タスクの作成に失敗しました: [エラー詳細]"
  }
  ```

---

### GET /api/tasks/{id}

**概要**: 指定されたIDのタスクを取得します

**パスパラメータ**:
| パラメータ | 型 | 説明 | 例 |
|-----------|-----|------|-----|
| `id` | integer | タスクID | `1` |

**cURLコマンド**:
```bash
curl -X GET "http://localhost:8080/api/tasks/1" \
  -H "Cookie: JSESSIONID=xxx" \
  -H "Accept: application/json"
```

**レスポンス**:
- **200 OK**: タスク取得成功
  ```json
  {
    "status": "success",
    "message": "タスクを取得しました",
    "data": {
      "id": 1,
      "userId": 1,
      "typeId": 1,
      "title": "サンプルタスク",
      "detail": "詳細説明",
      "deadline": "2025-12-31T23:59:59"
    }
  }
  ```

- **400 Bad Request**: タスクが見つからない、または権限なし
  ```json
  {
    "status": "error",
    "message": "タスクが見つかりません"
  }
  ```
  ```json
  {
    "status": "error",
    "message": "アクセス権限がありません"
  }
  ```

---

### PUT /api/tasks/{id}

**概要**: 指定されたIDのタスクを更新します

**パスパラメータ**:
| パラメータ | 型 | 説明 | 例 |
|-----------|-----|------|-----|
| `id` | integer | タスクID | `1` |

**リクエストボディ**:
| フィールド | 型 | 必須 | 説明 | 例 |
|-----------|-----|------|------|-----|
| `typeId` | integer | ✅ | タスク種別ID | `1` |
| `title` | string | ✅ | タスク名 | `"更新後のタスク"` |
| `detail` | string | ❌ | タスク詳細 | `"更新後の詳細"` |
| `deadline` | string | ❌ | 期限（YYYY-MM-DD形式） | `"2026-01-15"` |

**cURLコマンド**:
```bash
curl -X PUT "http://localhost:8080/api/tasks/1" \
  -H "Content-Type: application/json" \
  -H "Cookie: JSESSIONID=xxx" \
  -d '{
    "typeId": 1,
    "title": "更新後のタスク",
    "detail": "更新後の詳細",
    "deadline": "2026-01-15"
  }'
```

**レスポンス**:
- **200 OK**: タスク更新成功
  ```json
  {
    "status": "success",
    "message": "タスクを更新しました",
    "data": {
      "id": 1,
      "userId": 1,
      "typeId": 1,
      "title": "更新後のタスク",
      "detail": "更新後の詳細",
      "deadline": "2026-01-15T00:00:00"
    }
  }
  ```

- **400 Bad Request**: エラー
  ```json
  {
    "status": "error",
    "message": "タスクの更新に失敗しました: [エラー詳細]"
  }
  ```

---

### DELETE /api/tasks/{id}

**概要**: 指定されたIDのタスクを削除します

**パスパラメータ**:
| パラメータ | 型 | 説明 | 例 |
|-----------|-----|------|-----|
| `id` | integer | タスクID | `1` |

**cURLコマンド**:
```bash
curl -X DELETE "http://localhost:8080/api/tasks/1" \
  -H "Cookie: JSESSIONID=xxx"
```

**レスポンス**:
- **200 OK**: タスク削除成功
  ```json
  {
    "status": "success",
    "message": "タスクを削除しました"
  }
  ```

- **400 Bad Request**: タスクが見つからない、または権限なし
  ```json
  {
    "status": "error",
    "message": "タスクが見つかりません"
  }
  ```

---

## 🔑 認証API

### POST /api/auth/login

**概要**: メールアドレスとパスワードでログインします

**認証**: 不要

**リクエストボディ**:
| フィールド | 型 | 必須 | 説明 | 例 |
|-----------|-----|------|------|-----|
| `email` | string | ✅ | メールアドレス | `"user@example.com"` |
| `password` | string | ✅ | パスワード | `"password123"` |

**cURLコマンド**:
```bash
curl -X POST "http://localhost:8080/api/auth/login" \
  -H "Content-Type: application/json" \
  -c cookies.txt \
  -d '{
    "email": "user@example.com",
    "password": "password123"
  }'
```

**レスポンス**:
- **200 OK**: ログイン成功
  ```json
  {
    "status": "success",
    "message": "ログイン成功"
  }
  ```
  **Set-Cookie**: `JSESSIONID=xxx; Path=/; HttpOnly`

- **400 Bad Request**: ログイン失敗
  ```json
  {
    "status": "error",
    "message": "ログインに失敗しました: [エラー詳細]"
  }
  ```

---

### POST /api/auth/register

**概要**: 新しいユーザーを登録します

**認証**: 不要

**リクエストボディ**:
| フィールド | 型 | 必須 | 説明 | 例 |
|-----------|-----|------|------|-----|
| `email` | string | ✅ | メールアドレス | `"newuser@example.com"` |
| `password` | string | ✅ | パスワード | `"password123"` |
| `name` | string | ✅ | ユーザー名 | `"山田太郎"` |

**cURLコマンド**:
```bash
curl -X POST "http://localhost:8080/api/auth/register" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "newuser@example.com",
    "password": "password123",
    "name": "山田太郎"
  }'
```

**レスポンス**:
- **200 OK**: 登録成功
  ```json
  {
    "status": "success",
    "message": "登録成功"
  }
  ```

- **400 Bad Request**: 登録失敗
  ```json
  {
    "status": "error",
    "message": "このメールアドレスは既に登録されています"
  }
  ```

---

### POST /api/auth/logout

**概要**: ログアウトしてセッションを無効化します

**cURLコマンド**:
```bash
curl -X POST "http://localhost:8080/api/auth/logout" \
  -H "Cookie: JSESSIONID=xxx"
```

**レスポンス**:
- **200 OK**: ログアウト成功
  ```json
  {
    "status": "success",
    "message": "ログアウト成功"
  }
  ```

---

### GET /api/auth/status

**概要**: 現在の認証状態を確認します

**cURLコマンド**:
```bash
curl -X GET "http://localhost:8080/api/auth/status" \
  -H "Cookie: JSESSIONID=xxx"
```

**レスポンス**:
- **200 OK**: 認証済み
  ```json
  {
    "authenticated": true,
    "message": "認証済み"
  }
  ```

- **200 OK**: 未認証
  ```json
  {
    "authenticated": false,
    "message": "未認証"
  }
  ```

---

## 📦 データモデル

### Task（タスク）

| フィールド | 型 | Null許可 | 説明 | 例 |
|-----------|-----|----------|------|-----|
| `id` | integer | ❌ | タスクID | `1` |
| `userId` | integer | ❌ | ユーザーID | `1` |
| `typeId` | integer | ❌ | タスク種別ID | `1` |
| `title` | string | ❌ | タスク名 | `"サンプルタスク"` |
| `detail` | string | ✅ | タスク詳細 | `"詳細説明"` |
| `deadline` | datetime | ✅ | 期限 | `"2025-12-31T23:59:59"` |
| `taskType` | TaskType | ✅ | タスク種別オブジェクト | `{...}` |

### TaskType（タスク種別）

| フィールド | 型 | 説明 | 例 |
|-----------|-----|------|-----|
| `id` | integer | タスク種別ID | `1` |
| `type` | string | 種別名 | `"仕事"` |
| `comment` | string | コメント | `"業務関連タスク"` |

### TaskRequest（タスク作成・更新リクエスト）

| フィールド | 型 | 必須 | 説明 | 例 |
|-----------|-----|------|------|-----|
| `typeId` | integer | ✅ | タスク種別ID | `1` |
| `title` | string | ✅ | タスク名 | `"新しいタスク"` |
| `detail` | string | ❌ | タスク詳細 | `"詳細説明"` |
| `deadline` | string | ❌ | 期限（YYYY-MM-DD形式） | `"2025-12-31"` |

### ApiResponse（API共通レスポンス）

| フィールド | 型 | 説明 | 例 |
|-----------|-----|------|-----|
| `status` | string | 処理結果（`success` or `error`） | `"success"` |
| `message` | string | メッセージ | `"タスク一覧を取得しました"` |
| `data` | object | データ（任意） | `{...}` |

---

## ⚠️ エラーハンドリング

### エラーレスポンス形式

すべてのエラーは以下の形式で返されます:

```json
{
  "status": "error",
  "message": "エラーの詳細メッセージ"
}
```

### 主なHTTPステータスコード

| コード | 説明 |
|--------|------|
| `200` | 成功 |
| `400` | リクエストエラー（バリデーションエラー、データ不在等） |
| `401` | 認証エラー（未ログイン） |
| `403` | 権限エラー（アクセス権限なし） |
| `404` | リソースが見つからない |
| `500` | サーバー内部エラー |

### よくあるエラー

#### 1. ユーザーが見つかりません
```json
{
  "status": "error",
  "message": "ユーザーが見つかりません"
}
```
**原因**: セッションが無効、またはユーザーが削除された
**対処**: 再ログインしてください

#### 2. アクセス権限がありません
```json
{
  "status": "error",
  "message": "アクセス権限がありません"
}
```
**原因**: 他のユーザーのタスクにアクセスしようとした
**対処**: 自分のタスクのみアクセスしてください

#### 3. タスクが見つかりません
```json
{
  "status": "error",
  "message": "タスクが見つかりません"
}
```
**原因**: 指定されたIDのタスクが存在しない
**対処**: 正しいタスクIDを指定してください

---

## 📝 使用例

### 完全なワークフロー例

```bash
# 1. ユーザー登録
curl -X POST "http://localhost:8080/api/auth/register" \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"pass123","name":"テストユーザー"}'

# 2. ログイン（Cookieを保存）
curl -X POST "http://localhost:8080/api/auth/login" \
  -H "Content-Type: application/json" \
  -c cookies.txt \
  -d '{"email":"test@example.com","password":"pass123"}'

# 3. タスク作成
curl -X POST "http://localhost:8080/api/tasks" \
  -H "Content-Type: application/json" \
  -b cookies.txt \
  -d '{"typeId":1,"title":"初めてのタスク","detail":"テストタスク","deadline":"2025-12-31"}'

# 4. タスク一覧取得
curl -X GET "http://localhost:8080/api/tasks" \
  -b cookies.txt

# 5. タスク更新
curl -X PUT "http://localhost:8080/api/tasks/1" \
  -H "Content-Type: application/json" \
  -b cookies.txt \
  -d '{"typeId":1,"title":"更新したタスク","detail":"更新しました","deadline":"2026-01-15"}'

# 6. タスク削除
curl -X DELETE "http://localhost:8080/api/tasks/1" \
  -b cookies.txt

# 7. ログアウト
curl -X POST "http://localhost:8080/api/auth/logout" \
  -b cookies.txt
```

---

## 🔗 関連ドキュメント

- [OpenAPI仕様書 (YAML)](./api-spec.yaml)
- [プロジェクトREADME](../CLAUDE.md)
- [Flywayマイグレーションファイル](../src/main/resources/db/migration/)

---

**生成日**: 2025-10-19
**対応バージョン**: Spring Boot 2.7.18
**データベース**: PostgreSQL (本番) / H2 (開発・テスト)
