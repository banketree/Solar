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
import com.crystal.solar.ui.AllChart;
import com.crystal.solar.util.CoreEventUtils;
import com.crystal.solar.view.SegmentedRadioGroup;
import com.treecore.TBroadcastByInner;

public class TablesPage extends BaseTabActivity {
	private SegmentedRadioGroup radioGroup_chart_group;
	private TabHost tabHost;
	private String mCompanyId = "";

	// 【1. 最新生产工艺2. 最新行业时讯3. 最新设备推广】
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mCompanyId = getIntent().getStringExtra("company");

		setContentView(R.layout.activity_tables_page_layout);
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
		Intent intent = new Intent(TablesPage.this, AllChart.class);
		intent.putExtra("table", AllChart.Table_Capacity);
		intent.putExtra("company", mCompanyId);
		spec1.setContent(intent);
		spec1.setIndicator("tab1");

		TabSpec spec2 = tabHost.newTabSpec("tab2");
		intent = new Intent(TablesPage.this, AllChart.class);
		intent.putExtra("table", AllChart.Table_Working);
		intent.putExtra("company", mCompanyId);
		spec2.setContent(intent);
		spec2.setIndicator("tab2");

		TabSpec spec3 = tabHost.newTabSpec("tab3");
		intent = new Intent(TablesPage.this, AllChart.class);
		intent.putExtra("table", AllChart.Table_Day_Night);
		intent.putExtra("company", mCompanyId);
		spec3.setContent(intent);
		spec3.setIndicator("tab3");

		tabHost.addTab(spec1);
		tabHost.addTab(spec2);
		tabHost.addTab(spec3);

		radioGroup_chart_group = (SegmentedRadioGroup) findViewById(R.id.radioGroup_chart_group);
		radioGroup_chart_group
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {

						switch (checkedId) {
						case R.id.radio_chart_power: {
							tabHost.setCurrentTab(0);
							break;
						}
						case R.id.radio_run_chart: {
							tabHost.setCurrentTab(1);
							break;
						}
						case R.id.radio_h_chart: {
							tabHost.setCurrentTab(2);
							break;
						}
						}
					}
				});

		findViewById(R.id.Button_refresh).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (tabHost.getCurrentTab() == 0) {
							TBroadcastByInner.sentEvent(
									CoreEventUtils.Chart_Refresh,
									AllChart.Table_Capacity);
						} else if (tabHost.getCurrentTab() == 1) {
							TBroadcastByInner.sentEvent(
									CoreEventUtils.Chart_Refresh,
									AllChart.Table_Working);
						} else if (tabHost.getCurrentTab() == 2) {
							TBroadcastByInner.sentEvent(
									CoreEventUtils.Chart_Refresh,
									AllChart.Table_Day_Night);
						}
					}
				});
	}
}
