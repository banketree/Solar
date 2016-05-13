package com.crystal.solar.util;

import org.json.JSONObject;

import com.crystal.solar.R;
import com.crystal.solar.SolarApplication;
import com.crystal.solar.bean.Account;
import com.crystal.solar.http.Errors;
import com.treecore.TApplication;
import com.treecore.utils.TJBsonUtils;
import com.treecore.utils.TStringUtils;

public class CoreHttpApi
{
	private static String getHostApi()
	{
		return "http://115.29.172.235/subweb_csarks";//
	}

	private static String getBaseApi()
	{
		return getHostApi() + "/m/index.php";//
	}

	// Post
	public static String getLogin(String username, String password)
	{

		StringBuffer sb = new StringBuffer();
		sb.append("?act=" + CodeUtils.codeString("userlg"));
		sb.append("&uname=" + CodeUtils.codeString(username));
		sb.append("&upwd=" + CodeUtils.codeString(password));

		return getBaseApi() + sb;//
	}

	// get
	public static String getRegister(String username, String password, String email)
	{
		StringBuffer sb = new StringBuffer();
		sb.append("?act=reg");
		sb.append("&uname=" + username);
		sb.append("&upwd=" + password);
		sb.append("&umail=" + email);

		return getBaseApi() + sb;//
	}

	// Post
	public static String getPowerList(String companyId)
	{
		String username = Account.getInstance().getUname();
		String password = Account.getInstance().getUpwd();

		StringBuffer sb = new StringBuffer();
		sb.append("?act=" + CodeUtils.codeString("devicelist"));
		sb.append("&uname=" + CodeUtils.codeString(username));
		sb.append("&upwd=" + CodeUtils.codeString(password));
		sb.append("&comid=" + companyId);

		return getBaseApi() + sb;//
	}

	// POST
	public static String getTongji(String sbid, String companyId)
	{
		String username = Account.getInstance().getUname();
		String password = Account.getInstance().getUpwd();

		StringBuffer sb = new StringBuffer();
		sb.append("?act=" + CodeUtils.codeString("tongji"));
		sb.append("&uname=" + CodeUtils.codeString(username));
		sb.append("&upwd=" + CodeUtils.codeString(password));
		sb.append("&sbid=" + sbid);
		sb.append("&comid=" + companyId);
		return getBaseApi() + sb;//
	}

	public static String getPowerData(String companyId)
	{
		String username = Account.getInstance().getUname();
		String password = Account.getInstance().getUpwd();

		StringBuffer sb = new StringBuffer();
		sb.append("?act=" + CodeUtils.codeString("prostate"));
		sb.append("&uname=" + CodeUtils.codeString(username));
		sb.append("&upwd=" + CodeUtils.codeString(password));
		sb.append("&sbid=124");
		sb.append("&times=2014-9-25");
		sb.append("&comid=" + companyId);
		return getBaseApi() + sb;//
	}

	// 当前运行时间接口
	public static String getRuntime(String times, String etimes)
	{
		// act= runtime【接口类型】
		// uname=admin888【登陆号码】
		// upwd=admin888【登陆密码】
		// sbid=124【设备编号】
		// times=2014-9-27【日期】
		String username = Account.getInstance().getUname();
		String password = Account.getInstance().getUpwd();
		String cpId = Account.getInstance().getCpid();

		StringBuffer sb = new StringBuffer();
		sb.append("?act=" + CodeUtils.codeString("runtime"));
		sb.append("&uname=" + CodeUtils.codeString(username));
		sb.append("&upwd=" + CodeUtils.codeString(password));
		sb.append("&sbid=" + SolarApplication.getInstance().getStationID());
		sb.append("&times=" + times);
		sb.append("&etimes=" + etimes);
		sb.append("&comid=" + cpId);

		return getBaseApi() + sb;//
	}

	public static String getImageFile(String companyId)
	{
		String username = Account.getInstance().getUname();
		String password = Account.getInstance().getUpwd();
		String sbid = "124";

		StringBuffer sb = new StringBuffer();
		sb.append("?act=" + CodeUtils.codeString("piclist"));
		sb.append("&uname=" + CodeUtils.codeString(username));
		sb.append("&upwd=" + CodeUtils.codeString(password));
		sb.append("&sbid=" + sbid);
		sb.append("&comid=" + companyId);

		return getBaseApi() + sb;//
	}

