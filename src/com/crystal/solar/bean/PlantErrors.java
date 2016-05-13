package com.crystal.solar.bean;

import java.io.Serializable;
import java.util.List;

public class PlantErrors implements Serializable
{

	private static final long serialVersionUID = 3044297808423373628L;

	public String status;

	public String errorMsg;

	public String errTotal;

	public String page;

	public String perPage;

	public List<PlantError> plantErrorList;
}
