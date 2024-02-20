package com.example.demo.controller;

import java.sql.Date;
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
public class Sinsei2Controller {

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Autowired
	HttpSession session;

	//(ページ表示用メソッド)
	@RequestMapping(path = "/sinsei2", method = RequestMethod.GET)
	public String syuttaikinGet() {
		return "sinsei2";
	}

	// 申請日登録メソッド
	@RequestMapping(path = "/sinsei2", method = RequestMethod.POST)
	public String sinsei2(Date kaisi, Date syuuryou, String ziyuu, Model model, HttpSession session) {
	    try {
	        if (kaisi == null || syuuryou == null || ziyuu.equals("")) {
	            return "sinseinull";
	        } else {
	            String x = (String) session.getAttribute("useID");

	            // 出勤日数カウント
	            List<Map<String, Object>> resultnissuu = jdbcTemplate
	                    .queryForList("SELECT COUNT(*) FROM 出退勤 GROUP BY userID;");
	            String nwd = String.valueOf(resultnissuu.get(0).get("COUNT(*)"));

	            jdbcTemplate.update(
	                    "INSERT INTO 有給 (userID,stpaid,enpaid,detail,yearpaid,totalpaid,con) VALUES(?,?,?,?,?,?,?);", x,
	                    kaisi, syuuryou, ziyuu, 0, 0, 0);

	            // 有給取得数カウント
	            List<Map<String, Object>> result = jdbcTemplate.queryForList("SELECT COUNT(*) FROM 有給 WHERE userID = ?;",
	                    x);

	            String npl = String.valueOf(result.get(0).get("COUNT(*)"));

	            List<Map<String, Object>> resultList = jdbcTemplate.queryForList("SELECT * FROM 有給 WHERE userID = ?;", x);

	            int year = Integer.parseInt((String) resultList.get(0).get("yearpaid"));
	            year = year - Integer.parseInt(npl) +1;

	            jdbcTemplate.update("UPDATE 有給 SET yearpaid = ?, totalpaid = ? WHERE userID = ?;", year, npl, x);

	            jdbcTemplate.update("UPDATE 出勤 SET paid = ? WHERE userID = ?;", npl, x);

	            return "redirect:/sinsei1";
	        }
	    } catch (Exception e) {
	        System.out.println("データベースへのアクセスに失敗しました。");
	        e.printStackTrace();
	        return "dberror";
	    }
	}

}
