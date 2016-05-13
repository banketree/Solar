package com.crystal.solar.ui.tab;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

import com.crystal.solar.R;
import com.crystal.solar.ui.pages.HangyezixunList;
import com.treecore.utils.TStringUtils;

public class HangYePage extends BaseTabActivity {

	private TabHost tabHost;
	private RadioGroup radiogroup_btn;
	private String mCompanyId = "";

	// 【1. 最新生产工艺2. 最新行业时讯3. 最新设备推广】
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mCompanyId = getActivityParameter().get(0);
		if (TStringUtils.isEmpty(mCompanyId)) {
			makeText("数据异常");
			finish();
			return;
		}

		setContentView(R.layout.activity_hangye_page_layout);
		init();
		findViewById(R.id.return_back).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						finish();
					}
				});
	}

	public void init() {
		tabHost = getTabHost();
		tabHost.setup();

		TabSpec spec1 = tabHost.newTabSpec("tab1");

		Intent intent = new Intent(HangYePage.this, HangyezixunList.class);
		intent.putExtra("company", mCompanyId);
		intent.putExtra("type", 1);
		spec1.setContent(intent);
		spec1.setIndicator("tab1");

		TabSpec spec2 = tabHost.newTabSpec("tab2");
		intent = new Intent(HangYePage.this, HangyezixunList.class);
		intent.putExtra("company", mCompanyId);
		intent.putExtra("type", 2);
		spec2.setContent(intent);
		spec2.setIndicator("tab2");

		TabSpec spec3 = tabHost.newTabSpec("tab3");
		intent = new Intent(HangYePage.this, HangyezixunList.class);
		intent.putExtra("company", mCompanyId);
		intent.putExtra("type", 3);
		spec3.setContent(intent);
		spec3.setIndicator("tab3");

		tabHost.addTab(spec1);
		tabHost.addTab(spec2);
		tabHost.addTab(spec3);

		radiogroup_btn = (RadioGroup) findViewById(R.id.radiogroup_btn);
		radiogroup_btn
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						switch (checkedId) {
						case R.id.rdo_gongyi: {
							tabHost.setCurrentTab(0);
							break;
						}
						case R.id.rdo_shixun: {
							tabHost.setCurrentTab(1);
							break;
						}
						case R.id.rdo_device: {
							tabHost.setCurrentTab(2);
							break;
						}
						}
					}
				});
	}
}
