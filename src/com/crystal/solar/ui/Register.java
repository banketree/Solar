package com.crystal.solar.ui;

import org.apache.http.Header;
import org.json.JSONObject;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.crystal.solar.R;
import com.crystal.solar.dialogs.LoadingDialog;
import com.crystal.solar.util.CoreHttpApi;
import com.treecore.http.TAsyncHttpClient;
import com.treecore.http.core.AsyncHttpResponseHandler;
import com.treecore.utils.TStringUtils;
import com.treecore.utils.TToastUtils;
import com.treecore.utils.network.TNetWorkUtil;

public class Register extends BaseActivity
{

	private Button register;
	private EditText et_username;
	private EditText et_password;
	private EditText et_email;
	private Dialog dialog;
	private TAsyncHttpClient mTAsyncHttpClient;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_register_layout);
		dialog = LoadingDialog.createLoadingDialog(this, getString(R.string.loading_load));
		init();
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
		register = (Button) findViewById(R.id.register);
		register.setOnClickListener(clickListener);

		et_username = (EditText) findViewById(R.id.et_username);
		et_password = (EditText) findViewById(R.id.et_password);
		et_email = (EditText) findViewById(R.id.et_email);
	}

	private OnClickListener clickListener = new OnClickListener()
	{

		@Override
		public void onClick(View v)
		{
			switch (v.getId())
			{
			case R.id.register:
			{
				String uname = et_username.getText().toString().trim();
				String upwd = et_password.getText().toString().trim();
				String uemail = et_email.getText().toString().trim();

				if ("".equals(uname) || "".equals(upwd) || "".equals(uemail))
				{
					Toast.makeText(Register.this, getString(R.string.tip_pls_input_com), Toast.LENGTH_SHORT).show();
					return;
				}

				onRegister(uname, upwd, uemail);
				break;
			}
			default:
				break;
			}
		}
	};

	private void onRegister(final String uname, final String password, final String email)
	{
		if (!TNetWorkUtil.isNetworkConnected())
		{
			if (dialog != null)
				dialog.dismiss();
			makeText(getResString(R.string.not_network));
			return;
		}

		if (TStringUtils.isEmpty(password) || TStringUtils.isEmpty(uname))
		{
			TToastUtils.makeText("请输入内容");
			return;
		}

		dialog.show();

		if (mTAsyncHttpClient == null)
		{
			mTAsyncHttpClient = new TAsyncHttpClient();
		}

		mTAsyncHttpClient.get(CoreHttpApi.getRegister(uname, password, email), null, new AsyncHttpResponseHandler()
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
						JSONObject jsonObject = CoreHttpApi.getResponseJSONObject(responseBody);
						String result = jsonObject.getString("err");

						if ("0".equals(result))
						{
							makeText("注册成功");
							finish();
						} else if ("1".equals(result))
						{
							makeText("接口类型参数为空");
						} else if ("2".equals(result))
						{
							makeText("账号由6-16个字符,数字组成");
						} else if ("3".equals(result))
						{
							makeText("注册邮箱为空");
						} else if ("4".equals(result))
						{
							makeText("密码由6-16个字符,数字组成");
						} else if ("5".equals(result))
						{
							makeText("帐号已存在");
						} else
						{
							makeText(result);
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
