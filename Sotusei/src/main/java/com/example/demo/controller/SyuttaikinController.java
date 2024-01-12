package com.example.demo.controller;

import java.time.LocalDate;
import java.util.ArrayList;
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
public class SyuttaikinController {

	@Autowired
	JdbcTemplate jdbcTemplate;

	LocalDate day = LocalDate.now();

	List<String> ID, NAME, SYUKKIN, TAIKIN, BREAKST, BREAKEN, OVER = new ArrayList<String>();

	//(ページ表示用メソッド)
	@RequestMapping(path = "/syuttaikin", method = RequestMethod.GET)
	public String syuttaikinGet(HttpSession session) {
		/*	String id;
			String name;
			String syukkin;
			String taikin;
			String breakst;
			String breaken;
			String over;
			List<Map<String, Object>> resultList = jdbcTemplate.queryForList("SELECT * FROM 社員 INNER JOIN 出退勤 ON 社員.userID = 出退勤.userID;");
			session.setAttribute("Listsize", resultList.size());
			//セッションに値を入れて画面に一覧表みたいに表示させたい
			for (int i = 0; i < resultList.size(); i++) {
				id = resultList.get(i).get("userID").toString();
				name = resultList.get(i).get("name").toString();
				syukkin = resultList.get(i).get("wortime").toString();
				taikin = resultList.get(i).get("closetime").toString();
				breakst = resultList.get(i).get("breakegins").toString();
				breaken = resultList.get(i).get("breakends").toString();
				over = resultList.get(i).get("overtime").toString();
				ID.add(id);
				NAME.add(name);
				SYUKKIN.add(syukkin);
				TAIKIN.add(taikin);
				BREAKST.add(breakst);
				BREAKEN.add(breaken);
				OVER.add(over);
				
				session.setAttribute("userID", id);
				session.setAttribute("name", name);
				session.setAttribute("syukkin", syukkin);
				session.setAttribute("taikin", taikin);
				session.setAttribute("breaktime", breakst);
				session.setAttribute("over", over);
			}*/

		return "syuttaikin";
	}

	/* 出勤時間登録メソッド
	userIDと年月日を合わせて登録し出勤を一日一回までにする。*/
	@RequestMapping(path = "/syuttaikin", params = "syukkin", method = RequestMethod.POST)
	public String syukkin(Model model, HttpSession session) {

		List<Map<String, Object>> resultList = jdbcTemplate.queryForList("SELECT CURDATE();");
		String d = resultList.get(0).get("CURDATE()").toString();
		//userIDの引継ぎ方がわかり次第修正
		//String x = (String) session.getAttribute("userID");
		String x = "12345";
		String z = x + d;
		//
		jdbcTemplate.update("INSERT INTO 出退勤 (userID,date,workday) VALUES(?,?,CURTIME()) WHERE userID = ?;", x, z, x);

		return "syuttaikin";

	}

	//退勤時間登録メソッド
	@RequestMapping(path = "/syuttaikin", params = "taikin", method = RequestMethod.POST)
	public String taikin(Model model) {

		List<Map<String, Object>> resultList = jdbcTemplate.queryForList("SELECT CURDATE();");
		//String x = (String) session.getAttribute("userID");
		String d = resultList.get(0).get("CURDATE()").toString();
		String x = "12345";
		String z = x + d;
		jdbcTemplate.update("UPDATE 出退勤 SET (closetime) = CURTIME() WHERE date = ?;", z);
		return "syuttaikin";

	}

	//休憩開始時間登録メソッド
	@RequestMapping(path = "/syuttaikin", params = "kaisi", method = RequestMethod.POST)
	public String kaisi(Model model) {

		List<Map<String, Object>> resultList = jdbcTemplate.queryForList("SELECT CURDATE();");
		//String x = (String) session.getAttribute("userID");
		String d = resultList.get(0).get("CURDATE()").toString();
		String x = "12345";
		String z = x + d;
		jdbcTemplate.update("UPDATE 出退勤 SET (breakbegins) = CURTIME() WHERE date = ?;", z);

		return "syuttaikin";

	}

	//休憩終了時間登録メソッド
	@RequestMapping(path = "/syuttaikin", params = "syuuryou", method = RequestMethod.POST)
	public String syuuryou(Model model) {

		List<Map<String, Object>> resultList = jdbcTemplate.queryForList("SELECT CURDATE();");
		//String x = (String) session.getAttribute("userID");
		String d = resultList.get(0).get("CURDATE()").toString();
		String x = "12345";
		String z = x + d;
		jdbcTemplate.update("UPDATE 出退勤 SET (breakends) = CURTIME() WHERE date = ?;", z);

		return "syuttaikin";

	}

}