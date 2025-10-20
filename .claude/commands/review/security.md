Spring Bootアプリケーションの包括的なセキュリティレビューを実施します。

## 実行フロー

### 1. 対象ファイルの自動特定
ユーザーがファイルパスを指定した場合はそのファイルのみレビュー。
指定がない場合は以下を自動検索・レビュー：

- **セキュリティ設定**: `SecurityConfig.java`, `WebSecurityConfig.java`
- **コントローラー**: すべての `*Controller.java` (認可チェック)
- **DAO/Repository**: `*Dao.java`, `*DaoImpl.java`, `*Repository.java` (SQLインジェクション)
- **設定ファイル**: `application.yml`, `application.properties` (機密情報漏洩)
- **エンティティ**: `*Entity.java` (機密フィールドの扱い)

### 2. セキュリティチェック項目

#### 🔴 重大な脆弱性
- **SQLインジェクション**: 動的SQL構築、PreparedStatementの未使用
- **認証バイパス**: 認可設定の不備、permitAll()の過剰使用
- **機密情報露出**: パスワード・APIキーのハードコーディング、ログ出力

#### 🟡 警告レベル
- **CSRF対策**: Spring SecurityのCSRFトークン設定
- **XSS対策**: Thymeleafエスケープ、Content Security Policy
- **セッション管理**: タイムアウト、固定化攻撃対策
- **入力バリデーション**: @Valid, @Validated の未使用

#### 🟢 ベストプラクティス
- **パスワードエンコーディング**: BCryptPasswordEncoderの使用
- **HTTPSリダイレクト**: requiresSecure()の設定
- **セキュリティヘッダー**: X-Frame-Options, X-Content-Type-Options

### 3. 出力フォーマット

各ファイルごとに以下を生成：

```markdown
## 📄 ファイル名 (src/main/java/com/example/SecurityConfig.java:42)

### ステータス
- 🚨 重大な脆弱性: 2件
- ⚠️ 警告: 1件
- ✅ 問題なし

### 脆弱性詳細

#### 🚨 [SQL-001] SQLインジェクションの可能性
**場所**: TaskDaoImpl.java:156
**コード**:
\```java
String sql = "SELECT * FROM tasks WHERE user_id = " + userId;
\```

**問題**: ユーザー入力を直接SQL文字列に結合しています。

**攻撃シナリオ**:
\```sql
userId = "1 OR 1=1; DROP TABLE users;--"
\```

**修正コード**:
\```java
String sql = "SELECT * FROM tasks WHERE user_id = ?";
jdbcTemplate.query(sql, new Object[]{userId}, rowMapper);
\```

---

### 総合評価
- セキュリティスコア: 65/100
- 優先対応: SQL-001, AUTH-002
```

## 4. 実装手順

1. **ファイル探索**: Globツールで対象ファイルを検索
2. **ファイル読み込み**: Readツールで各ファイルの内容を取得
3. **脆弱性スキャン**: OWASP Top 10に基づき静的解析
4. **レポート生成**: 上記フォーマットで包括的なレポートを出力

## 5. 対応OWASP Top 10 (2021)

- A01: Broken Access Control
- A02: Cryptographic Failures
- A03: Injection
- A04: Insecure Design
- A05: Security Misconfiguration
- A07: Identification and Authentication Failures

---

**Note**: このコマンドは自律的にコードベースを探索し、セキュリティレビューを実施します。特定のファイルのみレビューしたい場合は、コマンド実行後にファイルパスを指定してください。
