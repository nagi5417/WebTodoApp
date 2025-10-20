# API Specification Generator

## あなたの役割
プロジェクト内のすべてのControllerを自動的に検索し、OpenAPI 3.0形式のAPI仕様書（YAML + Markdown）を生成してください。

## 前提条件（CLAUDE.md参照）
- プロジェクト: WebToDoApp（タスク管理システム）
- フレームワーク: Spring Boot 2.7.18
- 認証: Spring Security (Form Login + OAuth2/Google)
- データベース: PostgreSQL (本番) / H2 (開発・テスト)
- **javax.validation** (※jakarta.validationではない)

---

## 実行手順

### ステップ1: Controller検索

1. **Globツール**で全Controllerを検索:
   ```
   **/*Controller.java
   ```
2. **Grepツール**でREST API Controllerを抽出:
   ```
   pattern="@RestController"
   ```

---

### ステップ2: エンドポイント抽出

各Controllerファイルを**Readツール**で読み込み、以下を抽出:
1. **HTTPメソッドとパス**:
   - `@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping`, `@PatchMapping`, `@RequestMapping`
2. **パラメータ情報**:
   - `@RequestBody` - リクエストボディ
   - `@PathVariable` - パスパラメータ
   - `@RequestParam` - クエリパラメータ
3. **レスポンス型**:
   - メソッドの戻り値型（ResponseEntity<T>, T等）
4. **バリデーション**:
   - `@Valid`, `@NotNull`, `@Size`等のアノテーション

---

### ステップ3: Entity/DTO解析

**Globツール**で以下を検索:
1. `**/entity/**/*.java` - データモデル
2. `**/dto/**/*.java` - DTO

**Readツール**で各Entity/DTOを読み込み、スキーマ情報を抽出:
- フィールド名、型
- バリデーション制約
- 説明（JavaDocコメント）

---

### ステップ4: OpenAPI 3.0 YAML生成

以下の構造でYAMLファイルを生成:

```yaml
openapi: 3.0.0
info:
  title: WebToDoApp API
  version: 1.0.0
  description: タスク管理システムのREST API仕様書

servers:
  - url: http://localhost:8080
    description: ローカル開発環境

paths:
  /api/tasks:
    get:
      summary: タスク一覧取得
      tags: [Tasks]
      parameters:
        - name: userId
          in: query
          required: true
          schema:
            type: integer
          description: ユーザーID
      responses:
        '200':
          description: 成功
          content:
            application/json:
              schema:
                type: array
                items:
                  $ref: '#/components/schemas/Task'
        '400': {description: パラメータ不正}
        '401': {description: 認証エラー}
      security:
        - sessionAuth: []

    post:
      summary: タスク作成
      tags: [Tasks]
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/TaskCreateRequest'
      responses:
        '201':
          description: 作成成功
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/Task'
        '400': {description: バリデーションエラー}

components:
  schemas:
    Task:
      type: object
      properties:
        id: {type: integer, description: タスクID}
        title: {type: string, description: タスク名}
        description: {type: string, description: タスク詳細}
        status:
          type: string
          enum: [TODO, IN_PROGRESS, DONE]
          description: ステータス
        createdAt: {type: string, format: date-time}

  securitySchemes:
    sessionAuth:
      type: apiKey
      in: cookie
      name: JSESSIONID
```

---

### ステップ5: Markdown形式のAPI仕様書も生成

読みやすいMarkdown形式も併せて生成:

```markdown
# WebToDoApp API仕様書

## エンドポイント一覧

### タスク管理 (Tasks)

#### GET /api/tasks
- **概要**: タスク一覧取得
- **パラメータ**:
  - `userId` (query, required, integer): ユーザーID
- **レスポンス**:
  - 200: 成功（Task配列）
  - 400: パラメータ不正
  - 401: 認証エラー
- **使用例**:
  ```bash
  curl -X GET "http://localhost:8080/api/tasks?userId=1" \
    -H "Cookie: JSESSIONID=xxx"
  ```

#### POST /api/tasks
- **概要**: タスク作成
- **リクエストボディ**: TaskCreateRequest
- **レスポンス**:
  - 201: 作成成功
  - 400: バリデーションエラー
```

---

### ステップ6: ファイル出力

**Writeツール**で以下に保存:
1. `docs/api-spec.yaml` - OpenAPI 3.0仕様書
2. `docs/api-spec.md` - Markdown形式

---

## 出力に含めるべき情報

各エンドポイントについて:
- エンドポイントパス、HTTPメソッド
- 概要、タグ
- パラメータ（query, path, body）
- レスポンス（ステータスコード、スキーマ）
- 認証要件（sessionAuth）
- curlコマンド例

各スキーマについて:
- フィールド名、型
- バリデーション制約（@NotNull, @Size等を反映）
- 説明

---

## 重要な注意事項

1. **全Controllerを網羅的に探索**
2. **Spring Security設定を反映**（認証・認可要件）
3. **エラーレスポンス形式を統一**
4. **実際のEntityクラスからスキーマ生成**（推測禁止）
5. **javax.validation制約をOpenAPIスキーマに反映**
6. **curlコマンド例を必ず含める**
