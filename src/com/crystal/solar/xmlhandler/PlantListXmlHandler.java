package com.crystal.solar.xmlhandler;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.crystal.solar.bean.Error;
import com.crystal.solar.bean.Power;

public class PlantListXmlHandler extends DefaultHandler
{

	private static final String LIST = "list";
	private static final String POWER = "power";
	private static final String ERROR = "error";

	private String tagName;

	private List<Power> powerList;
	private Power power;
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
		if (LIST.equals(localName))
		{
			powerList = new ArrayList<Power>();
		} else if (POWER.equals(localName))
		{
			power = new Power();
		} else if (ERROR.equals(localName))
		{
			error = new Error();
			isError = true;
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
				} else if (!"".equals(data) && !data.equals("true") && !data.equals("false"))
				{
					power.status = data;
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
				}
			} else if (tagName.equals("stationID"))
			{
				power.stationID = data;
			} else if (tagName.equals("timezone"))
			{
				power.timeZone = data;
			} else if (tagName.equals("name"))
			{
				power.name = data;
			} else if (tagName.equals("pic"))
			{
				power.pic = data;
			} else if (tagName.equals("ActualPower"))
			{
				power.actualPower = data;
			} else if (tagName.equals("TotalIncome"))
			{
				power.income = data;
			} else if (tagName.equals("etotal"))
			{
				power.eTotal = data;
			} else if (tagName.equals("etoday"))
			{
				power.eToday = data;
			} else if (tagName.equals("unit"))
			{
				power.unit = data;
			} else if (tagName.equals("country"))
			{
				power.country = data;
			} else if (tagName.equals("province"))
			{
				power.province = data;
			} else if (tagName.equals("city"))
			{
				power.city = data;
			} else if (tagName.equals("street"))
			{
				power.street = data;
			}
		}

		if (POWER.equals(localName))
		{
			powerList.add(power);
			power = null;
		}

		this.tagName = null;
	}

	public Object getResult()
	{
		return powerList == null ? error : powerList;
	}
}
