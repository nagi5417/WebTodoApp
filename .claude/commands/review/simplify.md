複雑なコードを簡潔で読みやすく書き直し、可読性とメンテナンス性を向上させます。

## 実行フロー

### 1. 簡素化対象の特定

#### パターンA: ファイル指定あり
```
/review/simplify src/main/java/com/example/demo/service/TaskServiceImpl.java
```
→ 指定ファイルを簡素化

#### パターンB: 自動検出
```
/review/simplify
```
→ 以下を自動検索：
1. **深いネスト**: if-elseが3階層以上
2. **冗長なループ**: forループでStream APIに置換可能
3. **null チェック連鎖**: Optional で簡素化可能
4. **複雑な条件式**: 早期リターンで単純化可能
5. **重複コード**: 共通処理の抽出

### 2. 簡素化パターン

#### 🎯 ネスト削減
- **Early Return**: ガード節で早期リターン
- **Extract Method**: 条件ブロックをメソッド化
- **Invert Condition**: 条件を反転してネスト削減

#### 🌊 Stream API 活用
- **for → Stream**: 宣言的なコレクション操作
- **filter/map/reduce**: データ変換の簡潔化
- **Collectors**: 集約処理の簡素化

#### 💎 Optional 活用
- **null チェック削減**: Optional で安全な処理
- **orElse/orElseGet**: デフォルト値の提供
- **map/flatMap**: null 安全な変換

#### ✨ Java モダン構文
- **ラムダ式**: 匿名クラスの簡素化
- **メソッド参照**: さらなる簡潔化
- **var キーワード**: 型推論
- **Switch Expression**: 冗長なswitch文の改善

### 3. 出力フォーマット

