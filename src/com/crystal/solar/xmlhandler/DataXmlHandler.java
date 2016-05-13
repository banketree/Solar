package com.crystal.solar.xmlhandler;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.crystal.solar.bean.Data;
import com.crystal.solar.bean.Detail;
import com.crystal.solar.bean.GPRS;
import com.crystal.solar.bean.Income;
import com.crystal.solar.bean.Inverter;
import com.crystal.solar.bean.Saving;
import com.crystal.solar.bean.Wifi;
import com.crystal.solar.config.Config;
import com.crystal.solar.bean.Error;

public class DataXmlHandler extends DefaultHandler
{

	private static final String DATA = "data";
	private static final String INCOME = "income";
	private static final String DETAIL = "detail";
	private static final String WIFI = "WiFi";
	private static final String GPRS = "GPRS";
	private static final String INVERTER = "inverter";
	private static final String SAVING = "saving";
	private static final String ERROR = "error";

	private String tagName;

	private Data mData;
	private Income income;
	private Detail detail;
	private Wifi wifi;
	private GPRS gprs;
	private Saving saving;
	private Inverter inverter;
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
		if (DATA.equals(localName))
		{
			mData = new Data();

		} else if (INCOME.equals(localName))
		{
			income = new Income();

		} else if (DETAIL.equals(localName))
		{
			detail = new Detail();
			// initialize the WIFI list & GPRS list
			detail.wifiList = new ArrayList<Wifi>();
			detail.gprsList = new ArrayList<GPRS>();

		} else if (WIFI.equals(localName))
		{
			wifi = new Wifi();
			wifi.inverters = new ArrayList<Inverter>();

		} else if (GPRS.equals(localName))
		{
			gprs = new GPRS();
			gprs.inverters = new ArrayList<Inverter>();

		} else if (INVERTER.equals(localName))
		{
			inverter = new Inverter();

		} else if (SAVING.equals(localName))
		{
			saving = new Saving();

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
					inverter.status = data;
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
			} else if (tagName.equals("sunrise"))
			{
				mData.sunrise = data;
			} else if (tagName.equals("sunset"))
			{
				mData.sunset = data;
			} else if (tagName.equals("TodayIncome"))
			{
				income.todayIncome = data;
			} else if (tagName.equals("ActualPower"))
			{
				income.actualPower = data;
			} else if (tagName.equals("TotalIncome"))
			{
				income.totalIncome = data;
			} else if (tagName.equals("etotal"))
			{
				if (income != null)
				{
					income.etotal = data;
				} else if (inverter != null)
				{
					inverter.eTotal = data;
				}
			} else if (tagName.equals("etoday"))
			{
				if (income != null)
				{
					income.etoday = data;
				} else if (inverter != null)
				{
					inverter.eToday = data;
				}
			} else if (tagName.equals("Capacity"))
			{
				detail.capacity = data;
			} else if (tagName.equals("commissioning"))
			{
				detail.commissioning = data;
			} else if (tagName.equals("lastupdated"))
			{
				if (detail.lastupdated == null)
				{
					detail.lastupdated = data;
				} else
				{
					inverter.lastupdated = data;
				}
			} else if (tagName.equals("id"))
			{
				if (gprs != null)
				{
					gprs.id = data;
				} else if (wifi != null)
				{
					wifi.id = data;
				}
			} else if (tagName.equals("SN"))
			{
				inverter.sn = data;
			} else if (tagName.equals("power"))
			{
				inverter.power = data;
			} else if (tagName.equals("TodaySaveTree"))
			{
				saving.today_savedTrees = data;
			} else if (tagName.equals("TotalSaveTree"))
			{
				saving.total_savedTrees = data;
			} else if (tagName.equals("TodaySaveCo2"))
			{
				saving.today_reducedCo2 = data;
			} else if (tagName.equals("TotalSaveCo2"))
			{
				saving.total_reducedCo2 = data;
			}
		}

		if (INCOME.equals(localName))
		{
			mData.income = income;
			income = null;

		} else if (DETAIL.equals(localName))
		{
			mData.detail = detail;
			detail = null;

		} else if (SAVING.equals(localName))
		{
			mData.saving = saving;
			saving = null;

		} else if (GPRS.equals(localName))
		{
			detail.gprsList.add(gprs);
			gprs = null;

		} else if (WIFI.equals(localName))
		{
			detail.wifiList.add(wifi);
			wifi = null;

		} else if (INVERTER.equals(localName))
		{
			if (wifi == null && gprs != null)
			{
				inverter.id = gprs.id;
				inverter.type = Config.TYPE_GPRS;
				gprs.inverters.add(inverter);

			} else if (gprs == null && wifi != null)
			{
				inverter.id = wifi.id;
				inverter.type = Config.TYPE_WIFI;
				wifi.inverters.add(inverter);
			}
			inverter = null;
		}

		this.tagName = null;
	}

	public Object getResult()
	{
		return mData == null ? error : mData;
	}
}
