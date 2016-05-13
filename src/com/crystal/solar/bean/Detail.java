package com.crystal.solar.bean;

import java.io.Serializable;
import java.util.List;

public class Detail implements Serializable
{

	private static final long serialVersionUID = -8806802757559619891L;

	public String capacity;

	public String commissioning;

	public String lastupdated;

	public List<Wifi> wifiList;

	public List<GPRS> gprsList;

}
