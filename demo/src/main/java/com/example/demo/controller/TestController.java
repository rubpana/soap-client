package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.soap.SOAPClient;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("test")
public class TestController {
	
	@Autowired
	private SOAPClient newSOAPClient;
	
	@Autowired
	private SOAPClient homeSOAPClient;
	
	@GetMapping
	public void get() {
		this.newSOAPClient.invoke("GEEET", null, null, null, null);
	}
	
	@GetMapping("value")
	public void get2() {
		this.homeSOAPClient.invoke("VALUE", null, null, null, null);
	}

	
}
