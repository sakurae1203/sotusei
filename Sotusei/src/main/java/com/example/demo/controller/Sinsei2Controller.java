package com.example.demo.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpSession;

@Controller
public class Sinsei2Controller {

	@Autowired
	JdbcTemplate jdbcTemplate;

	//(ページ表示用メソッド)
	@RequestMapping(path = "/sinsei2", method = RequestMethod.GET)
	public String syuttaikinGet(HttpSession session) {
		return "sinsei2";
	}

	// 申請日登録メソッド
	@RequestMapping(path = "/sinsei2", method = RequestMethod.POST)
	public String syukkin(String kaisi, String syuuryou, String ziyuu, Model model, HttpSession session) {

		//String x = (String) session.getAttribute("userID");
		String x = "12345";
		jdbcTemplate.update("INSERT INTO 有給 (userID,stpaid,enpaid,detail,yearpaid,totalpaid,con) VALUES(?,?,?,?,?,?,?);", x, kaisi, syuuryou, ziyuu,0,0,0);

		List<Map<String, Object>> result = jdbcTemplate.queryForList("SELECT COUNT(*) FROM 有給 WHERE userID = ?;",x);
		
		String npl = String.valueOf(result.get(0).get("COUNT(*)"));
		
		jdbcTemplate.update("UPDATE 有給 SET totalpaid = ? WHERE userID = ?;", npl, x);
		
		jdbcTemplate.update("UPDATE 出勤 SET paid = ? WHERE userID = ?;", npl, x);
		
		//return "redirect:/sinsei1";
		return "sinsei2";
	}
}
