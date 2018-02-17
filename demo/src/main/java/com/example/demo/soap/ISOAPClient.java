package com.example.demo.soap;

import java.util.Map;

import org.springframework.http.HttpMethod;

public interface ISOAPClient {
	public <S> S invoke(String soapAction, HttpMethod method, SOAPXmlBody requestBody, Class<S> outputClass);

	public <S> S invoke(String soapAction, HttpMethod method, SOAPXmlBody requestBody, Class<S> outputClass,
			Map<String, String> additionalHeaders);
}