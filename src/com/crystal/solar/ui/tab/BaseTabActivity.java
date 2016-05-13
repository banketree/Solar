package com.crystal.solar.ui.tab;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

import com.crystal.solar.bean.Account;
import com.crystal.solar.ui.CompanyList;
import com.crystal.solar.ui.Login;
import com.crystal.solar.ui.MainPage;
import com.crystal.solar.ui.PlantList;
import com.crystal.solar.ui.Splash;
import com.crystal.solar.util.CoreEventUtils;
import com.crystal.solar.util.UserUtils;
import com.treecore.TBroadcastByInner;
import com.treecore.activity.TActivity;
import com.treecore.activity.TActivityManager;
import com.treecore.activity.TTabActivity;
import com.treecore.utils.TActivityUtils;

public class BaseTabActivity extends TTabActivity {

	@Override
	public void processEventByInner(Intent intent) {
		super.processEventByInner(intent);

		int mainEvent = intent.getIntExtra(TBroadcastByInner.MAINEVENT, 0);
		int subEvent = intent.getIntExtra(TBroadcastByInner.EVENT, 0);

		if (mainEvent == CoreEventUtils.Activity_Self_Destory) {
			finish();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (Account.getInstance().isDataValid()
				&& Account.getInstance().isAccountValid()) {
		} else {
			TActivityUtils.jumpToNewTopActivity(mContext, Login.class);
			TBroadcastByInner.sentEvent(CoreEventUtils.Activity_Self_Destory);
			return;
		}

		if (TActivityManager.getInstance().getSizeOfActivityStack() == 1) {
			if (UserUtils.isAdmin()) {
				TActivityUtils.jumpToNewActivity(mContext, CompanyList.class);
			} else {
				TActivityUtils.jumpToNewActivity(mContext, MainPage.class,
						Account.getInstance().getCompany());
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	public void startActivity(Intent intent) {
		super.startActivity(intent);
		overridePendingTransition(android.R.anim.fade_in,
				android.R.anim.fade_out);
	}

	@Override
	public void startActivityForResult(Intent intent, int requestCode) {
		super.startActivityForResult(intent, requestCode);
		overridePendingTransition(android.R.anim.fade_in,
				android.R.anim.fade_out);
	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(android.R.anim.fade_in,
				android.R.anim.fade_out);
	}
}