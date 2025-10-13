# Claude AI 開発ログ

## 📝 このファイルの目的
開発過程での変更、学習、課題を記録し、将来のClaudeセッションで継続的なサポートを受けるための履歴管理

---

## 📅 開発履歴

### [2025-10-12] OAuth2認証問題の解決

#### 🎯 セッション目標
- Google認証後のタスク取得エラー（400エラー）の解決

#### 🔍 発見した問題
1. **認証方式による`authentication.getName()`の違い**
   - フォーム認証: メールアドレスが返される
   - OAuth2認証: Google IDが返される

2. **タスクAPI での認証情報取得エラー**
   - `getUserIdByEmail(googleId)` でユーザーが見つからない
   - Google ID `108701405367126274473` をメールアドレスとして検索していた

3. **データベース制約違反**
   - OAuth2ユーザー作成時にプライマリキー重複エラー

#### 🛠️ 実施した修正

**1. TaskApiController の修正**
```java
// 修正前
String email = authentication.getName(); // Google IDが入る
Long userId = userService.getUserIdByEmail(email); // 失敗

// 修正後
Long userId = getUserIdFromAuthentication(authentication);
```

**2. 認証情報取得ヘルパーメソッド作成**
```java
private Long getUserIdFromAuthentication(Authentication authentication) {
    if (authentication instanceof OAuth2AuthenticationToken) {
        OAuth2User oauth2User = oauth2Token.getPrincipal();
        String email = oauth2User.getAttribute("email");
        return userService.getUserIdByEmail(email);
    } else {
        String email = authentication.getName();
        return userService.getUserIdByEmail(email);
    }
}
```

**3. Google ID検索機能の追加**
- `UserService.findByGoogleId()` メソッド追加
- `UserDao.findByGoogleId()` メソッド追加
- OAuth2認証時のユーザー重複チェック強化

**4. コード整理**
- `SecurityConfig.java` のインデント修正（8スペース → 4スペース）

#### 📚 学んだ知識
1. **Spring Security OAuth2の認証オブジェクト**
   - `OAuth2AuthenticationToken` の構造
   - `OAuth2User.getAttribute()` でのユーザー情報取得

2. **認証方式による処理分岐の重要性**
   - 型チェック `instanceof` の活用
   - 認証プロバイダーごとの適切な情報取得方法

3. **デバッグ手法**
   - ログからの問題特定方法
   - Spring Security のDEBUGログの活用

#### ✅ 解決結果
- Google認証後のタスクデータ取得が正常動作
- OAuth2ユーザーの適切な作成・更新
- 認証状態の正しい維持

---

### [日付] セッションタイトル

#### 🎯 セッション目標
- 

#### 🔍 発見した問題
1. **問題1**
   - 詳細: 
   - 原因: 

2. **問題2**
   - 詳細: 
   - 原因: 

#### 🛠️ 実施した修正
**1. 修正1**
```java
// 修正前

// 修正後
```

**2. 修正2**
- 

#### 📚 学んだ知識
1. **技術的学習**
   - 

2. **ベストプラクティス**
   - 

#### ✅ 解決結果
- 
- 

#### 🚧 残課題
- [ ] 
- [ ] 

---

## 🎓 学習メモ

### Spring Security
- **OAuth2AuthenticationToken**: OAuth2認証時の認証オブジェクト
- **認証方式判定**: `authentication instanceof Class` での型チェック
- **OAuth2User**: `getAttribute("key")` でプロバイダー情報取得

### Spring Boot
- **DevTools**: ファイル変更時の自動再起動
- **H2 Database**: インメモリDB、再起動で初期化
- **Flyway**: マイグレーション自動実行

### デバッグ技術
- **ログレベル設定**: `logging.level.org.springframework.security=DEBUG`
- **ブラウザ開発者ツール**: Network、Console タブの活用
- **H2コンソール**: リアルタイムDB確認

### API設計
- **CORS設定**: `allowCredentials=true` の重要性
- **エラーハンドリング**: 具体的なエラーメッセージの提供
- **認証情報取得**: 認証方式に応じた適切な処理分岐

---

## 🔧 技術的ノウハウ

### よく使うコマンド
```bash
# アプリケーション再起動
./gradlew bootRun

# テスト実行
./gradlew test

# ポート確認・停止
lsof -ti:8080 | xargs kill -9

# Google OAuth環境変数設定
export GOOGLE_CLIENT_ID="client_id"
export GOOGLE_CLIENT_SECRET="client_secret"
```

### 設定ファイルのポイント
```yaml
# application.yml
logging:
  level:
    org.springframework.security: DEBUG
    com.example.demo: DEBUG
```

### よく確認するファイル
- `SecurityConfig.java` - 認証・認可設定
- `TaskApiController.java` - タスクAPI
- `AuthController.java` - 認証API
- `application.yml` - アプリケーション設定

---

## 📋 今後の開発予定

### 短期目標（今週）
- [ ] 
- [ ] 
- [ ] 

### 中期目標（今月）
- [ ] 
- [ ] 
- [ ] 

### 長期目標（3ヶ月）
- [ ] 
- [ ] 
- [ ] 

---

## 🤝 Claude との協働パターン

### 効果的だった質問方法
1. **具体的なエラーメッセージの提供**
   - ログの該当箇所を抜粋
   - 再現手順の明示

2. **関連ファイルの共有**
   - 問題が発生しているコードの提示
   - 設定ファイルの内容確認

3. **段階的な問題解決**
   - 問題の切り分け
   - 一つずつ検証・修正

### 学習促進につながったアプローチ
- **「なぜそうなるのか」の理解**
- **類似問題への対応方法の習得**
- **デバッグ手法の学習**

---

## 📊 開発統計

### 解決した問題数
- 認証関連: 1件
- API関連: 1件
- データベース関連: 1件
- その他: 件

### 学習した技術要素
- Spring Security OAuth2
- 認証方式の違い
- デバッグ手法
- 

### 作成・修正したファイル数
- Java: 5ファイル
- 設定: 1ファイル
- ドキュメント: 3ファイル

---

**このログは継続的に更新し、開発の軌跡を記録してください。**