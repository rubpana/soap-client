package com.example.demo.commons;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import lombok.Getter;

/**
 * This class holds {@link XmlMapper} to avoid conflicts between JSON Object and XML mappers.
 * Instead of declaring XmlMapper as bean, we declare this class as bean and it's configured at
 * {@link ImediadorintegrationFeConfig#xmlMapperBean}
 *
 */
public class XmlMapperBean {

	@Getter
	private XmlMapper mapper;
	
	public XmlMapperBean (XmlMapper mapper) {
		this.mapper = mapper;
	}
	
}