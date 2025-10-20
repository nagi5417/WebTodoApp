Spring Bootアプリケーションのパフォーマンス分析と最適化を実施します。

## 実行フロー

### 1. 分析対象の特定

#### パターンA: ファイル指定あり
```
/review/performance src/main/java/com/example/demo/service/TaskServiceImpl.java
```
→ 指定ファイルのパフォーマンス分析

#### パターンB: 問題症状から自動特定
```
/review/performance
```
→ 以下を自動検索：
1. **データアクセス層**: `*Dao*.java`, `*Repository*.java` (N+1問題、遅いクエリ)
2. **サービス層**: `*Service*.java` (ループ内のDB呼び出し、重複処理)
3. **コントローラー**: `*Controller*.java` (同期処理、大量データ返却)
4. **設定ファイル**: `application.yml` (コネクションプール、キャッシュ設定)

### 2. パフォーマンスチェック項目

#### 🔴 重大なボトルネック

##### データベース関連
- **N+1クエリ問題**: ループ内でのクエリ実行
- **SELECT ***: 不要なカラムの取得
- **インデックス未使用**: WHERE句のカラムにインデックスなし
- **トランザクション**: @Transactionalの不適切な使用

##### メモリ関連
- **大量データ一括読み込み**: ページネーション未使用
- **ストリーム未使用**: コレクション操作の非効率性
- **メモリリーク**: リソースのクローズ漏れ

##### 並行処理
- **同期処理**: 並列化可能な処理の逐次実行
- **スレッドブロック**: 長時間のI/O待機
- **非同期処理の未活用**: @Async未使用

#### 🟡 最適化推奨

- **キャッシング**: 同じデータの重複取得
- **遅延ロード**: 不要なデータの先読み
- **バッチ処理**: 1件ずつの処理

### 3. 出力フォーマット

