package com.example.demo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.junit4.SpringRunner;

import com.example.demo.models.CDATAContactResponse;
import com.example.demo.models.Contact;
import com.example.demo.soap.SOAPClient;
import com.example.demo.soap.SOAPConversionException;
import com.example.demo.soap.SOAPXmlBody;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SOAPClientTests {

	private SOAPClient client;
	private List<Contact> testContacts = getTestContacts();

	@Before
	public void init() {
		this.client = SOAPClient.builder().serviceUrl("http://www.mocky.io/v2/5a8815a33000004d007f93cb").build();
	}

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
	public void testCDATAResponse() throws SOAPConversionException, IOException {
		CDATAContactResponse response = this.client.invoke(null, HttpMethod.GET, new SOAPXmlBody(),
				CDATAContactResponse.class);
		if (response == null) {
			fail("Empty contact response");
		}
		Contact contacts[] = response.getContacts();
		assertEquals(contacts[0], testContacts.get(0));
		assertEquals(contacts[1], testContacts.get(1));
	}

}
