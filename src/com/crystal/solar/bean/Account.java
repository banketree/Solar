package com.crystal.solar.bean;

import org.json.JSONObject;

import com.treecore.TIGlobalInterface;
import com.treecore.utils.TStringUtils;
import com.treecore.utils.config.TPreferenceConfig;

public class Account extends UserInfo implements TIGlobalInterface {
	private static String Tag = Account.class.getSimpleName();
	private static Account mThis = null;
	private static String Field_Name = Tag + ".uname";
	private static String Field_Password = Tag + ".upwd";
	private static String Field_Server = Tag + ".server";
	private static String Field_Company = Tag + ".company";
	private static String Field_Quanxian = Tag + ".quanxian";

	public static Account getInstance() {
		if (mThis == null)
			mThis = new Account();
		return mThis;
	}

	@Override
	public void initConfig() {
	}

	@Override
	public void release() {
		mThis = null;
	}

	public String getUname() {
		return TPreferenceConfig.getInstance().getString(Field_Name, "");
	}

	public void setUname(String uname) {
		TPreferenceConfig.getInstance().setString(Field_Name, uname);
	}

	public String getUpwd() {
		return TPreferenceConfig.getInstance().getString(Field_Password, "");
	}

	public void setUpwd(String upwd) {
		TPreferenceConfig.getInstance().setString(Field_Password, upwd);
	}

	public String getCompany() {
		return TPreferenceConfig.getInstance().getString(Field_Company, "");
	}

	public void setCompany(String company) {
		TPreferenceConfig.getInstance().setString(Field_Company, company);
	}

	public void setServer(String server) {
		TPreferenceConfig.getInstance().setString(Field_Server, server);
	}

	public String getQuanxian() {
		return TPreferenceConfig.getInstance().getString(Field_Quanxian, "");
	}

	public void setQuanxian(String quanxian) {
		TPreferenceConfig.getInstance().setString(Field_Quanxian, quanxian);
	}

	public void loadServer() {
		String json = TPreferenceConfig.getInstance().getString(Field_Server,
				"");
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(json);
			setJson(jsonObject);
		} catch (Exception e) {
		}
		jsonObject = null;
	}

	public void layout() {
		setServer("");
		release();
	}

	public boolean isAccountValid() {
		return !TStringUtils.isEmpty(getUname())
				&& !TStringUtils.isEmpty(getUpwd());
	}

	public boolean isDataValid() {
		return !TStringUtils.isEmpty(getRoleName())
				|| !TStringUtils.isEmpty(getRoleValue())
				|| !TStringUtils.isEmpty(getCpName())
				|| !TStringUtils.isEmpty(getCpid())
				|| !TStringUtils.isEmpty(getAreaid())
				|| !TStringUtils.isEmpty(getAreaName())
				|| !TStringUtils.isEmpty(getQuanxian());
	}

}
