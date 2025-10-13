package com.example.demo.app.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = { "http://localhost:3000", "http://127.0.0.1:3000" }, allowCredentials = "true")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        try {
            // 認証処理
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email, request.password));

            // セッションに認証情報を保存
            SecurityContextHolder.getContext().setAuthentication(authentication);
            HttpSession session = httpRequest.getSession(true);
            session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

            return ResponseEntity.ok().body(new LoginResponse("success", "ログイン成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new LoginResponse("error", "ログインに失敗しました: " + e.getMessage()));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            // ユーザーが既に存在するかチェック
            if (userService.findByEmail(request.email) != null) {
                return ResponseEntity.badRequest().body(new RegisterResponse("error", "このメールアドレスは既に登録されています"));
            }

            // 新しいユーザーを作成
            User user = new User();
            user.setEmail(request.email);
            user.setPassword(passwordEncoder.encode(request.password));
            user.setUsername(request.name);

            userService.save(user);

            return ResponseEntity.ok().body(new RegisterResponse("success", "登録成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new RegisterResponse("error", "登録に失敗しました: " + e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        try {
            // セッションを無効化
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            SecurityContextHolder.clearContext();

            return ResponseEntity.ok().body(new LogoutResponse("success", "ログアウト成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new LogoutResponse("error", "ログアウトに失敗しました: " + e.getMessage()));
        }
    }

    @GetMapping("/oauth2/success")
    public void oauth2Success(@AuthenticationPrincipal OAuth2User oauth2User,
            HttpServletRequest httpRequest, HttpServletResponse response) throws Exception {
        try {
            // OAuth2Userから情報を取得
            String email = oauth2User.getAttribute("email");
            String name = oauth2User.getAttribute("name");
            String googleId = oauth2User.getAttribute("sub");

            System.out.println("=== OAuth2Success処理開始 ===");
            System.out.println("Email: " + email);
            System.out.println("Name: " + name);
            System.out.println("Google ID: " + googleId);

            if (email == null) {
                System.out.println("ERROR: Email is null");
                response.sendRedirect("http://localhost:3000/users/login?error=no_email");
                return;
            }

            // ユーザーが既に存在するかチェック
            User existingUser = userService.findByEmail(email);
            if (existingUser == null) {
                // メールアドレスでの検索で見つからない場合、Google IDでも検索
                if (googleId != null) {
                    existingUser = userService.findByGoogleId(googleId);
                }
                
                if (existingUser == null) {
                    // 完全新規ユーザーを作成
                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setUsername(name != null ? name : email);
                    // Google認証ユーザーはランダムなパスワードを設定
                    newUser.setPassword(passwordEncoder.encode("GOOGLE_AUTH_" + System.currentTimeMillis()));
                    newUser.setGoogleId(googleId);
                    userService.save(newUser);
                    System.out.println("新規Googleユーザー作成: " + email);
                } else {
                    // Google IDで見つかった場合、メールアドレスを更新
                    existingUser.setEmail(email);
                    existingUser.setUsername(name != null ? name : email);
                    userService.save(existingUser);
                    System.out.println("既存GoogleユーザーのEmail更新: " + email);
                }
            } else {
                // 既存ユーザーの場合、Google IDを更新（初回Google認証の場合）
                if (existingUser.getGoogleId() == null || !existingUser.getGoogleId().equals(googleId)) {
                    existingUser.setGoogleId(googleId);
                    existingUser.setUsername(name != null ? name : existingUser.getUsername());
                    userService.save(existingUser);
                    System.out.println("既存ユーザーにGoogle ID追加/更新: " + email);
                } else {
                    System.out.println("既存Googleユーザーログイン: " + email);
                }
            }

            // 現在の認証情報をセッションに保存（OAuth2認証は既に完了している）
            Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();
            HttpSession session = httpRequest.getSession(true);
            session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
            
            System.out.println("認証情報をセッションに保存: " + currentAuth.getName());
            System.out.println("ユーザー処理完了、フロントエンドにリダイレクト");
            
            // フロントエンドにリダイレクト
            response.sendRedirect("http://localhost:3000/tasks");
        } catch (Exception e) {
            System.out.println("OAuth2Success処理でエラー: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect("http://localhost:3000/users/login?error=oauth2_failed");
        }
    }

    @GetMapping("/status")
    public ResponseEntity<?> getAuthStatus(Authentication authentication, HttpServletRequest request) {
        try {
            // Spring Securityの認証状態をチェック
            if (authentication != null && authentication.isAuthenticated()) {
                return ResponseEntity.ok().body(new AuthStatusResponse(true, "認証済み"));
            }

            // OAuth2認証の場合はSpring Securityが自動的に認証状態を管理

            return ResponseEntity.ok().body(new AuthStatusResponse(false, "未認証"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new AuthStatusResponse(false, "認証状態の確認に失敗しました: " + e.getMessage()));
        }
    }

    // 内部クラスでリクエスト・レスポンス用のDTOを定義

    public static class LoginRequest {
        public String email;
        public String password;
    }

    public static class RegisterRequest {
        public String email;
        public String password;
        public String name;
    }

    public static class LoginResponse {
        public String status;
        public String message;

        public LoginResponse(String status, String message) {
            this.status = status;
            this.message = message;
        }
    }

    public static class RegisterResponse {
        public String status;
        public String message;

        public RegisterResponse(String status, String message) {
            this.status = status;
            this.message = message;
        }
    }

    public static class LogoutResponse {
        public String status;
        public String message;

        public LogoutResponse(String status, String message) {
            this.status = status;
            this.message = message;
        }
    }

    public static class AuthStatusResponse {
        public boolean authenticated;
        public String message;

        public AuthStatusResponse(boolean authenticated, String message) {
            this.authenticated = authenticated;
            this.message = message;
        }
    }
}
