package com.example.demo.models;

import com.example.demo.commons.XMLProjection;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import lombok.Data;

@Data
@XMLProjection(CDATA = "/soapenv:Envelope/soapenv:Body/urn:MATRICULAS/xmlEntrada")
public class NamespacedCDATAContactResponse {
	@JacksonXmlElementWrapper(useWrapping = false)
	@JacksonXmlProperty(localName = "contactos")
	private Contact[] contacts;
	
	@JacksonXmlProperty(localName = "ERRORES")
	private Error error;
	
	public static class Error {
		@JacksonXmlProperty(localName = "codigo")
		private String code;
	}
}