	public static String getConfigFile(String companyId)
	{
		String username = Account.getInstance().getUname();
		String password = Account.getInstance().getUpwd();
		String sbid = "124";

		StringBuffer sb = new StringBuffer();
		sb.append("?act=" + CodeUtils.codeString("pzlist"));
		sb.append("&uname=" + CodeUtils.codeString(username));
		sb.append("&upwd=" + CodeUtils.codeString(password));
		sb.append("&sbid=" + sbid);
		sb.append("&comid=" + companyId);
		return getBaseApi() + sb;//
	}

	public static String getDeviceLog(String companyId)
	{
		String username = Account.getInstance().getUname();
		String password = Account.getInstance().getUpwd();
		String sbid = "124";

		StringBuffer sb = new StringBuffer();
		sb.append("?act=" + CodeUtils.codeString("lglist"));
		sb.append("&uname=" + CodeUtils.codeString(username));
		sb.append("&upwd=" + CodeUtils.codeString(password));
		sb.append("&sbid=" + sbid);
		sb.append("&comid=" + companyId);
		return getBaseApi() + sb;//
	}

	public static String getConfigInfo(String companyId)
	{
		String username = Account.getInstance().getUname();
		String password = Account.getInstance().getUpwd();
		String sbid = "124";

		StringBuffer sb = new StringBuffer();
		sb.append("?act=" + CodeUtils.codeString("pzinfo"));
		sb.append("&uname=" + CodeUtils.codeString(username));
		sb.append("&upwd=" + CodeUtils.codeString(password));
		sb.append("&sbid=" + sbid);
		sb.append("&comid=" + companyId);
		return getBaseApi() + sb;//
	}

	public static String getShouhouapply(int type, String contents, String companyId)
	{
		String username = Account.getInstance().getUname();
		String password = Account.getInstance().getUpwd();

		StringBuffer sb = new StringBuffer();
		sb.append("?act=" + CodeUtils.codeString("addhuzhu"));
		sb.append("&uname=" + CodeUtils.codeString(username));
		sb.append("&upwd=" + CodeUtils.codeString(password));
		sb.append("&types=" + type);
		sb.append("&remark=" + contents);
		sb.append("&comid=" + companyId);
		return getBaseApi() + sb;//
	}

	public static String getOrders(String page, String readid, String companyId)
	{
		String username = Account.getInstance().getUname();
		String password = Account.getInstance().getUpwd();

		StringBuffer sb = new StringBuffer();
		sb.append("?act=" + CodeUtils.codeString("orderlist"));
		sb.append("&uname=" + CodeUtils.codeString(username));
		sb.append("&upwd=" + CodeUtils.codeString(password));
		sb.append("&page" + page);
		if (!TStringUtils.isEmpty(readid))
			sb.append("&readid=" + readid);
		sb.append("&comid=" + companyId);

		return getBaseApi() + sb;//
	}

	public static String getNewOrders(String readid, String companyId)
	{
		String username = Account.getInstance().getUname();
		String password = Account.getInstance().getUpwd();

		StringBuffer sb = new StringBuffer();
		sb.append("?act=" + CodeUtils.codeString("neworderlist"));
		sb.append("&uname=" + CodeUtils.codeString(username));
		sb.append("&upwd=" + CodeUtils.codeString(password));
		sb.append("&readid=" + readid);
		sb.append("&comid=" + companyId);

		return getBaseApi() + sb;//
	}

	public static String getShouhouList(String page, String readid, String companyId)
	{
		String username = Account.getInstance().getUname();
		String password = Account.getInstance().getUpwd();

		StringBuffer sb = new StringBuffer();
		sb.append("?act=" + CodeUtils.codeString("applylist"));
		sb.append("&uname=" + CodeUtils.codeString(username));
		sb.append("&upwd=" + CodeUtils.codeString(password));
		sb.append("&page" + page);
		if (!TStringUtils.isEmpty(readid))
			sb.append("&readid=" + readid);
		sb.append("&comid=" + companyId);

		return getBaseApi() + sb;//
	}

	public static String getNewShouhoulist(String readid, String companyId)
	{
		String username = Account.getInstance().getUname();
		String password = Account.getInstance().getUpwd();

		StringBuffer sb = new StringBuffer();
		sb.append("?act=" + CodeUtils.codeString("newapplylist"));
		sb.append("&uname=" + CodeUtils.codeString(username));
		sb.append("&upwd=" + CodeUtils.codeString(password));
		sb.append("&readid=" + readid);
		sb.append("&comid=" + companyId);

		return getBaseApi() + sb;//
	}

