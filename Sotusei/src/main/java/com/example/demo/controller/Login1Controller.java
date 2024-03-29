package com.example.demo.controller;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

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
	public Object session;

	//(ページ表示用メソッド)
	@RequestMapping(path = "/login1", method = RequestMethod.GET)
	public String useridGet(HttpSession session) {
		
		return "login1";
	}

	// ログインメソッド
	@RequestMapping(path = "/login1", method = RequestMethod.POST)
	public String login1(String useID, String pass, HttpSession session) throws ScriptException, FileNotFoundException {

	    try {
	        //DBに繋ぐならこんな感じ(JdbcTemplate)
	        List<Map<String, Object>> resultList = jdbcTemplate.queryForList("SELECT * FROM 社員 WHERE userID = ?", useID);

	        if (resultList.size() > 0 && resultList.get(0).get("password").equals(pass)) {

	            session.setAttribute("resultList", resultList);
	            session.setAttribute("useID", useID);

	            System.out.println("成功");
	            return "redirect:/syuttaikin";
	        } else {
	            System.out.println("失敗");
	            return "login2";
	        }
	    } catch (Exception e) {
	        System.out.println("DB接続失敗");
	        e.printStackTrace();
	        return "dberror";
	    }
	}
	
	

}
