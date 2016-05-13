package com.crystal.solar.ui.pages;

import java.util.ArrayList;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Bundle;

import com.costum.android.widget.PullAndLoadListView;
import com.costum.android.widget.PullAndLoadListView.OnLoadMoreListener;
import com.costum.android.widget.PullToRefreshListView.OnRefreshListener;
import com.crystal.solar.R;
import com.crystal.solar.ui.BaseActivity;
import com.crystal.solar.ui.pages.adpters.ShouhouAdapter;
import com.crystal.solar.ui.pages.bean.ShouhouItem;
import com.crystal.solar.util.CoreHttpApi;
import com.treecore.http.TAsyncHttpClient;
import com.treecore.http.core.AsyncHttpResponseHandler;
import com.treecore.utils.TStringUtils;
import com.treecore.utils.TToastUtils;
import com.treecore.utils.network.TNetWorkUtil;

public class ShouhouInfo extends BaseActivity
{

	private PullAndLoadListView listView;
	private ShouhouAdapter orderAdapter;
	private ArrayList<ShouhouItem> list = new ArrayList<ShouhouItem>();

	private TAsyncHttpClient mTAsyncHttpClient;
	private String mCompanyId = "";

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		mCompanyId = getIntent().getStringExtra("company");
		setContentView(R.layout.activity_order_layout);
		init();

		onGetShouhous(false, "1", null);
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
		listView = (PullAndLoadListView) findViewById(R.id.listview);
		orderAdapter = new ShouhouAdapter(list, this);
		listView.setAdapter(orderAdapter);

		listView.setOnRefreshListener(new OnRefreshListener()
		{

			public void onRefresh()
			{
				// Do work to refresh the list here.
				onGetShouhous(true, "", "0");
			}
		});

		// set a listener to be invoked when the list reaches the end

		listView.setOnLoadMoreListener(new OnLoadMoreListener()
		{

			public void onLoadMore()
			{
				// Do the work to load more items at the end of list
				// here
				if (list.size() > 0)
					onGetShouhous(false, "1", list.get(0).getId());
				else
					onGetShouhous(false, "1", null);
			}
		});
	}

	private void onGetShouhous(Boolean isNew, String page, String readid)
	{
		if (!TNetWorkUtil.isNetworkConnected())
		{
			makeText(getResString(R.string.not_network));
			return;
		}

		if (mTAsyncHttpClient != null)
			return;

		mTAsyncHttpClient = new TAsyncHttpClient();

		String url = "";
		if (isNew)
		{
			url = CoreHttpApi.getNewShouhoulist(readid, mCompanyId);
		} else
		{
			url = CoreHttpApi.getShouhouList(page, readid, mCompanyId);
		}

		mTAsyncHttpClient.get(url, null, new AsyncHttpResponseHandler()
		{

			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody)
			{
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
								ShouhouItem item = new ShouhouItem();

								JSONObject obj = array.getJSONObject(i);
								String id = obj.getString("id");
								String types = obj.getString("types");
								String addtime = obj.getString("addtime");
								String remark = obj.getString("remark");
								String apply = obj.getString("apply");
								item.setId(id);
								item.setTypes(types);
								item.setAddtime(addtime);
								item.setRemark(remark);
								item.setApply(apply);
								list.add(item);
							}

							orderAdapter.notifyDataSetChanged();
							listView.onLoadMoreComplete();
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
