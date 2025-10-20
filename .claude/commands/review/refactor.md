コードの構造を改善し、保守性・拡張性を向上させるリファクタリングを提案します。

## 実行フロー

### 1. リファクタリング対象の特定

#### パターンA: ファイル指定あり
```
/review/refactor src/main/java/com/example/demo/service/TaskServiceImpl.java
```
→ 指定ファイルをリファクタリング

#### パターンB: 自動検出
```
/review/refactor
```
→ 以下を自動検索：
1. **長いメソッド**: 50行以上のメソッド
2. **大きいクラス**: 300行以上のクラス
3. **複雑な条件分岐**: ネストが3階層以上
4. **重複コード**: 同じロジックの繰り返し
5. **God Object**: 責務が多すぎるクラス

### 2. リファクタリングパターン

#### 🔧 メソッドレベル
- **Extract Method**: 長いメソッドを分割
- **Inline Method**: 不要に小さいメソッドを統合
- **Replace Temp with Query**: 一時変数をメソッド化
- **Introduce Parameter Object**: 多数の引数をオブジェクト化

#### 🏗️ クラスレベル
- **Extract Class**: 責務を分離
- **Move Method**: メソッドを適切なクラスへ移動
- **Replace Conditional with Polymorphism**: 条件分岐をポリモーフィズムで置換
- **Introduce Strategy Pattern**: アルゴリズムの切り替え

#### 📦 アーキテクチャレベル
- **Layer Separation**: レイヤー間の責務明確化
- **Dependency Injection**: 依存性の注入
- **Interface Segregation**: インターフェースの分割

### 3. 出力フォーマット

