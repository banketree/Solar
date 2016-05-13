package com.crystal.solar.ui;

import java.util.ArrayList;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Dialog;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.crystal.solar.R;
import com.crystal.solar.dialogs.LoadingDialog;
import com.crystal.solar.util.CoreHttpApi;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.treecore.http.TAsyncHttpClient;
import com.treecore.http.core.AsyncHttpResponseHandler;
import com.treecore.utils.TActivityUtils;
import com.treecore.utils.TStringUtils;
import com.treecore.utils.TToastUtils;
import com.treecore.utils.network.TNetWorkUtil;

public class OrderTrackList extends BaseActivity
{

	private PullToRefreshListView listView;
	private ArrayList<TrackItem> mlist = new ArrayList<OrderTrackList.TrackItem>();
	private TrackAdapter trackAdapter;
	private LayoutInflater inflater;
	private Dialog dialog;
	private int mPage = 0;

	private TAsyncHttpClient mTAsyncHttpClient;
	private String mCompanyId = "";

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		mCompanyId = getActivityParameter().get(0);
		if (TStringUtils.isEmpty(mCompanyId))
		{
			makeText("数据异常");
			finish();
			return;
		}

		setContentView(R.layout.activity_track_layout);
		dialog = LoadingDialog.createLoadingDialog(this, getString(R.string.loadmoredata));
		init();
		findViewById(R.id.return_back).setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				finish();
			}
		});

		int querypage = mPage + 1;
		onGetOrderTracklist(querypage + "");
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
		inflater = LayoutInflater.from(this);
		listView = (PullToRefreshListView) findViewById(R.id.track_list);
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
				// NetDataProxyManager.getNewOrders(mainHandler, "0");
				int querypage = mPage + 1;
				onGetOrderTracklist(querypage + "");
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

		trackAdapter = new TrackAdapter();
		actualListView.setAdapter(trackAdapter);
		actualListView.setDividerHeight(0);
		// actualListView.setOnItemClickListener(new OnItemClickListener() {
		//
		// @Override
		// public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
		// long arg3) {
		//
		// }
		// });

		listView.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3)
			{
				TActivityUtils.jumpToActivity(mContext, OrderTrackDetails.class, mCompanyId, mlist.get(pos - 1).getBianhao());
			}
		});
	}

	private class TrackItem
	{
		private String id;// :【记录id】
		private String title;// :【订单描述】
		private String addtime;// :【提交时间】
		private String remark;// :【备注】
		private String bianhao;
		private String recever;
		private String jige;
		private String paytype;
		private String state;

		public String getId()
		{
			return id;
		}

		public void setId(String id)
		{
			this.id = id;
		}

		public String getTitle()
		{
			return title;
		}

		public void setTitle(String title)
		{
			this.title = title;
		}

		public String getAddtime()
		{
			return addtime;
		}

		public void setAddtime(String addtime)
		{
			this.addtime = addtime;
		}

		public String getRemark()
		{
			return remark;
		}

		public void setRemark(String remark)
		{
			this.remark = remark;
		}

		public String getBianhao()
		{
			return bianhao;
		}

		public void setBianhao(String bianhao)
		{
			this.bianhao = bianhao;
		}

		public String getRecever()
		{
			return recever;
		}

		public void setRecever(String recever)
		{
			this.recever = recever;
		}

		public String getJige()
		{
			return jige;
		}

		public void setJige(String jige)
		{
			this.jige = jige;
		}

		public String getPaytype()
		{
			return paytype;
		}

		public void setPaytype(String paytype)
		{
			this.paytype = paytype;
		}

		public String getState()
		{
			return state;
		}

		public void setState(String state)
		{
			this.state = state;
		}

	}

	private class TrackAdapter extends BaseAdapter
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
			convertView = inflater.inflate(R.layout.view_track_list_item, null);
			TextView tv_station_title = (TextView) convertView.findViewById(R.id.tv_station_title);
			TextView tv_station_id = (TextView) convertView.findViewById(R.id.tv_station_id);
			Button order_track_btn = (Button) convertView.findViewById(R.id.order_track_btn);

			tv_station_title.setText(mlist.get(position).getTitle());
			tv_station_id.setText(mlist.get(position).getBianhao());
			order_track_btn.setOnClickListener(clickListener);
			return convertView;
		}

		private OnClickListener clickListener = new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				TActivityUtils.jumpToActivity(mContext, OrderTrackDetails.class, mCompanyId);
			}
		};

	}

	private void onGetOrderTracklist(final String page)
	{
		if (mTAsyncHttpClient != null)
			return;

		if (!TNetWorkUtil.isNetworkConnected())
		{
			if (dialog != null)
				dialog.dismiss();
			makeText(getResString(R.string.not_network));
			return;
		}

		dialog.show();

		mTAsyncHttpClient = new TAsyncHttpClient();
		mTAsyncHttpClient.get(CoreHttpApi.getOrderTracklist(page, mCompanyId), null, new AsyncHttpResponseHandler()
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
								TrackItem item = new TrackItem();

								JSONObject obj = array.getJSONObject(i);

								String id = obj.getString("id");
								String title = obj.getString("title");
								String addtime = obj.getString("addtime");
								String bianhao = obj.getString("bianhao");

								String recever = obj.getString("recever");
								String jine = obj.getString("jine");
								String paytype = obj.getString("paytype");
								String state = obj.getString("state");

								item.setId(id);
								item.setTitle(title);
								item.setAddtime(addtime);
								item.setBianhao(bianhao);

								item.setJige(jine);
								item.setPaytype(paytype);
								item.setRecever(recever);
								item.setState(state);
								boolean isexsiting = false;
								for (TrackItem tmpitem : mlist)
								{
									if (tmpitem.getId().equals(item.getId()))
									{
										isexsiting = true;
										break;
									}
								}
								if (!isexsiting)
									mlist.add(0, item);
							}

							if (array.length() > 0)
							{
								mPage += 1;
							}
							trackAdapter.notifyDataSetChanged();
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

				if (mTAsyncHttpClient != null)
				{
					mTAsyncHttpClient.cancelRequests(mContext, true);
					mTAsyncHttpClient.cancelAllRequests(true);
				}
				mTAsyncHttpClient = null;
			}

			@Override
			public void onFinish()
			{
				super.onFinish();
				if (dialog != null)
					dialog.dismiss();

				if (mTAsyncHttpClient != null)
				{
					mTAsyncHttpClient.cancelRequests(mContext, true);
					mTAsyncHttpClient.cancelAllRequests(true);
				}
				mTAsyncHttpClient = null;
			}

			@Override
			public void onStart()
			{
				super.onStart();
			}
		});
	}
}
