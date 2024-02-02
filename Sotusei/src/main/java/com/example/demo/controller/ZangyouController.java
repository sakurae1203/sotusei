package com.example.demo.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class ZangyouController {

    @Autowired
    JdbcTemplate jdbcTemplate;
    
    //userIDの引継ぎ方がわかり次第修正
    //String x = (String) session.getAttribute("userID");
    String x = "12345";
    
    //(ページ表示用メソッド)
    @RequestMapping(path = "/zangyou", method = RequestMethod.GET)
    public String zangyouGet(Model model) {
        
        List<Map<String, Object>> resultList = jdbcTemplate
                .queryForList("SELECT * FROM 社員 INNER JOIN 残業時間 ON 社員.userID = 残業時間.userID GROUP BY 社員.userID;");
        
        model.addAttribute("resultList", resultList);
        
        
        return "zangyou";
    }
    
}