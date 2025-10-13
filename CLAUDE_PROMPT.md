# WebToDoStarter - Claude AI プロンプト用ファイル

## プロジェクト概要

WebToDoStarterは、Spring BootとReactを使用したタスク管理Webアプリケーションです。ユーザー認証（フォーム認証・Google OAuth2）機能付きで、個人のタスクを管理できます。

## 技術スタック

### バックエンド
- **Spring Boot 2.7.18**
- **Java 17**
- **Spring Security** (フォーム認証 + Google OAuth2)
- **Spring Data JPA + Hibernate**
- **Spring JDBC** (手動SQL実装)
- **H2 Database** (インメモリ)
- **Flyway** (マイグレーション管理)
- **Gradle** (ビルドツール)

### フロントエンド
- **React**
- **Axios** (HTTP通信)
- **ポート**: 3000

### 開発環境
- **バックエンドポート**: 8080
- **H2コンソール**: http://localhost:8080/h2-console
- **JDBC URL**: `jdbc:h2:mem:test`
- **ユーザー名**: `sa`, **パスワード**: (空)

## アーキテクチャ

```
┌─────────────────┐    HTTP/REST API    ┌─────────────────┐
│   React App     │ ←─────────────────→ │  Spring Boot    │
│  (port: 3000)   │     CORS有効        │   (port: 8080)  │
└─────────────────┘                     └─────────────────┘
                                                │
                                        ┌─────────────────┐
                                        │  H2 Database    │
                                        │   (in-memory)   │
                                        └─────────────────┘
```

### レイヤー構成
- **Controller層**: REST API + Thymeleaf
- **Service層**: ビジネスロジック
- **DAO層**: データアクセス（Spring JDBC使用）
- **Entity層**: データモデル

## ディレクトリ構造

```
src/main/java/com/example/demo/
├── app/
│   ├── api/          # REST APIコントローラー
│   ├── task/         # タスク関連
│   └── user/         # ユーザー関連
├── config/           # 設定クラス
├── dao/              # データアクセス層
├── entity/           # エンティティクラス
└── service/          # サービス層

src/main/resources/
├── db/migration/     # Flywayマイグレーション
├── templates/        # Thymeleafテンプレート
└── static/          # 静的リソース
```

## 主要なエンティティ

### User
- id (PK)
- email (ユニーク)
- username
- password (ハッシュ化)
- role (USER/ADMIN)
- googleId (Google OAuth用)
- isActive
- createdAt, updatedAt, lastLoginAt

### Task
- id (PK)
- userId (FK → User.id)
- typeId (FK → TaskType.id)
- title
- detail
- deadline
- createdAt, updatedAt

### TaskType
- id (PK)
- type (例: "仕事", "プライベート")
- comment

## 認証システム

### 1. フォーム認証
- ログインURL: `/users/login`
- 成功時リダイレクト: `http://localhost:3000/tasks`

### 2. Google OAuth2認証
- OAuth2エンドポイント: `/oauth2/authorization/google`
- コールバック: `/login/oauth2/code/google`
- 成功時処理: `/api/auth/oauth2/success`
- 最終リダイレクト: `http://localhost:3000/tasks`

### 認証フロー
```
Google認証 → /api/auth/oauth2/success → ユーザー作成/更新 → フロントエンドリダイレクト
```

## API エンドポイント

### 認証API (/api/auth)
- `POST /api/auth/login` - フォームログイン
- `POST /api/auth/register` - ユーザー登録
- `GET /api/auth/status` - 認証状態確認
- `POST /api/auth/logout` - ログアウト
- `GET /api/auth/oauth2/success` - OAuth2成功時処理

### タスクAPI (/api/tasks)
- `GET /api/tasks` - タスク一覧取得
- `GET /api/tasks/{id}` - タスク詳細取得
- `POST /api/tasks` - タスク作成
- `PUT /api/tasks/{id}` - タスク更新
- `DELETE /api/tasks/{id}` - タスク削除

## CORS設定

```java
// 許可オリジン
"http://localhost:3000"
"http://127.0.0.1:3000"

// 許可メソッド: 全て
// 許可ヘッダー: 全て
// Credentials: 有効
```

## セキュリティ設定

### 許可パス（認証不要）
- `/h2-console/**` - H2コンソール
- `/users/login`, `/users/register` - 認証画面
- `/api/auth/**` - 認証API
- `/login/oauth2/code/**` - OAuth2コールバック
- 静的リソース (`/css/**`, `/js/**`, etc.)

### 保護パス（認証必要）
- `/api/tasks/**` - タスクAPI
- その他全てのパス

## データベース

### 初期データ
- テストユーザー: `test@example.com` / `test5417`
- タスクタイプ: "仕事", "プライベート", "学習"

### Flyway マイグレーション
1. `V1__ensure_task_type_and_seed.sql` - 基本テーブル作成
2. `V2__backfill_task_type.sql` - タスクタイプデータ投入
3. `V3__add_notnull_and_fk.sql` - 制約追加
4. `V4__add_google_id_column.sql` - Google ID列追加

## 開発・運用コマンド

```bash
# アプリケーション起動
./gradlew bootRun

# テスト実行
./gradlew test

# 特定テスト実行
./gradlew test --tests TaskServiceImplTest

# Google OAuth認証用環境変数
export GOOGLE_CLIENT_ID="your_client_id"
export GOOGLE_CLIENT_SECRET="your_client_secret"
```

## 既知の問題と解決済み事項

### 解決済み
1. **OAuth2認証後のタスク取得エラー**: 
   - 原因: `authentication.getName()`がGoogle IDを返すため、メールアドレス検索で失敗
   - 解決: OAuth2認証時は`oauth2User.getAttribute("email")`でメールアドレスを取得

2. **Google認証時のデータベースエラー**:
   - 原因: 既存ユーザーとのID衝突
   - 解決: Google ID検索機能追加、重複チェック強化

## 注意事項

1. **認証方式による違い**:
   - フォーム認証: `authentication.getName()` = メールアドレス
   - OAuth2認証: `authentication.getName()` = プロバイダーID

2. **CORS設定**: フロントエンドとの通信に必須

3. **セッション管理**: Spring Securityが自動管理

4. **環境変数**: Google OAuth設定は環境変数で管理

## トラブルシューティング

- **認証エラー**: ログレベルをDEBUGに設定して確認
- **CORS エラー**: オリジン設定とCredentials設定を確認
- **データベースエラー**: H2コンソールで直接確認可能
- **OAuth設定エラー**: Google Cloud Consoleの設定を確認

---

このファイルを参照することで、プロジェクトの全体像を把握し、効率的な開発サポートが可能です。