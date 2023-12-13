package com.example.demo.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class Login1Controller {

	@Autowired
	JdbcTemplate jdbcTemplate;

	//(ページ表示用メソッド)
	@RequestMapping(path = "/login1", method = RequestMethod.GET)
	public String useridGet() {
		return "mictodo_add";
	}

	// ログインメソッド
	@RequestMapping(path = "/login1", method = RequestMethod.POST)
	public String mictodo_add(String useID, String pass, Model model) {

		//DBに繋ぐならこんな感じ(JdbcTemplate)
		List<Map<String, Object>> resultList = jdbcTemplate.queryForList("SELECT * FROM 社員 WHERE useID = ?",useID);
		
		Map<String, Object> password = resultList.get(1);
		
		model.addAttribute("useID", useID);
		model.addAttribute("password", pass);
		
		if(password.equals(pass)) {
			return "redirect:/login2";
		}else {
			String javascriptPath = "drive:/js/login1.js";
			return "login1";
		}
		
	}

}
