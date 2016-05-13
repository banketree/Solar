package com.crystal.solar.ui.pages;

import org.apache.http.Header;
import org.json.JSONObject;

import android.os.Bundle;
import android.widget.TextView;

import com.crystal.solar.R;
import com.crystal.solar.ui.BaseActivity;
import com.crystal.solar.util.CoreHttpApi;
import com.treecore.http.TAsyncHttpClient;
import com.treecore.http.core.AsyncHttpResponseHandler;
import com.treecore.utils.TStringUtils;
import com.treecore.utils.TToastUtils;
import com.treecore.utils.network.TNetWorkUtil;

public class Data extends BaseActivity
{

	private TextView dingdian_txt;
	private TextView xizui_txt;
	private TextView dianjiaotou_txt;

	private TextView jingdu_x;
	private TextView jingdu_y;
	private TextView jingdu_z;

	private TextView suoji_txt;
	private TextView suoji_time;
	private TextView suoji_hao;
	private TAsyncHttpClient mTAsyncHttpClient;
	private String mCompanyId = "";

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		mCompanyId = getIntent().getStringExtra("company");
		setContentView(R.layout.activity_data_layout);
		init();
		String sbid = getIntent().getStringExtra("sbid");
		onGetTongji("124");// 124
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

		dingdian_txt = (TextView) findViewById(R.id.dingdian_txt);
		xizui_txt = (TextView) findViewById(R.id.xizui_txt);
		dianjiaotou_txt = (TextView) findViewById(R.id.dianjiaotou_txt);

		jingdu_x = (TextView) findViewById(R.id.jingdu_x);
		jingdu_y = (TextView) findViewById(R.id.jingdu_y);
		jingdu_z = (TextView) findViewById(R.id.jingdu_z);

		suoji_txt = (TextView) findViewById(R.id.suoji_txt);
		suoji_time = (TextView) findViewById(R.id.suoji_time);
		suoji_hao = (TextView) findViewById(R.id.suoji_hao);
	}

	private void onGetTongji(String sbid)
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

		mTAsyncHttpClient.post(CoreHttpApi.getTongji(sbid, mCompanyId), null, new AsyncHttpResponseHandler()
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

							String dzshiyong = jsonObject.getString("dzshiyong");
							String dzlimit = jsonObject.getString("dzlimit");
							String xzshiyong = jsonObject.getString("xzshiyong");
							String xzlimit = jsonObject.getString("xzlimit");
							String djtshiyong = jsonObject.getString("djtshiyong");
							String djtlimit = jsonObject.getString("djtlimit");

							String x = jsonObject.getString("x");
							String y = jsonObject.getString("y");
							String z = jsonObject.getString("z");

							String locknum = jsonObject.getString("locknum");
							String locktime = jsonObject.getString("locktime");
							String lockuser = jsonObject.getString("lockuser");

							dingdian_txt.setText(dzshiyong);
							xizui_txt.setText(xzshiyong);
							dianjiaotou_txt.setText(djtshiyong);

							jingdu_x.setText(x);
							jingdu_y.setText(y);
							jingdu_z.setText(z);

							suoji_txt.setText(locknum);
							suoji_time.setText(locktime);
							suoji_hao.setText(lockuser);
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
