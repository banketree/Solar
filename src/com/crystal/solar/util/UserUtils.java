package com.crystal.solar.util;

import com.crystal.solar.bean.Account;

public class UserUtils {

	public static boolean isAdmin() {
		if (Account.getInstance().getQuanxian().contains("客户公司查询")) {
			return true;
		}

		return false;
	}

}
