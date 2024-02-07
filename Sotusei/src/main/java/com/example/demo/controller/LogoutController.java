package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpSession;

@Controller
public class LogoutController {
	
	@Autowired
	HttpSession session;


	//(ページ表示用メソッド)
	@RequestMapping(path = "/logout", method = RequestMethod.GET)
	public String logout(Model model) {

		return "logout";
	}
	
	@RequestMapping(path = "/logout", method = RequestMethod.POST)
	public String logout() {
		
		session.invalidate();
		
		return "redirect:/login1";
	}
	
}
