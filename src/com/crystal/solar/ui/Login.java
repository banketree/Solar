package com.crystal.solar.ui;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.crystal.solar.R;
import com.crystal.solar.bean.Account;
import com.crystal.solar.dialogs.LoadingDialog;
import com.crystal.solar.util.CoreHttpApi;
import com.crystal.solar.util.UserUtils;
import com.treecore.http.TAsyncHttpClient;
import com.treecore.http.core.AsyncHttpResponseHandler;
import com.treecore.utils.TActivityUtils;
import com.treecore.utils.TStringUtils;
import com.treecore.utils.TToastUtils;
import com.treecore.utils.config.TPreferenceConfig;
import com.treecore.utils.network.TNetWorkUtil;

public class Login extends BaseActivity {
	private Button btn_login;
	private Button register;
	private EditText et_username;
	private EditText et_password;
	private CheckBox ckbox_remember;
	private Dialog dialog;
	private TAsyncHttpClient mLoginClient;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_newlogin);
		Account.getInstance().layout();

		dialog = LoadingDialog.createLoadingDialog(this,
				getString(R.string.loading_load));
		init();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (mLoginClient != null) {
			mLoginClient.cancelRequests(mContext, true);
			mLoginClient.cancelAllRequests(true);
		}
		mLoginClient = null;
	}

	@Override
	public void processEventByInner(Intent intent) {

	}

	public void init() {
		ckbox_remember = (CheckBox) findViewById(R.id.ckbox_remember);
		btn_login = (Button) findViewById(R.id.btn_login);
		btn_login.setOnClickListener(clickListener);
		register = (Button) findViewById(R.id.register);
		register.setOnClickListener(clickListener);

		et_username = (EditText) findViewById(R.id.et_username);
		et_password = (EditText) findViewById(R.id.et_password);

		if (TPreferenceConfig.getInstance().getBoolean("ischecked", false)) {
			ckbox_remember.setChecked(true);
			et_username.setText(Account.getInstance().getUname());
			et_password.setText(Account.getInstance().getUpwd());
		} else {
			ckbox_remember.setChecked(false);
			et_username.setText("");
			et_password.setText("");
		}
	}

	private OnClickListener clickListener = new OnClickListener() {
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_login: {

				String uname = et_username.getText().toString().trim();
				String upwd = et_password.getText().toString().trim();
				if ("".equals(uname) || "".equals(upwd)) {
					Toast.makeText(Login.this,
							getString(R.string.tip_pls_input_com),
							Toast.LENGTH_SHORT).show();
					return;
				}

				onLogin(uname, upwd);

				break;
			}
			case R.id.register: {
				Intent intent = new Intent(Login.this, Register.class);
				startActivity(intent);
				break;
			}

			default:
				break;
			}
		}
	};

	private void onLogin(final String uname, final String password) {
		if (!TNetWorkUtil.isNetworkConnected()) {
			makeText(getResString(R.string.tip_pls_net_error));
			dialog.dismiss();
			return;
		}

		if (TStringUtils.isEmpty(password) || TStringUtils.isEmpty(uname)) {
			TToastUtils.makeText("请输入内容");
			return;
		}

		dialog.show();

		if (mLoginClient == null) {
			mLoginClient = new TAsyncHttpClient();
		}

		if (ckbox_remember.isChecked()) {
			TPreferenceConfig.getInstance().setBoolean("ischecked", true);
		} else {
			TPreferenceConfig.getInstance().setBoolean("ischecked", false);
		}

		mLoginClient.post(CoreHttpApi.getLogin(uname, password), null,
				new AsyncHttpResponseHandler() {

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							byte[] responseBody) {
						//
						try {
							if (statusCode == 200) {
								JSONObject jsonObject = CoreHttpApi
										.checkResponseStatus(responseBody);

								Account.getInstance().setUname(uname);
								Account.getInstance().setUpwd(password);
								Account.getInstance().release();
								Account.getInstance().setJson(jsonObject);

								if (Account.getInstance().isDataValid()) {
									onLoginQuanXian();
								} else {
									TToastUtils.makeText("登录失败");
								}
							} else {
								TToastUtils.makeText("服务器异常");
								dialog.dismiss();
							}
						} catch (Exception e) {
							dialog.dismiss();

							if (e == null
									|| TStringUtils.isEmpty(e.getMessage()))
								return;
							TToastUtils.makeText(e.getMessage());
						}
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							byte[] responseBody, Throwable error) {
						TToastUtils
								.makeText(getString(R.string.tip_pls_net_error));
					}

					@Override
					public void onProgress(int bytesWritten, int totalSize) {
						super.onProgress(bytesWritten, totalSize);
					}

					@Override
					public void onRetry(int retryNo) {
						TToastUtils.makeText(String.format(
								"Request is retried, retry no. %d", retryNo));
					}

					@Override
					public void onCancel() {
						super.onCancel();
						if (dialog != null)
							dialog.dismiss();
					}

					@Override
					public void onFinish() {
						super.onFinish();
						if (dialog != null)
							dialog.dismiss();
					}

					@Override
					public void onStart() {
						super.onStart();
					}
				});
	}

	private void onLoginQuanXian() {
		if (!TNetWorkUtil.isNetworkConnected()) {
			makeText(getResString(R.string.tip_pls_net_error));
			dialog.dismiss();
			return;
		}

		if (mLoginClient == null) {
			mLoginClient = new TAsyncHttpClient();
		}

		mLoginClient.get(CoreHttpApi.getLoginQuanXian(), null,
				new AsyncHttpResponseHandler() {

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							byte[] responseBody) {
						//
						try {
							if (statusCode == 200) {
								JSONObject jsonObject = CoreHttpApi
										.checkResponseStatus(responseBody);

								JSONArray array = jsonObject
										.getJSONArray("datalist");
								if (array.length() > 0) {
									JSONObject obj = (JSONObject) array.get(0);
									Account.getInstance().setCompany(
											obj.getString("id"));
									Account.getInstance().setQuanxian(
											obj.getString("quanxian"));
								}

								if (UserUtils.isAdmin()) {
									TActivityUtils.jumpToNewActivity(mContext,
											CompanyList.class);
								} else {
									TActivityUtils
											.jumpToNewActivity(mContext,
													MainPage.class, Account
															.getInstance()
															.getCompany());
								}

								finish();
							} else {
								TToastUtils.makeText("服务器异常");
							}
						} catch (Exception e) {
							if (e == null
									|| TStringUtils.isEmpty(e.getMessage()))
								return;
							TToastUtils.makeText(e.getMessage());
						}
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							byte[] responseBody, Throwable error) {
						TToastUtils
								.makeText(getString(R.string.tip_pls_net_error));
					}

					@Override
					public void onProgress(int bytesWritten, int totalSize) {
						super.onProgress(bytesWritten, totalSize);
					}

					@Override
					public void onRetry(int retryNo) {
						TToastUtils.makeText(String.format(
								"Request is retried, retry no. %d", retryNo));
					}

					@Override
					public void onCancel() {
						if (dialog != null)
							dialog.dismiss();
						super.onCancel();
					}

					@Override
					public void onFinish() {
						if (dialog != null)
							dialog.dismiss();
						super.onFinish();
					}

					@Override
					public void onStart() {
						super.onStart();
					}
				});
	}
}
