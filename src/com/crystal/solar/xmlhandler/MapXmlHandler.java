package com.crystal.solar.xmlhandler;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.crystal.solar.bean.Error;
import com.crystal.solar.bean.Station;

public class MapXmlHandler extends DefaultHandler
{
	private static final String STATIONS = "stations";
	private static final String STATION = "station";
	private static final String ERROR = "error";

	private String tagName;

	private List<Station> stationList;
	private Station station;
	private Error error;

	private boolean isError;

	@Override
	public void startDocument() throws SAXException
	{
		super.startDocument();
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
	{
		if (STATIONS.equals(localName))
		{
			stationList = new ArrayList<Station>();
		} else if (STATION.equals(localName))
		{
			station = new Station();
		} else if (ERROR.equals(localName))
		{
			error = new Error();
			isError = true;
		}
		this.tagName = localName;

	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException
	{
		if (tagName != null)
		{
			String data = new String(ch, start, length);
			if (tagName.equals("status"))
			{
				if (isError)
				{
					error.status = data;
				}
			}
			if (tagName.equals("errorCode"))
			{
				if (isError)
				{
					error.errorCode = data;
				}
			}
			if (tagName.equals("errorMessage"))
			{
				if (isError)
				{
					error.errorMessage = data;
				}
			}
			if (tagName.equals("name"))
			{
				station.name = data;
			}
			if (tagName.equals("longitude"))
			{
				station.longitude = data;
			}
			if (tagName.equals("latitude"))
			{
				station.latitude = data;
			}
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException
	{
		if (STATION.equals(localName))
		{
			stationList.add(station);
			station = null;
		}

		this.tagName = null;
	}

	public Object getResult()
	{
		return stationList == null ? error : stationList;
	}
}
