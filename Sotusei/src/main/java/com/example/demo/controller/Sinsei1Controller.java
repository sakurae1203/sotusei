package com.example.demo.controller;

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
public class Sinsei1Controller {
    
    @Autowired
    JdbcTemplate jdbcTemplate;
    
    @Autowired
	HttpSession session;
    
    ArrayList<String> paid = new ArrayList<>();
    
    //(ページ表示用メソッド)
    @RequestMapping(path = "/sinsei1", method = RequestMethod.GET)
    public String sinsei1Get(Model model) {
    	
    	String x = (String) session.getAttribute("useID");
        
        List<Map<String, Object>> resultList = jdbcTemplate
                .queryForList("SELECT * FROM 有給 WHERE userID = ?;", x);
        
        for(int i = 1; i<resultList.size();i++) {
        String st = (String) resultList.get(i).get("stpaid");
        
        String en = (String) resultList.get(i).get("enpaid");
        
         paid.add(st + "～" + en);
        }
        
        model.addAttribute("paid", paid);
        
        
        return "sinsei1";
    }
    
}
