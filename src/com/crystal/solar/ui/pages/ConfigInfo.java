package com.crystal.solar.ui.pages;

import org.apache.http.Header;
import org.json.JSONObject;

import android.app.Dialog;
import android.os.Bundle;
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

public class ConfigInfo extends BaseActivity
{

	private TextView device_xinhao;
	private TextView device_bianhao;
	private TextView device_company;
	private TextView device_beizhu;
	private TextView device_addtime;

	private Dialog dialog;
	private TAsyncHttpClient mTAsyncHttpClient;

	private String mCompanyId = "";

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		mCompanyId = getIntent().getStringExtra("company");

		setContentView(R.layout.activity_configinfo_layout);
		dialog = LoadingDialog.createLoadingDialog(getParent(), getString(R.string.loadmoredata));
		init();

		onGetCompinfo();
	}

	public void init()
	{
		device_xinhao = (TextView) findViewById(R.id.device_xinhao);
		device_bianhao = (TextView) findViewById(R.id.device_bianhao);
		device_company = (TextView) findViewById(R.id.device_company);
		device_beizhu = (TextView) findViewById(R.id.device_beizhu);
		device_addtime = (TextView) findViewById(R.id.device_addtime);

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

	private void onGetCompinfo()
	{
		if (!TNetWorkUtil.isNetworkConnected())
		{
			if (dialog != null)
				dialog.dismiss();
			makeText(getResString(R.string.not_network));
			return;
		}

		if (mTAsyncHttpClient == null)
		{
			mTAsyncHttpClient = new TAsyncHttpClient();
		}

		dialog.show();

		mTAsyncHttpClient.get(CoreHttpApi.getCompinfo(mCompanyId), null, new AsyncHttpResponseHandler()
		{

			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody)
			{
				//
				try
				{
					if (statusCode == 200)
					{
						dialog.dismiss();
						JSONObject jsonObject = CoreHttpApi.checkResponseStatus(responseBody);

						try
						{
							JSONObject json2 = (JSONObject) jsonObject.getJSONArray("datalist").get(0);

							String xinhao = json2.getString("cname");
							String bianhao = json2.getString("contact");
							String companyname = json2.getString("address");
							String remark = json2.getString("remark");
							String addtime = json2.getString("area");

							device_xinhao.setText(xinhao.trim());
							device_bianhao.setText(bianhao.trim());
							device_company.setText(companyname.trim());
							device_beizhu.setText(remark.trim());
							device_addtime.setText(addtime.trim());
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
				if (dialog != null)
					dialog.dismiss();
				super.onCancel();
			}

			@Override
			public void onFinish()
			{
				if (dialog != null)
					dialog.dismiss();
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
