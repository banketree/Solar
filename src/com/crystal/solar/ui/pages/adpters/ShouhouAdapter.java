package com.crystal.solar.ui.pages.adpters;

import java.util.ArrayList;

import com.crystal.solar.R;
import com.crystal.solar.ui.pages.bean.OrderItem;
import com.crystal.solar.ui.pages.bean.ShouhouItem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ShouhouAdapter extends BaseAdapter
{

	private ArrayList<ShouhouItem> list;
	private Context context;
	private LayoutInflater inflater;

	public ShouhouAdapter(ArrayList<ShouhouItem> list, Context context)
	{
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
	public Object getItem(int position)
	{
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{

		convertView = inflater.inflate(R.layout.view_order_item, null);
		TextView name = (TextView) convertView.findViewById(R.id.name);
		TextView time = (TextView) convertView.findViewById(R.id.time);

		name.setText(list.get(position).getRemark());
		time.setText(list.get(position).getAddtime());

		return convertView;
	}

}
