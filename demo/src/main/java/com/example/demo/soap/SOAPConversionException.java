package com.example.demo.soap;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class SOAPConversionException extends Exception {
	private static final long serialVersionUID = 1L;
		
	public SOAPConversionException(Exception e) {
		super(e);
	}
	
	public SOAPConversionException(String message) {
		super(message);
	}
	
	public SOAPConversionException(String message, Throwable e) {
		super(message, e);
	}
}