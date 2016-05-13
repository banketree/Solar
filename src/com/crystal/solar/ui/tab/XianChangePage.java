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
import com.crystal.solar.ui.PlantList;
import com.crystal.solar.ui.pages.ConfigInfo;
import com.crystal.solar.ui.pages.MapLocate;
import com.treecore.utils.TStringUtils;

public class XianChangePage extends BaseTabActivity {
	private TabHost tabHost;
	private RadioGroup radiogroup_btn;
	private String mCompanyId = "";

	@SuppressWarnings("deprecation")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mCompanyId = getActivityParameter().get(0);
		if (TStringUtils.isEmpty(mCompanyId)) {
			makeText("数据异常");
			finish();
			return;
		}

		setContentView(R.layout.activity_xianchengpage_layout);
		init();
		findViewById(R.id.return_back).setOnClickListener(
				new OnClickListener() {
					public void onClick(View v) {
						finish();
					}
				});
	}

	public void init() {
		tabHost = getTabHost();
		tabHost.setup();

		TabSpec spec4 = tabHost.newTabSpec("tab1");
		Intent intent = new Intent(XianChangePage.this, ConfigInfo.class);
		intent.putExtra("company", mCompanyId);
		spec4.setContent(intent);
		spec4.setIndicator("tab1");

		TabSpec spec5 = tabHost.newTabSpec("tab2");
		intent = new Intent(XianChangePage.this, MapLocate.class);
		intent.putExtra("company", mCompanyId);
		spec5.setContent(intent);
		spec5.setIndicator("tab2");

		TabSpec spec6 = tabHost.newTabSpec("tab3");
		intent = new Intent(XianChangePage.this, PlantList.class);
		intent.putExtra("company", mCompanyId);
		spec6.setContent(intent);
		spec6.setIndicator("tab3");

		tabHost.addTab(spec6);
		tabHost.addTab(spec5);
		tabHost.addTab(spec4);

		radiogroup_btn = (RadioGroup) findViewById(R.id.radiogroup_btn);
		radiogroup_btn
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						switch (checkedId) {
						case R.id.rdo_gongsi: {
							tabHost.setCurrentTab(2);
							break;
						}
						case R.id.rdo_map: {
							tabHost.setCurrentTab(1);
							break;
						}
						case R.id.radio_device: {
							tabHost.setCurrentTab(0);
							break;
						}
						}
					}
				});
	}
}
