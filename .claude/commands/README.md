# Claude Code カスタムコマンド一覧

このディレクトリには、プロジェクト専用のClaude Codeスラッシュコマンドが格納されています。

## 📂 ディレクトリ構成

```
.claude/commands/
├── explain/         解説系コマンド（5個）
├── review/          レビュー系コマンド（6個）
├── doc/             ドキュメント生成系コマンド（9個）
└── utility/         実用ツール系コマンド（10個）
```

---

## 🔍 解説系コマンド (explain/)

コードやアーキテクチャの理解を深めるためのコマンド群

| コマンド | 説明 | 使用例 |
|---------|------|--------|
| `/explain-basic` | コードの基本的な解説 | コードを選択して実行 |
| `/explain-architecture` | アーキテクチャ全体の解説 | プロジェクト構造を知りたい時 |
| `/explain-error` | エラー原因の分析と解決方法 | エラーメッセージを貼り付け |
| `/explain-tech-choice` | 技術選定の理由を解説 | Spring Boot, PostgreSQL等 |
| `/explain-api` | API仕様の詳細解説 | エンドポイント名を指定 |

### 使用例
```
/explain-basic
[解説してほしいコードを貼り付け]
```

---

## 🔎 レビュー系コマンド (review/)

コード品質向上のためのレビューとリファクタリング

| コマンド | 説明 | 使用例 |
|---------|------|--------|
| `/review-comprehensive` | 包括的なコードレビュー | 全体的な品質チェック |
| `/review-security` | セキュリティ観点のレビュー | 脆弱性チェック |
| `/review-performance` | パフォーマンス改善提案 | 遅い処理の最適化 |
| `/review-refactor` | リファクタリング提案 | 保守性向上 |
| `/review-test` | テストコード作成 | テストが必要な時 |
| `/review-simplify` | コードの簡素化 | 複雑なコードの整理 |

### 使用例
```
/review-security
[セキュリティチェックしたいコードを貼り付け]
```

---

## 📚 ドキュメント生成系コマンド (doc/)

プロジェクトドキュメントの自動生成

| コマンド | 説明 | 生成物 |
|---------|------|--------|
| `/doc-readme` | README.md生成 | プロジェクト概要 |
| `/doc-api-spec` | API仕様書生成 | OpenAPI/Swagger |
| `/doc-javadoc` | JavaDocコメント追加 | JavaDoc |
| `/doc-database` | DB設計書生成 | ER図、テーブル定義 |
| `/doc-architecture` | アーキテクチャドキュメント | システム設計書 |
| `/doc-operation` | 運用手順書生成 | デプロイ手順等 |
| `/doc-troubleshooting` | トラブルシューティングガイド | 障害対応手順 |
| `/doc-changelog` | CHANGELOG生成 | リリースノート |
| `/doc-setup-guide` | 環境構築ガイド生成 | セットアップ手順 |

### 使用例
```
/doc-readme
[対象ユーザー: 新規参加者]
```

---

## 🛠️ 実用ツール系コマンド (utility/)

開発を効率化する実用的なコマンド群

| コマンド | 説明 | 主な用途 |
|---------|------|---------|
| `/utility-debug` | デバッグ支援 | バグの原因特定 |
| `/utility-crud-generator` | CRUD操作コード生成 | ボイラープレート削減 |
| `/utility-dependency-analysis` | 依存関係分析 | ライブラリ更新 |
| `/utility-migration-create` | Flywayマイグレーション作成 | DB変更管理 |
| `/utility-spring-test` | Spring Boot テスト生成 | 統合テスト作成 |
| `/utility-commit-message` | Gitコミットメッセージ生成 | Conventional Commits |
| `/utility-code-compare` | コード比較・差分解説 | 変更レビュー |
| `/utility-env-setup` | 環境変数管理 | .env設定 |
| `/utility-log-analysis` | ログ分析 | エラー調査 |
| `/utility-performance-measure` | パフォーマンス計測 | ボトルネック特定 |

### 使用例
```
/utility-crud-generator
[Entityクラスのコードを貼り付け]
```

---

## 💡 使い方のコツ

### 1. タブ補完を活用
```
/explain-   [Tab] → 候補が表示される
/review-    [Tab] → 候補が表示される
/doc-       [Tab] → 候補が表示される
/utility-   [Tab] → 候補が表示される
```

### 2. コードの貼り付け
多くのコマンドでは、対象となるコードを貼り付ける必要があります：

```
/explain-basic

このコードの動作を詳しく解説してください。

## 解説してほしい内容
...

## 対象コード
public class UserService {
    // ここにコードを貼り付け
}
```

### 3. プレースホルダーの置き換え
各コマンドには `[例: ...]` のようなプレースホルダーがあります。
これらを実際の値に置き換えて使用してください。

---

## 🎯 よく使うコマンド Top 5

1. **`/explain-basic`** - コードの理解
2. **`/review-comprehensive`** - プルリクエスト前のレビュー
3. **`/utility-debug`** - バグ調査
4. **`/doc-readme`** - ドキュメント作成
5. **`/utility-commit-message`** - コミットメッセージ生成

---

## 🔧 カスタマイズ方法

### 新しいコマンドの追加

1. 適切なカテゴリディレクトリに`.md`ファイルを作成
2. Markdownでプロンプトを記述
3. 保存すると自動的にスラッシュコマンドとして利用可能に

**例**: `.claude/commands/utility/my-command.md`
```markdown
# 私のカスタムコマンド

## 説明
このコマンドは...

## 入力
[ここに入力内容]
```

使用方法:
```
/utility-my-command
```

### 既存コマンドの編集

各`.md`ファイルを直接編集することで、プロンプトをカスタマイズできます。

---

## 📖 参考リソース

- [Claude Code 公式ドキュメント](https://docs.claude.com/en/docs/claude-code)
- [スラッシュコマンドの詳細](https://docs.claude.com/en/docs/claude-code/slash-commands)
- [カスタムコマンドの作成方法](https://docs.claude.com/en/docs/claude-code/slash-commands#custom-slash-commands)

---

## 🤝 貢献

新しいコマンドのアイデアや改善提案があれば、プルリクエストを作成してください！

---

## 🔧 カスタマイズのベストプラクティス

### コマンド作成時の注意点

1. **役割を明確に**: 「あなたの役割」セクションで具体的な指示を記載
2. **ツールを明記**: Glob, Read, Bash等、使用するツールを明示的に指定
3. **CLAUDE.mdを参照**: プロジェクト共通ルールはCLAUDE.mdに記載し、コマンドでは参照のみ
4. **出力形式を明確化**: 期待する出力のフォーマットを具体的に示す
5. **バージョン情報を含める**: Spring Boot 2.7.18, javax.validation等、バージョン固有の情報を明記

### 悪い例と良い例

**❌ 悪い例**（説明書になっている）:
```markdown
## 概要
このコマンドはテストを生成します。

## 実行フロー
テストファイルを検索し、生成します。
```

**✅ 良い例**（実行可能な指示）:
```markdown
## あなたの役割
指定されたクラスに対して、JUnit 5 + Mockitoのテストコードを生成してください。

## 実行手順
### ステップ1: 対象クラスの特定
1. **Globツール**で `**/*Service*.java` を検索
2. ユーザーに対象を確認
```

---

**最終更新**: 2025-10-19
**コマンド数**: 合計29個
**対応バージョン**: Spring Boot 2.7.18, PostgreSQL + H2併用
