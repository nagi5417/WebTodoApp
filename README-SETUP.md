# Spring Boot + React 分割アーキテクチャ セットアップ

## プロジェクト構成

```
WebToDoStarter/
├── src/                    # Spring Boot バックエンド
│   └── main/java/com/example/demo/
│       ├── app/api/        # REST API コントローラー
│       └── config/         # CORS設定など
└── frontend/               # React フロントエンド
    ├── src/
    │   ├── api/           # API クライアント
    │   └── components/    # React コンポーネント
    └── package.json
```

## 起動方法

### 1. Spring Boot バックエンドの起動

```bash
# プロジェクトルートで実行
./gradlew bootRun
```

バックエンドは `http://localhost:8080` で起動します。

### 2. React フロントエンドの起動

```bash
# frontendディレクトリで実行
cd frontend
npm start
```

フロントエンドは `http://localhost:3000` で起動します。

## API エンドポイント

- `POST /api/auth/login` - ログイン
- `POST /api/auth/register` - ユーザー登録
- `POST /api/auth/logout` - ログアウト

## 開発の流れ

1. 両方のサーバーを起動
2. ブラウザで `http://localhost:3000` にアクセス
3. ログインフォームでテスト

## 注意事項

- CORS 設定により、フロントエンド（3000 番ポート）からバックエンド（8080 番ポート）への通信が許可されています
- 現在はダミーレスポンスを返す API になっています
- 実際の認証機能を実装する場合は、Spring Security の設定を調整してください
