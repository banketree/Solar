package com.crystal.solar.xmlhandler;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import com.crystal.solar.bean.Error;
import com.crystal.solar.bean.Login;

public class LoginXmlHandler extends DefaultHandler {

	private static final String LOGIN = "login";
	private static final String ERROR = "error";

	private String tagName;

	private Login login;
	private Error error;

	private boolean isError;

	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		if (LOGIN.equals(localName)) {
			login = new Login();
		} else if (ERROR.equals(localName)) {
			error = new Error();
			isError = true;
		}
		this.tagName = localName;

	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		if (tagName != null) {
			String data = new String(ch, start, length);
			if (tagName.equals("status")) {
				if (isError) {
					error.status = data;
				} else {
					login.status = data;
				}
			}
			if (tagName.equals("errorCode")) {
				if (isError) {
					error.errorCode = data;
				} else {
					login.errorCode = data;
				}
			}
			if (tagName.equals("errorMessage")) {
				if (isError) {
					error.errorMessage = data;
				} else {
					login.errorMessage = data;
				}
			}
			if (tagName.equals("userID")) {
				login.userID = data;
			}
			if (tagName.equals("userName")) {
				login.userName = data;
			}
			if (tagName.equals("token")) {
				login.token = data;
			}

		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		this.tagName = null;
	}

	public Object getResult() {
		return login == null ? error : login;
	}
}