	public static String getPowerTableData(String sbid, String date, String companyId)
	{
		String username = Account.getInstance().getUname();
		String password = Account.getInstance().getUpwd();

		StringBuffer sb = new StringBuffer();
		sb.append("?act=" + CodeUtils.codeString("prostatepic"));
		sb.append("&uname=" + CodeUtils.codeString(username));
		sb.append("&upwd=" + CodeUtils.codeString(password));
		sb.append("&sbid=124");
		sb.append("&times=" + date);
		sb.append("&comid=" + companyId);
		return getBaseApi() + sb;//
	}

	// 产能状态图表统计接口
	public static String getChannengTongjiData(final String sbid, final String startDate, final String endDate, final String types, String companyId)
	{
		// act= channengtongji【接口类型】
		// uname=admin888【登陆号码】
		// upwd=admin888【登陆密码】
		// bh=124【设备编号】
		// Time=2014-9-27【开始日期】
		// Etime=2014-9-27【截止日期】
		// types=类型【0今日统计1周统计2月统计3，年统计】
		String username = Account.getInstance().getUname();
		String password = Account.getInstance().getUpwd();

		StringBuffer sb = new StringBuffer();
		sb.append("?act=" + CodeUtils.codeString("channengtongji"));
		sb.append("&uname=" + CodeUtils.codeString(username));
		sb.append("&upwd=" + CodeUtils.codeString(password));
		sb.append("&bh=" + SolarApplication.getInstance().getStationID());

		sb.append("&Time=" + startDate);
		sb.append("&Etime=" + endDate);
		sb.append("&types=" + types);
		sb.append("&comid=" + companyId);
		return getBaseApi() + sb;//
	}

	// 产能状态图表横图统计接口
	public static String getStackedbarData(String sbid, String date, String companyId)
	{
		// act= todayprostate【接口类型】
		// uname=admin888【登陆号码】
		// upwd=admin888【登陆密码】
		// sbid=124【设备编号】
		// times=2014-9-27【日期】
		String username = Account.getInstance().getUname();
		String password = Account.getInstance().getUpwd();

		StringBuffer sb = new StringBuffer();
		sb.append("?act=" + CodeUtils.codeString("todayprostate"));
		sb.append("&uname=" + CodeUtils.codeString(username));
		sb.append("&upwd=" + CodeUtils.codeString(password));
		sb.append("&sbid=" + SolarApplication.getInstance().getStationID());
		sb.append("&times=" + date);
		sb.append("&comid=" + companyId);
		return getBaseApi() + sb;//
	}

	public static String getOrderTracklist(String page, String companyId)
	{
		String username = Account.getInstance().getUname();
		String password = Account.getInstance().getUpwd();

		StringBuffer sb = new StringBuffer();
		sb.append("?act=" + CodeUtils.codeString("orderlist"));
		sb.append("&uname=" + CodeUtils.codeString(username));
		sb.append("&upwd=" + CodeUtils.codeString(password));
		sb.append("&page" + page);
		sb.append("&comid=" + companyId);
		return getBaseApi() + sb;//

	}

	public static String getOrderTrackDetails(String bh, String companyId)
	{
		String username = Account.getInstance().getUname();
		String password = Account.getInstance().getUpwd();

		StringBuffer sb = new StringBuffer();
		sb.append("?act=" + CodeUtils.codeString("orderdetail"));
		sb.append("&uname=" + CodeUtils.codeString(username));
		sb.append("&upwd=" + CodeUtils.codeString(password));
		sb.append("&bh=" + bh);
		sb.append("&comid=" + companyId);

		return getBaseApi() + sb;//
	}

	public static String getHuzulist(String page, String companyId)
	{
		String username = Account.getInstance().getUname();
		String password = Account.getInstance().getUpwd();

		StringBuffer sb = new StringBuffer();
		sb.append("?act=" + CodeUtils.codeString("huzhulist"));
		sb.append("&uname=" + CodeUtils.codeString(username));
		sb.append("&upwd=" + CodeUtils.codeString(password));
		sb.append("&page=" + page);
		sb.append("&comid=" + companyId);
		return getBaseApi() + sb;//
	}

	public static String getHangyelist(String page, String type, String readid, String companyId)
	{
		String username = Account.getInstance().getUname();
		String password = Account.getInstance().getUpwd();

		StringBuffer sb = new StringBuffer();
		sb.append("?act=" + CodeUtils.codeString("newslist"));
		sb.append("&uname=" + CodeUtils.codeString(username));
		sb.append("&upwd=" + CodeUtils.codeString(password));
		sb.append("&page=" + page);
		sb.append("&types=" + type);
		if (TStringUtils.isEmpty(readid))
			sb.append("&readid=" + readid);
		sb.append("&comid=" + companyId);
		return getBaseApi() + sb;//
	}