```markdown
# ⚡ パフォーマンス分析レポート

## 🎯 実行サマリー
- 分析対象: 5ファイル (789行)
- 検出問題: 🔴 3件 (重大) / 🟡 5件 (推奨)
- 推定改善効果: **レスポンス時間 85% 削減** (3000ms → 450ms)

---

## 📄 TaskServiceImpl.java

### 🔴 [PERF-001] N+1クエリ問題
**深刻度**: 🔴 Critical
**影響**: 100件取得時、101回のクエリ実行 (現在のレスポンス: 2800ms)

**現在のコード** (line 67):
\```java
public List<TaskDto> getAllTasksWithUsers() {
    List<Task> tasks = taskDao.findAll();  // 1回
    return tasks.stream()
        .map(task -> {
            User user = userDao.findById(task.getUserId());  // N回
            return new TaskDto(task, user);
        })
        .collect(Collectors.toList());
}
\```

**最適化案**:
\```java
public List<TaskDto> getAllTasksWithUsers() {
    // JOINで一括取得 (1回のクエリ)
    List<Task> tasks = taskDao.findAllWithUsers();
    return tasks.stream()
        .map(TaskDto::new)
        .collect(Collectors.toList());
}

// TaskDao.java
public List<Task> findAllWithUsers() {
    String sql = """
        SELECT t.*, u.id as user_id, u.username, u.email
        FROM tasks t
        LEFT JOIN users u ON t.user_id = u.id
    """;
    return jdbcTemplate.query(sql, new TaskWithUserRowMapper());
}
\```

**効果**:
- クエリ数: 101回 → 1回 (99% 削減)
- レスポンス時間: 2800ms → 350ms (87.5% 改善)
- メモリ使用量: ほぼ同等

**トレードオフ**:
- ✅ メリット: 劇的な速度改善、DBサーバー負荷軽減
- ⚠️ デメリット: JOINロジックの複雑化 (RowMapperの実装必要)
- 💡 推奨度: **最優先で対応**

---

### 🟡 [PERF-002] ページネーション未実装
**深刻度**: 🟡 Medium
**影響**: 全件取得による大量メモリ消費

**現在のコード** (line 42):
\```java
@GetMapping("/tasks")
public List<TaskDto> getAllTasks() {
    return taskService.findAll();  // 全件取得
}
\```

**最適化案**:
\```java
@GetMapping("/tasks")
public Page<TaskDto> getAllTasks(
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "20") int size
) {
    Pageable pageable = PageRequest.of(page, size);
    return taskService.findAll(pageable);
}
\```

**効果**:
- メモリ: 10MB → 200KB (98% 削減、10000件の場合)
- レスポンス: 1200ms → 80ms (93% 改善)

---

### 🔴 [PERF-003] 同期処理によるブロッキング
**深刻度**: 🔴 Critical
**影響**: メール送信で3秒ブロック

**現在のコード** (line 156):
\```java
@Transactional
public void createTask(TaskDto dto) {
    Task task = taskDao.save(dto.toEntity());
    emailService.sendNotification(task);  // 3秒ブロック
    slackService.postMessage(task);       // 2秒ブロック
}
\```

**最適化案**:
\```java
@Transactional
public void createTask(TaskDto dto) {
    Task task = taskDao.save(dto.toEntity());

    // 非同期で通知送信
    CompletableFuture.allOf(
        CompletableFuture.runAsync(() -> emailService.sendNotification(task)),
        CompletableFuture.runAsync(() -> slackService.postMessage(task))
    );
}

// または@Asyncを使用
@Async
public void sendNotifications(Task task) {
    emailService.sendNotification(task);
    slackService.postMessage(task);
}
\```

**効果**:
- レスポンス: 5200ms → 120ms (97.7% 改善)
- スループット: 5倍向上

---

## 📊 最適化優先度

| ID | 問題 | 影響度 | 実装難易度 | 優先度 |
|----|------|--------|-----------|--------|
| PERF-001 | N+1クエリ | 🔴 High | 中 | 🔥 最優先 |
| PERF-003 | 同期ブロック | 🔴 High | 低 | 🔥 最優先 |
| PERF-002 | ページネーション | 🟡 Medium | 低 | ⚡ 高 |

---

## 🧪 ベンチマーク方法

### JMeterテスト設定
\```xml
<ThreadGroup>
  <numThreads>100</numThreads>
  <rampUp>10</rampUp>
  <loops>10</loops>
</ThreadGroup>
\```

### Spring Actuatorでメトリクス収集
\```yaml
management:
  endpoints:
    web:
      exposure:
        include: metrics,health
  metrics:
    export:
      prometheus:
        enabled: true
\```

### 改善前後の比較
| メトリクス | 改善前 | 改善後 | 改善率 |
|-----------|--------|--------|--------|
| 平均レスポンス | 3000ms | 450ms | 85% ⬇️ |
| P95レスポンス | 5200ms | 680ms | 87% ⬇️ |
| スループット | 12 req/s | 65 req/s | 441% ⬆️ |
| DB接続数 | 45 | 8 | 82% ⬇️ |

---

## 🎯 総合推奨事項

1. **即時対応**: PERF-001, PERF-003 (2日で実装可能)
2. **次フェーズ**: PERF-002 (APIのバージョニング必要)
3. **監視**: Spring Actuator + Prometheusでメトリクス継続監視
```

### 4. 実装手順

1. **ファイル探索**: Globでサービス層・DAO層を検索
2. **静的解析**: ループ内DB呼び出し、SELECT *, @Transactional スコープをチェック
3. **ベンチマーク**: 既存コードの実行時間を測定（可能な場合）
4. **最適化提案**: 上記フォーマットで具体的な改善案を生成
5. **優先順位付け**: 影響度×実装難易度でランク付け

---

**Note**: このコマンドは自律的にパフォーマンスボトルネックを特定し、データに基づいた最適化提案を行います。特定のファイルを分析したい場合は、コマンド実行時にファイルパスを指定してください。
