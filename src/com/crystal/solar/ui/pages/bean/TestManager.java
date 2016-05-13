package com.crystal.solar.ui.pages.bean;

import java.util.ArrayList;

public class TestManager
{

	public static ArrayList<DataTableItem> getDataTables()
	{
		ArrayList<DataTableItem> list = new ArrayList<DataTableItem>();
		String[] names = new String[]
		{ "顶点使用次数", "西嘴使用次数", "点胶头使用次数", "在线固晶精度", "解锁信息成功" };
		for (int i = 0; i < 5; i++)
		{
			DataTableItem item = new DataTableItem();
			item.setName(names[i]);
			item.setToday("0");
			item.setWeek("0");
			item.setMonth("0");
			item.setYear("0");
			item.setTotal("0");
			list.add(item);
		}
		return list;
	}

	public static ArrayList<ImageItem> getImages()
	{
		ArrayList<ImageItem> list = new ArrayList<ImageItem>();

		for (int i = 0; i < 5; i++)
		{
			ImageItem item = new ImageItem();
			item.setName(i + ".jpg");
			item.setSize((i * 10 + 10) + "kb");
			list.add(item);
		}

		return list;
	}

	public static ArrayList<ConfigFileItem> getConfigFileItems()
	{
		ArrayList<ConfigFileItem> list = new ArrayList<ConfigFileItem>();

		for (int i = 0; i < 5; i++)
		{
			ConfigFileItem item = new ConfigFileItem();
			item.setName(i + ".txt");
			item.setSize((i * 10 + 10) + "kb");
			list.add(item);
		}

		return list;
	}

	public static ArrayList<DeviceLogItem> getDeviceLogs()
	{
		ArrayList<DeviceLogItem> list = new ArrayList<DeviceLogItem>();

		for (int i = 0; i < 5; i++)
		{
			DeviceLogItem item = new DeviceLogItem();
			item.setName(i + "log" + System.currentTimeMillis() + ".txt");
			item.setSize((i * 10 + 10) + "kb");
			list.add(item);
		}

		return list;
	}
}
