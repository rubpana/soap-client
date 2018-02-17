package com.example.demo.soap;

import org.springframework.http.client.ClientHttpResponse;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class SOAPClientException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	public enum Kind { CONVERSION, HTTP }
	private Kind kind;
	private ClientHttpResponse response;
	
	public SOAPClientException(Throwable e) {
		super(e);
	}
	
	public SOAPClientException(String message, Throwable e) {
		super(message, e);
	}
}