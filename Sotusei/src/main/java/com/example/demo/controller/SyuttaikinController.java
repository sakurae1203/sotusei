//package com.example.demo.controller;
//
//import java.util.List;
//import java.util.Map;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//
//public class SyuttaikinController {
//	@Autowired
//	JdbcTemplate jdbcTemplate;
//
//	//(ページ表示用メソッド)
//	@RequestMapping(path = "/syuttai", method = RequestMethod.GET)
//	public String useridGet() {
//		return "mictodo_add";
//}
//	List<Map<String, Object>> resultList = jdbcTemplate.queryForList("INSERT INTO VALUE(?,?)", useID,pass);
//
//	model.addAttribute("id", useID);
//	model.addAttribute("pass", pass);
//	return "syuttaikin";
//}
//
//}
