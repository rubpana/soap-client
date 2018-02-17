package com.example.demo.soap;

public interface ISOAPClientErrorHandler {
	/**
	 * It must return {@link SOAPClientException} or throw another
	 * {@link RuntimeException}. In case of returning a {@link SOAPClientException},
	 * this will be thrown and should be caught in the global exceptions handler.
	 * 
	 * @param e
	 * @return {@link SOAPClientException}
	 */
	public SOAPClientException onError(SOAPClientException e);
}