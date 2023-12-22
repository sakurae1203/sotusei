package com.example.demo.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpSession;

@Controller
public class Login1Controller {

	@Autowired
	JdbcTemplate jdbcTemplate;

	//ScripteEnginManagerインスタンスを作成
	ScriptEngineManager factory = new ScriptEngineManager();
	//ScripteEngineManagerインスタンスのgetEngineByNameメソッドでJSのスクリプトエンジンを指定
	ScriptEngine engine = factory.getEngineByName("JavaScript");

	String p = "C:/Users/S0513/git/localrepositorysotusei/Sotusei/src/main/resources/static/js/login1.js";
	File file1 = new File(p);

	//(ページ表示用メソッド)
	@RequestMapping(path = "/login1", method = RequestMethod.GET)
	public String useridGet() {
		return "login1";
	}

	// ログインメソッド
	@RequestMapping(path = "/login1", method = RequestMethod.POST)
	public String login1(String useID, String pass, HttpSession session) throws ScriptException, FileNotFoundException {

		//DBに繋ぐならこんな感じ(JdbcTemplate)
		List<Map<String, Object>> resultList = jdbcTemplate.queryForList("SELECT * FROM 社員 WHERE userID = ?", useID);

		if (resultList.size() > 0 && resultList.get(0).get("password").equals(pass)) {

			session.setAttribute("resultList", resultList);

			//return "redirect:/login2";

			System.out.println("成功");
			return "login1";
		} else {

			try {
				engine.eval(new java.io.FileReader(file1));
			} catch (FileNotFoundException e) {
				// TODO 自動生成された catch ブロック
				throw e;
			} catch (ScriptException e) {
				// TODO 自動生成された catch ブロック
				throw e;
			}

			System.out.println("失敗");

			return "login1";
		}
	}

}
