package com.example.demo.soap;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.example.demo.commons.XMLProjection;
import com.example.demo.commons.XmlUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SOAPClientConverter implements ISOAPClientConverter {

	private XmlMapper xmlMapper;

	public SOAPClientConverter() {
		this(new XmlMapper());
	}

	public SOAPClientConverter(XmlMapper xmlMapper) {
		if (xmlMapper == null) {
			xmlMapper = new XmlMapper();
			xmlMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		}
		this.xmlMapper = xmlMapper;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <S> S fromBody(String outputXml, Class<S> outputClazz) throws SOAPConversionException {
		if (String.class.equals(outputClazz)) {
			return (S) outputXml;
		}
		if (outputXml != null) {
			XMLProjection xmlProjection = this.getXMLAnnotation(outputClazz);
			if (xmlProjection != null) {
				return this.parseResponseBodyWithProjection(outputXml, xmlProjection, outputClazz);
			} else {
				return this.parseResponseBody(outputXml, outputClazz);
			}
		}
		return null;
	}

	@Override
	public String toBody(SOAPXmlBody requestBody) {
		if (requestBody != null && requestBody.getTemplate() != null) {
			String body = requestBody.getTemplate();
			Map<String, Object> inputs = requestBody.getInputs();
			for (String replacer : inputs.keySet()) {
				String inputXmlModel = null;
				try {
					inputXmlModel = this.xmlMapper.writeValueAsString(inputs.get(replacer));
				} catch (JsonProcessingException e) {
					log.error("Error generating body for replacer {}, but it will be ignored: {}", replacer, e);
				}
				body = StringUtils.replaceOnce(body, replacer, inputXmlModel);
			}
			return body;
		}
		return StringUtils.EMPTY;
	}

	private XMLProjection getXMLAnnotation(Class<?> clazz) {
		if (clazz == null) {
			return null;
		}
		for (Annotation annotation : clazz.getDeclaredAnnotations()) {
			if (annotation instanceof XMLProjection) {
				return (XMLProjection) annotation;
			}
		}
		return null;
	}

	private <S> S parseResponseBodyWithProjection(String xml, XMLProjection xmlProjection, Class<S> outputClazz)
			throws SOAPConversionException {
		String projectedXml = xml;
		if (StringUtils.isNotBlank(xmlProjection.CDATA())) {
			projectedXml = this.projectXml(projectedXml, xmlProjection.CDATA(), true, xmlProjection.decodeCDATA());
		}
		if (StringUtils.isNotBlank(xmlProjection.value())) {
			projectedXml = this.projectXml(projectedXml, xmlProjection.value(), false, false);
		}
		return this.parseResponseBody(projectedXml, outputClazz);
	}
	
	private String projectXml(String xml, String xPathExpression, boolean isCData, boolean decode) throws SOAPConversionException {
		Document xmlDoc = this.generateXMLDoc(xml);
		if (xmlDoc != null) {
			XPath xPath = XPathFactory.newInstance().newXPath();
			xPath.setNamespaceContext(new NamespaceResolver(xmlDoc));
			try {
				Node node = (Node) xPath.compile(xPathExpression).evaluate(xmlDoc, XPathConstants.NODE);
				if (node != null) {
					String nodeAsAString = null;
					if (!isCData) {
						nodeAsAString = this.deserializeNode(node);
					} else {
						nodeAsAString = node.getTextContent();
					}
					if (nodeAsAString != null && decode) {
						nodeAsAString = StringEscapeUtils.unescapeHtml(nodeAsAString);
					}
					return nodeAsAString;
				} else {
					throw new SOAPConversionException(
							String.format("The XPath expression evaluation didn't return any result. %s", xPathExpression));
				}
			} catch (XPathExpressionException e) {
				throw new SOAPConversionException("Error compiling XPath expression", e);
			}
		}
		throw new SOAPConversionException(String.format("Error generating XML document for XML {}", xml)) ;
	}

	private String deserializeNode(Node node) throws SOAPConversionException {
		try {
			return XmlUtils.toString(node);
		} catch (TransformerConfigurationException e) {
			throw new SOAPConversionException(
					"Error deserializing SOAP projected node becasue of a wrong configuration", e);
		} catch (TransformerFactoryConfigurationError e) {
			throw new SOAPConversionException(
					"Error deserializing SOAP projected node becasue of a wrong factory configuration", e);
		} catch (TransformerException e) {
			throw new SOAPConversionException("Error deserializing SOAP projected node because of the transformer", e);
		}
	}

	private Document generateXMLDoc(String xmlBody) throws SOAPConversionException {
		try {
			return XmlUtils.buildXmlDoc(xmlBody);
		} catch (ParserConfigurationException e) {
			log.error("Error generating response XML document: {}. Exception: {}", xmlBody, e);
			throw new SOAPConversionException("Error generating response XML document. Exception doc: {}", e);
		} catch (SAXException e) {
			log.error("Error generating response XML document: {}. Exception: {}", xmlBody, e);
			throw new SOAPConversionException("Error generating response XML document. Exception doc: {}", e);
		} catch (IOException e) {
			log.error("Error generating response XML document: {}. Exception: {}", xmlBody, e);
			throw new SOAPConversionException("Error generating response XML document. Exception doc: {}", e);
		}
	}

	private <S> S parseResponseBody(String xml, Class<S> outputClass) throws SOAPConversionException {
		if (xml != null) {
			try {
				return this.xmlMapper.readValue(xml, outputClass);
			} catch (IOException e) {
				throw new SOAPConversionException("Error mapping response to object. Exception: {}", e);
			}
		}
		return null;
	}
}
