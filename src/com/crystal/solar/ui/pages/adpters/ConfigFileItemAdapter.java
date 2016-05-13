package com.crystal.solar.ui.pages.adpters;

import java.util.ArrayList;

import com.crystal.solar.R;
import com.crystal.solar.ui.pages.bean.ConfigFileItem;
import com.crystal.solar.ui.pages.bean.DataTableItem;
import com.crystal.solar.ui.pages.bean.ImageItem;

import android.content.Context;
import android.util.FloatMath;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ConfigFileItemAdapter extends BaseAdapter
{

	private ArrayList<ConfigFileItem> list;
	private Context context;
	private LayoutInflater inflater;

	public ConfigFileItemAdapter(ArrayList<ConfigFileItem> list, Context context)
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

		convertview = inflater.inflate(R.layout.view_image_item_layout, null);

		TextView name = (TextView) convertview.findViewById(R.id.name);
		TextView size = (TextView) convertview.findViewById(R.id.size);
		TextView del = (TextView) convertview.findViewById(R.id.deltxt);

		ConfigFileItem item = list.get(pos);

		name.setText(item.getName());
		size.setText(item.getSize());

		return convertview;
	}

}
