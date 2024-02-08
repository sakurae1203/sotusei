package com.example.demo.controller;

import java.text.SimpleDateFormat;
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

@Controller
public class TourokuController {

	@Autowired
	JdbcTemplate jdbcTemplate;

	//(ページ表示用メソッド)
	@RequestMapping(path = "/touroku", method = RequestMethod.GET)
	public String tourokuGet() {
		return "touroku";
	}

	// 新規登録メソッド
	@RequestMapping(path = "/touroku", method = RequestMethod.POST)
	public String touroku(String useID, String pass, String name, String mail, Model model) {
		try {
			List<Map<String, Object>> resultList = jdbcTemplate.queryForList("SELECT * FROM 社員 WHERE userID = ?",
					useID);

			int ucount = countCharacters(useID);
			int pcount = countCharacters(pass);
			int ncount = countCharacters(name);
			int mcount = countCharacters(mail);

			System.out.println(ucount + "," + pcount + "," + ncount + "," + mcount);

			if (useID == null && pass != null && name != null && mail != null) {

				if (ucount <= 16 && pcount <= 16 && ncount <= 30 && mcount <= 40) {

					if (resultList.size() < 1) {

						SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy/MM/dd");
						Calendar zen = Calendar.getInstance();

						Date now = zen.getTime();

						zen.add(Calendar.DAY_OF_MONTH, -1);
						now = zen.getTime();
						String bday = sdf1.format(now);

						//DBに繋ぐならこんな感じ(JdbcTemplate)
						jdbcTemplate.update("INSERT INTO 社員 VALUES (?,?,?,?);", useID, pass, name, mail);
						jdbcTemplate.update("INSERT INTO ワンタイム VALUES (?,?);", mail, 0);
						jdbcTemplate.update("INSERT INTO 残業時間 VALUES (?,?,?,?,?,?);", useID, useID + bday, bday, 0, 0,
								0, 0);
						jdbcTemplate.update("INSERT INTO 出勤 VALUES (?,?,?,?);", useID, 0, 0, 0);
						jdbcTemplate.update("INSERT INTO 出退勤 VALUES (?,?,?,?,?,?,?);", useID, 0, 0, 0, 0, 0, 0);
						jdbcTemplate.update("INSERT INTO 有給 VALUES(?,?,?,?,?,?,?);", useID, 10, 0, 0, 0, 0, 0);
						return "redirect:/login1";
					} else {
						return "tourokudualert";
					}
				} else {
					return "tourokuoralert";
				}
			} else {
				return "tourokunullalert";
			}

		} catch (Exception e) {
			System.out.println("データベースへのアクセスに失敗しました。");
			e.printStackTrace();
			return "dberror";
		}
	}

	public static int countCharacters(String text) {
		return text.length();
	}

}
