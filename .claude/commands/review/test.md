Spring Bootアプリケーションの包括的なテストコードを生成します。

## 実行フロー

### 1. テスト対象の特定

#### パターンA: ファイル指定あり
```
/review/test src/main/java/com/example/demo/service/TaskServiceImpl.java
```
→ 指定ファイルのテストコードを生成

#### パターンB: 自動検出
```
/review/test
```
→ 以下を自動検索してテスト生成：
1. **テストが不足しているファイル**: カバレッジ < 80%
2. **最近変更されたファイル**: `git diff --name-only HEAD~3..HEAD`
3. **重要なビジネスロジック**: `*Service*.java`, `*Controller*.java`
4. **テストが全くないファイル**: 対応するテストクラスが存在しない

### 2. テスト戦略

#### 🧪 テストレベル

##### ユニットテスト (Unit Test)
- **対象**: Service, Util, Helper クラス
- **分離**: 外部依存をモック化
- **速度**: 高速 (< 100ms/test)
- **カバレッジ目標**: 80%以上

##### 統合テスト (Integration Test)
- **対象**: Controller, DAO, Repository
- **環境**: @SpringBootTest, TestContainers
- **DB**: H2またはTestContainers (PostgreSQL)
- **カバレッジ目標**: 主要フロー100%

##### コントラクトテスト (Contract Test)
- **対象**: REST API
- **ツール**: Spring REST Docs, OpenAPI
- **目的**: API仕様の保証

#### 📋 テストケース設計 (OWASP Testing Guide準拠)

1. **正常系 (Happy Path)**: 期待通りの動作
2. **境界値 (Boundary)**: 最小値、最大値、null、空文字
3. **異常系 (Exception)**: バリデーションエラー、権限エラー
4. **エッジケース (Edge Case)**: タイムアウト、同時実行、大量データ

### 3. 出力フォーマット

