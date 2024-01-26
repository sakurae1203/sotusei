package com.example.demo.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class NissuuController {

	@Autowired
	JdbcTemplate jdbcTemplate;

	//(ページ表示用メソッド)
	@RequestMapping(path = "/nissuu", method = RequestMethod.GET)
	public String syuttaikinGet() {
		
		ArrayList<String> nwd = new ArrayList<>();

		List<Map<String, Object>> result = jdbcTemplate.queryForList("SELECT COUNT(*) FROM 出退勤 GROUP BY userID;");
		for (int i = 0; i < result.size(); i++) {
			nwd .add(String.valueOf(result.get(i).get("COUNT(*)")));
		}
		System.out.println(nwd);

		return "nissuu";
	}

	//特に何もないメソッド(一応)
	/*@RequestMapping(path = "/nissuu", method = RequestMethod.POST)
	public String nissuu() throws ParseException {
		
		return "syuttaikin";
	
	}*/

}
