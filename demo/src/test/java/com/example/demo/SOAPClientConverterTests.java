package com.example.demo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.example.demo.models.CDATAContactResponse;
import com.example.demo.models.NamespacedCDATAContactResponse;
import com.example.demo.models.Contact;
import com.example.demo.models.SimpleContactResponse;
import com.example.demo.soap.ISOAPClientConverter;
import com.example.demo.soap.SOAPClientConverter;
import com.example.demo.soap.SOAPConversionException;
import com.example.demo.soap.SOAPXmlBody;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SOAPClientConverterTests {

	private ISOAPClientConverter converter = new SOAPClientConverter();
	private List<Contact> testContacts = getTestContacts();

	private List<Contact> getTestContacts() {
		Contact contact = new Contact();
		contact.setId("1234567");
		contact.setName("LOREM");
		Contact contact2 = new Contact();
		contact2.setId("9999999");
		contact2.setName("TEST NAME");
		return Arrays.asList(contact, contact2);
	}

	@Test
	public void testToBody() throws IOException, SOAPConversionException {
		Map<String, Object> inputs = new HashMap<String, Object>();
		
		inputs.put("$contact", testContacts.get(0));
		inputs.put("$contact2", testContacts.get(1));

		String template = IOUtils.toString(this.getClass().getResourceAsStream("/SOAP_REQUEST_BODY_TEMPLATE_1.txt"),
				"UTF-8");
		SOAPXmlBody requestBody = new SOAPXmlBody(template, inputs);
		String result = this.converter.toBody(requestBody);
		String expectedResult = IOUtils.toString(this.getClass().getResourceAsStream("/SOAP_REQUEST_BODY_TEMPLATE_RESULT_1.txt"),
				"UTF-8");
		assertEquals(result, expectedResult);
	}
	
	@Test
	public void testSimpleFromBody() throws SOAPConversionException, IOException {
		String xml = IOUtils.toString(this.getClass().getResourceAsStream("/SOAP_SUCCESS_RESPONSE_BODY.xml"), "UTF-8");
		SimpleContactResponse response = this.converter.fromBody(xml, SimpleContactResponse.class);
		if (response == null) {
			fail("Empty contact response");
		}
		Contact contacts[] = response.getContacts();
		assertEquals(contacts[0], testContacts.get(0));
		assertEquals(contacts[1], testContacts.get(1));
	}
	
	@Test
	public void testUnsuccessSimpleFromBody() throws SOAPConversionException, IOException {
		String xml = IOUtils.toString(this.getClass().getResourceAsStream("/SOAP_UNSUCCESS_RESPONSE_BODY.xml"), "UTF-8");
		SimpleContactResponse response = this.converter.fromBody(xml, SimpleContactResponse.class);
		if (response == null) {
			fail("Empty contact response");
		}
		com.example.demo.models.SimpleContactResponse.Error error = response.getError();
		if (error == null) {
			fail("Empty error response");
		}
		assertEquals(error.getCode(), "12312");
	}
	
	@Test
	public void testCDATAFromBody() throws SOAPConversionException, IOException {
		String xml = IOUtils.toString(this.getClass().getResourceAsStream("/SOAP_SUCCESS_CDATA_RESPONSE_BODY.xml"), "UTF-8");
		CDATAContactResponse response = this.converter.fromBody(xml, CDATAContactResponse.class);
		if (response == null) {
			fail("Empty contact response");
		}
		Contact contacts[] = response.getContacts();
		assertEquals(contacts[0], testContacts.get(0));
		assertEquals(contacts[1], testContacts.get(1));
	}
	
	@Test
	public void testCDATAFromBodyWithNamespaces() throws SOAPConversionException, IOException {
		String xml = IOUtils.toString(this.getClass().getResourceAsStream("/SOAP_SUCCESS_CDATA_RESPONSE_BODY_2.xml"), "UTF-8");
		NamespacedCDATAContactResponse response = this.converter.fromBody(xml, NamespacedCDATAContactResponse.class);
		if (response == null) {
			fail("Empty contact response");
		}
		Contact contacts[] = response.getContacts();
		assertEquals(contacts[0], testContacts.get(0));
	}

}
