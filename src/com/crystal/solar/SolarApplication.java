package com.crystal.solar;

import com.baidu.mapapi.SDKInitializer;
import com.crystal.solar.bean.Account;
import com.crystal.solar.crash.EmailIntentSender;
import com.treecore.TApplication;
import com.treecore.crash.TCrash;
import com.treecore.utils.network.TNetworkStateReceiver;

public class SolarApplication extends TApplication
{
	private String stationID = "";
	private String address = "";
	private boolean isNeedCom = false;

	@Override
	public void onCreate()
	{
		super.onCreate();
		// 奔溃处理
		TCrash.getInstance().setReportSender(new EmailIntentSender(this));
		TNetworkStateReceiver.getInstance().initConfig(this);
		TNetworkStateReceiver.getInstance().registerObserver(this);

		SDKInitializer.initialize(this);
		Account.getInstance().initConfig();
	}

	@Override
	public void appExit(Boolean isBackground)
	{
		super.appExit(isBackground);

		if (!isBackground)
		{
			Account.getInstance().release();
		}
	}

	@Override
	public void onAppCrash(String crashFile)
	{
		super.onAppCrash(crashFile);
	}

	public static SolarApplication getInstance()
	{ // 获取程序实例
		return (SolarApplication) mThis;
	}

	public String getStationID()
	{
		return stationID;
	}

	public void setStationID(String stationID)
	{
		this.stationID = stationID;
	}

	public String getAddress()
	{
		return address;
	}

	public void setAddress(String address)
	{
		this.address = address;
	}

	public boolean isNeedCom()
	{
		return isNeedCom;
	}

	public void setNeedCom(boolean isNeedCom)
	{
		this.isNeedCom = isNeedCom;
	}

}