package com.crystal.solar.ui.pages;

import java.net.URLEncoder;

import org.apache.http.Header;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.crystal.solar.R;
import com.crystal.solar.dialogs.LoadingDialog;
import com.crystal.solar.ui.BaseActivity;
import com.crystal.solar.util.CoreHttpApi;
import com.treecore.http.TAsyncHttpClient;
import com.treecore.http.core.AsyncHttpResponseHandler;
import com.treecore.utils.TStringUtils;
import com.treecore.utils.TToastUtils;
import com.treecore.utils.network.TNetWorkUtil;

public class ModifyReq extends BaseActivity
{

	private TextView fuyupower;

	private Spinner spinner1;
	private EditText context_edt;
	private EditText power_edt;
	private Button submit;

	String[] names = null;
	private int selectpos = 0;
	private Dialog dialog;

	private Spinner spinner_xilie;
	private Spinner spinner_leixing;
	private Spinner spinner_xiangmu;

	private TAsyncHttpClient mTAsyncHttpClient, mGetFuyuPowerClient;
	private String mCompanyId = "";

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		mCompanyId = getIntent().getStringExtra("company");

		dialog = LoadingDialog.createLoadingDialog(this, getString(R.string.loading_submit));
		View viewToLoad = LayoutInflater.from(this.getParent()).inflate(R.layout.activity_shouhou_apply, null);
		this.setContentView(viewToLoad);
		initArrays();
		init();
		onGetFuyuPower();
	}

	public void init()
	{

		// names = new String[]{
		// getString(R.string.bug_apply_typeremote),
		// getString(R.string.bug_apply_typetel),
		// getString(R.string.bug_apply_typeemail),
		// };

		fuyupower = (TextView) findViewById(R.id.fuyupower);

		names = new String[]
		{ getString(R.string.find_daigong), getString(R.string.find_dingdan), };
		spinner1 = (Spinner) findViewById(R.id.spinner1);
		spinner_xilie = (Spinner) findViewById(R.id.spinner_xilie);
		spinner_leixing = (Spinner) findViewById(R.id.spinner_leixing);

		ArrayAdapter<String> adapterxilie = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, xiliearray);
		adapterxilie.setDropDownViewResource(R.layout.view_drop_down_item);
		spinner_xilie.setAdapter(adapterxilie);

		ArrayAdapter<String> adapterleixing = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, leixinarray);
		adapterleixing.setDropDownViewResource(R.layout.view_drop_down_item);
		spinner_leixing.setAdapter(adapterleixing);

		spinner_xilie.setOnItemSelectedListener(xilieItemSelectedListener);
		spinner_leixing.setOnItemSelectedListener(leixingItemSelectedListener);

		context_edt = (EditText) findViewById(R.id.context_edt);
		power_edt = (EditText) findViewById(R.id.power_edt);

		submit = (Button) findViewById(R.id.submit);

		// spinner

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, names);
		adapter.setDropDownViewResource(R.layout.view_drop_down_item);
		spinner1.setAdapter(adapter);

		spinner1.setOnItemSelectedListener(new OnItemSelectedListener()
		{

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
			{
				selectpos = position;
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent)
			{

			}
		});

		submit.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{

				// if(context_edt.getText().toString().trim().equals(""))
				// {
				// Toast.makeText(ModifyReqActivity.this,
				// getString(R.string.tip_pls_input_com),
				// Toast.LENGTH_SHORT).show();
				// return;
				// }
				StringBuffer sb = new StringBuffer();
				sb.append(getString(R.string.huzhu_xilie) + ":" + spinner_xilie.getSelectedItem().toString() + "\n");
				sb.append(getString(R.string.huzhu_leixing) + ":" + spinner_leixing.getSelectedItem().toString() + "\n");

				if (!power_edt.getText().toString().equals(""))
				{
					sb.append(getString(R.string.powers_str) + ":" + power_edt.getText().toString() + "k\n");
				}
				if (!context_edt.getText().toString().equals(""))
				{
					sb.append(getString(R.string.beizhu) + ":" + context_edt.getText().toString());
				}

				dialog.show();
				String remarks = "";
				try
				{
					remarks = URLEncoder.encode(sb.toString(), "UTF-8");
				} catch (Exception e)
				{
				}
				Log.i("testremark", remarks);
				int type = selectpos + 1;
				onGetShouhouApply(type, remarks);
			}
		});
	}

	private String[] xiliearray;
	private String[] leixinarray;
	private String[] xiangmuarray;

	public void initArrays()
	{
		Resources res = getResources();
		xiliearray = res.getStringArray(R.array.xilie_array);
		leixinarray = res.getStringArray(R.array.leibie_array);
		xiangmuarray = res.getStringArray(R.array.xiangmu_array);
	}

	private OnItemSelectedListener xilieItemSelectedListener = new OnItemSelectedListener()
	{

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
		{
			if (position == 0)
			{
				ArrayAdapter<String> adapterleixing = new ArrayAdapter<String>(ModifyReq.this, android.R.layout.simple_spinner_item, leixinarray);
				adapterleixing.setDropDownViewResource(R.layout.view_drop_down_item);
				spinner_leixing.setAdapter(adapterleixing);
			} else if (position == 1)
			{
				ArrayAdapter<String> adapterleixing = new ArrayAdapter<String>(ModifyReq.this, android.R.layout.simple_spinner_item, xiangmuarray);
				adapterleixing.setDropDownViewResource(R.layout.view_drop_down_item);
				spinner_leixing.setAdapter(adapterleixing);
			} else if (position == 2)// otner
			{
				ArrayAdapter<String> adapterleixing = new ArrayAdapter<String>(ModifyReq.this, android.R.layout.simple_spinner_item, new String[]
				{ getString(R.string.xilie_other) });
				adapterleixing.setDropDownViewResource(R.layout.view_drop_down_item);
				spinner_leixing.setAdapter(adapterleixing);
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent)
		{

		}
	};

	private OnItemSelectedListener leixingItemSelectedListener = new OnItemSelectedListener()
	{

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
		{

		}

		@Override
		public void onNothingSelected(AdapterView<?> parent)
		{

		}
	};

	private void onGetShouhouApply(final int type, final String contents)
	{
		if (!TNetWorkUtil.isNetworkConnected())
		{
			makeText(getResString(R.string.not_network));
			return;
		}

		dialog.show();

		if (mTAsyncHttpClient == null)
		{
			mTAsyncHttpClient = new TAsyncHttpClient();
		}

		mTAsyncHttpClient.get(CoreHttpApi.getShouhouapply(type, contents, mCompanyId), null, new AsyncHttpResponseHandler()
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
						Toast.makeText(ModifyReq.this, getString(R.string.submit_success), Toast.LENGTH_SHORT).show();
						Toast.makeText(ModifyReq.this, getString(R.string.submit_success), Toast.LENGTH_SHORT).show();
						context_edt.setText("");
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
				dialog.dismiss();
			}

			@Override
			public void onFinish()
			{
				super.onFinish();
				dialog.dismiss();
			}

			@Override
			public void onStart()
			{
				super.onStart();
			}
		});
	}

	private void onGetFuyuPower()
	{
		if (!TNetWorkUtil.isNetworkConnected())
		{
			if (dialog != null)
				dialog.dismiss();
			makeText(getResString(R.string.not_network));
			return;
		}

		dialog.show();

		if (mGetFuyuPowerClient == null)
		{
			mGetFuyuPowerClient = new TAsyncHttpClient();
		}

		mGetFuyuPowerClient.get(CoreHttpApi.getFuyuPower(mCompanyId), null, new AsyncHttpResponseHandler()
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
						String err = jsonObject.getString("err");
						String sycn = jsonObject.getString("sycn");
						fuyupower.setText(getString(R.string.syncpower) + sycn + "k");
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
