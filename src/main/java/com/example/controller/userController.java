package com.example.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.form.UserForm;

/**
 * 通常であればDomain, Service, Repositoryを作成する必要があるが
 * コードが長くなりスライドに映りきらない為、ControllerとFormにて作成 コードを簡略化する為にtokenへの値は文字列を代入しているが
 * 実際はランダムに生成した値を代入することが多い。 会員登録を制御するコントローラ.
 * 
 * @author hatakeyamakouta
 *
 */
@Controller
@RequestMapping("/presentation")
public class userController {

	@Autowired
	private HttpSession session;

	@Autowired
	private NamedParameterJdbcTemplate template;

	/**
	 * セッションスコープの"token"に空白を代入し、 TOP画面にあたる登録フォームの画面に遷移させる.
	 * 
	 * @return 登録フォーム
	 */
	@RequestMapping("")
	public String index() {
		session.setAttribute("token", " ");
		return "input-form";
	}

	/**
	 * セッションスコープの値が"finish"ではない場合、"checkNumber"という値を代入する。
	 * userFormのフィールド変数tokenに"checkNumber"を代入する。
	 * userFormの値をリクエストスコープにセットし、入力内容確認画面に遷移させる.
	 * 
	 * @param userForm name, emailに値がセットされているuserForm
	 * @param model    リクエストスコープに値を代入するために引数に配置
	 * @return 入力内容確認画面
	 */
	@RequestMapping("/check")
	public String check(UserForm userForm, Model model) {
		session.setAttribute("token", "checkNumber");
		userForm.setToken("checkNumber");
		model.addAttribute("userForm", userForm);
		return "check";
	}

	/**
	 * セッションスコープとuserFormの"token"に入っている値を比較し
	 * 同じ場合はデータベース登録処理を行い完了画面へ、異なる場合はエラーページに遷移させる.
	 * 
	 * @param userForm email, name, tokenに値がセットされているuserForm
	 * @return エラーページ、もしくは登録完了ページ
	 */
	@RequestMapping("/finish")
	public String finish(UserForm userForm) {
		if (!session.getAttribute("token").equals(userForm.getToken())) {
			return "error";
		}
		String sql = "INSERT INTO presentations(name, email) VALUES(:name, :email)";
		SqlParameterSource param = new MapSqlParameterSource().addValue("name", userForm.getName()).addValue("email",
				userForm.getEmail());
		template.update(sql, param);
		session.setAttribute("token", "finish");
		return "redirect:/presentation/to-finish";
	}

	/**
	 * 登録完了画面へリダイレクトする.
	 * 
	 * @return 登録完了画面
	 */
	@RequestMapping("to-finish")
	public String toFinish() {
		return "finish";
	}

}
