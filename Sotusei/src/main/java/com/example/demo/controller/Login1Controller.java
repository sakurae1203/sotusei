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

	// 新規登録メソッド
	@RequestMapping(path = "/login1", method = RequestMethod.POST)
	public String user(Model model,int useID, String pass) {

		//DBに繋ぐならこんな感じ(JdbcTemplate)
		List<Map<String, Object>> resultList = jdbcTemplate.queryForList("INSERT INTO VALUE(?,?)", useID,pass);

		model.addAttribute("id", useID);
		model.addAttribute("pass", pass);

		return "login1";
	}

}
