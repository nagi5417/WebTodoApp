# CRUD Generator

## あなたの役割
指定されたEntityクラス、またはプロジェクト内の全Entityに対して、Spring Boot + Spring JDBC + PostgreSQL/H2の標準パターンに従った完全なCRUDレイヤーを生成してください。

## 前提条件（CLAUDE.md参照）
- プロジェクト: WebToDoApp（タスク管理システム）
- フレームワーク: Spring Boot 2.7.18
- データアクセス: Spring JDBC (JdbcTemplate)
- データベース: PostgreSQL (本番) / H2 (テスト)
- バリデーション: **javax.validation** (※jakarta.validationではない)
- **既存コードスタイル（TaskDaoImpl, TaskServiceImpl等）に厳密に従うこと**

---

## 実行手順

### ステップ1: Entity検索と対象決定

1. **Globツール**で `**/entity/*.java` を検索し、全Entityクラスをリストアップ
2. ユーザーに対象Entityを確認（コマンド引数で指定がない場合）
3. **Readツール**で対象Entityファイルを読み込み、以下を把握:
   - クラス名
   - フィールド一覧（型、名前、アノテーション）
   - 主キー（@Idまたはid フィールド）

---

### ステップ2: 既存実装の確認（重複防止）

**Globツール**で既存ファイルを検索し、重複を防止:
1. `**/dao/*Dao*.java` - 既存DAO
2. `**/service/*Service*.java` - 既存Service
3. `**/controller/*Controller*.java` - 既存Controller

**既に同名ファイルが存在する場合**:
- ユーザーに上書き確認を求める
- または、既存ファイルに不足しているメソッドのみ追加提案

---

### ステップ3: 既存パターンの参照

**Readツール**で以下のファイルを読み込み、プロジェクト固有のコーディングスタイルを把握:

1. `src/main/java/com/example/demo/dao/TaskDaoImpl.java`
   - JdbcTemplateの使い方
   - RowMapperの実装パターン
   - SQL文の記述スタイル
2. `src/main/java/com/example/demo/service/TaskServiceImpl.java`
   - トランザクション管理（@Transactional）
   - 例外ハンドリング（EmptyResultDataAccessException → カスタム例外）
3. `src/test/java/com/example/demo/service/TaskServiceImplTest.java`
   - テスト構成（@SpringBootTest, @TestPropertySource）
   - テストデータの準備方法

---

### ステップ4: CRUDレイヤーの生成

以下のファイルを**既存パターンに厳密に従って**生成:

#### **DAO層**
- `{Entity}Dao.java` - インターフェース
- `{Entity}DaoImpl.java` - Spring JDBC実装
  - `findAll()`, `findById(Long id)`, `save({Entity})`, `update({Entity})`, `deleteById(Long id)`
  - JdbcTemplate + RowMapper使用

#### **Service層**
- `{Entity}Service.java` - インターフェース
- `{Entity}ServiceImpl.java` - ビジネスロジック
  - @Transactional付与
  - EmptyResultDataAccessException → {Entity}NotFoundExceptionに変換
- `{Entity}NotFoundException.java` - カスタム例外（RuntimeException継承）

#### **Controller層**
- `{Entity}ApiController.java` - REST API
  - @RestController, @RequestMapping("/api/{entity-path}")
  - GET, POST, PUT, DELETE実装
  - **javax.validation.Valid**使用
- `{Entity}Controller.java` - Thymeleaf View（必要な場合）

#### **DTO層**
- `{Entity}Request.java` - 作成/更新リクエスト（javax.validation制約付き）
- `{Entity}Response.java` - レスポンス

#### **Test層**
- `{Entity}ServiceImplTest.java` - 統合テスト
  - @SpringBootTest
  - @TestPropertySource(H2設定)
- `{Entity}ServiceImplUnitTest.java` - ユニットテスト
  - @ExtendWith(MockitoExtension.class)

---

### ステップ5: バリデーション追加

DTOに**javax.validation.constraints**を適切に配置:
- `@NotNull` - null禁止
- `@NotBlank` - 空文字禁止
- `@Size(min=1, max=100)` - 長さ制限
- `@Email` - メールアドレス形式

---

### ステップ6: 生成結果の確認

1. **Bashツール**でビルド実行:
   ```bash
   ./gradlew clean build
   ```
2. エラーが出た場合は修正
3. **Bashツール**でテスト実行:
   ```bash
   ./gradlew test --tests {Entity}ServiceImplTest
   ```

---

## 出力形式

### 生成完了メッセージ
```markdown
# ✅ CRUD生成完了: {Entity}

## 生成ファイル一覧
- DAO: {Entity}Dao.java, {Entity}DaoImpl.java
- Service: {Entity}Service.java, {Entity}ServiceImpl.java, {Entity}NotFoundException.java
- Controller: {Entity}ApiController.java
- DTO: {Entity}Request.java, {Entity}Response.java
- Test: {Entity}ServiceImplTest.java, {Entity}ServiceImplUnitTest.java

## ビルド結果
✅ コンパイル成功

## テスト結果
✅ 全テスト通過 (8/8)

## 次のステップ
1. マイグレーションファイル作成: `/utility-migration-create`
2. API仕様書生成: `/doc-api-spec`
```

---

## 重要な注意事項

1. **既存ファイル上書き前に必ずユーザー確認**
2. **データベーステーブルが未作成の場合、マイグレーションファイル生成を提案**
3. **TaskDaoImpl, TaskServiceImplのコーディングスタイルを厳守**
4. **javax.validation使用（jakarta.validationではない）**
5. **テストは@TestPropertySourceでH2（MODE=PostgreSQL）を使用**
