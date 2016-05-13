package com.crystal.solar.ui;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.crystal.solar.R;
import com.crystal.solar.bean.Account;
import com.crystal.solar.ui.tab.HangYePage;
import com.crystal.solar.ui.tab.HuzhuPage;
import com.crystal.solar.ui.tab.XianChangePage;
import com.crystal.solar.util.CoreEventUtils;
import com.treecore.TBroadcastByInner;
import com.treecore.utils.TActivityUtils;
import com.treecore.utils.TStringUtils;

public class MainPage extends BaseActivity
{
	private View text_tab1;
	private View text_tab2;
	private View text_tab3;
	private View text_tab4;
	private View text_com_list;
	private String mCompanyId = "";

	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		mCompanyId = getActivityParameter().get(0);

		setContentView(R.layout.activity_main_page_layout);
		init();
	}

	public void init()
	{
		text_tab1 = findViewById(R.id.text_tab1);
		text_tab2 = findViewById(R.id.text_tab2);
		text_tab3 = findViewById(R.id.text_tab3);
		text_tab4 = findViewById(R.id.text_tab4);
		findViewById(R.id.com_layout);
		text_com_list = findViewById(R.id.text_com_list);

		text_tab1.setOnClickListener(clickListener);
		text_tab2.setOnClickListener(clickListener);
		text_tab3.setOnClickListener(clickListener);
		text_tab4.setOnClickListener(clickListener);
		text_com_list.setOnClickListener(clickListener);
	}

	private OnClickListener clickListener = new OnClickListener()
	{
		public void onClick(View v)
		{
			switch (v.getId())
			{
			case R.id.text_tab1:
			{
				TActivityUtils.jumpToActivity(mContext, XianChangePage.class, mCompanyId);
				break;
			}
			case R.id.text_tab2:
			{
				TActivityUtils.jumpToActivity(mContext, HangYePage.class, mCompanyId);

				break;
			}
			case R.id.text_tab3:
			{
				TActivityUtils.jumpToActivity(mContext, HuzhuPage.class, mCompanyId);
				break;
			}
			case R.id.text_tab4:
			{
				TActivityUtils.jumpToActivity(mContext, OrderTrackList.class, mCompanyId);
				break;
			}
			case R.id.text_com_list:
			{
				TActivityUtils.jumpToNewTopActivity(mContext, CompanyList.class);
				break;
			}
			}
		}
	};

	@Override
	protected void onResume()
	{
		super.onResume();

		if (!Account.getInstance().isDataValid() || TStringUtils.isEmpty(mCompanyId))
		{
			Account.getInstance().release();
			TActivityUtils.jumpToNewTopActivity(mContext, Login.class);
			makeText("请登录");
			TBroadcastByInner.sentPostEvent(CoreEventUtils.Activity_Self_Destory, 0);
			finish();
			return;
		}
	};
}
