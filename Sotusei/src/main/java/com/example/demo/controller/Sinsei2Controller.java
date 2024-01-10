package com.example.demo.controller;

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
	@RequestMapping(path = "/syuttaikin", method = RequestMethod.GET)
	public String syuttaikinGet(HttpSession session) {
	return "syuttaikin";
}
	
	// 申請日登録メソッド
	@RequestMapping(path = "/syuttaikin", params = "syukkin", method = RequestMethod.POST)
	public String syukkin(String kaisi,String syuuryou,String ziyuu,Model model, HttpSession session) {

		String x = (String) session.getAttribute("userID");
		jdbcTemplate.update("INSERT INTO 有給 (stpaid,enpaid,detail) VALUES(?,?,?) WHERE userID = ?;",kaisi,syuuryou,ziyuu, x);

		return "sinsei2";

	}
}
