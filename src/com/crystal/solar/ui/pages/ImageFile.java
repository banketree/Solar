package com.crystal.solar.ui.pages;

import java.util.ArrayList;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Bundle;
import android.widget.ListView;

import com.crystal.solar.R;
import com.crystal.solar.ui.BaseActivity;
import com.crystal.solar.ui.pages.adpters.ImageItemAdapter;
import com.crystal.solar.ui.pages.bean.ImageItem;
import com.crystal.solar.util.CoreHttpApi;
import com.treecore.http.TAsyncHttpClient;
import com.treecore.http.core.AsyncHttpResponseHandler;
import com.treecore.utils.TStringUtils;
import com.treecore.utils.TToastUtils;
import com.treecore.utils.network.TNetWorkUtil;

public class ImageFile extends BaseActivity
{

	private ListView listView;
	private ImageItemAdapter imageItemAdapter;
	private ArrayList<ImageItem> list = new ArrayList<ImageItem>();

	private TAsyncHttpClient mTAsyncHttpClient;
	private String mCompanyId = "";

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		mCompanyId = getIntent().getStringExtra("company");
		setContentView(R.layout.activity_imagefile_layout);
		init();
		onGetFile();
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
		listView = (ListView) findViewById(R.id.listview);
		imageItemAdapter = new ImageItemAdapter(list, this);
		listView.setAdapter(imageItemAdapter);
	}

	private void onGetFile()
	{
		if (!TNetWorkUtil.isNetworkConnected())
		{
			makeText(getResString(R.string.not_network));
			return;
		}

		if (mTAsyncHttpClient == null)
		{
			mTAsyncHttpClient = new TAsyncHttpClient();
		}

		mTAsyncHttpClient.get(CoreHttpApi.getImageFile(mCompanyId), null, new AsyncHttpResponseHandler()
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
								JSONObject obj = array.getJSONObject(i);
								String name = obj.getString("names");
								String size = obj.getString("size");
								String addtime = obj.getString("addtime");
								ImageItem item = new ImageItem();
								item.setAddtime(addtime);
								item.setName(name);
								item.setSize(size + "kb");
								list.add(item);

							}

							imageItemAdapter.notifyDataSetChanged();
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
			}

			@Override
			public void onFinish()
			{
				super.onFinish();
			}

			@Override
			public void onStart()
			{
				super.onStart();
			}
		});
	}
}
