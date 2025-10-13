package com.example.demo.app.api;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.example.demo.entity.Task;
import com.example.demo.service.TaskService;
import com.example.demo.service.UserService;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = { "http://localhost:3000", "http://127.0.0.1:3000" })
public class TaskApiController {

    private final TaskService taskService;
    private final UserService userService;

    public TaskApiController(TaskService taskService, UserService userService) {
        this.taskService = taskService;
        this.userService = userService;
    }

    /**
     * タスク一覧を取得
     */
    @GetMapping
    public ResponseEntity<?> getTasks(Authentication authentication) {
        try {
            Long userId = getUserIdFromAuthentication(authentication);

            if (userId == null) {
                return ResponseEntity.badRequest().body(new ApiResponse("error", "ユーザーが見つかりません"));
            }

            List<Task> tasks = taskService.findAll(userId.intValue());
            return ResponseEntity.ok().body(new ApiResponse("success", "タスク一覧を取得しました", tasks));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse("error", "タスクの取得に失敗しました: " + e.getMessage()));
        }
    }

    /**
     * タスクを一件取得
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getTask(@PathVariable int id, Authentication authentication) {
        try {
            Long userId = getUserIdFromAuthentication(authentication);

            if (userId == null) {
                return ResponseEntity.badRequest().body(new ApiResponse("error", "ユーザーが見つかりません"));
            }

            Optional<Task> taskOpt = taskService.getTask(id);
            if (taskOpt.isPresent()) {
                Task task = taskOpt.get();
                // ユーザーIDの確認
                if (task.getUserId() != userId.intValue()) {
                    return ResponseEntity.badRequest().body(new ApiResponse("error", "アクセス権限がありません"));
                }
                return ResponseEntity.ok().body(new ApiResponse("success", "タスクを取得しました", task));
            } else {
                return ResponseEntity.badRequest().body(new ApiResponse("error", "タスクが見つかりません"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse("error", "タスクの取得に失敗しました: " + e.getMessage()));
        }
    }

    /**
     * タスクを作成
     */
    @PostMapping
    @Transactional
    public ResponseEntity<?> createTask(@Valid @RequestBody TaskRequest request, Authentication authentication) {
        try {
            Long userId = getUserIdFromAuthentication(authentication);

            if (userId == null) {
                return ResponseEntity.badRequest().body(new ApiResponse("error", "ユーザーが見つかりません"));
            }

            Task task = new Task();
            task.setUserId(userId.intValue());
            task.setTypeId(request.getTypeId());
            task.setTitle(request.getTitle());
            task.setDetail(request.getDetail());
            task.setDeadline(parseDeadline(request.getDeadline()));

            taskService.insert(task);
            return ResponseEntity.ok().body(new ApiResponse("success", "タスクを作成しました", task));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse("error", "タスクの作成に失敗しました: " + e.getMessage()));
        }
    }

    /**
     * タスクを更新
     */
    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<?> updateTask(@PathVariable int id, @Valid @RequestBody TaskRequest request,
            Authentication authentication) {
        try {
            Long userId = getUserIdFromAuthentication(authentication);

            if (userId == null) {
                return ResponseEntity.badRequest().body(new ApiResponse("error", "ユーザーが見つかりません"));
            }

            Optional<Task> taskOpt = taskService.getTask(id);
            if (taskOpt.isPresent()) {
                Task task = taskOpt.get();
                // ユーザーIDの確認
                if (task.getUserId() != userId.intValue()) {
                    return ResponseEntity.badRequest().body(new ApiResponse("error", "アクセス権限がありません"));
                }

                task.setTypeId(request.getTypeId());
                task.setTitle(request.getTitle());
                task.setDetail(request.getDetail());
                task.setDeadline(parseDeadline(request.getDeadline()));

                taskService.update(task);
                return ResponseEntity.ok().body(new ApiResponse("success", "タスクを更新しました", task));
            } else {
                return ResponseEntity.badRequest().body(new ApiResponse("error", "タスクが見つかりません"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse("error", "タスクの更新に失敗しました: " + e.getMessage()));
        }
    }

    /**
     * タスクを削除
     */
    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<?> deleteTask(@PathVariable int id, Authentication authentication) {
        try {
            Long userId = getUserIdFromAuthentication(authentication);

            if (userId == null) {
                return ResponseEntity.badRequest().body(new ApiResponse("error", "ユーザーが見つかりません"));
            }

            Optional<Task> taskOpt = taskService.getTask(id);
            if (taskOpt.isPresent()) {
                Task task = taskOpt.get();
                // ユーザーIDの確認
                if (task.getUserId() != userId.intValue()) {
                    return ResponseEntity.badRequest().body(new ApiResponse("error", "アクセス権限がありません"));
                }

                taskService.deleteById(id);
                return ResponseEntity.ok().body(new ApiResponse("success", "タスクを削除しました"));
            } else {
                return ResponseEntity.badRequest().body(new ApiResponse("error", "タスクが見つかりません"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse("error", "タスクの削除に失敗しました: " + e.getMessage()));
        }
    }

    // リクエスト用のDTO
    public static class TaskRequest {
        private int typeId;
        private String title;
        private String detail;
        private String deadline;

        // Getters and Setters
        public int getTypeId() {
            return typeId;
        }

        public void setTypeId(int typeId) {
            this.typeId = typeId;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDetail() {
            return detail;
        }

        public void setDetail(String detail) {
            this.detail = detail;
        }

        public String getDeadline() {
            return deadline;
        }

        public void setDeadline(String deadline) {
            this.deadline = deadline;
        }
    }

    // ヘルパーメソッド: String を LocalDateTime に変換
    private LocalDateTime parseDeadline(String deadline) {
        if (deadline == null || deadline.isEmpty()) {
            return null;
        }
        try {
            return LocalDateTime.parse(deadline + "T00:00:00");
        } catch (Exception e) {
            return null;
        }
    }

    // ヘルパーメソッド: 認証情報からユーザーIDを取得
    private Long getUserIdFromAuthentication(Authentication authentication) {
        if (authentication instanceof org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken) {
            // OAuth2認証の場合
            org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken oauth2Token = 
                (org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken) authentication;
            org.springframework.security.oauth2.core.user.OAuth2User oauth2User = oauth2Token.getPrincipal();
            String email = oauth2User.getAttribute("email");
            if (email != null) {
                try {
                    return userService.getUserIdByEmail(email);
                } catch (Exception e) {
                    return null;
                }
            }
        } else {
            // 通常のフォーム認証の場合
            String email = authentication.getName();
            try {
                return userService.getUserIdByEmail(email);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    // レスポンス用のDTO
    public static class ApiResponse {
        private String status;
        private String message;
        private Object data;

        public ApiResponse(String status, String message) {
            this.status = status;
            this.message = message;
        }

        public ApiResponse(String status, String message, Object data) {
            this.status = status;
            this.message = message;
            this.data = data;
        }

        // Getters and Setters
        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Object getData() {
            return data;
        }

        public void setData(Object data) {
            this.data = data;
        }
    }
}
