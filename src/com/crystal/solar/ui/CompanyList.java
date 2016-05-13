package com.crystal.solar.ui;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.crystal.solar.R;
import com.crystal.solar.bean.Account;
import com.crystal.solar.util.CoreEventUtils;
import com.crystal.solar.util.CoreHttpApi;
import com.treecore.TBroadcastByInner;
import com.treecore.http.TAsyncHttpClient;
import com.treecore.http.core.AsyncHttpResponseHandler;
import com.treecore.utils.TActivityUtils;
import com.treecore.utils.TStringUtils;
import com.treecore.utils.TToastUtils;
import com.treecore.utils.network.TNetWorkUtil;

public class CompanyList extends BaseActivity
{
	private ListView listview;
	private ArrayList<Comitem> mlist = new ArrayList<CompanyList.Comitem>();
	private ComAdater adater = new ComAdater();
	private LayoutInflater inflater;

	private TAsyncHttpClient mTAsyncHttpClient;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		if (!Account.getInstance().isDataValid())
		{
			Account.getInstance().release();
			TActivityUtils.jumpToNewTopActivity(mContext, Login.class);
			makeText("请登录");
			TBroadcastByInner.sentPostEvent(CoreEventUtils.Activity_Self_Destory, 0);
			finish();
			return;
		}

		setContentView(R.layout.activity_company_list_layout);
		inflater = LayoutInflater.from(this);
		initview();
		getLoginComplist();

		findViewById(R.id.return_back).setOnClickListener(new OnClickListener()
		{

			public void onClick(View v)
			{
				if (isWaitingExit)
				{
					isWaitingExit = false;
					CompanyList.this.finish();
				} else
				{
					Toast.makeText(CompanyList.this, "再按一次退出", Toast.LENGTH_SHORT).show();
					isWaitingExit = true;
					Timer timer = new Timer();
					timer.schedule(new TimerTask()
					{
						public void run()
						{
							isWaitingExit = false;
						}
					}, 2000);
				}
			}
		});
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

	private boolean isWaitingExit = false;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			if (isWaitingExit)
			{
				isWaitingExit = false;
				CompanyList.this.finish();
			} else
			{
				Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
				isWaitingExit = true;
				Timer timer = new Timer();
				timer.schedule(new TimerTask()
				{
					public void run()
					{
						isWaitingExit = false;
					}
				}, 2000);
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	public void initview()
	{
		listview = (ListView) findViewById(R.id.listview);
		listview.setAdapter(adater);
		listview.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3)
			{
				Comitem item = mlist.get(pos);
				TActivityUtils.jumpToNewActivity(mContext, MainPage.class, item.getComid());
			}
		});
	}

	public class ComAdater extends BaseAdapter
	{
		public int getCount()
		{
			return mlist.size();
		}

		public Object getItem(int position)
		{
			return mlist.get(position);
		}

		public long getItemId(int position)
		{
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent)
		{
			convertView = inflater.inflate(R.layout.view_company_item, null);
			TextView name = (TextView) convertView.findViewById(R.id.name);
			TextView addr = (TextView) convertView.findViewById(R.id.addr);

			name.setText(mlist.get(position).getName());

			String addStr = mlist.get(position).getAddr();
			if (addStr == null || "".equals(addStr))
			{
				addr.setVisibility(View.GONE);
			} else
			{
				addr.setVisibility(View.VISIBLE);
				addr.setText(addStr);
			}
			return convertView;
		}

	}

	public class Comitem
	{
		private String name;
		private String addr;
		private String uname;
		private String upwd;
		private String comid;

		public String getName()
		{
			return name;
		}

		public void setName(String name)
		{
			this.name = name;
		}

		public String getAddr()
		{
			return addr;
		}

		public void setAddr(String addr)
		{
			this.addr = addr;
		}

		public String getUname()
		{
			return uname;
		}

		public void setUname(String uname)
		{
			this.uname = uname;
		}

		public String getUpwd()
		{
			return upwd;
		}

		public void setUpwd(String upwd)
		{
			this.upwd = upwd;
		}

		public String getComid()
		{
			return comid;
		}

		public void setComid(String comid)
		{
			this.comid = comid;
		}

	}

	private void getLoginComplist()
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

		mTAsyncHttpClient.get(CoreHttpApi.getLoginComplist(), null, new AsyncHttpResponseHandler()
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
							if (!"0".equals(err))
							{
								Toast.makeText(CompanyList.this, getString(R.string.not_null), Toast.LENGTH_SHORT).show();
								return;
							}

							JSONArray array = jsonObject.getJSONArray("datalist");
							for (int i = 0; i < array.length(); i++)
							{
								JSONObject obj = (JSONObject) array.get(i);
								String name = obj.getString("cname");
								// String addr = obj.getString("area");
								String comid = obj.getString("c_id");

								Comitem item = new Comitem();
								item.setName(name);
								// item.setAddr(addr);
								item.setComid(comid);

								mlist.add(item);
							}
							adater.notifyDataSetChanged();
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