```markdown
# 🧪 テストコード生成

## 📊 対象サマリー
- テスト対象: TaskServiceImpl.java (345行)
- 生成テスト数: 23件 (ユニット: 18件, 統合: 5件)
- 推定カバレッジ: 87%
- 推定実装時間: 3時間

---

## 📄 TaskServiceImplTest.java (ユニットテスト)

### テスト構成

**依存関係のモック化**:
\```java
@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock
    private TaskDao taskDao;

    @Mock
    private UserDao userDao;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private TaskServiceImpl taskService;

    // テストデータ準備
    private Task sampleTask;
    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = User.builder()
            .id(1L)
            .username("testuser")
            .email("test@example.com")
            .build();

        sampleTask = Task.builder()
            .id(100L)
            .title("Sample Task")
            .description("Description")
            .userId(1L)
            .status(TaskStatus.PENDING)
            .build();
    }
}
\```

---

### ✅ [TEST-001] 正常系: タスク作成成功

**テストシナリオ**: 有効なリクエストでタスクが正常に作成される

\```java
@Test
@DisplayName("createTask: 正常系 - 有効なリクエストでタスクが作成される")
void createTask_Success_WhenValidRequest() {
    // Arrange (準備)
    TaskCreateRequest request = TaskCreateRequest.builder()
        .title("New Task")
        .description("Task Description")
        .userId(1L)
        .build();

    Task savedTask = Task.builder()
        .id(100L)
        .title(request.getTitle())
        .description(request.getDescription())
        .userId(request.getUserId())
        .status(TaskStatus.PENDING)
        .build();

    when(taskDao.save(any(Task.class))).thenReturn(savedTask);
    when(userDao.findById(1L)).thenReturn(Optional.of(sampleUser));

    // Act (実行)
    TaskDto result = taskService.createTask(request);

    // Assert (検証)
    assertNotNull(result);
    assertEquals(100L, result.getId());
    assertEquals("New Task", result.getTitle());
    assertEquals(TaskStatus.PENDING, result.getStatus());

    // モックの呼び出し検証
    verify(taskDao, times(1)).save(any(Task.class));
    verify(notificationService, times(1)).sendTaskCreated(any(Task.class));
}
\```

---

### 🔴 [TEST-002] 異常系: タイトルがnull

**テストシナリオ**: タイトルがnullの場合、バリデーションエラー

\```java
@Test
@DisplayName("createTask: 異常系 - タイトルがnullの場合はIllegalArgumentException")
void createTask_ThrowsException_WhenTitleIsNull() {
    // Arrange
    TaskCreateRequest request = TaskCreateRequest.builder()
        .title(null)
        .description("Description")
        .userId(1L)
        .build();

    // Act & Assert
    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> taskService.createTask(request)
    );

    assertEquals("Title is required", exception.getMessage());

    // DAOが呼ばれていないことを確認
    verify(taskDao, never()).save(any());
}
\```

---

### 📏 [TEST-003] 境界値: タイトル長さ制限

**テストシナリオ**: タイトルが100文字ちょうど、101文字の境界値テスト

\```java
@Test
@DisplayName("createTask: 境界値 - タイトル100文字は許可")
void createTask_Success_WhenTitleIs100Characters() {
    // Arrange
    String title100 = "a".repeat(100);
    TaskCreateRequest request = TaskCreateRequest.builder()
        .title(title100)
        .userId(1L)
        .build();

    when(taskDao.save(any())).thenReturn(sampleTask);

    // Act & Assert
    assertDoesNotThrow(() -> taskService.createTask(request));
}

@Test
@DisplayName("createTask: 境界値 - タイトル101文字は拒否")
void createTask_ThrowsException_WhenTitleIs101Characters() {
    // Arrange
    String title101 = "a".repeat(101);
    TaskCreateRequest request = TaskCreateRequest.builder()
        .title(title101)
        .userId(1L)
        .build();

    // Act & Assert
    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> taskService.createTask(request)
    );

    assertTrue(exception.getMessage().contains("too long"));
}
\```

---

### 🎯 [TEST-004] エッジケース: 同時実行

**テストシナリオ**: 同じタスクへの並行更新

\```java
@Test
@DisplayName("updateTask: エッジケース - 同時更新で楽観ロックエラー")
void updateTask_ThrowsException_WhenOptimisticLockFailure() {
    // Arrange
    when(taskDao.update(any())).thenThrow(new OptimisticLockException());

    // Act & Assert
    assertThrows(
        OptimisticLockException.class,
        () -> taskService.updateTask(sampleTask)
    );
}
\```

---

## 📄 TaskControllerIntegrationTest.java (統合テスト)

### テスト構成

\```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
class TaskControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TaskDao taskDao;

    @Test
    @DisplayName("POST /api/tasks: タスク作成APIの統合テスト")
    void createTask_Integration_Success() throws Exception {
        // Arrange
        TaskCreateRequest request = TaskCreateRequest.builder()
            .title("Integration Test Task")
            .description("Description")
            .userId(1L)
            .build();

        // Act & Assert
        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.title").value("Integration Test Task"))
            .andExpect(jsonPath("$.status").value("PENDING"));

        // DB確認
        List<Task> tasks = taskDao.findByTitle("Integration Test Task");
        assertEquals(1, tasks.size());
    }

    @Test
    @DisplayName("GET /api/tasks/{id}: タスク取得APIの統合テスト")
    void getTask_Integration_Success() throws Exception {
        // Arrange: DBにテストデータ投入
        Task task = taskDao.save(sampleTask);

        // Act & Assert
        mockMvc.perform(get("/api/tasks/{id}", task.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(task.getId()))
            .andExpect(jsonPath("$.title").value(task.getTitle()));
    }

    @Test
    @DisplayName("GET /api/tasks/{id}: 存在しないIDで404")
    void getTask_Integration_ReturnsNotFound() throws Exception {
        mockMvc.perform(get("/api/tasks/{id}", 99999L))
            .andExpect(status().isNotFound());
    }
}
\```

---

## 📈 テストカバレッジ予測

| クラス | メソッド | 行 | 分岐 |
|--------|----------|-----|------|
| TaskServiceImpl | 92% | 87% | 85% |
| TaskController | 88% | 85% | 82% |
| TaskDaoImpl | 75% | 72% | 70% |
| **総合** | **85%** | **81%** | **79%** |

---

## 🔧 テスト実行コマンド

\```bash
# すべてのテストを実行
./gradlew test

# 特定のテストクラスのみ
./gradlew test --tests TaskServiceImplTest

# カバレッジレポート生成 (JaCoCo)
./gradlew test jacocoTestReport

# タグ別実行
./gradlew test --tests "*Integration*"  # 統合テストのみ
\```

---

## 🎯 未カバー領域

以下のメソッドはテストが不足しています：

1. **TaskServiceImpl.bulkDelete()** (line 234-256)
   - 複数削除の境界値テスト不足
   - 推奨: 0件、1件、1000件の削除テストを追加

2. **TaskDaoImpl.findByComplexConditions()** (line 89-145)
   - 複雑なクエリの組み合わせテスト不足
   - 推奨: パラメトライズドテストで網羅

---

## ✅ テスト品質チェックリスト

- [x] AAA (Arrange/Act/Assert) パターン準拠
- [x] @DisplayName で日本語の説明
- [x] モックの過剰使用を避ける (統合テストで実DB使用)
- [x] テストデータは @BeforeEach で初期化
- [x] 各テストは独立して実行可能
- [x] assertThat より具体的なアサーションを使用
- [x] verify() でモックの呼び出しを検証
```

### 4. 実装手順

1. **対象特定**: Glob で `*Service*.java`, `*Controller*.java` を検索
2. **既存テスト確認**: 対応する `*Test.java` の有無をチェック
3. **メソッド抽出**: publicメソッドを抽出してテストケースを設計
4. **テストコード生成**: 上記フォーマットで包括的なテストを生成
5. **カバレッジ分析**: 推定カバレッジを計算

---

**Note**: このコマンドは自律的にテスト対象を特定し、JUnit 5 + Mockito による包括的なテストコードを生成します。特定のファイルのテストを生成したい場合は、コマンド実行時にファイルパスを指定してください。
