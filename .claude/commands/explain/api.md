REST APIエンドポイントの仕様を包括的に解説します。

## 実行フロー

### 1. 自動でAPIエンドポイントを検出

以下を自動検索・分析：
- `*Controller.java`, `*ApiController.java` - すべてのエンドポイント
- `@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping`
- リクエスト/レスポンスDTO
- セキュリティ設定 (`SecurityConfig.java`)

### 2. 解説内容

- **HTTPメソッドとパス**: GET /api/tasks, POST /api/tasks など
- **リクエストパラメータ**: クエリ、パス、ボディ
- **レスポンス形式**: JSON構造、ステータスコード
- **認証・認可**: Spring Security設定、必要なロール
- **エラーレスポンス**: 400, 401, 403, 404, 500
- **使用例**: curlコマンド、JavaScriptのfetch例

### 3. 出力フォーマット

自動的にOpenAPI風の仕様書とcurlコマンド例を生成します。

---

**Note**: このコマンドは自律的にコントローラーを探索し、REST API仕様書を生成します。特定のエンドポイントを指定する場合は、コマンド実行後にパスを指定してください。
