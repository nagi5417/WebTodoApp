# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

WebToDoStarter is a Spring Boot web application for task management with user authentication. It follows a layered architecture with Controllers, Services, DAOs, and Entities.

## Architecture

- **Entity Layer**: Plain POJOs for data models (Task, TaskType, User)
- **DAO Layer**: Data access using Spring JDBC with manual SQL
- **Service Layer**: Business logic interfaces and implementations
- **Controller Layer**: Web controllers for REST endpoints and Thymeleaf views
- **Configuration**: Security config with Spring Security, MVC configuration
- **Database**: H2 in-memory database with Flyway migrations (db/migration/V*.sql)

## Development Commands

### Build and Run
```bash
./gradlew build          # Build the project
./gradlew bootRun        # Run the Spring Boot application
./gradlew test           # Run all tests
```

### Testing
```bash
./gradlew test --tests TaskServiceImplTest                    # Run specific test class
./gradlew test --tests TaskServiceImplUnitTest               # Run unit tests
./gradlew test --tests "*.TaskService*"                      # Run tests matching pattern
```

### Database Access
- Application runs on http://localhost:8080
- H2 Console available at http://localhost:8080/h2-console when running
- JDBC URL: `jdbc:h2:mem:test`
- Username: `sa`, Password: (empty)

## Key Implementation Details

### Data Access Pattern
- Uses Spring JDBC with DAO pattern alongside JPA/Hibernate
- Manual SQL queries in TaskDaoImpl and UserDao
- Database schema managed by Flyway migrations in src/main/resources/db/migration/

### Security Configuration
- Spring Security configured in SecurityConfig
- OAuth2 authentication with Google Sign-In support
- Custom form-based authentication with UserAuthenticationService
- Password encoding with BCrypt (PasswordConfig)

### Web Layer
- Thymeleaf templates in src/main/resources/templates/
- Static resources in src/main/resources/static/
- Controllers use both REST endpoints (TaskApiController, AuthController) and view rendering (TaskController, UserController)
- API endpoints available under /api/ prefix

### Project Structure
- Main package: `com.example.demo`
- Modular organization: app/, config/, dao/, entity/, service/
- Test configuration in src/test/resources/application-unit.yml


# プロジェクトガイドライン

## 作業原則

- 作業は **必ずタスクファイルを起点**に開始し、進捗を反映し続ける
- 「途中で止まらず、完了まで自律的に進める」ことを優先とする
- **重要判断時は必ず根拠を明示**し、リスクを最小化する
- **段階的な確認**：大きな変更前は中間成果物を提示する
- 常に深く思考（ultrathink）して回答する必要がある
- 説明は省かず、最大限丁寧に、具体的な回答を生成すること
- ユーザーの指示や提案に過剰に忖度・迎合することは禁止。プロフェッショナルなエンジニアとして該当テーマに関するメリット、デメリットそれぞれを必ず提示し、その上で最善なあるべき案を思考し、提示すること。
- ネット検索を活用して、必要な最新情報を収集すること
- ベストプラクティスに従った生成をすること

## **Task ファイル運用の厳格ルール（重複作成防止）**

- **原則**：新しい task ファイルを作成する前に、**必ず既存の未完了タスクを探索**して再利用すること。
- **探索手順（Preflight）**：

1. `.github/tasks/ACTIVE.md` を読み、path: があれば**そのファイルのみ編集**する。
2. ACTIVE.md が空・不在なら、`.github/tasks/index.json` を読み、status: "open" の**最終更新が最新**のものを採用する。
3. 1. と 2) でも見つからない場合のみ新規作成してよい。ただし作成前に `.github/tasks/*.md` を検索し、task_id / status を再確認する。

### **タスクファイルの基本フォーマット**

```markdown
# タスク ID: 20250115-feature-name-abc123

# タイトル: 機能名の実装

# ステータス: open

# 優先度: high

# 担当者: developer

# 作成日: 2025-01-15T10:00:00+09:00

# 更新日: 2025-01-15T10:00:00+09:00

# ツール: cursor,kiro

# パス: .github/tasks/active.md

## 概要

機能の詳細説明

## サブタスク

- [ ] 要件定義
- [ ] 設計
- [ ] 実装
- [ ] テスト
- [ ] レビュー

## 進捗メモ

作業中のメモや気づきを記録

## 完了条件

- テストが全て通る
- コードレビューが完了
- ドキュメントが更新済み
```

### **タスクファイルのディレクトリ構造**

```
.github/
├── tasks/
│   ├── ACTIVE.md           # 現在進行中のタスク（ポインタ）
│   ├── index.json          # 全タスクのインデックス
│   ├── backlog/            # 未着手タスク
│   │   ├── feature-*.md
│   │   ├── bugfix-*.md
│   │   └── refactor-*.md
│   ├── in_progress/        # 進行中タスク
│   │   └── task_*.md
│   ├── completed/          # 完了済みタスク
│   │   └── task_*.md
│   └── templates/          # テンプレート
│       ├── feature.md
│       ├── bugfix.md
│       └── refactor.md
```

