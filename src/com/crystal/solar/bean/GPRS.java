package com.crystal.solar.bean;

import java.io.Serializable;
import java.util.List;

public class GPRS implements Serializable
{

	private static final long serialVersionUID = 5013225543658436744L;

	public String id;

	public List<Inverter> inverters;
}
