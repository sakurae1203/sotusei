package com.example.demo.controller;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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

	@Autowired
	HttpSession session;

	SimpleDateFormat ymd = new SimpleDateFormat("yyyy:MM:dd");

	int oversum = 0;

	//(ページ表示用メソッド)
	@RequestMapping(path = "/syuttaikin", method = RequestMethod.GET)
	public String syuttaikinGet(Model model) {
		
		Date nD = new Date();
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy/MM/dd");
		String d = sdf1.format(nD);
		String x = (String) session.getAttribute("useID");
		String z = x + d;

		List<Map<String, Object>> resultList = jdbcTemplate
				.queryForList(
						"SELECT * FROM 社員 INNER JOIN 出退勤 ON 社員.userID = 出退勤.userID WHERE 出退勤.day = ? GROUP BY 社員.userID;",
						d);

		model.addAttribute("resultList", resultList);

		return "syuttaikin";
	}

	/* 出勤時間登録メソッド
	userIDと年月日を合わせて登録し出勤を一日一回までにする。*/
	@RequestMapping(path = "/syuttaikin", params = "syukkin", method = RequestMethod.POST)
	public String syukkin(Model model) {
		
		Date nD = new Date();
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy/MM/dd");
		String d = sdf1.format(nD);
		String x = (String) session.getAttribute("useID");
		String z = x + d;

		jdbcTemplate.update("INSERT INTO 出退勤 (userID, day, date,wortime) VALUES(?,?,?,CURTIME());", x, d, z);
		jdbcTemplate.update("INSERT INTO 残業時間 (userID,userIDdate,date) VALUES(?,?,?);", x, z, d);
		//jdbcTemplate.update("INSERT INTO 出勤 (userID) VALUES(?);", x);

		List<Map<String, Object>> resultList = jdbcTemplate
				.queryForList(
						"SELECT * FROM 社員 INNER JOIN 出退勤 ON 社員.userID = 出退勤.userID WHERE 出退勤.day = ? GROUP BY 社員.userID;",
						d);

		model.addAttribute("resultList", resultList);

		return "syuttaikin";

	}

	//退勤・残業時間登録メソッド
	@RequestMapping(path = "/syuttaikin", params = "taikin", method = RequestMethod.POST)
	public String taikin(Model model) throws ParseException {
		
		Date nD = new Date();
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy/MM/dd");
		String d = sdf1.format(nD);
		String x = (String) session.getAttribute("useID");
		String z = x + d;

		//定時設定
		String teizi = "10:29:00";

		//残業開始判定時間
		String targetTimeStr = teizi;

		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		java.util.Date targetTime = sdf.parse(targetTimeStr);

		//残業開始判定時間を過ぎているかどうか
		if (isTimeAfterTarget(targetTime)) {
			LocalDateTime nowDate = LocalDateTime.now();
			DateTimeFormatter dtf1 = DateTimeFormatter.ofPattern("HH:mm:ss");
			//SimpleDateFormat DateFormat = new SimpleDateFormat("HH:mm:ss");
			String formatNowDate = dtf1.format(nowDate);
			//残業時間計算
			String stTime = teizi;
			String endTime = formatNowDate.toString();
			java.util.Date stDate = sdf.parse(stTime);
			java.util.Date endDate = sdf.parse(endTime);
			int zisa = (int) calculateTimeDifference(stDate, endDate);
			String lagwhile = convertMinutesToTime(zisa);
			int lag = Integer.parseInt(lagwhile);
			int hou = zisa / 3600;
			int min = zisa % 3600 / 60;
			int sec = zisa % 60;
			System.out.println(zisa);
			System.out.println(lagwhile);
			System.out.println(lag);
			System.out.println(hou);
			System.out.println(min);
			System.out.println(sec);
			//前日取得
			Calendar zen = Calendar.getInstance();

			Date now = zen.getTime();

			zen.add(Calendar.DAY_OF_MONTH, -1);
			now = zen.getTime();
			String bday = sdf1.format(now);
			String a = x + bday;
			//週月年にその日の残業時間を足していく
			List<Map<String, Object>> zangyouList = jdbcTemplate
					.queryForList("SELECT * FROM 残業時間 WHERE userIDdate = ?;", a);
			Time sqlTime = (Time) zangyouList.get(0).get("week");

			// SQL TimeからLocalTimeに変換
			LocalTime localTime = sqlTime.toLocalTime();

			// LocalTimeを使用してLocalDateTimeを作成（日付部分は任意の値を使用）
			LocalDateTime bwe = LocalDateTime.of(2024, 1, 1, localTime.getHour(), localTime.getMinute(),
					localTime.getSecond());

			LocalDateTime oneLater = bwe.plusHours(hou);
			oneLater = oneLater.plusMinutes(min);
			oneLater = oneLater.plusSeconds(sec);

			LocalDateTime we = oneLater;

			sqlTime = (Time) zangyouList.get(0).get("month");

			// SQL TimeからLocalTimeに変換
			localTime = sqlTime.toLocalTime();

			// LocalTimeを使用してLocalDateTimeを作成（日付部分は任意の値を使用）
			LocalDateTime bmo = LocalDateTime.of(2024, 1, 1, localTime.getHour(), localTime.getMinute(),
					localTime.getSecond());

			oneLater = bmo.plusHours(hou);
			oneLater = oneLater.plusMinutes(min);
			oneLater = oneLater.plusSeconds(sec);

			LocalDateTime mo = oneLater;

			sqlTime = (Time) zangyouList.get(0).get("year");

			// SQL TimeからLocalTimeに変換
			localTime = sqlTime.toLocalTime();

			// LocalTimeを使用してLocalDateTimeを作成（日付部分は任意の値を使用）
			LocalDateTime bye = LocalDateTime.of(2024, 1, 1, localTime.getHour(), localTime.getMinute(),
					localTime.getSecond());

			oneLater = bye.plusHours(hou);
			oneLater = oneLater.plusMinutes(min);
			oneLater = oneLater.plusSeconds(sec);

			LocalDateTime ye = oneLater;
			//退勤時間・残業時間登録
			jdbcTemplate.update("UPDATE 出退勤 SET closetime = CURTIME(), overtime = ? WHERE date = ?;", lag, z);
			jdbcTemplate.update("UPDATE 残業時間 SET day = ?, week = ?, month = ?, year = ? WHERE userIDdate = ?;",
					lagwhile, we, mo, ye, z);

			List<Map<String, Object>> resultList = jdbcTemplate
					.queryForList(
							"SELECT * FROM 社員 INNER JOIN 出退勤 ON 社員.userID = 出退勤.userID WHERE 出退勤.day = ? GROUP BY 社員.userID;",
							d);

			model.addAttribute("resultList", resultList);
		} else {
			//前日取得
			Calendar zen = Calendar.getInstance();

			Date now = zen.getTime();

			zen.add(Calendar.DAY_OF_MONTH, -1);
			now = zen.getTime();
			String bday = sdf1.format(now);
			String a = x + bday;
			List<Map<String, Object>> zangyouList = jdbcTemplate
					.queryForList("SELECT * FROM 残業時間 WHERE userIDdate = ?;", a);
			Time sqlTime = (Time) zangyouList.get(0).get("week");

			// SQL TimeからLocalTimeに変換
			LocalTime localTime = sqlTime.toLocalTime();

			// LocalTimeを使用してLocalDateTimeを作成（日付部分は任意の値を使用）
			LocalDateTime bwe = LocalDateTime.of(2024, 1, 1, localTime.getHour(), localTime.getMinute(),
					localTime.getSecond());
			sqlTime = (Time) zangyouList.get(0).get("month");

			// SQL TimeからLocalTimeに変換
			localTime = sqlTime.toLocalTime();

			// LocalTimeを使用してLocalDateTimeを作成（日付部分は任意の値を使用）
			LocalDateTime bmo = LocalDateTime.of(2024, 1, 1, localTime.getHour(), localTime.getMinute(),
					localTime.getSecond());
			sqlTime = (Time) zangyouList.get(0).get("year");

			// SQL TimeからLocalTimeに変換
			localTime = sqlTime.toLocalTime();

			// LocalTimeを使用してLocalDateTimeを作成（日付部分は任意の値を使用）
			LocalDateTime bye = LocalDateTime.of(2024, 1, 1, localTime.getHour(), localTime.getMinute(),
					localTime.getSecond());
			//残業してない場合はその日に0、週月年に前日の値を入れる
			jdbcTemplate.update("UPDATE 出退勤 SET closetime = CURTIME(), overtime = 0 WHERE date = ?;", z);
			jdbcTemplate.update("UPDATE 残業時間 SET day = 0, week = ?, month = ?, year = ? WHERE userIDdate = ?;", bwe,
					bmo, bye, z);

			List<Map<String, Object>> resultList = jdbcTemplate
					.queryForList(
							"SELECT * FROM 社員 INNER JOIN 出退勤 ON 社員.userID = 出退勤.userID WHERE 出退勤.day = ? GROUP BY 社員.userID;",
							d);

			model.addAttribute("resultList", resultList);
		}

		return "syuttaikin";

	}

	//休憩開始時間登録メソッド
	@RequestMapping(path = "/syuttaikin", params = "kaisi", method = RequestMethod.POST)
	public String kaisi(Model model) {
		
		Date nD = new Date();
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy/MM/dd");
		String d = sdf1.format(nD);
		String x = (String) session.getAttribute("useID");
		String z = x + d;

		jdbcTemplate.update("UPDATE 出退勤 SET breakbegins = CURTIME() WHERE date = ?;", z);

		List<Map<String, Object>> resultList = jdbcTemplate
				.queryForList(
						"SELECT * FROM 社員 INNER JOIN 出退勤 ON 社員.userID = 出退勤.userID WHERE 出退勤.day = ? GROUP BY 社員.userID;",
						d);

		model.addAttribute("resultList", resultList);

		return "syuttaikin";

	}

	//休憩終了時間登録メソッド
	@RequestMapping(path = "/syuttaikin", params = "syuuryou", method = RequestMethod.POST)
	public String syuuryou(Model model) {
		
		Date nD = new Date();
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy/MM/dd");
		String d = sdf1.format(nD);
		String x = (String) session.getAttribute("useID");
		String z = x + d;

		LocalTime nowtime = LocalTime.now();
		if (nowtime.equals("17:00:00")) {

		}
		jdbcTemplate.update("UPDATE 出退勤 SET breakends = CURTIME() WHERE date = ?;", z);

		List<Map<String, Object>> resultList = jdbcTemplate
				.queryForList(
						"SELECT * FROM 社員 INNER JOIN 出退勤 ON 社員.userID = 出退勤.userID WHERE 出退勤.day = ? GROUP BY 社員.userID;",
						d);

		model.addAttribute("resultList", resultList);

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
		return String.format("%02d%02d%02d", hou, min, sec);
	}

}