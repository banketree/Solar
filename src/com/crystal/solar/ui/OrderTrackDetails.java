package com.crystal.solar.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ListView;

import com.crystal.solar.R;
import com.crystal.solar.dialogs.LoadingDialog;
import com.crystal.solar.ui.pages.adpters.TimelineAdapter;
import com.crystal.solar.util.CoreHttpApi;
import com.treecore.http.TAsyncHttpClient;
import com.treecore.http.core.AsyncHttpResponseHandler;
import com.treecore.utils.TStringUtils;
import com.treecore.utils.TToastUtils;
import com.treecore.utils.network.TNetWorkUtil;

public class OrderTrackDetails extends BaseActivity
{

	private ListView track_list;
	private TimelineAdapter timelineAdapter;
	private Dialog dialog;
	private String bh;
	private List<Map<String, Object>> mlist = new ArrayList<Map<String, Object>>();
	private TAsyncHttpClient mTAsyncHttpClient;
	private String mCompanyId = "";

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_track_detai_layout);

		mCompanyId = getActivityParameter().get(0);
		if (TStringUtils.isEmpty(mCompanyId))
		{
			makeText("数据异常");
			finish();
			return;
		}

		bh = getActivityParameter().get(1);

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
		Log.i("bhtest", "bh=" + bh);
		onGetOrderTrackDetails(bh);
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
		track_list = (ListView) findViewById(R.id.track_list);
		track_list.setDividerHeight(0);
		timelineAdapter = new TimelineAdapter(this, mlist);
		track_list.setAdapter(timelineAdapter);
	}

	private void onGetOrderTrackDetails(final String bh)
	{
		if (!TNetWorkUtil.isNetworkConnected())
		{
			makeText(getResString(R.string.not_network));
			if (dialog != null)
				dialog.dismiss();
			return;
		}

		dialog.show();

		if (mTAsyncHttpClient == null)
		{
			mTAsyncHttpClient = new TAsyncHttpClient();
		}

		mTAsyncHttpClient.get(CoreHttpApi.getOrderTrackDetails(bh, mCompanyId), null, new AsyncHttpResponseHandler()
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
								JSONObject obj = array.getJSONObject(i);

								String id = obj.getString("id");
								String title = obj.getString("title");
								String addtime = obj.getString("addtime");
								String use = obj.getString("use");

								Map<String, Object> map = new HashMap<String, Object>();
								map.put("title", title);
								map.put("time", addtime);
								mlist.add(map);
							}

							timelineAdapter.notifyDataSetChanged();
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