```markdown
# 🔧 リファクタリング提案

## 📊 対象サマリー
- ファイル: TaskServiceImpl.java (458行 → 312行予定)
- リファクタリング項目: 7件
- 推定工数: 4時間
- リスク: 🟢 低 (既存テストで検証可能)

---

## 📄 TaskServiceImpl.java

### 🔴 [REF-001] 長いメソッドの分割 (line 156-234)
**問題**: 79行の巨大メソッド、責務が混在

**現在のコード**:
\```java
public TaskDto createTask(TaskCreateRequest request) {
    // バリデーション (15行)
    if (request.getTitle() == null || request.getTitle().isEmpty()) {
        throw new IllegalArgumentException("Title is required");
    }
    if (request.getTitle().length() > 100) {
        throw new IllegalArgumentException("Title too long");
    }
    // ... 省略 ...

    // エンティティ変換 (10行)
    Task task = new Task();
    task.setTitle(request.getTitle());
    task.setDescription(request.getDescription());
    // ... 省略 ...

    // 保存 (5行)
    task = taskDao.save(task);

    // 通知送信 (20行)
    if (task.isHighPriority()) {
        emailService.send(...);
        slackService.post(...);
    }
    // ... 省略 ...

    // DTO変換 (8行)
    TaskDto dto = new TaskDto();
    dto.setId(task.getId());
    // ... 省略 ...

    return dto;
}
\```

**リファクタリング後**:
\```java
public TaskDto createTask(TaskCreateRequest request) {
    validateTaskRequest(request);
    Task task = convertToEntity(request);
    task = taskDao.save(task);
    sendNotificationsIfNeeded(task);
    return convertToDto(task);
}

private void validateTaskRequest(TaskCreateRequest request) {
    if (request.getTitle() == null || request.getTitle().isEmpty()) {
        throw new IllegalArgumentException("Title is required");
    }
    if (request.getTitle().length() > 100) {
        throw new IllegalArgumentException("Title too long");
    }
    // ... バリデーションロジック ...
}

private Task convertToEntity(TaskCreateRequest request) {
    Task task = new Task();
    task.setTitle(request.getTitle());
    task.setDescription(request.getDescription());
    task.setPriority(request.getPriority());
    task.setDueDate(request.getDueDate());
    return task;
}

private void sendNotificationsIfNeeded(Task task) {
    if (task.isHighPriority()) {
        notificationService.sendTaskCreated(task);
    }
}

private TaskDto convertToDto(Task task) {
    return TaskDto.builder()
        .id(task.getId())
        .title(task.getTitle())
        .description(task.getDescription())
        .build();
}
\```

**改善効果**:
- ✅ 可読性: メソッド名で意図が明確
- ✅ 保守性: 各責務を独立して修正可能
- ✅ テスタビリティ: 各メソッドを個別にテスト可能
- 📉 行数: 79行 → 15行 (メインメソッド)

---

### 🟡 [REF-002] Strategy パターンの導入 (line 89-145)
**問題**: タスクタイプごとのif-else分岐が多い

**現在のコード**:
\```java
public void processTask(Task task) {
    if (task.getType() == TaskType.EMAIL) {
        // メール送信処理 (15行)
    } else if (task.getType() == TaskType.SLACK) {
        // Slack投稿処理 (18行)
    } else if (task.getType() == TaskType.SMS) {
        // SMS送信処理 (12行)
    }
}
\```

**リファクタリング後**:
\```java
// 1. インターフェース定義
public interface TaskProcessor {
    void process(Task task);
    TaskType supportedType();
}

// 2. 各実装
@Component
public class EmailTaskProcessor implements TaskProcessor {
    @Override
    public void process(Task task) {
        // メール送信処理
    }

    @Override
    public TaskType supportedType() {
        return TaskType.EMAIL;
    }
}

@Component
public class SlackTaskProcessor implements TaskProcessor {
    @Override
    public void process(Task task) {
        // Slack投稿処理
    }

    @Override
    public TaskType supportedType() {
        return TaskType.SLACK;
    }
}

// 3. Factory
@Component
public class TaskProcessorFactory {
    private final Map<TaskType, TaskProcessor> processors;

    public TaskProcessorFactory(List<TaskProcessor> processorList) {
        this.processors = processorList.stream()
            .collect(Collectors.toMap(
                TaskProcessor::supportedType,
                Function.identity()
            ));
    }

    public TaskProcessor getProcessor(TaskType type) {
        return Optional.ofNullable(processors.get(type))
            .orElseThrow(() -> new UnsupportedOperationException(
                "Unsupported task type: " + type
            ));
    }
}

// 4. サービスクラス
@Service
public class TaskService {
    private final TaskProcessorFactory processorFactory;

    public void processTask(Task task) {
        TaskProcessor processor = processorFactory.getProcessor(task.getType());
        processor.process(task);
    }
}
\```

**改善効果**:
- ✅ 拡張性: 新しいタイプ追加時、既存コード変更不要 (Open/Closed原則)
- ✅ 責務分離: 各処理が独立したクラスに
- ✅ テスト: 各Processorを独立してテスト可能
- ⚠️ トレードオフ: クラス数増加 (1クラス → 6クラス)

---

### 🟢 [REF-003] Builder パターンの適用
**問題**: コンストラクタの引数が多すぎる

**現在のコード**:
\```java
Task task = new Task(
    null, // id
    "Task Title",
    "Description",
    TaskType.EMAIL,
    Priority.HIGH,
    LocalDateTime.now(),
    null, // completedAt
    userId,
    false
);
\```

**リファクタリング後**:
\```java
Task task = Task.builder()
    .title("Task Title")
    .description("Description")
    .type(TaskType.EMAIL)
    .priority(Priority.HIGH)
    .createdAt(LocalDateTime.now())
    .userId(userId)
    .completed(false)
    .build();

// Lombokを使用
@Data
@Builder
public class Task {
    private Long id;
    private String title;
    private String description;
    private TaskType type;
    private Priority priority;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
    private Long userId;
    private boolean completed;
}
\```

**改善効果**:
- ✅ 可読性: パラメータ名が明確
- ✅ 保守性: パラメータ順序の間違いを防ぐ
- ✅ デフォルト値: @Builder.Defaultで設定可能

---

## 📋 リファクタリング計画

### フェーズ1: 低リスク改善 (2時間)
1. REF-001: 長いメソッド分割
2. REF-003: Builderパターン適用

### フェーズ2: 構造変更 (4時間)
1. REF-002: Strategyパターン導入
2. テストコード更新

### フェーズ3: 検証 (1時間)
1. 既存テストの実行
2. 統合テストの実行
3. コードレビュー

---

## 🧪 テストコード更新案

**リファクタリング前**:
\```java
@Test
void createTask_Success() {
    // 巨大メソッドのテスト (すべてをモック)
}
\```

**リファクタリング後**:
\```java
@Test
void validateTaskRequest_ThrowsException_WhenTitleIsEmpty() {
    // バリデーションのみをテスト
}

@Test
void convertToEntity_CorrectlyMapsFields() {
    // 変換ロジックのみをテスト
}

@Test
void sendNotificationsIfNeeded_SendsEmail_WhenHighPriority() {
    // 通知ロジックのみをテスト
}
\```

---

## 🎯 総合評価

| 観点 | 改善前 | 改善後 |
|------|--------|--------|
| 平均メソッド行数 | 45行 | 12行 |
| 循環的複雑度 | 8.2 | 2.1 |
| クラス数 | 3 | 8 |
| テストカバレッジ | 62% | 85% (予測) |
| 保守性スコア | C | A |
```

### 4. 実装手順

1. **コード匂い検出**: Glob/Grepで長いメソッド、複雑な条件分岐を検索
2. **リファクタリング候補抽出**: 循環的複雑度、ネストレベルを分析
3. **パターン適用**: 適切なデザインパターンを提案
4. **Before/Afterコード生成**: 具体的なリファクタリング例を提示
5. **テスト戦略**: 更新されたテストコードを提案

---

**Note**: このコマンドは自律的にリファクタリング候補を検出し、デザインパターンに基づいた改善提案を行います。特定のファイルをリファクタリングしたい場合は、コマンド実行時にファイルパスを指定してください。
