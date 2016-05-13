package com.crystal.solar.ui.tab;

import com.crystal.solar.R;
import com.crystal.solar.ui.pages.ModifyReq;
import com.crystal.solar.ui.pages.HangyezixunList;
import com.crystal.solar.ui.pages.ShouhouInfo;
import com.treecore.utils.TStringUtils;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class OtherInfoPage extends BaseTabActivity {

	private TabHost tabHost;
	private RadioGroup radiogroup_btn;
	private String mCompanyId = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mCompanyId = getIntent().getStringExtra("company");

		if (TStringUtils.isEmpty(mCompanyId))
			mCompanyId = getActivityParameter().get(0);

		if (TStringUtils.isEmpty(mCompanyId)) {
			makeText("数据异常");
			finish();
			return;
		}

		setContentView(R.layout.activity_otherinfopage_layout);
		init();
	}

	public void init() {
		tabHost = getTabHost();
		tabHost.setup();

		TabSpec spec4 = tabHost.newTabSpec("tab4");
		Intent intent = new Intent(OtherInfoPage.this, HangyezixunList.class);
		intent.putExtra("company", mCompanyId);
		spec4.setContent(intent);
		spec4.setIndicator("tab4");

		TabSpec spec5 = tabHost.newTabSpec("tab5");
		intent = new Intent(OtherInfoPage.this, ModifyReq.class);
		intent.putExtra("company", mCompanyId);
		spec5.setContent(intent);
		spec5.setIndicator("tab5");

		TabSpec spec6 = tabHost.newTabSpec("tab6");
		intent = new Intent(OtherInfoPage.this, ShouhouInfo.class);
		intent.putExtra("company", mCompanyId);
		spec6.setContent(intent);
		spec6.setIndicator("tab6");

		tabHost.addTab(spec4);
		tabHost.addTab(spec5);
		tabHost.addTab(spec6);

		radiogroup_btn = (RadioGroup) findViewById(R.id.radiogroup_btn);
		radiogroup_btn
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						switch (checkedId) {
						case R.id.rdo_order: {
							tabHost.setCurrentTab(0);
							break;
						}
						case R.id.rdo_shouhou: {
							tabHost.setCurrentTab(2);
							break;
						}
						case R.id.rdo_req: {
							tabHost.setCurrentTab(1);
							break;
						}

						}
					}
				});
	}
}
