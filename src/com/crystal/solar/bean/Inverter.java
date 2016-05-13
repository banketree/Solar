package com.crystal.solar.bean;

import java.io.Serializable;

public class Inverter implements Serializable
{

	private static final long serialVersionUID = -5268497237679154534L;

	public String id;

	public int type; // WIFI: 0 GPRS: 1

	public String sn;

	public String status;

	public String power;

	public String eToday;

	public String eTotal;

	public String lastupdated;
}
