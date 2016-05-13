package com.crystal.solar.ui.pages.bean;

public class ShouhouItem
{

	private String id;// :【记录id】
	private String apply;// :【订单描述】
	private String addtime;// :【提交时间】
	private String remark;// :【备注】
	private String types;

	public String getTypes()
	{
		return types;
	}

	public void setTypes(String types)
	{
		this.types = types;
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public String getAddtime()
	{
		return addtime;
	}

	public void setAddtime(String addtime)
	{
		this.addtime = addtime;
	}

	public String getRemark()
	{
		return remark;
	}

	public void setRemark(String remark)
	{
		this.remark = remark;
	}

	public String getApply()
	{
		return apply;
	}

	public void setApply(String apply)
	{
		this.apply = apply;
	}

}
