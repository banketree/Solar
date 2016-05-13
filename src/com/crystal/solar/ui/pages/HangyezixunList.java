package com.crystal.solar.ui.pages;

import java.util.ArrayList;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.crystal.solar.R;
import com.crystal.solar.dialogs.LoadingDialog;
import com.crystal.solar.ui.BaseActivity;
import com.crystal.solar.ui.pages.adpters.HangYelistAdapter;
import com.crystal.solar.ui.pages.bean.HangyeListItem;
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

public class HangyezixunList extends BaseActivity
{

	private PullToRefreshListView listView;
	private HangYelistAdapter orderAdapter;
	private ArrayList<HangyeListItem> list = new ArrayList<HangyeListItem>();

	private Dialog dialog;
	private int types = 1;
	private int mPage = 1;
	private TAsyncHttpClient mTAsyncHttpClient;
	private String mCompanyId = "";

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order_layout);
		types = getIntent().getIntExtra("type", 1);
		mCompanyId = getIntent().getStringExtra("company");

		dialog = LoadingDialog.createLoadingDialog(getParent(), getString(R.string.loadmoredata));
		init();

		getHangyelist(mPage + "", types + "", null);
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
				// NetDataProxyManager.getNewOrders(mainHandler, "0");
				int temppage = mPage + 1;
				getHangyelist(temppage + "", types + "", null);
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

		orderAdapter = new HangYelistAdapter(list, this);
		actualListView.setAdapter(orderAdapter);
		actualListView.setDividerHeight(0);

		listView.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				Intent intent = new Intent(HangyezixunList.this, HangyeDetail.class);
				intent.putExtra("id", list.get(position - 1).getId());
				startActivity(intent);
			}
		});

	}

	private void getHangyelist(final String page, final String type, final String readid)
	{
		if (!TNetWorkUtil.isNetworkConnected())
		{
			if (dialog != null)
				dialog.dismiss();
			makeText(getResString(R.string.not_network));
			return;
		}

		if (mTAsyncHttpClient != null)
			return;

		dialog.show();

		mTAsyncHttpClient = new TAsyncHttpClient();
		mTAsyncHttpClient.get(CoreHttpApi.getHangyelist(page, type, readid, mCompanyId), null, new AsyncHttpResponseHandler()
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
							if (array.length() > 0)
							{
								mPage += 1;
							}

							for (int i = 0; i < array.length(); i++)
							{
								HangyeListItem item = new HangyeListItem();

								JSONObject obj = array.getJSONObject(i);
								String id = obj.getString("id");
								String title = obj.getString("title");
								String addtime = obj.getString("addtime");
								String author = obj.getString("author");
								String picurl = obj.getString("picurl");

								item.setId(id);
								item.setTitle(title);
								item.setAddtime(addtime);
								item.setAuthor(author);
								item.setPicurl(picurl);
								boolean isexsiting = false;
								for (HangyeListItem tempitem : list)
								{
									if (tempitem.getId().equals(item.getId()))
									{
										isexsiting = true;
										break;
									}
								}

								if (!isexsiting)
									list.add(item);
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