### **ツール間統一ルール**

- **Cursor 用**: 作業開始時は ACTIVE.md を更新し、該当タスクを in_progress/に移動
- **Kiro 用**: 同様のワークフローを適用
- **競合時**: 作成時刻の新しい方を優先
- **完了時**: completed/に移動し、ACTIVE.md をクリア

## **プロジェクト概要**

- **プロジェクト名**: WebToDoApp（タスク管理システム）
- **アーキテクチャ**: Spring Boot + Thymeleaf + H2 Database
- **言語**: Java 11
- **フレームワーク**: Spring Boot 2.7.18, Spring Security, Spring JDBC, Flyway
- **ビルドツール**: Gradle 7.6
- **データベース**: H2 (in-memory), Flyway migrations
- **認証**: Spring Security (Form Login + OAuth2/Google)

# **出力フォーマット**

- 視認性を最大限高めるため、 **すべての回答は Markdown 形式で出力すること。**
- コード例は必ず ``` 言語名 で囲む。例
- 箇条書きは - または 1. を使用して明確に階層化する。
- 説明や仕様は必ず見出し（## や ###）を使って区切る。
- 表形式で示せる内容は Markdown の表を用いる。
- 重要なポイントは太字（**）や斜体（_）で強調する。_**
- 出力の途中で Markdown を省略したり、プレーンテキストに戻らないこと。
- 中間的な確認ステップも Markdown で整理し、最終成果物の見やすさを常に意識する。

# **自律ワークフロー**

1. **タスク理解**

- 与えられた情報をもとに
- 現在のタスクファイルを読み、目標・サブタスクを把握する
- 不足している場合は自分で分解してタスクファイルに追加する

1. **実装・修正**

- コードを追加・変更する
- 必要に応じてドキュメントや設定も更新する

1. **検証**

- テストを実行し、すべて成功するまで修正を繰り返す
- ビルド・Lint・静的解析を通す

1. **リファクタ**

- コードを読みやすく保守性高く整理する
- 命名、責務分割、エラーハンドリングを改善する

1. **タスクファイル更新**

- 完了した項目はチェックを入れる
- 新しく発見したサブタスクは追記する
- 必要なら成果の概要を記録する

1. **完了確認**

- すべてのタスクが完了し、テストが成功し、コード品質が担保された時点で終了とする

# **コーディング規約**

- プロジェクトの既存コードスタイルに厳密に従う
- 適切なエラーハンドリングとログ出力を行う
- テストを必ず追加・更新する
- セキュリティとパフォーマンスを意識し、不要な負荷を避ける

# **コミュニケーション原則**

- 原則としてユーザーに逐次確認を求めない
- 曖昧さが大きい場合のみ、タスクファイルに「要確認」として記載する
- それ以外は推測・判断して進め、最終成果物でレビューを受ける

# **その他ルール**

- ファイルやフォルダの物理削除は許可します。
- git の commit と push は禁止します。
- フォルダ・ファイル新規作成処理は insert_edit_into_file ツール で実行してください。mkdir , echo, type 等で作成することは禁止します。
- 新規にファイルを作成を試みる場合には、作成パスに同名のファイルが存在するかどうかを確認してから作成すること。
- 事前に同じ責務を負った関連ファイルが存在するかどうか探索したうえで作成の要否を判断すること。
- ソースコードの作成および修正後、当該ファイルに対して get_errors ツールによるエラー確認を行い、エラーが解消するまで修正すること。
- コード品質管理\*\*: 修正によって前回までコードが不要になった場合は確認して削除するようにしてください。

# **新規ライブラリ・パッケージ導入時のルール**

- 新規のライブラリやパッケージを導入する場合は、インターネットで調査し、利用可能な安定最新版を使ってください。また当該バージョンにおける利用方法もインターネットのリファレンスを参照し、精度の高い実装をしてください。

## 【テスト方針】

- ユニットテスト（UT）：ドメインロジック/データ変換/ユースケース分岐を高速に検証。外部 I/O は Fake/Stub で隔離。
- 統合テスト（IT）：DB/HTTP/メッセージング/ストレージ/キャッシュ/フレームワーク配線を最小本物で検証。少数精鋭、@Tag("integration") 等でマーク。
- 契約テスト（CT）：入替可能なインターフェース/ポートは **共通スイート** を作成し、各実装に同一テストを適用。対外 API は **CDC（Pact 等）** を使用。

## 【必須要件（各レイヤ共通）】

- AAA（Arrange/Act/Assert）または Given/When/Then コメントを入れる。
- 正常系だけでなく、**境界値/例外系/冪等性/リトライ分岐**を最低 1 件ずつ。
- 時刻/乱数/ID は注入して固定（再現性）。ランダムは seed 固定。
- アサーションは短く断定的なメッセージで。内部実装には過剰に結合しない。
- データはテスト毎にリセット（BeforeEach/fixture）。遅い初期化は BeforeAll で一度だけ。
