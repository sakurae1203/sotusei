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
	public String touroku(String useID, String pass, String name, String mail, Model model) {

		List<Map<String, Object>> resultList = jdbcTemplate.queryForList("SELECT * FROM 社員 WHERE userID = ?", useID);
		
		if (resultList.size()<1) {
			//DBに繋ぐならこんな感じ(JdbcTemplate)
			jdbcTemplate.update("INSERT INTO 社員 VALUES (?,?,?,?);", useID, pass, name, mail);
			jdbcTemplate.update("INSERT INTO ワンタイム (mail) VALUES (?);", mail);
			jdbcTemplate.update("INSERT INTO 残業時間 (userID) VALUES (?);", useID);
			jdbcTemplate.update("INSERT INTO 出勤 (userID) VALUES (?);", useID);
			jdbcTemplate.update("INSERT INTO 出退勤 (userID) VALUES (?);", useID);
			jdbcTemplate.update("INSERT INTO 有給 (userID) VALUES (?);", useID);
			return "redirect:/login1";
		} else {
			return "touroku";
		}
	}

}
