package com.crystal.solar.ui.tab;

import com.crystal.solar.R;
import com.crystal.solar.ui.pages.ConfigFile;
import com.crystal.solar.ui.pages.ConfigInfo;
import com.crystal.solar.ui.pages.DeviceLog;
import com.crystal.solar.ui.pages.ImageFile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class ConfigfilesPage extends BaseTabActivity {

	private TabHost tabHost;
	private RadioGroup radiogroup_btn;
	private String mCompanyId = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mCompanyId = getIntent().getStringExtra("company");
		setContentView(R.layout.activity_configfilepage_layout);
		init();
	}

	public void init() {
		tabHost = getTabHost();
		tabHost.setup();

		TabSpec spec4 = tabHost.newTabSpec("tab4");
		Intent intent = new Intent(ConfigfilesPage.this, ImageFile.class);
		intent.putExtra("company", mCompanyId);
		spec4.setContent(intent);
		spec4.setIndicator("tab4");

		TabSpec spec5 = tabHost.newTabSpec("tab5");
		intent = new Intent(ConfigfilesPage.this, ConfigFile.class);
		intent.putExtra("company", mCompanyId);
		spec5.setContent(intent);
		spec5.setIndicator("tab5");

		TabSpec spec6 = tabHost.newTabSpec("tab6");
		intent = new Intent(ConfigfilesPage.this, DeviceLog.class);
		intent.putExtra("company", mCompanyId);
		spec6.setContent(intent);

		spec6.setIndicator("tab6");
		TabSpec spec7 = tabHost.newTabSpec("tab7");
		intent = new Intent(ConfigfilesPage.this, ConfigInfo.class);
		intent.putExtra("company", mCompanyId);
		spec7.setContent(intent);
		spec7.setIndicator("tab7");
		tabHost.addTab(spec4);
		tabHost.addTab(spec5);
		tabHost.addTab(spec6);
		tabHost.addTab(spec7);

		radiogroup_btn = (RadioGroup) findViewById(R.id.radiogroup_btn);
		radiogroup_btn
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						switch (checkedId) {
						case R.id.rdo_image: {
							tabHost.setCurrentTab(0);
							break;
						}
						case R.id.rdo_configurefile: {
							tabHost.setCurrentTab(1);
							break;
						}
						case R.id.rdo_devicelog: {
							tabHost.setCurrentTab(2);
							break;
						}
						case R.id.rdo_configinfo: {
							tabHost.setCurrentTab(3);
							break;
						}
						}
					}
				});
	}
}
