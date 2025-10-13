package com.example.demo.app.task;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.entity.Task;
import com.example.demo.service.TaskService;
import com.example.demo.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;

/**
 * ToDoアプリ
 */
@Controller
@RequestMapping("/task")
public class TaskController {

    private final TaskService taskService;
    private final UserService userService;

    public TaskController(TaskService taskService, UserService userService) {
        this.taskService = taskService;
        this.userService = userService;
    }

    /**
     * タスクの一覧を表示します
     * 
     * @param taskForm タスクフォームの入力内容
     * @param model    HTMLに渡すデータオブジェクト
     * @return resources/templates下のHTMLファイル名
     */
    @GetMapping
    public String task(TaskForm taskForm, Model model, Authentication authentication) {
        taskForm.setNewTask(true);

        // ログインユーザーのメールアドレス取得
        String email = authentication.getName();
        System.out.println("認証メールアドレス: " + email);

        // メールアドレスからユーザーIDを取得
        Long userId = userService.getUserIdByEmail(email);
        System.out.println("取得したユーザーID: " + userId);

        if (userId == null) {
            System.out.println("ユーザーIDがnullです！");
            // 空のリストを返す
            model.addAttribute("list", new ArrayList<>());
            model.addAttribute("title", "タスク一覧（ユーザーIDが見つかりません）");
            return "task/index";
        }

        List<Task> list = taskService.findAll(userId.intValue());
        System.out.println("タスク件数: " + list.size());

        model.addAttribute("list", list);
        model.addAttribute("title", "タスク一覧");
        return "task/index";
    }

    /**
     * タスクデータを一件挿入
     * 
     * @param taskForm タスクフォームの入力内容
     * @param result   バリデーション結果
     * @param model    HTMLに渡すデータオブジェクト
     * @return resources/templates下のHTMLファイル名
     */
    @PostMapping("/insert")
    @Transactional
    public String insert(
            @Valid @ModelAttribute TaskForm taskForm,
            BindingResult result,
            Model model,
            Authentication authentication) {

        // ログインユーザーのIDを取得
        String email = authentication.getName();
        Long userId = userService.getUserIdByEmail(email);

        Task task = makeTask(taskForm, 0, userId.intValue());

        if (!result.hasErrors()) {

            taskService.insert(task);
            return "redirect:/task";
        } else {
            taskForm.setNewTask(true);
            model.addAttribute("taskForm", taskForm);
            List<Task> list = taskService.findAll(userId.intValue()); // 既に取得済みのuserIdを使用
            model.addAttribute("list", list);
            model.addAttribute("title", "タスク一覧 ※エラー内容を確認してください");
            return "task/index";
        }
    }

    /**
     * 一件タスクデータを取得し、フォーム内に表示
     * 
     * @param taskForm タスクフォームの入力内容
     * @param id       URLからの入力値
     * @param model    HTMLに渡すデータオブジェクト
     * @return resources/templates下のHTMLファイル名
     */
    @GetMapping("/{id}")
    public String showUpdate(
            TaskForm taskForm,
            @PathVariable int id,
            Model model) {

        // Taskを取得
        Optional<Task> taskOpt = taskService.getTask(id);

        // TaskFormへの詰め直し
        Optional<TaskForm> taskFormOpt = taskOpt.map(t -> makeTaskForm(t));

        // TaskFormがnullでなければ中身を取り出し
        if (taskFormOpt.isPresent()) {
            taskForm = taskFormOpt.get();
        }

        model.addAttribute("taskForm", taskForm);
        List<Task> list = taskService.findAll(1); // 仮のユーザーID
        model.addAttribute("list", list);
        model.addAttribute("taskId", id);
        model.addAttribute("title", "更新用フォーム");

        return "task/index";
    }

