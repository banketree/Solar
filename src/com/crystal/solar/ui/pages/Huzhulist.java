package com.crystal.solar.ui.pages;

import java.util.ArrayList;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Dialog;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.crystal.solar.R;
import com.crystal.solar.dialogs.LoadingDialog;
import com.crystal.solar.ui.BaseActivity;
import com.crystal.solar.util.CoreHttpApi;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.treecore.http.TAsyncHttpClient;
import com.treecore.http.core.AsyncHttpResponseHandler;
import com.treecore.utils.TStringUtils;
import com.treecore.utils.TToastUtils;
import com.treecore.utils.network.TNetWorkUtil;

public class Huzhulist extends BaseActivity
{

	private PullToRefreshListView listView;
	private HuzuAdapter orderAdapter;
	private ArrayList<HuzuItem> mlist = new ArrayList<HuzuItem>();
	private LayoutInflater inflater;

	private Dialog dialog;
	private TAsyncHttpClient mTAsyncHttpClient;
	private String mCompanyId = "";

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		mCompanyId = getIntent().getStringExtra("company");
		setContentView(R.layout.activity_order_layout);
		inflater = LayoutInflater.from(this);
		dialog = LoadingDialog.createLoadingDialog(getParent(), getString(R.string.loadmoredata));
		init();

		onGetHuzulist("1");
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();

		if (mTAsyncHttpClient != null)
		{
			mTAsyncHttpClient.cancelRequests(mContext, true);
			mTAsyncHttpClient.cancelAllRequests(true);
		}
		mTAsyncHttpClient = null;
	}

	public void init()
	{
		listView = (PullToRefreshListView) findViewById(R.id.listview);

		// Set a listener to be invoked when the list should be refreshed.
		listView.setOnRefreshListener(new OnRefreshListener<ListView>()
		{
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView)
			{
				String label = DateUtils.formatDateTime(getApplicationContext(), System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

				// Update the LastUpdatedLabel
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
				// load data
				onGetHuzulist("1");
			}
		});

		// Add an end-of-list listener
		listView.setOnLastItemVisibleListener(new OnLastItemVisibleListener()
		{

			@Override
			public void onLastItemVisible()
			{

			}
		});

		listView.setMode(Mode.BOTH);

		ListView actualListView = listView.getRefreshableView();

		orderAdapter = new HuzuAdapter();
		actualListView.setAdapter(orderAdapter);
		actualListView.setDividerHeight(0);

	}

	private class HuzuItem
	{
		public String id;
		public String types;
		public String addtime;
		public String remark;
	}

	private class HuzuAdapter extends BaseAdapter
	{
		@Override
		public int getCount()
		{
			return mlist.size();
		}

		@Override
		public Object getItem(int position)
		{
			return mlist.get(position);
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

			name.setText(mlist.get(position).remark);
			time.setText(mlist.get(position).addtime);

			return convertView;
		}
	}

	private void onGetHuzulist(final String page)
	{
		if (!TNetWorkUtil.isNetworkConnected())
		{
			if (dialog != null)
				dialog.dismiss();
			makeText(getResString(R.string.not_network));
			return;
		}

		dialog.show();

		if (mTAsyncHttpClient == null)
		{
			mTAsyncHttpClient = new TAsyncHttpClient();
		}

		mTAsyncHttpClient.get(CoreHttpApi.getHuzulist(page, mCompanyId), null, new AsyncHttpResponseHandler()
		{

			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody)
			{
				dialog.dismiss();
				//
				try
				{
					if (statusCode == 200)
					{
						JSONObject jsonObject = CoreHttpApi.checkResponseStatus(responseBody);

						try
						{
							String err = jsonObject.getString("err");
							JSONArray array = jsonObject.getJSONArray("datalist");
							for (int i = 0; i < array.length(); i++)
							{
								HuzuItem item = new HuzuItem();

								JSONObject obj = array.getJSONObject(i);
								String id = obj.getString("id");
								String types = obj.getString("type");
								String addtime = obj.getString("addtime");
								String remark = obj.getString("remark");

								item.id = id;
								item.types = types;
								item.addtime = addtime;
								item.remark = remark;
								boolean isexsiting = false;
								for (HuzuItem itemtemp : mlist)
								{
									if (itemtemp.id.equals(id))
									{
										isexsiting = true;
										break;
									}
								}
								if (!isexsiting)
									mlist.add(item);
							}

							orderAdapter.notifyDataSetChanged();
							listView.onRefreshComplete();
						} catch (Exception e)
						{
							makeText(getResString(R.string.operation_error));
						}
					} else
					{
						TToastUtils.makeText("服务器异常");
					}
				} catch (Exception e)
				{
					if (e == null || TStringUtils.isEmpty(e.getMessage()))
						return;
					TToastUtils.makeText(e.getMessage());
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error)
			{
				TToastUtils.makeText(getString(R.string.tip_pls_net_error));
			}

			@Override
			public void onProgress(int bytesWritten, int totalSize)
			{
				super.onProgress(bytesWritten, totalSize);
			}

			@Override
			public void onRetry(int retryNo)
			{
				TToastUtils.makeText(String.format("Request is retried, retry no. %d", retryNo));
			}

			@Override
			public void onCancel()
			{
				super.onCancel();
				if (dialog != null)
					dialog.dismiss();
			}

			@Override
			public void onFinish()
			{
				super.onFinish();
				if (dialog != null)
					dialog.dismiss();
			}

			@Override
			public void onStart()
			{
				super.onStart();
			}
		});
	}
}
