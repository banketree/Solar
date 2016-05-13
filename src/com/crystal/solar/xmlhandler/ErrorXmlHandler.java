package com.crystal.solar.xmlhandler;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.crystal.solar.bean.PlantError;
import com.crystal.solar.bean.PlantErrors;
import com.crystal.solar.bean.Error;

public class ErrorXmlHandler extends DefaultHandler
{

	private static final String ERRORS = "errors";
	private static final String ERROR = "error";

	private String tagName;

	private PlantErrors errors;
	private PlantError plantError;
	private Error error;

	private boolean isError;

	private StringBuffer sb = new StringBuffer();

	@Override
	public void startDocument() throws SAXException
	{
		super.startDocument();
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
	{
		if (ERRORS.equals(localName))
		{
			errors = new PlantErrors();
			errors.plantErrorList = new ArrayList<PlantError>();
		} else if (ERROR.equals(localName))
		{
			if (errors != null)
			{
				plantError = new PlantError();
			} else
			{
				error = new Error();
				isError = true;
			}
		}
		this.tagName = localName;
		sb.setLength(0);

	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException
	{
		sb.append(ch, start, length);
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException
	{
		String data = sb.toString();
		if (tagName != null)
		{
			if (tagName.equals("status"))
			{
				if (isError)
				{
					error.status = data;
				} else
				{
					errors.status = data;
				}
			} else if (tagName.equals("errorCode"))
			{
				if (isError)
				{
					error.errorCode = data;
				}
			} else if (tagName.equals("errorMessage"))
			{
				if (isError)
				{
					error.errorMessage = data;
				} else
				{
					errors.errorMsg = data;
				}
			} else if (tagName.equals("errTotal"))
			{
				errors.errTotal = data;
			} else if (tagName.equals("page"))
			{
				errors.page = data;
			} else if (tagName.equals("perPage"))
			{
				errors.perPage = data;
			} else if (tagName.equals("DateTime"))
			{
				plantError.DateTime = data;
			} else if (tagName.equals("inverter"))
			{
				plantError.inverter = data;
			} else if (tagName.equals("invErrCode"))
			{
				plantError.invErrorCode = data;
			} else if (tagName.equals("state"))
			{
				plantError.state = data;
			} else if (tagName.equals("text"))
			{
				plantError.text = data;
			}

		}

		if (ERROR.equals(localName))
		{
			if (errors != null)
			{
				errors.plantErrorList.add(plantError);
				plantError = null;
			}
		}
		this.tagName = null;
	}

	public Object getResult()
	{
		return errors == null ? error : errors;
	}
}
