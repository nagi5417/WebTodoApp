# Claude AI トラブルシューティングガイド

## 🎯 このファイルの目的
問題発生時にClaudeと効率的にトラブルシューティングを行うための情報整理ファイル

---

## 🚨 現在発生中の問題

### 問題 #1
**カテゴリ**: [ ] 認証 [ ] API [ ] データベース [ ] フロントエンド [ ] ビルド [ ] その他  
**重要度**: [ ] Critical [ ] High [ ] Medium [ ] Low  
**発生日時**: 
**影響範囲**: 

**症状・エラーメッセージ**:
```
（エラーメッセージやスタックトレースをここに貼り付け）
```

**再現手順**:
1. 
2. 
3. 

**試した解決策**:
- [ ] 
- [ ] 
- [ ] 

**その他の情報**:
- 
- 

---

## 📋 よくある問題と解決策

### 🔐 認証関連

#### Google OAuth認証エラー
**症状**: `invalid_client` エラー  
**原因**: 環境変数の設定不備  
**解決策**:
```bash
export GOOGLE_CLIENT_ID="正しいクライアントID"
export GOOGLE_CLIENT_SECRET="正しいクライアントシークレット"
./gradlew bootRun
```

#### 認証後にタスクが表示されない
**症状**: 400エラー「ユーザーが見つかりません」  
**原因**: OAuth2認証時の `authentication.getName()` がGoogle IDを返すため  
**解決策**: `getUserIdFromAuthentication()` メソッドでメールアドレスを取得

#### セッションが維持されない
**症状**: 認証後すぐにログアウト状態になる  
**原因**: CORS設定やセッション設定の問題  
**確認項目**:
- CORS設定で `allowCredentials = true`
- フロントエンドでクッキーを送信設定

### 🔗 API関連

#### CORS エラー
**症状**: `Access to XMLHttpRequest has been blocked by CORS policy`  
**解決策**:
1. `SecurityConfig.java` のCORS設定確認
2. フロントエンドのオリジン（localhost:3000）が許可されているか確認

#### 400 Bad Request
**よくある原因**:
- リクエストボディの形式エラー
- 必須パラメータの不足
- 認証情報の不備

**デバッグ方法**:
```bash
# ログレベルをDEBUGに設定
logging.level.com.example.demo=DEBUG
```

### 💾 データベース関連

#### H2データベース接続エラー
**H2コンソール**: http://localhost:8080/h2-console  
**JDBC URL**: `jdbc:h2:mem:test`  
**ユーザー名**: `sa`  
**パスワード**: (空)

#### Flyway マイグレーションエラー
**症状**: `FlywayException`  
**解決策**:
1. `src/main/resources/db/migration/` のSQLファイル確認
2. マイグレーション履歴確認: `SELECT * FROM flyway_schema_history;`

#### 制約違反エラー
**症状**: `ConstraintViolationException`  
**よくある原因**:
- ユニーク制約違反（メールアドレス重複等）
- 外部キー制約違反
- NOT NULL制約違反

### 🏗️ ビルド関連

#### Gradleビルドエラー
**よくある解決策**:
```bash
# キャッシュクリア
./gradlew clean

# 依存関係更新
./gradlew build --refresh-dependencies

# デーモン停止
./gradlew --stop
```

#### ポート使用中エラー
**症状**: `Port 8080 was already in use`  
**解決策**:
```bash
# プロセス確認・停止
lsof -ti:8080 | xargs kill -9
```

---

## 🔍 デバッグ情報収集

### 基本情報チェックリスト
- [ ] エラーメッセージ全文
- [ ] スタックトレース
- [ ] 再現手順
- [ ] 発生タイミング
- [ ] 環境情報（OS、Java版本等）

### ログ確認項目
```bash
# Spring Bootログ確認
./gradlew bootRun

# 特定のログレベル設定
logging.level.org.springframework.security=DEBUG
logging.level.com.example.demo=DEBUG
```

### ブラウザ開発者ツール
- [ ] Network タブ（HTTP ステータス、リクエスト/レスポンス）
- [ ] Console タブ（JavaScript エラー）
- [ ] Application タブ（セッション、クッキー）

### データベース確認
```sql
-- ユーザー情報確認
SELECT * FROM users;

-- タスク情報確認
SELECT * FROM tasks;

-- マイグレーション履歴
SELECT * FROM flyway_schema_history;
```

---

## 🛠️ 緊急時の対応手順

### 1. アプリケーション停止・再起動
```bash
# Gradleプロセス停止
Ctrl + C

# 強制停止
./gradlew --stop

# 再起動
./gradlew bootRun
```

### 2. データベースリセット
```bash
# H2は再起動で自動リセット（インメモリのため）
# 初期データは Flyway で自動投入
```

### 3. 設定確認
- [ ] `application.yml` 設定
- [ ] 環境変数設定
- [ ] Google Cloud Console設定（OAuth）

### 4. バックアップからの復旧
```bash
# Gitで前の状態に戻す
git log --oneline
git checkout [コミットハッシュ]

# 特定ファイルのみ復旧
git checkout HEAD~1 -- path/to/file
```

---

## 📞 サポート依頼時の情報

### Claudeに提供すべき情報
1. **問題の詳細**: 症状、エラーメッセージ
2. **環境情報**: OS、Java版本、IDE
3. **再現手順**: 具体的なステップ
4. **関連ファイル**: エラーが発生しているファイルの内容
5. **ログ**: 関連するログの抜粋
6. **試した解決策**: 既に試行したこと

### 効果的な質問の仕方
```
❌ 悪い例: "エラーが出ます。直してください。"

✅ 良い例: 
"Google認証後にタスク一覧画面で400エラーが発生しています。
エラーメッセージ: 'ユーザーが見つかりません'
再現手順: 1. Googleでログイン 2. タスク画面遷移 3. エラー発生
ログ: [具体的なエラーログ]
関連ファイル: TaskApiController.java の getTasks メソッド"
```

---

## 📝 解決済み問題の記録

### [2025-10-12] OAuth2認証後のタスク取得エラー
**問題**: Google認証後にタスクAPIで400エラー  
**原因**: `authentication.getName()` がGoogle IDを返し、メールアドレスでの検索に失敗  
**解決策**: `getUserIdFromAuthentication()` メソッドでOAuth2認証時はメールアドレスを取得  
**修正ファイル**: `TaskApiController.java`, `AuthController.java`

### [日付] 問題タイトル
**問題**: 
**原因**: 
**解決策**: 
**修正ファイル**: 

---

**このファイルは問題発生時に更新し、ナレッジベースとして活用してください。**