    /**
     * タスクidを取得し、一件のデータ更新
     * 
     * @param taskForm           タスクフォームの入力内容
     * @param result             バリデーション結果
     * @param model              HTMLに渡すデータオブジェクト
     * @param redirectAttributes リダイレクト先に渡すデータオブジェクト
     * @return resources/templates下のHTMLファイル名
     */
    @PostMapping("/update")
    public String update(
            @Valid @ModelAttribute TaskForm taskForm,
            BindingResult result,
            @RequestParam("taskId") int taskId,
            Model model,
            RedirectAttributes redirectAttributes,
            Authentication authentication) {

        if (!result.hasErrors()) {
            // ログインユーザーのIDを取得
            String email = authentication.getName();
            Long userId = userService.getUserIdByEmail(email);

            Task task = makeTask(taskForm, taskId, userId.intValue());

            taskService.update(task);
            redirectAttributes.addFlashAttribute("complete", "変更が完了しました");
            return "redirect:/task/" + taskId;

        } else {
            model.addAttribute("taskForm", taskForm);
            model.addAttribute("title", "タスク一覧");
            return "task/index";
        }
    }

    /**
     * タスクidを取得し、一件のデータ削除
     * 
     * @param id
     * @param model HTMLに渡すデータオブジェクト
     * @return resources/templates下のHTMLファイル名
     */
    @PostMapping("/delete")
    public String delete(
            @RequestParam("taskId") int id,
            Model model) {

        // タスクを一件削除しリダイレクト
        taskService.deleteById(id);
        return "redirect:/task";
    }

    /**
     * 複製用に一件タスクデータを取得し、フォーム内に表示
     * 
     * @param taskForm タスクフォームの入力内容
     * @param id
     * @param model    HTMLに渡すデータオブジェクト
     * @return resources/templates下のHTMLファイル名
     */

    public String duplicate(
            TaskForm taskForm,
            int id,
            Model model,
            Authentication authentication) {

        Optional<Task> taskOpt = Optional.empty();

        Optional<TaskForm> taskFormOpt = taskOpt.map(t -> makeTaskForm(t));

        if (taskFormOpt.isPresent()) {
            taskForm = taskFormOpt.get();
        }

        taskForm.setNewTask(true);

        model.addAttribute("taskForm", taskForm);

        // ログインユーザーのIDを取得
        String email = authentication.getName();
        Long userId = userService.getUserIdByEmail(email);
        List<Task> list = taskService.findAll(userId != null ? userId.intValue() : 1);
        model.addAttribute("list", list);
        model.addAttribute("title", "タスク一覧");

        return "task/index";
    }

    /**
     * 選択したタスクタイプのタスク一覧を表示
     * 
     * @param taskForm タスクフォームの入力内容
     * @param id
     * @param model    HTMLに渡すデータオブジェクト
     * @return HTMLに渡すデータオブジェクト
     */
    public String selectType(
            TaskForm taskForm,
            int id,
            Model model) {

        // 新規登録か更新かを判断する仕掛け
        taskForm.setNewTask(true);

        List<Task> list = null;

        model.addAttribute("list", list);
        model.addAttribute("title", "タスク一覧");

        return "task/index";
    }

    /**
     * TaskFormのデータをTaskに入れて返す
     * 
     * @param taskForm タスクフォームの入力内容
     * @param taskId   新規登録の場合は0を指定
     * @return Entityオブジェクト
     */
    private Task makeTask(TaskForm taskForm, int taskId, int userId) {
        Task task = new Task();
        if (taskId != 0) {
            task.setId(taskId);
        }
        task.setUserId(userId);
        task.setTypeId(taskForm.getTypeId());
        task.setTitle(taskForm.getTitle());
        task.setDetail(taskForm.getDetail());
        task.setDeadline(taskForm.getDeadline());
        return task;
    }

    /**
     * TaskのデータをTaskFormに入れて返す
     * 
     * @param task Entityオブジェクト
     * @return Formオブジェクト
     */
    private TaskForm makeTaskForm(Task task) {

        TaskForm taskForm = new TaskForm();

        taskForm.setTypeId(task.getTypeId());
        taskForm.setTitle(task.getTitle());
        taskForm.setDetail(task.getDetail());
        taskForm.setDeadline(task.getDeadline());
        taskForm.setNewTask(false);

        return taskForm;
    }
}