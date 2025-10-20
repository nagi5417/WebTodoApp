# Spring Test Generator

## あなたの役割
指定されたクラス、またはプロジェクト内のテスト未実装クラスに対して、Spring Boot 2.7.18 + JUnit 5 + Mockitoによる包括的なテストコード（統合テスト + ユニットテスト）を生成してください。

## 前提条件（CLAUDE.md参照）
- プロジェクト: WebToDoApp（タスク管理システム）
- フレームワーク: Spring Boot 2.7.18
- テストフレームワーク: JUnit 5 + Spring Test + Mockito
- データベース: H2 (MODE=PostgreSQL) でPostgreSQLをエミュレート
- **javax.validation** (※jakarta.validationではない)
- **CLAUDE.mdのテスト方針に厳密に従うこと**

---

## 実行手順

### ステップ1: テスト対象の特定

1. **Globツール**で既存テストを確認:
   - `**/test/java/**/*Test.java` - 統合テスト
   - `**/test/java/**/*UnitTest.java` - ユニットテスト

2. **Globツール**でテスト対象候補を検索:
   - `**/service/*ServiceImpl.java` - Service層
   - `**/dao/*DaoImpl.java` - DAO層
   - `**/controller/*Controller.java` - Controller層

3. **テスト未実装クラスをリストアップ**:
   - 実装クラスと既存テストを突合
   - テストが不足しているクラスをユーザーに提示
   - コマンド引数で指定された場合はそれを優先

---

### ステップ2: 既存テストパターンの参照

**Readツール**で以下を読み込み、プロジェクト固有のテストスタイルを把握:

1. `src/test/java/com/example/demo/service/TaskServiceImplTest.java`
   - 統合テストの構成（@SpringBootTest, @TestPropertySource）
   - テストデータの準備方法（@BeforeEach）
2. `src/test/java/com/example/demo/service/TaskServiceImplUnitTest.java`
   - ユニットテストの構成（@ExtendWith(MockitoExtension.class)）
   - モックの使い方（@Mock, @InjectMocks）
3. `src/test/resources/application-unit.yml`
   - テスト用データベース設定

---

### ステップ3: テスト対象クラスと依存関係の分析

**Readツール**で以下を読み込み:
1. テスト対象クラス本体（例: TaskServiceImpl.java）
2. 依存しているDAO/Service/Entityクラス
3. publicメソッド一覧とパラメータ/戻り値を把握

---

### ステップ4: テストコード生成

対象クラスの種類に応じて、適切なテストを生成:

#### **A. Service層のテスト**

**統合テスト** (`{Service}ImplTest.java`):
```java
@SpringBootTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:integrationtest;MODE=PostgreSQL;DATABASE_TO_UPPER=false;DB_CLOSE_DELAY=-1",
    "spring.jpa.hibernate.ddl-auto=none"
})
@DisplayName("{Service}の統合テスト")
class {Service}ImplTest {
    @Autowired
    private {Service} service;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        // テストデータのリセット
    }

    // AAA（Arrange/Act/Assert）パターン
    // 正常系、異常系、境界値テスト
}
```

**ユニットテスト** (`{Service}ImplUnitTest.java`):
```java
@ExtendWith(MockitoExtension.class)
@DisplayName("{Service}のユニットテスト")
class {Service}ImplUnitTest {
    @Mock
    private {Dao} dao;

    @InjectMocks
    private {Service}Impl service;

    // Given/When/Then パターン
    // ドメインロジック、例外分岐を高速に検証
}
```

#### **B. Controller層のテスト**

**WebMvcテスト** (`{Controller}Test.java`):
```java
@WebMvcTest({Controller}.class)
@DisplayName("{Controller}のWebMvcテスト")
class {Controller}Test {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private {Service} service;

    // HTTPリクエスト/レスポンスの検証
}
```

#### **C. DAO層のテスト**

**統合テスト** (`{Dao}ImplTest.java`):
```java
@SpringBootTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:daotest;MODE=PostgreSQL;DATABASE_TO_UPPER=false"
})
@Tag("integration")
@DisplayName("{Dao}の統合テスト")
class {Dao}ImplTest {
    @Autowired
    private {Dao} dao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // SQL実行結果の検証（CRUD全操作）
}
```

---

### ステップ5: テストケース設計（CLAUDE.md準拠）

#### **必須カバレッジ（全レイヤ共通）**

**1. 正常系**
- 基本的なCRUD操作が正常に動作すること

**2. 異常系**
- `EmptyResultDataAccessException` → `{Entity}NotFoundException`への変換
- バリデーションエラー（@Valid, @NotNull等）
- null/空文字の処理

**3. 境界値テスト**
- ID=0, ID=負数
- 文字列の最小/最大長
- 日時の境界（過去/未来）

**4. 冪等性/リトライ分岐**
- 同一操作の複数回実行
- トランザクション境界の検証

---

### ステップ6: AAA/Given-When-Thenパターンの適用

各テストメソッドは以下のいずれかのパターンに従う:

**AAA（Arrange/Act/Assert）パターン**:
```java
@Test
@DisplayName("存在しないIDで取得した場合、例外がスローされること")
void testGetNonExistentIdThrowsException() {
    // Arrange（準備）
    int nonExistentId = 99999;

    // Act & Assert（実行と検証）
    assertThrows(TaskNotFoundException.class, () -> {
        service.getTask(nonExistentId);
    });
}
```

**Given/When/Thenコメント**も許容

---

### ステップ7: テストデータ準備と再現性確保

**BeforeEachでテストデータリセット**:
```java
@BeforeEach
void setUp() {
    jdbcTemplate.execute("DELETE FROM task");
    jdbcTemplate.execute("DELETE FROM users");
}
```

**時刻/乱数/IDの固定**（再現性確保）:
```java
// 時刻を固定する場合
private final Clock fixedClock = Clock.fixed(
    Instant.parse("2025-01-15T10:00:00Z"), ZoneId.of("Asia/Tokyo")
);
```

---

### ステップ8: テスト実行とエラー修正

1. **Bashツール**でテスト実行:
   ```bash
   ./gradlew test --tests {Class}Test
   ```
2. エラーが出た場合は修正
3. 全テスト通過まで繰り返し

---

## 出力形式

### 生成完了メッセージ
```markdown
# ✅ テスト生成完了: {Class}

## 生成ファイル
- 統合テスト: {Class}Test.java
- ユニットテスト: {Class}UnitTest.java

## テスト結果
✅ 全テスト通過 (15/15)

## カバレッジ
- 行カバレッジ: 87%
- 分岐カバレッジ: 82%
```

---

## 重要な注意事項（CLAUDE.md準拠）

1. **AAA（Arrange/Act/Assert）または Given/When/Then コメント必須**
2. **正常系、境界値、例外系、冪等性/リトライ分岐を最低1件ずつ**
3. **時刻/乱数/IDは注入して固定（再現性確保）**
4. **アサーションは短く断定的なメッセージで**
5. **内部実装に過剰結合しない**
6. **テストデータは各テスト毎にリセット（BeforeEach）**
7. **@DisplayNameで日本語の説明を付与**
8. **既存テストファイルがある場合は追記のみ（上書き禁止）**
9. **H2データベース設定: MODE=PostgreSQL;DATABASE_TO_UPPER=false**
10. **javax.validation使用（jakarta.validationではない）**
