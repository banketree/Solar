package com.crystal.solar.ui.pages;

import java.util.ArrayList;

import com.crystal.solar.R;
import com.crystal.solar.ui.BaseActivity;
import com.crystal.solar.ui.pages.bean.IOItem;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

public class IOSingnal extends BaseActivity {

	private GridView io_gridview;
	private ArrayList<IOItem> list = new ArrayList<IOItem>();
	private IOadapter ioadapter;
	private LayoutInflater inflater;
	private String mCompanyId = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mCompanyId = getIntent().getStringExtra("company");
		setContentView(R.layout.activity_io_layout);
		initview();
	}

	public void initview() {

		inflater = LayoutInflater.from(this);

		io_gridview = (GridView) findViewById(R.id.io_gridview);
		ioadapter = new IOadapter();
		io_gridview.setAdapter(ioadapter);

		// test

		for (int i = 0; i < 32; i++) {
			IOItem ioItem = new IOItem();
			ioItem.setName("第" + (i + 1) + "位");
			list.add(ioItem);
		}

		ioadapter.notifyDataSetChanged();
	}

	private class IOadapter extends BaseAdapter {

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			convertView = inflater.inflate(R.layout.view_io_item, null);
			TextView ioname = (TextView) convertView.findViewById(R.id.name);

			ioname.setText(list.get(position).getName());
			return convertView;
		}

	}
}
