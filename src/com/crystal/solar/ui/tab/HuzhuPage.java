package com.crystal.solar.ui.tab;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

import com.crystal.solar.R;
import com.crystal.solar.ui.pages.Huzhulist;
import com.crystal.solar.ui.pages.ModifyReq;
import com.treecore.utils.TStringUtils;

public class HuzhuPage extends BaseTabActivity {

	private TabHost tabHost;
	private RadioGroup radiogroup_btn;
	private String mCompanyId = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mCompanyId = getActivityParameter().get(0);
		if (TStringUtils.isEmpty(mCompanyId)) {
			makeText("数据异常");
			finish();
			return;
		}

		setContentView(R.layout.activity_huzhupage_layout);
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

		TabSpec spec4 = tabHost.newTabSpec("tab1");
		Intent intent = new Intent(HuzhuPage.this, ModifyReq.class);
		intent.putExtra("company", mCompanyId);
		spec4.setContent(intent);
		spec4.setIndicator("tab1");

		TabSpec spec5 = tabHost.newTabSpec("tab2");
		intent = new Intent(HuzhuPage.this, Huzhulist.class);
		intent.putExtra("company", mCompanyId);
		spec5.setContent(intent);
		spec5.setIndicator("tab2");

		tabHost.addTab(spec4);
		tabHost.addTab(spec5);

		radiogroup_btn = (RadioGroup) findViewById(R.id.radiogroup_btn);
		radiogroup_btn
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						switch (checkedId) {
						case R.id.rdo_daigong: {
							tabHost.setCurrentTab(0);
							break;
						}
						case R.id.rdo_dingdan: {
							tabHost.setCurrentTab(1);
							break;
						}
						}
					}
				});
	}
}