	public static String getHangyelistDetail(String id, String companyId)
	{
		String username = Account.getInstance().getUname();
		String password = Account.getInstance().getUpwd();

		StringBuffer sb = new StringBuffer();
		sb.append("?act=" + CodeUtils.codeString("newsdetail"));
		sb.append("&uname=" + CodeUtils.codeString(username));
		sb.append("&upwd=" + CodeUtils.codeString(password));
		sb.append("&id=" + id);
		sb.append("&comid=" + companyId);
		return getBaseApi() + sb;//
	}

	public static String getUploadGPS(String sbid, String lat, String lang, String companyId)
	{
		String username = Account.getInstance().getUname();
		String password = Account.getInstance().getUpwd();

		StringBuffer sb = new StringBuffer();
		sb.append("?act=" + CodeUtils.codeString("addgps"));
		sb.append("&uname=" + CodeUtils.codeString(username));
		sb.append("&upwd=" + CodeUtils.codeString(password));
		sb.append("&sbid=" + sbid);
		sb.append("&gpsx=" + lat);
		sb.append("&gpsy=" + lang);
		sb.append("&comid=" + companyId);
		return getBaseApi() + sb;//
	}

	public static String getCompinfo(String companyId)
	{
		String username = Account.getInstance().getUname();
		String password = Account.getInstance().getUpwd();

		StringBuffer sb = new StringBuffer();
		sb.append("?act=" + CodeUtils.codeString("company"));
		sb.append("&uname=" + CodeUtils.codeString(username));
		sb.append("&upwd=" + CodeUtils.codeString(password));
		sb.append("&comid=" + companyId);
		return getBaseApi() + sb;//
	}

	public static String getFuyuPower(String companyId)
	{
		String username = Account.getInstance().getUname();
		String password = Account.getInstance().getUpwd();

		StringBuffer sb = new StringBuffer();
		sb.append("?act=" + CodeUtils.codeString("fycn"));
		sb.append("&uname=" + CodeUtils.codeString(username));
		sb.append("&upwd=" + CodeUtils.codeString(password));
		sb.append("&comid=" + companyId);
		return getBaseApi() + sb;//
	}

	public static String getLoginQuanXian()
	{
		String username = Account.getInstance().getUname();
		String password = Account.getInstance().getUpwd();

		StringBuffer sb = new StringBuffer();
		sb.append("?act=" + CodeUtils.codeString("power"));
		sb.append("&uname=" + CodeUtils.codeString(username));
		sb.append("&upwd=" + CodeUtils.codeString(password));
		return getBaseApi() + sb;//
	}

	public static String getLoginComplist()
	{
		String username = Account.getInstance().getUname();
		String password = Account.getInstance().getUpwd();

		StringBuffer sb = new StringBuffer();
		sb.append("?act=" + CodeUtils.codeString("company"));
		sb.append("&uname=" + CodeUtils.codeString(username));
		sb.append("&upwd=" + CodeUtils.codeString(password));
		return getBaseApi() + sb;//
	}

	public static String getResponseString(byte[] responseByte) throws Exception
	{
		return new String(responseByte, "UTF-8");
	}

	public static JSONObject getResponseJSONObject(byte[] responseByte) throws Exception
	{
		String responseString = new String(responseByte, "UTF-8");
		if (responseString.equals(Errors.ERROR_SERVER))
		{
			throw new Exception(TApplication.getResString(R.string.tip_pls_server_error));
		}

		JSONObject jsonObject = null;
		jsonObject = new JSONObject(responseString.substring(responseString.indexOf("{"), responseString.lastIndexOf("}") + 1));
		return jsonObject;
	}

	public static JSONObject checkResponseStatus(byte[] responseByte) throws Exception
	{
		JSONObject jsonObject = getResponseJSONObject(responseByte);
		checkResponseStatus(jsonObject);
		return jsonObject;
	}

	public static boolean checkResponseStatus(JSONObject jsonObject) throws Exception
	{
		if (jsonObject == null)
			throw new Exception("服务器返回数据异常！");

		// err：0成功1 接口类型参数为空2.登陆账号为空3.登陆密码为空4. 帐号不存在
		String result = TJBsonUtils.getString(jsonObject, "err");
		if ("0".equals(result))
		{
		} else if ("1".equals(result))
		{
			throw new Exception("参数为空");
		} else if ("2".equals(result))
		{
			throw new Exception("登陆账号为空");
		} else if ("3".equals(result))
		{
			throw new Exception("登陆密码为空");
		} else if ("4".equals(result))
		{
			throw new Exception("帐号不存在");
		} else
		{
			throw new Exception("参数异常");
		}

		return true;
	}
}
