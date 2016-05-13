package com.crystal.solar.bean;

import org.json.JSONObject;

import com.treecore.utils.TJBsonUtils;

class UserInfo {
	public String roleName;
	public String roleValue;
	public String cpName;
	public String cpid;
	public String areaName;
	public String areaid;

	public String quanxian;

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getRoleValue() {
		return roleValue;
	}

	public void setRoleValue(String roleValue) {
		this.roleValue = roleValue;
	}

	public String getCpName() {
		return cpName;
	}

	public void setCpName(String cpName) {
		this.cpName = cpName;
	}

	public String getCpid() {
		return cpid;
	}

	public void setCpid(String cpid) {
		this.cpid = cpid;
	}

	public String getAreaName() {
		return areaName;
	}

	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}

	public String getAreaid() {
		return areaid;
	}

	public void setAreaid(String areaid) {
		this.areaid = areaid;
	}



	public void setJson(JSONObject jsonObject) {
		setRoleName(TJBsonUtils.getString(jsonObject, "rolename"));
		setRoleValue(TJBsonUtils.getString(jsonObject, "rolevalue"));

		setCpName(TJBsonUtils.getString(jsonObject, "cpname"));
		setCpid(TJBsonUtils.getString(jsonObject, "cpid"));
		setAreaName(TJBsonUtils.getString(jsonObject, "areaname"));
		setAreaid(TJBsonUtils.getString(jsonObject, "areaid"));
		// setQuanxian(TJBsonUtils.getString(jsonObject, "quanxian"));
	}
}
