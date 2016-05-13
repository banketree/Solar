package com.crystal.solar.ui.pages.adpters;

import java.util.ArrayList;

import com.crystal.solar.R;
import com.crystal.solar.ui.pages.bean.DataTableItem;

import android.content.Context;
import android.util.FloatMath;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class DataTableItemAdapter extends BaseAdapter
{

	private ArrayList<DataTableItem> list;
	private Context context;
	private LayoutInflater inflater;

	public DataTableItemAdapter(ArrayList<DataTableItem> list, Context context)
	{
		super();
		this.list = list;
		this.context = context;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount()
	{
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int arg0)
	{
		// TODO Auto-generated method stub
		return list.get(arg0);
	}

	@Override
	public long getItemId(int arg0)
	{
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int pos, View convertview, ViewGroup arg2)
	{

		convertview = inflater.inflate(R.layout.view_data_item_layout, null);

		TextView name = (TextView) convertview.findViewById(R.id.good_name);
		TextView total_today = (TextView) convertview.findViewById(R.id.total_today);
		TextView total_week = (TextView) convertview.findViewById(R.id.total_week);
		TextView total_month = (TextView) convertview.findViewById(R.id.total_month);
		TextView total_year = (TextView) convertview.findViewById(R.id.total_year);
		TextView total_total = (TextView) convertview.findViewById(R.id.total_total);

		DataTableItem item = list.get(pos);

		name.setText(item.getName());
		total_today.setText(item.getToday());
		total_week.setText(item.getWeek());
		total_month.setText(item.getMonth());
		total_year.setText(item.getYear());
		total_total.setText(item.getTotal());

		return convertview;
	}

}
