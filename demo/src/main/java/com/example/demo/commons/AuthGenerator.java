package com.example.demo.commons;

import org.springframework.util.Base64Utils;

public class AuthGenerator {

	private static final String DOTS_CHAR = ":";
	private static final String BASIC_AUTH = "Basic ";

	public static String generateBasicAuth(String username, String password) {
		String basicCredentials = new StringBuilder().append(username).append(DOTS_CHAR).append(password).toString();
		String encodedCredentials = Base64Utils.encodeToString(basicCredentials.getBytes());
		return new StringBuilder().append(BASIC_AUTH).append(encodedCredentials).toString();
	}
}