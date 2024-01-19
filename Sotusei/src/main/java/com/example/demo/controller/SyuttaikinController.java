package com.example.demo.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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

	List<Map<String, Object>> resultList = jdbcTemplate.queryForList("SELECT CURDATE();");
	String d = resultList.get(0).get("CURDATE()").toString();
	//userIDの引継ぎ方がわかり次第修正
	//String x = (String) session.getAttribute("userID");
	String x = "12345";
	String z = x + d;

	SimpleDateFormat  ymd = new SimpleDateFormat("yyyy:mm:dd");
			
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

		jdbcTemplate.update("INSERT INTO 出退勤 (userID,date,wortime) VALUES(?,?,CURTIME());", x, z);
		jdbcTemplate.update("INSERT INTO 残業時間 (userID,userIDdate,date) VALUES(?,?,?);", x, z, d);

		return "syuttaikin";

	}

	//退勤・残業時間登録メソッド
	@RequestMapping(path = "/syuttaikin", params = "taikin", method = RequestMethod.POST)
	public String taikin(Model model) throws ParseException {

		String targetTimeStr = "10:29:00";

		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		java.util.Date targetTime = sdf.parse(targetTimeStr);

		//17時から退勤時間までの時差を計算する
		if (isTimeAfterTarget(targetTime)) {
			LocalDateTime nowDate = LocalDateTime.now();
			DateTimeFormatter dtf1 = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
			String formatNowDate = dtf1.format(nowDate);
			String stTime = "10:29:00";
			String endTime = formatNowDate.toString();
			java.util.Date stDate = sdf.parse(stTime);
			java.util.Date endDate = sdf.parse(endTime);
			int zisa = (int) calculateTimeDifference(stDate, endDate);
			String lag = convertMinutesToTime(zisa);
			//前日取得
			Calendar zen = Calendar.getInstance();

			Date now = zen.getTime();

			zen.add(Calendar.DAY_OF_MONTH, -1);
			now = zen.getTime();
			ymd.format(now);
			String daybefore = now.toString();
			List<Map<String, Object>> zangyouList = jdbcTemplate.queryForList("SELECT * FROM 残業時間 WHERE date = ?;", z);
			int h = (int) zangyouList.get(0).get("week");
			jdbcTemplate.update("UPDATE 出退勤 SET closetime = CURTIME(), overtime = ? WHERE date = ?;", lag, z);
			jdbcTemplate.update("UPDATE 残業時間 SET day = ? WHERE userIDdate = ?;", zisa, z);
		} else {
			jdbcTemplate.update("UPDATE 出退勤 SET closetime = CURTIME(), overtime = 0 WHERE date = ?;", z);
			jdbcTemplate.update("UPDATE 残業時間 SET day = 0 WHERE userIDdate = ?;", z);
		}

		return "syuttaikin";

	}

	//休憩開始時間登録メソッド
	@RequestMapping(path = "/syuttaikin", params = "kaisi", method = RequestMethod.POST)
	public String kaisi(Model model) {

		jdbcTemplate.update("UPDATE 出退勤 SET breakbegins = CURTIME() WHERE date = ?;", z);

		return "syuttaikin";

	}

	//休憩終了時間登録メソッド
	@RequestMapping(path = "/syuttaikin", params = "syuuryou", method = RequestMethod.POST)
	public String syuuryou(Model model) {

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
	public static double calculateTimeDifference(java.util.Date stDate, java.util.Date endDate) {
		LocalTime stLocalTime = LocalTime.ofInstant(stDate.toInstant(), java.time.ZoneId.systemDefault());
		LocalTime endLocalTime = LocalTime.ofInstant(endDate.toInstant(), java.time.ZoneId.systemDefault());

		// 終了時刻が開始時刻より小さい場合翌日とする
		if (endLocalTime.isBefore(stLocalTime)) {
			endLocalTime = endLocalTime.plusHours(24);
		}

		Duration duration = Duration.between(stLocalTime, endLocalTime);
		double secondsDifference = duration.toMillis() / 1000.0;

		//return minutesDifference; // 分単位での差
		return secondsDifference; // 秒単位での差
	}

	public static String convertMinutesToTime(long zisa) {
		// 秒を時間、分、秒に変換
		long hou = zisa / 3600;
		long min = zisa % 3600 / 60;
		long sec = zisa % 60;

		// 時刻形式に変換して文字列として返す
		return String.format("%02d:%02d:%02d", hou, min, sec);
	}
}