package com.example.demo.models;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import lombok.Data;

@Data
@JacksonXmlRootElement(localName = "contacto")
public class Contact {
	@JacksonXmlProperty(localName = "codigo")
	private String id;
	@JacksonXmlProperty(localName = "nombre")
	private String name;
}