```markdown
# ✨ コード簡素化提案

## 📊 サマリー
- 対象ファイル: TaskServiceImpl.java
- 簡素化箇所: 8件
- コード行数: 456行 → 298行 (35% 削減)
- 循環的複雑度: 7.8 → 3.2 (59% 改善)

---

## 📄 TaskServiceImpl.java

### ❌ [SIMP-001] 深いネストの削減 (line 89-124)
**問題**: 4階層のネスト、可読性が低い

**改善前** (36行):
\```java
public void processTask(Task task) {
    if (task != null) {
        if (task.isActive()) {
            if (task.getUser() != null) {
                if (task.getUser().hasPermission("EDIT")) {
                    // 実際の処理 (25行)
                    task.setStatus(TaskStatus.PROCESSING);
                    taskDao.update(task);
                    notificationService.send(task);
                } else {
                    throw new PermissionDeniedException();
                }
            } else {
                throw new UserNotFoundException();
            }
        } else {
            throw new TaskInactiveException();
        }
    } else {
        throw new IllegalArgumentException("Task is null");
    }
}
\```

**改善後** (12行):
\```java
public void processTask(Task task) {
    // ガード節で早期リターン
    if (task == null) {
        throw new IllegalArgumentException("Task is null");
    }
    if (!task.isActive()) {
        throw new TaskInactiveException();
    }
    if (task.getUser() == null) {
        throw new UserNotFoundException();
    }
    if (!task.getUser().hasPermission("EDIT")) {
        throw new PermissionDeniedException();
    }

    // メインロジックがネストなしで明確
    task.setStatus(TaskStatus.PROCESSING);
    taskDao.update(task);
    notificationService.send(task);
}
\```

**改善効果**:
- ネストレベル: 4階層 → 0階層
- 行数: 36行 → 12行 (67% 削減)
- 可読性: メインロジックが一目瞭然

---

### ✅ [SIMP-002] forループをStream APIに (line 156-178)
**問題**: 冗長なforループ、中間変数が多い

**改善前** (23行):
\```java
public List<TaskDto> getActiveTasksForUser(Long userId) {
    List<Task> allTasks = taskDao.findByUserId(userId);
    List<Task> activeTasks = new ArrayList<>();

    for (Task task : allTasks) {
        if (task.isActive() && !task.isDeleted()) {
            activeTasks.add(task);
        }
    }

    List<TaskDto> dtoList = new ArrayList<>();
    for (Task task : activeTasks) {
        TaskDto dto = new TaskDto();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setStatus(task.getStatus());
        dtoList.add(dto);
    }

    return dtoList;
}
\```

**改善後** (7行):
\```java
public List<TaskDto> getActiveTasksForUser(Long userId) {
    return taskDao.findByUserId(userId).stream()
        .filter(Task::isActive)
        .filter(task -> !task.isDeleted())
        .map(this::convertToDto)
        .collect(Collectors.toList());
}

private TaskDto convertToDto(Task task) {
    return TaskDto.builder()
        .id(task.getId())
        .title(task.getTitle())
        .status(task.getStatus())
        .build();
}
\```

**改善効果**:
- 行数: 23行 → 7行 (70% 削減)
- 中間変数: 3個 → 0個
- 宣言的: 「何をするか」が明確

---

### 💎 [SIMP-003] null チェックをOptionalに (line 234-256)
**問題**: nullチェックの連鎖

**改善前** (23行):
\```java
public String getUserEmail(Long taskId) {
    Task task = taskDao.findById(taskId);
    if (task != null) {
        User user = task.getUser();
        if (user != null) {
            String email = user.getEmail();
            if (email != null && !email.isEmpty()) {
                return email;
            } else {
                return "no-reply@example.com";
            }
        } else {
            return "no-reply@example.com";
        }
    } else {
        return "no-reply@example.com";
    }
}
\```

**改善後** (6行):
\```java
public String getUserEmail(Long taskId) {
    return Optional.ofNullable(taskDao.findById(taskId))
        .map(Task::getUser)
        .map(User::getEmail)
        .filter(email -> !email.isEmpty())
        .orElse("no-reply@example.com");
}
\```

**改善効果**:
- 行数: 23行 → 6行 (74% 削減)
- null チェック: 明示的0回 (Optionalで暗黙的に安全)
- 可読性: データフローが明確

---

### 🔄 [SIMP-004] Switch式の活用 (line 312-345)
**問題**: 冗長なswitch文

**改善前** (34行):
\```java
public String getStatusMessage(TaskStatus status) {
    String message;
    switch (status) {
        case PENDING:
            message = "タスクは保留中です";
            break;
        case IN_PROGRESS:
            message = "タスクは処理中です";
            break;
        case COMPLETED:
            message = "タスクは完了しました";
            break;
        case CANCELLED:
            message = "タスクはキャンセルされました";
            break;
        default:
            message = "不明なステータス";
            break;
    }
    return message;
}
\```

**改善後** (Java 14+) (11行):
\```java
public String getStatusMessage(TaskStatus status) {
    return switch (status) {
        case PENDING -> "タスクは保留中です";
        case IN_PROGRESS -> "タスクは処理中です";
        case COMPLETED -> "タスクは完了しました";
        case CANCELLED -> "タスクはキャンセルされました";
    };
}
\```

**改善後** (Java 11互換) (10行):
\```java
private static final Map<TaskStatus, String> STATUS_MESSAGES = Map.of(
    TaskStatus.PENDING, "タスクは保留中です",
    TaskStatus.IN_PROGRESS, "タスクは処理中です",
    TaskStatus.COMPLETED, "タスクは完了しました",
    TaskStatus.CANCELLED, "タスクはキャンセルされました"
);

public String getStatusMessage(TaskStatus status) {
    return STATUS_MESSAGES.getOrDefault(status, "不明なステータス");
}
\```

**改善効果**:
- 行数: 34行 → 11行 (68% 削減)
- break忘れリスク: 0
- 拡張性: Mapなら動的追加も可能

---

### ⚡ [SIMP-005] メソッド参照の活用 (line 89-102)

**改善前**:
\```java
tasks.stream()
    .filter(task -> task.isActive())
    .map(task -> task.getTitle())
    .forEach(title -> System.out.println(title));
\```

**改善後**:
\```java
tasks.stream()
    .filter(Task::isActive)
    .map(Task::getTitle)
    .forEach(System.out::println);
\```

**改善効果**: さらに簡潔、意図が明確

---

## 📈 総合改善効果

| メトリクス | 改善前 | 改善後 | 改善率 |
|-----------|--------|--------|--------|
| 総行数 | 456行 | 298行 | 35% ⬇️ |
| 平均メソッド行数 | 28行 | 12行 | 57% ⬇️ |
| 最大ネストレベル | 4階層 | 1階層 | 75% ⬇️ |
| 循環的複雑度 | 7.8 | 3.2 | 59% ⬇️ |
| null チェック数 | 45箇所 | 8箇所 | 82% ⬇️ |

---

## 🎯 適用優先順位

### フェーズ1: 即時適用可能 (低リスク)
1. SIMP-005: メソッド参照 (機械的置換)
2. SIMP-004: Switch式/Map (動作変更なし)

### フェーズ2: テスト要 (中リスク)
1. SIMP-001: ネスト削減 (ロジック同等性確認)
2. SIMP-002: Stream API (境界値テスト)
3. SIMP-003: Optional (null処理の動作確認)

---

## 🧪 検証チェックリスト

- [ ] 既存テストがすべて通過
- [ ] 境界値テストの追加 (Stream APIのempty case等)
- [ ] null安全性の検証 (Optionalの動作)
- [ ] パフォーマンス検証 (Stream は遅いケースもある)
- [ ] コードレビュー完了
```

### 4. 実装手順

1. **複雑度スキャン**: Glob/Grepでネスト深度、冗長なループを検索
2. **パターン検出**: null連鎖、switch文、forループを抽出
3. **モダン構文提案**: Stream API, Optional, Switch式で書き直し
4. **Before/After生成**: 具体的な簡素化例を提示
5. **メトリクス測定**: 行数、複雑度の改善効果を定量化

---

**Note**: このコマンドは自律的に複雑なコードを検出し、Java のモダンな構文を活用した簡潔な書き方を提案します。特定のファイルを簡素化したい場合は、コマンド実行時にファイルパスを指定してください。
