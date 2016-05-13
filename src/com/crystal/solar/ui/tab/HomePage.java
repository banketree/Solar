package com.crystal.solar.ui.tab;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

import com.crystal.solar.R;
import com.crystal.solar.ui.AllChart;
import com.crystal.solar.ui.pages.Data;
import com.crystal.solar.ui.pages.IOSingnal;
import com.crystal.solar.ui.pages.MapLocate;
import com.treecore.utils.TStringUtils;

public class HomePage extends BaseTabActivity {

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

		setContentView(R.layout.activity_homepage_layout);
		init();
	}

	public void init() {
		tabHost = getTabHost();
		tabHost.setup();

		TabSpec spec1 = tabHost.newTabSpec("tab1");
		Intent intent = new Intent(HomePage.this, AllChart.class);
		intent.putExtra("company", mCompanyId);
		spec1.setContent(intent);
		spec1.setIndicator("tab1");

		TabSpec spec2 = tabHost.newTabSpec("tab2");
		intent = new Intent(HomePage.this, Data.class);
		intent.putExtra("company", mCompanyId);
		spec2.setContent(intent);
		spec2.setIndicator("tab2");

		TabSpec spec3 = tabHost.newTabSpec("tab3");
		intent = new Intent(HomePage.this, MapLocate.class);
		intent.putExtra("company", mCompanyId);
		spec3.setContent(intent);
		spec3.setIndicator("tab3");

		TabSpec spec4 = tabHost.newTabSpec("tab4");
		intent = new Intent(HomePage.this, ConfigfilesPage.class);
		intent.putExtra("company", mCompanyId);
		spec4.setContent(intent);
		spec4.setIndicator("tab4");
		//

		TabSpec io = tabHost.newTabSpec("io");
		intent = new Intent(HomePage.this, IOSingnal.class);
		intent.putExtra("company", mCompanyId);
		io.setContent(intent);
		io.setIndicator("io");

		TabSpec spec5 = tabHost.newTabSpec("tab5");
		intent = new Intent(HomePage.this, OtherInfoPage.class);
		intent.putExtra("company", mCompanyId);
		spec5.setContent(intent);
		spec5.setIndicator("tab5");
		//
		// TabSpec spec6 = tabHost.newTabSpec("tab6");
		// spec6.setContent(new Intent(HomePageActivity.this,
		// DeviceLogActivity.class));
		// spec6.setIndicator("tab6");

		// TabSpec spec7 = tabHost.newTabSpec("tab7");
		// spec7.setContent(new Intent(HomePageActivity.this,
		// ConfigInfoActivity.class));
		// spec7.setIndicator("tab7");

		tabHost.addTab(spec1);
		tabHost.addTab(spec2);
		tabHost.addTab(spec3);
		tabHost.addTab(spec4);
		tabHost.addTab(spec5);
		tabHost.addTab(io);
		// tabHost.addTab(spec6);
		// tabHost.addTab(spec7);

		radiogroup_btn = (RadioGroup) findViewById(R.id.radiogroup_btn);
		radiogroup_btn
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						switch (checkedId) {
						case R.id.rdo_tongjitu: {
							tabHost.setCurrentTab(0);
							break;
						}
						case R.id.rdo_data: {
							tabHost.setCurrentTab(1);
							break;
						}
						case R.id.rdo_map: {
							tabHost.setCurrentTab(2);
							break;
						}
						case R.id.rdo_configurefile: {
							tabHost.setCurrentTab(3);
							break;
						}
						case R.id.rdo_otherinfo: {
							tabHost.setCurrentTab(4);
							break;
						}
						case R.id.rdo_ioinfo: {
							tabHost.setCurrentTab(5);
							break;
						}
						}
					}
				});
	}
}
