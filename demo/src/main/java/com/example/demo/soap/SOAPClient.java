package com.example.demo.soap;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.example.demo.commons.AuthGenerator;
import com.example.demo.soap.SOAPClientException.Kind;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Builder
public class SOAPClient implements ISOAPClient {

	private static final String SOAP_ACTION_HEADER = "SOAPAction";
	private static final String CONTENT_TYPE_XML = "text/xml;charset=UTF-8";

	@SuppressWarnings("unused")
	private XmlMapper xmlMapper;
	private RestTemplate restTemplate;
	private String serviceUrl;
	private String username;
	private String password;
	private Map<String, Object> extraInputs;
	private ISOAPClientErrorHandler errorHandler;
	private ISOAPClientConverter converter;

	public <S> S invoke(String soapAction, HttpMethod method, SOAPXmlBody requestBody, Class<S> outputClass) {
		return this.invoke(soapAction, method, requestBody, outputClass, null);
	}
	
	public <S> S invoke(String soapAction, HttpMethod method, SOAPXmlBody requestBody, Class<S> outputClass,
			Map<String, String> additionalHeaders) {

		if (requestBody == null) {
			throw new IllegalArgumentException("Request SOAP body cannot be NULL");
		}
		// Customize SOAPXmlBody with extra inputs configured on client
		requestBody.addInputs(this.extraInputs);
				
		// Build request headers
		HttpHeaders headers = this.getDefaultHeaders();
		if (StringUtils.isNotBlank(soapAction)) {
			headers.set(SOAP_ACTION_HEADER, soapAction);
		}
		// Add AUTH Basic header if the client has the credentials configured
		if (this.hasCredentials()) {
			String basic = AuthGenerator.generateBasicAuth(username, password);
			headers.set(HttpHeaders.AUTHORIZATION, basic);
		}
		if (additionalHeaders != null) {
			headers.setAll(additionalHeaders);
		}

		try {
			// Build request
			HttpEntity<String> entity = new HttpEntity<String>(this.converter.toBody(requestBody), headers);
			ResponseEntity<String> response = null;
			try {
				response = restTemplate.exchange(this.serviceUrl, method, entity, String.class);
			} catch (ResourceAccessException e) {
				throw emitHttpError("Error invoking a SOAP service", e);
			} catch (RestClientException e) {
				throw emitHttpError("Error invoking a SOAP service", e);
			}

			// Parse SOAP response body
			if (response != null) {
				String responseBody = response.getBody();
				return this.converter.fromBody(responseBody, outputClass);
			}

			return null;
		} catch (SOAPConversionException e) {
			log.error("A conversion error ocurred either preparing or post-processing the SOAP request");
			throw emitConversionError(e);
		}
	}

	private boolean hasCredentials() {
		return username != null && password != null;
	}

	protected HttpHeaders getDefaultHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.CONTENT_TYPE, CONTENT_TYPE_XML);
		return headers;
	}

	/**
	 * Emits the HTTP error to the linked {@link ISOAPClientErrorHandler} and
	 * returns the exception that handler returns
	 * 
	 * @param message
	 * @param e
	 * @return {@link SOAPClientException}
	 */
	private SOAPClientException emitHttpError(String message, Throwable e) {
		SOAPClientException clienException = new SOAPClientException(message, e);
		clienException.setKind(Kind.HTTP);
		return this.errorHandler.onError(clienException);
	}

	/**
	 * Emits the CONVERSION error to the linked {@link ISOAPClientErrorHandler} and
	 * returns the exception that handler returns
	 * 
	 * @param e
	 * @return {@link SOAPClientException}
	 */
	private SOAPClientException emitConversionError(Throwable e) {
		SOAPClientException clienException = new SOAPClientException(e);
		clienException.setKind(Kind.CONVERSION);
		return this.errorHandler.onError(clienException);
	}
	
	public static SOAPClientBuilder builder() {
        return new CustomSOAPClientBuilder();
    }

    /**
     * Extends builder class
     */
    private static class CustomSOAPClientBuilder extends SOAPClientBuilder {
        
        @Override
        public SOAPClient build() {
	    		if (super.errorHandler == null) {
	    			super.errorHandler = new SOAPClient.DefaultSOAPClientErrorHandler();
	    		}
	
	    		if (super.restTemplate == null) {
	    			super.restTemplate = new RestTemplate();
	    		}
	    		super.restTemplate.setErrorHandler(new SOAPClient.SOAPHttpErrorHandler(super.errorHandler));
	
	    		if (super.converter == null) {
	    			super.converter = new SOAPClientConverter(super.xmlMapper);
	    		}
	
	    		return super.build();

        }
       
    }

	private static class SOAPHttpErrorHandler implements ResponseErrorHandler {

		private ISOAPClientErrorHandler errorHandler;

		public SOAPHttpErrorHandler(ISOAPClientErrorHandler errorHandler) {
			this.errorHandler = errorHandler;
		}

		@Override
		public boolean hasError(ClientHttpResponse response) throws IOException {
			if (response.getStatusCode().is2xxSuccessful()) {
				return false;
			}
			return true;
		}

		@Override
		public void handleError(ClientHttpResponse response) throws IOException {
			SOAPClientException e = new SOAPClientException();
			e.setKind(Kind.HTTP);
			e.setResponse(response);
			if (this.errorHandler != null) {
				e = this.errorHandler.onError(e);
			}
			throw e;
		}

	}
	
	private static class DefaultSOAPClientErrorHandler implements ISOAPClientErrorHandler {
		
		@Override
		public SOAPClientException onError(SOAPClientException e) {
			return e;
		}
		
	}
}