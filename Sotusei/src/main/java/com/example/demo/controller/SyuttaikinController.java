package com.example.demo.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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

	int oversum = 0;
	// 終了する時刻を指定
	String targetTimeStr = "23:58";
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
	LocalTime targetTime = LocalTime.parse(targetTimeStr, formatter);

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
		jdbcTemplate.update("INSERT INTO 出退勤 (userID,date,wortime) VALUES(?,?,CURTIME());", x, z);

		return "syuttaikin";

	}

	//退勤・残業時間登録メソッド
	@RequestMapping(path = "/syuttaikin", params = "taikin", method = RequestMethod.POST)
	public String taikin(Model model) throws ParseException {

		List<Map<String, Object>> resultList = jdbcTemplate.queryForList("SELECT CURDATE();");
		//String x = (String) session.getAttribute("userID");
		String d = resultList.get(0).get("CURDATE()").toString();
		String x = "12345";
		String z = x + d;
		String targetTimeStr = "10:55:00";

		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		java.util.Date targetTime = sdf.parse(targetTimeStr);
		//17時から退勤時間までの時差を計算する
		if (isTimeAfterTarget(targetTime)) {
			LocalDateTime nowDate = LocalDateTime.now();
			String stTime = "10:55:00";
			String endTime = nowDate.toString();
			java.util.Date stDate = sdf.parse(stTime);
			java.util.Date endDate = sdf.parse(endTime);
			long zisa = calculateTimeDifference(stDate, endDate);
			jdbcTemplate.update("UPDATE 出退勤 SET closetime = CURTIME(), overtime = ? WHERE date = ?;", zisa, z);
		} else {
			jdbcTemplate.update("UPDATE 出退勤 SET closetime = CURTIME(), overtime = 0 WHERE date = ?;", z);
		}

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
		jdbcTemplate.update("UPDATE 出退勤 SET breakbegins = CURTIME() WHERE date = ?;", z);

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
		LocalTime nowtime = LocalTime.now();
		if (nowtime.equals("17:00:00")) {

		}
		jdbcTemplate.update("UPDATE 出退勤 SET breakends = CURTIME() WHERE date = ?;", z);

		return "syuttaikin";

	}

	//時間超過判定メソッド
	public static boolean isTimeAfterTarget(java.util.Date targetTime2) {
		LocalTime currentLocalTime = LocalTime.now();
		LocalTime targetLocalTime = LocalTime.ofInstant(targetTime2.toInstant(), java.time.ZoneId.systemDefault());

		return currentLocalTime.isAfter(targetLocalTime);

	}

	//時間差計算メソッド
	public static long calculateTimeDifference(java.util.Date stDate, java.util.Date endDate) {
		LocalTime stLocalTime = LocalTime.ofInstant(stDate.toInstant(), java.time.ZoneId.systemDefault());
		LocalTime endLocalTime = LocalTime.ofInstant(endDate.toInstant(), java.time.ZoneId.systemDefault());

		// 終了時刻が開始時刻より小さい場合翌日とする
		if (endLocalTime.isBefore(stLocalTime)) {
			endLocalTime = endLocalTime.plusHours(24);
		}

		Duration duration = Duration.between(stLocalTime, endLocalTime);
		return duration.toMinutes();
	}
}