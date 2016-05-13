package com.crystal.solar.ui.pages;

import org.apache.http.Header;
import org.json.JSONObject;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

import com.crystal.solar.R;
import com.crystal.solar.dialogs.LoadingDialog;
import com.crystal.solar.ui.BaseActivity;
import com.crystal.solar.util.CoreHttpApi;
import com.treecore.http.TAsyncHttpClient;
import com.treecore.http.core.AsyncHttpResponseHandler;
import com.treecore.utils.TStringUtils;
import com.treecore.utils.TToastUtils;
import com.treecore.utils.network.TNetWorkUtil;

public class HangyeDetail extends BaseActivity
{
	private TextView details;
	private Dialog dialog;

	private TAsyncHttpClient mTAsyncHttpClient;
	private String mCompanyId = "";

	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_hangye_detail_layout);
		dialog = LoadingDialog.createLoadingDialog(this, getString(R.string.loadmoredata));
		init();
		findViewById(R.id.return_back).setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				finish();
			}
		});

		String id = getIntent().getStringExtra("id");
		onGetHangyelistDetail(id);
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
		details = (TextView) findViewById(R.id.details);
	}

	private void onGetHangyelistDetail(final String id)
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

		mTAsyncHttpClient.get(CoreHttpApi.getHangyelistDetail(id, mCompanyId), null, new AsyncHttpResponseHandler()
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
							String text = jsonObject.getString("text");
							// if(err.equals(0))
							{
								details.setMovementMethod(ScrollingMovementMethod.getInstance());
								details.setText(Html.fromHtml(text));
							}
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
