package com.crystal.solar.bean;

import java.io.Serializable;
import java.util.List;

public class Wifi implements Serializable
{

	private static final long serialVersionUID = 5550496236396088929L;

	public String id;

	public List<Inverter> inverters;
}
