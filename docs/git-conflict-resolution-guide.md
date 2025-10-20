# 🔧 Git競合解決ガイド

**対象者**: Git初心者〜中級者
**作成日**: 2025-10-20
**プロジェクト**: WebToDoApp

---

## 📋 目次

1. [競合とは](#競合とは)
2. [今回発生した競合の状況](#今回発生した競合の状況)
3. [競合解決の手順（ステップバイステップ）](#競合解決の手順ステップバイステップ)
4. [よくある質問（FAQ）](#よくある質問faq)
5. [トラブルシューティング](#トラブルシューティング)
6. [参考資料](#参考資料)

---

## 🤔 競合とは

### 競合が発生する理由

**競合（コンフリクト）**は、2つの異なるブランチで**同じファイルの同じ箇所**を異なる内容で変更した場合に発生します。

### 具体例

```
メインブランチ:
  ファイルA.txt の3行目: "こんにちは"

ブランチ1:
  ファイルA.txt の3行目: "Hello"

ブランチ2:
  ファイルA.txt の3行目: "Bonjour"
```

この状態でブランチ1とブランチ2をマージしようとすると、Gitは「どちらを採用すべきか分からない」ため、**競合**として報告します。

---

## 📊 今回発生した競合の状況

### ブランチの状態

```
feature/migrate-to-postgresql (現在のブランチ)
    ├─ PostgreSQL + H2 併用構成の実装
    ├─ Claudeプロンプト・ドキュメント作成
    └─ 最新のコミット: f7fe209

feature/user-settings (マージしたいブランチ)
    ├─ ユーザー設定変更機能の実装
    ├─ OAuth2認証情報の環境変数化
    └─ 最新のコミット: 7549052
```

### ブランチが分岐した図

```
* f7fe209 (feature/migrate-to-postgresql) claude code用のプロンプト・ドキュメント作成
* e825115 H2からPostgresqlとH2の併用しての運用に変更
| * 7549052 (feature/user-settings) ユーザー設定変更機能実装
|/
* e3521e5 CLAUDE.mdのファイルの内容を変更
* 9dd7df6 Initial commit
```

**問題**: 両ブランチが共通の親コミット（e3521e5）から分岐しており、それぞれが独自の変更を持っている。

---

## 🛠️ 競合解決の手順（ステップバイステップ）

### ステップ1: 現在のブランチを確認

```bash
git status
```

**出力例**:
```
On branch feature/migrate-to-postgresql
Your branch is up to date with 'origin/feature/migrate-to-postgresql'.

nothing to commit, working tree clean
```

**確認ポイント**:
- ✅ 現在のブランチが正しいか
- ✅ 作業ツリーがクリーンか（未コミットの変更がないか）

---

### ステップ2: マージを実行

```bash
git merge feature/user-settings
```

**意味**: `feature/user-settings` ブランチの変更を、現在のブランチ（`feature/migrate-to-postgresql`）に統合する

---

### ステップ3: 競合の確認

マージを実行すると、以下のようなエラーメッセージが表示されます：

```
Auto-merging .env.example
CONFLICT (add/add): Merge conflict in .env.example
Auto-merging src/test/java/com/example/demo/UserServiceImplTest.java
CONFLICT (content): Merge conflict in src/test/java/com/example/demo/UserServiceImplTest.java
Automatic merge failed; fix conflicts and then commit the result.
```

**重要な情報**:
- `CONFLICT (add/add)`: 両方のブランチで新規追加されたファイルが競合
- `CONFLICT (content)`: ファイルの内容が競合
- `Automatic merge failed`: 自動マージ失敗、手動解決が必要

---

### ステップ4: 競合ファイルの一覧を確認

```bash
git status
```

**出力例**:
```
On branch feature/migrate-to-postgresql
You have unmerged paths.
  (fix conflicts and run "git commit")
  (use "git merge --abort" to abort the merge)

Unmerged paths:
  (use "git add/rm <file>..." as appropriate to mark resolution)
	both added:      .env.example
	both modified:   .gradle/7.6/checksums/checksums.lock
	both modified:   src/test/java/com/example/demo/UserServiceImplTest.java
```

**ファイルの状態の意味**:
- `both added`: 両方のブランチで新規追加された
- `both modified`: 両方のブランチで変更された
- `deleted by them`: マージ元ブランチで削除された

---

### ステップ5: 競合の種類を理解する

今回発生した競合は3種類：

#### A. 重要な競合（手動解決が必要）
- `.env.example` - 環境変数設定ファイル
- `src/test/java/com/example/demo/UserServiceImplTest.java` - テストファイル

#### B. 自動生成ファイルの競合（削除してOK）
- `.gradle/` ディレクトリ - Gradleのキャッシュ
- `build/` ディレクトリ - ビルド結果

---

### ステップ6: 自動生成ファイルの競合を解決

自動生成されるファイルは、再ビルドで作り直せるため、削除して問題ありません。

```bash
# .gradle/ ディレクトリの競合ファイルを削除
git rm -f .gradle/7.6/checksums/checksums.lock
git rm -f .gradle/7.6/dependencies-accessors/dependencies-accessors.lock
git rm -f .gradle/7.6/executionHistory/executionHistory.bin
# （その他のファイルも同様に削除）

# build/ ディレクトリの競合ファイルを削除
git rm -f build/resources/main/application.yml
git rm -f build/resources/main/db/migration/V1__ensure_task_type_and_seed.sql
# （その他のファイルも同様に削除）
```

**ポイント**: `git rm -f` は「ファイルを削除してステージングエリアから除外」する

---

### ステップ7: .env.example の競合を手動解決

#### 7-1. ファイルを開く

```bash
cat .env.example
```

#### 7-2. 競合マーカーを確認

競合しているファイルには、以下のような**競合マーカー**が含まれています：

```
<<<<<<< HEAD
# データベース設定
DATABASE_URL=jdbc:postgresql://localhost:5432/webtodo
DATABASE_DRIVER=org.postgresql.Driver
=======
# Google OAuth2 Configuration
GOOGLE_CLIENT_ID=your-client-id-here.apps.googleusercontent.com
GOOGLE_CLIENT_SECRET=your-client-secret-here
>>>>>>> feature/user-settings
```

**マーカーの意味**:
- `<<<<<<< HEAD`: 現在のブランチの内容の開始
- `=======`: 区切り線
- `>>>>>>> feature/user-settings`: マージ元ブランチの内容の終了

#### 7-3. 競合を解決（両方の内容を統合）

エディタで開き、競合マーカーを削除して、両方の内容を統合します：

```
# ========================================
# データベース設定
# ========================================

# PostgreSQLを使用する場合
DATABASE_URL=jdbc:postgresql://localhost:5432/webtodo
DATABASE_DRIVER=org.postgresql.Driver
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=postgres
DATABASE_PLATFORM=org.hibernate.dialect.PostgreSQLDialect
H2_CONSOLE_ENABLED=false

# ========================================
# Google OAuth2設定
# ========================================
# Get these values from: https://console.cloud.google.com/apis/credentials

GOOGLE_CLIENT_ID=your-client-id-here.apps.googleusercontent.com
GOOGLE_CLIENT_SECRET=your-client-secret-here
```

**重要**: 競合マーカー（`<<<<<<<`, `=======`, `>>>>>>>`）を必ず削除してください。

---

### ステップ8: UserServiceImplTest.java の競合を解決

#### 8-1. 競合内容を確認

```bash
git diff HEAD src/test/java/com/example/demo/UserServiceImplTest.java
```

#### 8-2. 今回の場合の判断

競合箇所がコメントの違いのみ（内容的にはほぼ同じ）だったため、`feature/user-settings` の内容を採用：

```bash
git checkout --theirs src/test/java/com/example/demo/UserServiceImplTest.java
```

**コマンドの意味**:
- `--theirs`: マージ元ブランチ（`feature/user-settings`）の内容を採用
- `--ours`: 現在のブランチ（`feature/migrate-to-postgresql`）の内容を採用

---

### ステップ9: 解決したファイルをステージング

競合を解決したファイルは、`git add` でステージングエリアに追加します：

```bash
git add .env.example
git add src/test/java/com/example/demo/UserServiceImplTest.java
```

---

### ステップ10: 競合解決の状態を確認

```bash
git status
```

**出力例**:
```
On branch feature/migrate-to-postgresql
All conflicts fixed but you are still merging.
  (use "git commit" to conclude merge)

Changes to be committed:
	modified:   .env.example
	modified:   .gitignore
	deleted:    .gradle/...
	deleted:    build/...
	new file:   src/main/java/.../UserSettingsApiController.java
	modified:   src/test/java/.../UserServiceImplTest.java
```

**確認ポイント**:
- ✅ `All conflicts fixed` - 全ての競合が解決された
- ✅ `Changes to be committed` - コミット予定の変更が表示される

---

### ステップ11: マージをコミット

```bash
git commit -m "Merge feature/user-settings into feature/migrate-to-postgresql

統合内容:
- ユーザー設定変更機能を実装
- OAuth認証情報を環境変数に移行
- .env.exampleを統合（データベース設定 + OAuth2設定）

競合解決:
- .env.example: 両ブランチの内容を統合
- .gradle/build: 自動生成ファイルを削除
- UserServiceImplTest: feature/user-settingsの内容を採用"
```

---

### ステップ12: マージ完了の確認

```bash
git log --oneline --graph -5
```

**出力例**:
```
*   be7eea7 (HEAD) Merge feature/user-settings into feature/migrate-to-postgresql
|\
| * 7549052 ユーザー設定変更機能実装
* | f7fe209 claude code用のプロンプト・ドキュメント作成
* | e825115 H2からPostgresqlとH2の併用しての運用に変更
|/
* e3521e5 CLAUDE.mdのファイルの内容を変更
```

**確認ポイント**:
- ✅ マージコミット（`be7eea7`）が作成されている
- ✅ 2つのブランチが統合されている（グラフが合流している）

---

### ステップ13: 追加改善（.gitignore の修正）

自動生成ファイルが将来も競合しないように、`.gitignore` に追加します：

```bash
# .gitignore に .gradle/ を追加（エディタで編集）
echo ".gradle/" >> .gitignore

# コミット
git add .gitignore
git commit -m "Add .gradle/ to .gitignore"
```

---

### ステップ14: 既存の自動生成ファイルを除外

```bash
# 既にGit管理下にある .gradle/ と build/ を除外
git rm -r --cached .gradle/ build/

# コミット
git commit -m "Remove .gradle/ and build/ from Git tracking"
```

**`--cached` の意味**: ファイルはディスク上に残し、Git管理からのみ除外する

---

### ステップ15: 最終確認

```bash
git status
```

**出力例**:
```
On branch feature/migrate-to-postgresql
Your branch is ahead of 'origin/feature/migrate-to-postgresql' by 4 commits.
  (use "git push" to publish your local commits)

nothing to commit, working tree clean
```

**確認ポイント**:
- ✅ `nothing to commit, working tree clean` - 作業ツリーがクリーン
- ✅ ローカルブランチがリモートより4コミット先行

---

### ステップ16: リモートにプッシュ

```bash
git push origin feature/migrate-to-postgresql
```

---

## ❓ よくある質問（FAQ）

### Q1. マージを途中で中止したい場合は？

```bash
git merge --abort
```

これで、マージ開始前の状態に戻ります。

---

### Q2. どちらのブランチの内容を採用すべきか分からない

**判断基準**:

| 状況 | 採用すべき内容 |
|------|--------------|
| 両方必要 | 両方の内容を統合（手動編集） |
| 片方が明らかに新しい | 新しい方を採用 |
| 自動生成ファイル | 削除して再生成 |
| 設定ファイル | 環境に応じて判断 |

**コマンド**:
```bash
# 現在のブランチの内容を採用
git checkout --ours ファイル名

# マージ元ブランチの内容を採用
git checkout --theirs ファイル名
```

---

### Q3. 競合ファイルが多すぎて対処しきれない

**対策**:
1. まず重要なファイルから解決する
2. 自動生成ファイルは削除する
3. 必要なら `git merge --abort` で中止してブランチを整理

---

### Q4. マージコミット後に間違いに気づいた

**直前のマージを取り消す**:
```bash
git reset --hard HEAD~1
```

**注意**: コミット後にリモートにプッシュした場合は、この方法は使えません。

---

### Q5. 競合マーカーを削除し忘れた

コンパイルエラーになるので、エディタで検索して削除：

```bash
# 競合マーカーを検索
grep -r "<<<<<<< HEAD" .
grep -r "=======" .
grep -r ">>>>>>>" .
```

---

## 🚨 トラブルシューティング

### 問題1: `git add` しても競合が解決されない

**原因**: ファイル内に競合マーカー（`<<<<<<<`, `=======`, `>>>>>>>`）が残っている

**解決策**:
```bash
# ファイルを開いて競合マーカーを全て削除
vim ファイル名

# 再度 git add
git add ファイル名
```

---

### 問題2: マージ後にビルドエラーが発生

**原因**: 競合解決時にコードの整合性が失われた

**解決策**:
```bash
# ビルドを実行してエラーを確認
./gradlew clean build

# エラー箇所を修正して再コミット
git add .
git commit --amend
```

---

### 問題3: .gitignore に追加したのに追跡される

**原因**: 既にGit管理下にあるファイルは `.gitignore` に追加しても無視されない

**解決策**:
```bash
# Git管理から除外（ファイルは残る）
git rm --cached ファイル名

# コミット
git commit -m "Remove ファイル名 from Git tracking"
```

---

## 📚 参考資料

### Git公式ドキュメント
- [Git - Basic Branching and Merging](https://git-scm.com/book/en/v2/Git-Branching-Basic-Branching-and-Merging)
- [Git - Advanced Merging](https://git-scm.com/book/en/v2/Git-Tools-Advanced-Merging)

### 推奨ツール
- **VSCode**: Git統合機能で視覚的に競合解決
- **GitKraken**: グラフィカルなGitクライアント
- **Meld**: 差分表示・マージツール

### コマンドチートシート

| コマンド | 説明 |
|---------|------|
| `git status` | 現在の状態を確認 |
| `git merge <ブランチ名>` | ブランチをマージ |
| `git merge --abort` | マージを中止 |
| `git checkout --ours <ファイル>` | 現在のブランチの内容を採用 |
| `git checkout --theirs <ファイル>` | マージ元の内容を採用 |
| `git add <ファイル>` | 競合解決したファイルをステージング |
| `git commit` | マージをコミット |
| `git rm -f <ファイル>` | ファイルを削除してステージング |
| `git rm --cached <ファイル>` | Git管理から除外（ファイルは残す） |
| `git log --oneline --graph` | コミット履歴をグラフ表示 |

---

## 📝 まとめ

### 競合解決の基本ステップ

1. ✅ **マージ実行**: `git merge <ブランチ名>`
2. ✅ **競合確認**: `git status` で競合ファイルを確認
3. ✅ **競合分類**: 重要なファイル vs 自動生成ファイル
4. ✅ **手動解決**: エディタで競合マーカーを削除して統合
5. ✅ **ステージング**: `git add` で解決済みファイルを追加
6. ✅ **コミット**: `git commit` でマージを完了
7. ✅ **確認**: `git log` でマージが成功したか確認
8. ✅ **プッシュ**: `git push` でリモートに反映

### 重要なポイント

- 🔴 **競合マーカーは必ず削除**
- 🟡 **自動生成ファイルは削除してOK**
- 🟢 **重要なファイルは慎重に統合**
- 🔵 **マージ後は必ずビルド・テストを実行**

---

**作成者**: Claude Code
**最終更新**: 2025-10-20
**バージョン**: 1.0.0
