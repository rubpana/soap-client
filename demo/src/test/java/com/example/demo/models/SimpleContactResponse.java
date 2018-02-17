package com.example.demo.models;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import lombok.Data;

@Data
@JacksonXmlRootElement(localName = "XML")
public class SimpleContactResponse {
	@JacksonXmlElementWrapper(useWrapping = false)
	@JacksonXmlProperty(localName = "contactos")
	private Contact[] contacts;
	
	@JacksonXmlProperty(localName = "ERRORES")
	private Error error;
	
	@Data
	public static class Error {
		@JacksonXmlProperty(localName = "codigo")
		private String code;
	}
}
