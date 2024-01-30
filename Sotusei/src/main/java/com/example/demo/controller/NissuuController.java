package com.example.demo.controller;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
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

		ArrayList<String> userIDlist = new ArrayList<>();
		ArrayList<String> name = new ArrayList<>();
		ArrayList<String> paid = new ArrayList<>();
		ArrayList<String> adsent = new ArrayList<>();
		ArrayList<String> nwd = new ArrayList<>();

		String userID;

		//強までの日数を計算
		LocalDate currentDate = LocalDate.now();
		LocalDate januaryFirst = LocalDate.of(currentDate.getYear(), 1, 1);
		long yearday = ChronoUnit.DAYS.between(januaryFirst, currentDate);
		yearday++;

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

			System.out.println(yearday);
			System.out.println(nwd.get(i));
			System.out.println(npl);

			int daysAbsent = (int) (yearday - (Integer.parseInt(nwd.get(i)) + Integer.parseInt(npl)));
			jdbcTemplate.update("UPDATE 出勤 SET workday = ?, adsent = ? WHERE userID = ?;", nwd.get(i), daysAbsent,
					userID);
		}
		List<Map<String, Object>> resultlist = jdbcTemplate
				.queryForList("SELECT * FROM 社員 INNER JOIN 出勤  ON 社員.userID = 出勤.userID;");
		for (int i = 0; i < resultlist.size(); i++) {
			userIDlist.add(String.valueOf(resultlist.get(i).get("userID")));
			name.add(String.valueOf(resultlist.get(i).get("name")));
			paid.add(String.valueOf(resultlist.get(i).get("paid")));
			adsent.add(String.valueOf(resultlist.get(i).get("adsent")));
		}

		return "nissuu";
	}

	//特に何もないメソッド(一応)
	/*@RequestMapping(path = "/nissuu", method = RequestMethod.POST)
	public String nissuu() throws ParseException {
		
		return "syuttaikin";
	
	}*/

}
