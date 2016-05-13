package com.crystal.solar.ui.pages.bean;

public class OrderItem
{

	private String id;// :【记录id】
	private String title;// :【订单描述】
	private String addtime;// :【提交时间】
	private String remark;// :【备注】

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
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

}
