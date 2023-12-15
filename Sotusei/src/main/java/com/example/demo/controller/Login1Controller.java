package com.example.demo.controller;

import java.util.List;
import java.util.Map;

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

	//(ページ表示用メソッド)
	@RequestMapping(path = "/login1", method = RequestMethod.GET)
	public String useridGet() {
		return "login1";
	}

	// ログインメソッド
	@RequestMapping(path = "/login1", method = RequestMethod.POST)
	public String mictodo_add(String useID, String pass, HttpSession session) {

		//DBに繋ぐならこんな感じ(JdbcTemplate)
		List<Map<String, Object>> resultList = jdbcTemplate.queryForList("SELECT * FROM 社員 WHERE userID = ?",useID);
		
		if(resultList.size()>0&&resultList.get(0).get("password").equals(pass)) {
			
			session.setAttribute("resultList",resultList);
			
			//return "redirect:/login2";
			
			System.out.println("成功");
			return "login1";
		}else {
			System.out.println(resultList.get(0));
			
			return "login1";
		}
	}

}
