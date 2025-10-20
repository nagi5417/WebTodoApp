# Architecture Explanation

## あなたの役割
WebToDoAppプロジェクトのアーキテクチャを包括的に解説してください。プロジェクト構造、データフロー、技術スタック、設計パターンをMermaid図を含めて視覚的に説明すること。

## 前提条件（CLAUDE.md参照）
- プロジェクト: WebToDoApp（タスク管理システム）
- フレームワーク: Spring Boot 2.7.18
- アーキテクチャ: Layered Architecture (Controller → Service → DAO → Entity)
- データベース: **PostgreSQL (本番) + H2 (開発・テスト) の併用構成**
- 認証: Spring Security (Form Login + OAuth2/Google)

---

## 実行手順

### ステップ1: プロジェクト構成ファイルの読み込み

**Readツール**で以下を読み込み、プロジェクト基本情報を把握:
1. `build.gradle` - 依存関係、Javaバージョン、プラグイン
2. `src/main/resources/application.yml` - データベース設定、Spring設定
3. `src/main/resources/application-dev.yml` (存在する場合)
4. `src/main/resources/application-prod.yml` (存在する場合)

### ステップ2: ディレクトリ構造の探索

**Globツール**で以下を検索し、パッケージ構成を把握:
1. `src/main/java/com/example/demo/**/*.java` - 全Javaファイル
2. `src/main/resources/db/migration/V*.sql` - Flywayマイグレーション
3. `src/main/resources/templates/**/*.html` - Thymeleafテンプレート

### ステップ3: 主要クラスの分析

**Readツール**で代表的なクラスを読み込み、実装パターンを理解:
1. `src/main/java/com/example/demo/app/controller/TaskController.java`
2. `src/main/java/com/example/demo/service/TaskServiceImpl.java`
3. `src/main/java/com/example/demo/dao/TaskDaoImpl.java`
4. `src/main/java/com/example/demo/entity/Task.java`
5. `src/main/java/com/example/demo/config/SecurityConfig.java`

### ステップ4: データベーススキーマの解析

**Readツール**でFlywayマイグレーションファイルを読み込み、テーブル構造を把握:
1. `src/main/resources/db/migration/V1__*.sql`
2. `src/main/resources/db/migration/V2__*.sql`
3. 以降の全マイグレーションファイル

---

## 出力内容

以下の内容をMarkdown形式で出力してください:

### **1. プロジェクト概要**
- プロジェクト名、目的
- Spring Bootバージョン、Javaバージョン
- ビルドツール（Gradle/Maven）
- **データベース構成: PostgreSQL (本番) + H2 (開発・テスト)**

### **2. ディレクトリ構造**
```
src/main/java/com/example/demo/
├── app/
│   ├── controller/
│   └── dto/
├── config/
├── service/
├── dao/
└── entity/
```
各パッケージの役割を説明

### **3. レイヤードアーキテクチャ図**
Mermaid記法で以下を作成:
- Controller → Service → DAO → Database のフロー図
- 各レイヤーの責務を明記

### **4. データフロー（シーケンス図）**
タスク作成などの具体例でMermaidシーケンス図を作成:
```mermaid
sequenceDiagram
    User->>Controller: POST /api/tasks
    Controller->>Service: createTask()
    Service->>DAO: save()
    DAO->>DB: INSERT
    ...
```

### **5. 認証・認可アーキテクチャ**
- Spring Securityの設定内容
- Form Login + OAuth2/Googleの仕組み
- Mermaidフローチャートで認証フローを図示

### **6. データベース設計**
- ER図（Mermaid記法）
- 主要テーブル（users, tasks, task_types等）の説明
- **PostgreSQL (本番) / H2 (開発・テスト) の使い分け**
- Flywayマイグレーション戦略

### **7. 技術スタック一覧**
| 技術 | バージョン | 用途 |
|------|-----------|------|
| Spring Boot | 2.7.18 | ... |
| PostgreSQL | 14+ | 本番DB |
| H2 | latest | 開発・テストDB |
| ... | ... | ... |

### **8. 設計パターン**
- Repository/DAO Pattern
- Dependency Injection
- DTO Pattern
- Builder Pattern

---

## 出力形式の例

以下のような構造でMarkdownドキュメントを生成してください:

```markdown
# 🏛️ WebToDoApp アーキテクチャ解説

## 📊 プロジェクト概要
- プロジェクト名: WebToDoApp
- アーキテクチャ: Layered Architecture
- フレームワーク: Spring Boot 2.7.18
- 言語: Java 11
- ビルドツール: Gradle 7.6
- **データベース**:
  - **本番環境**: PostgreSQL 14+
  - **開発環境**: H2 (in-memory, MODE=PostgreSQL)
  - **テスト環境**: H2 (in-memory, MODE=PostgreSQL)

## 📁 ディレクトリ構造
[実際のプロジェクト構造を記載]

## 🏗️ レイヤードアーキテクチャ
[Mermaid図で図示]

## 🔄 データフロー
[Mermaidシーケンス図で図示]

## 🛡️ 認証・認可
[Spring Security設定の詳細]

## 💾 データベース設計
[ER図（Mermaid）+ テーブル説明]
**重要**: PostgreSQL (本番) / H2 (開発・テスト) の併用構成

## 🔧 技術スタック
[技術一覧表]

## 🎯 設計パターン
[使用している設計パターンの説明]
```

---

## 重要な注意事項

1. **データベース構成を正確に記載**: PostgreSQL (本番) + H2 (開発・テスト)
2. **Mermaid図を活用**: ER図、シーケンス図、フローチャートで視覚化
3. **実際のファイルを読み込んで正確な情報を記載**（推測禁止）
4. **バージョン情報を明記**: Spring Boot 2.7.18, Java 11等
5. **javax.validation使用（jakarta.validationではない）**を明記
