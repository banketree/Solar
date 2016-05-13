package com.crystal.solar.ui.pages.adpters;

import java.util.List;
import java.util.Map;

import com.crystal.solar.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class TimelineAdapter extends BaseAdapter
{

	private Context context;
	private List<Map<String, Object>> list;
	private LayoutInflater inflater;

	public TimelineAdapter(Context context, List<Map<String, Object>> list)
	{
		super();
		this.context = context;
		this.list = list;
	}

	@Override
	public int getCount()
	{

		return list.size();
	}

	@Override
	public Object getItem(int position)
	{
		return position;
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ViewHolder viewHolder = null;
		if (convertView == null)
		{
			inflater = LayoutInflater.from(parent.getContext());
			convertView = inflater.inflate(R.layout.view_listview_item, null);
			viewHolder = new ViewHolder();

			viewHolder.title = (TextView) convertView.findViewById(R.id.title);
			viewHolder.time = (TextView) convertView.findViewById(R.id.time);
			viewHolder.image = (ImageView) convertView.findViewById(R.id.image);
			convertView.setTag(viewHolder);
		} else
		{
			viewHolder = (ViewHolder) convertView.getTag();
		}

		String titleStr = list.get(position).get("title").toString();
		String timestr = list.get(position).get("time").toString();

		viewHolder.title.setText(titleStr);
		viewHolder.time.setText(timestr);

		if (position == list.size() - 1)
		{
			viewHolder.image.setImageResource(R.drawable.timeline_red);
		} else
		{
			viewHolder.image.setImageResource(R.drawable.timeline_green);
		}
		return convertView;
	}

	static class ViewHolder
	{
		public TextView year;
		public TextView month;
		public TextView title;
		public TextView time;
		public ImageView image;
	}
}
