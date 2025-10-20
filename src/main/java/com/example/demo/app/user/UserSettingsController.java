package com.example.demo.app.user;

import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

/**
 * ユーザー設定変更コントローラー
 */
@Controller
@RequestMapping("/user/settings")
public class UserSettingsController {

    @Autowired
    private UserService userService;

    /**
     * ユーザー設定画面を表示
     */
    @GetMapping
    public String showSettings(Model model, Authentication authentication) {
        String email = authentication.getName();
        User user = userService.findByEmail(email);

        if (user == null) {
            return "redirect:/task";
        }

        UserSettingsForm form = new UserSettingsForm(user.getEmail(), user.getUsername());
        model.addAttribute("userSettingsForm", form);
        model.addAttribute("title", "ユーザー設定変更");

        return "users/settings";
    }

    /**
     * 確認画面へ遷移（バリデーション実行）
     */
    @PostMapping("/confirm")
    public String confirmSettings(
            @Valid @ModelAttribute UserSettingsForm userSettingsForm,
            BindingResult result,
            Model model,
            Authentication authentication) {

        // パスワード変更の場合のバリデーション
        if (userSettingsForm.isChangePassword()) {
            if (userSettingsForm.getPassword() == null || userSettingsForm.getPassword().isEmpty()) {
                result.rejectValue("password", "error.password", "パスワードを入力してください");
            }
            if (userSettingsForm.getPasswordConfirm() == null || userSettingsForm.getPasswordConfirm().isEmpty()) {
                result.rejectValue("passwordConfirm", "error.passwordConfirm", "確認用パスワードを入力してください");
            }
            if (userSettingsForm.getPassword() != null && userSettingsForm.getPasswordConfirm() != null
                    && !userSettingsForm.getPassword().equals(userSettingsForm.getPasswordConfirm())) {
                result.rejectValue("passwordConfirm", "error.passwordConfirm", "パスワードが一致しません");
            }
        }

        // メールアドレス重複チェック（自分以外）
        String currentEmail = authentication.getName();
        if (!currentEmail.equals(userSettingsForm.getEmail())) {
            if (userService.existsByEmail(userSettingsForm.getEmail())) {
                result.rejectValue("email", "error.email", "このメールアドレスは既に使用されています");
            }
        }

        if (result.hasErrors()) {
            model.addAttribute("title", "ユーザー設定変更 ※エラーを確認してください");
            return "users/settings";
        }

        model.addAttribute("userSettingsForm", userSettingsForm);
        model.addAttribute("title", "ユーザー設定変更確認");

        return "users/settings-confirm";
    }

    /**
     * 設定を確定してDB更新
     */
    @PostMapping("/update")
    public String updateSettings(
            @ModelAttribute UserSettingsForm userSettingsForm,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        String email = authentication.getName();
        User user = userService.findByEmail(email);

        if (user == null) {
            return "redirect:/task";
        }

        // パスワードの処理
        String newPassword = null;
        if (userSettingsForm.isChangePassword() && userSettingsForm.getPassword() != null) {
            newPassword = userSettingsForm.getPassword();
        }

        // ユーザー設定を更新
        userService.updateUserSettings(
                user.getId(),
                userSettingsForm.getEmail(),
                userSettingsForm.getUsername(),
                newPassword
        );

        redirectAttributes.addFlashAttribute("complete", "ユーザー設定を変更しました");
        return "redirect:/task";
    }

    /**
     * 設定変更をキャンセルして戻る
     */
    @GetMapping("/cancel")
    public String cancel() {
        return "redirect:/task";
    }
}
