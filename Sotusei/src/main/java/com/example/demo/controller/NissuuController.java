package com.example.demo.controller;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class NissuuController {

	@Autowired
	JdbcTemplate jdbcTemplate;

	//(ページ表示用メソッド)
	@RequestMapping(path = "/nissuu", method = RequestMethod.GET)
	public String syuttaikinGet(Model model) {

		ArrayList<String> nwd = new ArrayList<>();

		String userID;

		//今日までの日数を計算(年度)
		Date nD = new Date();
		SimpleDateFormat sdfy = new SimpleDateFormat("yyyy");
		SimpleDateFormat sdfm = new SimpleDateFormat("MM");
		String y = sdfy.format(nD);
		String m = sdfm.format(nD);
		int yyyy = Integer.parseInt(y);
		int MM = Integer.parseInt(m);
		if(MM < 4) {
			yyyy--;
		}
		
		//出勤日数カウント
		List<Map<String, Object>> result = jdbcTemplate.queryForList("SELECT COUNT(*) FROM 出退勤 GROUP BY userID;");
		for (int i = 0; i < result.size(); i++) {
			nwd.add(String.valueOf(result.get(i).get("COUNT(*)")));
		}

		//出勤日数、欠勤日数登録(別のクラスに移したほうがよさそう)
		List<Map<String, Object>> resultID = jdbcTemplate
				.queryForList("SELECT * FROM 出勤;");
		for (int i = 0; i < resultID.size(); i++) {
			userID = (String) resultID.get(i).get("userID");
			List<Map<String, Object>> resultpaid = jdbcTemplate
					.queryForList("SELECT COUNT(*) FROM 有給 WHERE userID = ?;", userID);
			String npl = String.valueOf(resultpaid.get(0).get("COUNT(*)"));

			int year = yyyy;
			long days = daysUntilTodayInFiscalYear(year);
			int daysAbsent = (int) (days - (Integer.parseInt(nwd.get(i)) + Integer.parseInt(npl)));
			jdbcTemplate.update("UPDATE 出勤 SET workday = ?, adsent = ? WHERE userID = ?;", nwd.get(i), daysAbsent,
					userID);
		}
		List<Map<String, Object>> resultList = jdbcTemplate
				.queryForList("SELECT * FROM 社員 INNER JOIN 出勤  ON 社員.userID = 出勤.userID INNER JOIN 有給  ON 出勤.userID = 有給.userID GROUP BY 社員.userID;");

		model.addAttribute("resultList", resultList);
		

		return "nissuu";
	}

	public static long daysUntilTodayInFiscalYear(int fiscalYear) {
		LocalDate today = LocalDate.now();
		LocalDate fiscalYearStart = LocalDate.of(fiscalYear, 4, 1); // 4月1日を年度の開始日とする
		LocalDate fiscalYearEnd = LocalDate.of(fiscalYear + 1, 3, 31); // 翌年の3月31日を年度の終了日とする

		if (today.isBefore(fiscalYearStart)) {
			return 0; // 今日が年度開始日より前の場合は0を返す
		} else if (today.isAfter(fiscalYearEnd)) {
			return ChronoUnit.DAYS.between(fiscalYearStart, fiscalYearEnd) + 1; // 年度終了日を超える場合は年度の全日数を返す
		} else {
			return ChronoUnit.DAYS.between(fiscalYearStart, today) + 1; // 年度の開始日から今日までの日数を返す
		}
	}

	//特に何もないメソッド(一応)
	/*@RequestMapping(path = "/nissuu", method = RequestMethod.POST)
	public String nissuu() throws ParseException {
		
		return "syuttaikin";
	
	}*/

}
