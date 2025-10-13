package com.example.demo.app.user;

import com.example.demo.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;

import javax.validation.Valid;
import java.util.Objects;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    /**
     * コンストラクタによる依存関係の注入
     * 
     * @param userService ユーザー関連のビジネスロジックを提供するサービスクラス
     */
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(false));
    }

    /**
     * ユーザー登録画面を表示
     * 
     * @param model 画面に出力される内容を格納する
     * @return 処理結果に応じた遷移先のURL
     */
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("userRegistrationDto", new UserRegistrationDto());
        return "users/register";
    }

    /**
     * ユーザー情報登録処理
     * 
     * @param registrationDto    ユーザー登録情報を格納
     * @param bindingResult      バリデーションの結果を格納
     * @param redirectAttributes リダイレクト先に渡すフラッシュスコープ
     * @param model              画面に出力される内容を格納する
     * @return 処理結果に応じた遷移先のURL
     */
    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("userRegistrationDto") UserRegistrationDto registrationDto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        // パスワードと確認用パスワードの一致チェック
        if (!Objects.equals(registrationDto.getPassword(), registrationDto.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "password.mismatch", "パスワードが一致しません");
        }

        // バリデーションエラーがある場合
        if (bindingResult.hasErrors()) {
            return "users/register";
        }
        try {
            // ユーザー登録
            userService.registerUser(registrationDto);
            redirectAttributes.addFlashAttribute("successMessage", "登録が完了しました");
            logger.info("User registration completed successfully: {}", registrationDto.getEmail());
            return "redirect:/users/login";

        } catch (DataIntegrityViolationException e) {
            // データベース制約違反
            logger.error("Failed to register user: {}", registrationDto.getEmail(), e);
            model.addAttribute("errorMessage", "登録情報に問題があります");
            return "users/register";
        } catch (Exception e) {
            // その他のシステムエラー
            logger.error("Failed to register user: {}", registrationDto.getEmail(), e);
            model.addAttribute("errorMessage", "登録処理中にエラーが発生しました");
            return "users/register";
        }

    }

    /**
     * メールアドレスの重複チェックAPI
     * Ajax呼び出し用
     * 
     * @param email チェックするメールアドレス
     * @return 重複している場合true、していない場合false
     */
    @GetMapping("/api/check-email")
    @ResponseBody
    public ResponseEntity<Boolean> checkEmailDuplicate(@RequestParam String email) {
        logger.debug("Checking email duplicate: {}", email);
        boolean exists = userService.existsByEmail(email);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/login")
    public String showLoginPage(Model model) {
        return "users/login";
    }
}
