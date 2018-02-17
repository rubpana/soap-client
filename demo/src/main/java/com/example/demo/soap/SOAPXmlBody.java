package com.example.demo.soap;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SOAPXmlBody {
		
	private String template;
	// Key = replacer, value = model
	private final Map<String, Object> inputs = new HashMap<String, Object>();
	
	public SOAPXmlBody(String template, Map<String, Object> inputs) {
		this.template = template;
		this.addInputs(inputs);
	}
		
	public void addInputs(Map<String, Object> inputs) {
		if (inputs != null) {
			this.inputs.putAll(inputs);
		}
	}
}