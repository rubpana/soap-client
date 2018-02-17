package com.example.demo.soap;

import com.example.demo.commons.XmlMapperBean;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

public class SOAPClientGenerator {
	
	private XmlMapper xmlMapper;

	public SOAPClientGenerator(XmlMapperBean xmlMapperBean) {
		this.xmlMapper = xmlMapperBean.getMapper();
	}
	
	public SOAPClient generateClient(String url, String username, String password) {
		return this.generateClient(url, username, password, null);
	}
	
	public SOAPClient generateClient(String url, String username, String password, ISOAPClientErrorHandler errorHandler) {
		return SOAPClient.builder()
				.xmlMapper(this.xmlMapper)
				.serviceUrl(url)
				.username(username)
				.password(password)
				.build();
	}
	
}