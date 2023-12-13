package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class TourokuController {

	@Autowired
	JdbcTemplate jdbcTemplate;

	//(ページ表示用メソッド)
	@RequestMapping(path = "/touroku", method = RequestMethod.GET)
	public String tourokuGet() {
		return "touroku";
	}

	// 新規登録メソッド
	@RequestMapping(path = "/touroku", method = RequestMethod.POST)
	public String mictodo_add(String useID, String pass, String name, String mail, Model model) {

		//DBに繋ぐならこんな感じ(JdbcTemplate)
		jdbcTemplate.update("INSERT INTO 社員 VALUES (?,?,?,?);",useID,pass,name,mail);

		model.addAttribute("useID", useID);
		model.addAttribute("password", pass);
		model.addAttribute("name", name);
		model.addAttribute("mail", mail);

		return "touroku";
	}

}
