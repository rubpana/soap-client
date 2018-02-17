package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.example.demo.commons.XmlMapperBean;
import com.example.demo.soap.SOAPClient;
import com.example.demo.soap.SOAPClientGenerator;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

@SpringBootApplication
public class DemoApplication {

	@Autowired
	private XmlMapperBean xmlMapperBean;
	@Autowired
	private SOAPClientGenerator soapClientGenerator;

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}
	
	@Bean
	public XmlMapperBean xmlMapperBean() {
		return new XmlMapperBean(new XmlMapper());
	}
	
	@Bean
	public SOAPClientGenerator soapServiceGenerator() {
		return new SOAPClientGenerator(this.xmlMapperBean);
	}
	
	@Bean
	public SOAPClient newSOAPClient() {
		return this.soapClientGenerator.generateClient("BASE", null, null, null);
	}
	
	@Bean
	public SOAPClient homeSOAPClient() {
		return this.soapClientGenerator.generateClient("BASE2", "Prueba", "test", null);
	}

}
