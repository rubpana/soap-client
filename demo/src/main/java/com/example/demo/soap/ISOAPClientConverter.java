package com.example.demo.soap;

public interface ISOAPClientConverter {
	public <S> S fromBody(String outputXml, Class<S> outputClazz) throws SOAPConversionException;
	public String toBody(SOAPXmlBody requestBody) throws SOAPConversionException;
}
