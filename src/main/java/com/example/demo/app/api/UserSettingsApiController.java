package com.example.demo.app.api;

import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * ユーザー設定API
 */
@RestController
@RequestMapping("/api/user/settings")
@CrossOrigin(origins = "*")
public class UserSettingsApiController {

    @Autowired
    private UserService userService;

    /**
     * 現在のユーザー情報を取得
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getCurrentUser(Authentication authentication) {
        try {
            User user = getUserFromAuthentication(authentication);

            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "User not found"));
            }

            Map<String, Object> response = new HashMap<>();
            response.put("id", user.getId());
            response.put("email", user.getEmail());
            response.put("username", user.getUsername());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * ユーザー設定を更新
     * 指定されたフィールドのみ更新（nullまたは空のフィールドは更新しない）
     */
    @PutMapping
    public ResponseEntity<Map<String, Object>> updateSettings(
            @RequestBody UserSettingsRequest request,
            Authentication authentication) {
        try {
            User user = getUserFromAuthentication(authentication);

            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "User not found"));
            }

            String currentEmail = user.getEmail();

            // メールアドレス重複チェック（変更がある場合のみ、かつ自分以外）
            if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
                if (!currentEmail.equals(request.getEmail())) {
                    if (userService.existsByEmail(request.getEmail())) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(Map.of("error", "このメールアドレスは既に使用されています"));
                    }
                }
            }

            // パスワードの処理
            String newPassword = null;
            if (request.isChangePassword() && request.getPassword() != null && !request.getPassword().trim().isEmpty()) {
                newPassword = request.getPassword();
            }

            // ユーザー設定を部分的に更新（nullまたは空のフィールドは更新しない）
            userService.updateUserSettingsPartial(
                    user.getId(),
                    request.getEmail(),
                    request.getUsername(),
                    newPassword
            );

            Map<String, Object> response = new HashMap<>();
            response.put("message", "ユーザー設定を更新しました");
            response.put("success", true);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 認証情報からユーザーを取得
     * フォーム認証とOAuth2認証の両方に対応
     */
    private User getUserFromAuthentication(Authentication authentication) {
        if (authentication instanceof OAuth2AuthenticationToken) {
            // OAuth2認証の場合
            OAuth2AuthenticationToken oauth2Token = (OAuth2AuthenticationToken) authentication;
            OAuth2User oauth2User = oauth2Token.getPrincipal();
            String email = oauth2User.getAttribute("email");
            String googleId = oauth2User.getAttribute("sub");

            // まずGoogle IDで検索
            User user = userService.findByGoogleId(googleId);
            if (user != null) {
                return user;
            }

            // 見つからない場合はメールアドレスで検索
            if (email != null) {
                return userService.findByEmail(email);
            }

            return null;
        } else {
            // フォーム認証の場合（メールアドレスがName）
            String email = authentication.getName();
            return userService.findByEmail(email);
        }
    }

    /**
     * ユーザー設定更新リクエスト
     */
    public static class UserSettingsRequest {
        private String email;
        private String username;
        private String password;
        private boolean changePassword;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public boolean isChangePassword() {
            return changePassword;
        }

        public void setChangePassword(boolean changePassword) {
            this.changePassword = changePassword;
        }
    }
}
