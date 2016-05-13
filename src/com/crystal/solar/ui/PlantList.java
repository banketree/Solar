package com.crystal.solar.ui;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.crystal.solar.R;
import com.crystal.solar.SolarApplication;
import com.crystal.solar.bean.Account;
import com.crystal.solar.bean.Error;
import com.crystal.solar.bean.Power;
import com.crystal.solar.config.Config;
import com.crystal.solar.dialogs.LoadingDialog;
import com.crystal.solar.ui.tab.TablesPage;
import com.crystal.solar.util.CoreEventUtils;
import com.crystal.solar.util.CoreHttpApi;
import com.crystal.solar.util.OmnikUtil;
import com.crystal.solar.util.ShowDialogHandler;
import com.treecore.TBroadcastByInner;
import com.treecore.http.TAsyncHttpClient;
import com.treecore.http.core.AsyncHttpResponseHandler;
import com.treecore.utils.TActivityUtils;
import com.treecore.utils.TStringUtils;
import com.treecore.utils.TToastUtils;
import com.treecore.utils.network.TNetWorkUtil;

@SuppressLint("HandlerLeak")
public class PlantList extends BaseActivity implements OnClickListener
{

	private TextView device_xinhao;
	private TextView device_bianhao;
	private TextView device_company;
	private TextView device_beizhu;
	private TextView device_addtime;
	// ///////////////////////////////////////////////////////////

	// private TextView tv_title;
	// private Button btn_logout;
	// private Button btn_refresh;

	private ListView plantListView;
	private LinearLayout loadingLayout;
	private LinearLayout mListFootLoadingLayout;

	private String username;
	private String token;

	private PlantListAdapter listAdapter;

	private SharedPreferences imagePrefs;

	private static ShowDialogHandler handler;

	private static Handler postHandler;
	private static final int PLANTLIST_POST = 0;

	private TAsyncHttpClient mTAsyncHttpClient;

	private Dialog dialog;
	private String mCompanyId = "";

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		mCompanyId = getIntent().getStringExtra("company");
		setContentView(R.layout.activity_plant_list);
		dialog = LoadingDialog.createLoadingDialog(getParent(), getString(R.string.loadmoredata));
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

	@Override
	protected void onResume()
	{
		super.onResume();
		// postHandler.sendEmptyMessageDelayed(PLANTLIST_POST,
		// Config.REFRESH_INTERVAL);
	}

	private void init()
	{
		handler = new ShowDialogHandler();

		postHandler = new Handler()
		{
			@Override
			public void handleMessage(Message msg)
			{
				super.handleMessage(msg);
				switch (msg.what)
				{
				case PLANTLIST_POST:
					// new RefreshPlantsTask().execute();
					this.sendEmptyMessageDelayed(PLANTLIST_POST, Config.REFRESH_INTERVAL);
					break;
				default:
					break;
				}
			}
		};

		// tv_title = (TextView) findViewById(R.id.tv_title);
		// tv_title.setText(R.string.plantlist);
		// btn_logout = (Button) findViewById(R.id.btn_right);
		// btn_refresh = (Button) findViewById(R.id.btn_left);
		// btn_refresh.setBackgroundResource(R.drawable.button_refresh);
		// btn_refresh.setVisibility(View.VISIBLE);
		loadingLayout = (LinearLayout) findViewById(R.id.layout_loading);
		mListFootLoadingLayout = (LinearLayout) findViewById(R.id.layout_list_loading);

		// btn_refresh.setOnClickListener(this);
		// btn_logout.setOnClickListener(this);
		plantListView = (ListView) findViewById(R.id.plantListView);
		listAdapter = new PlantListAdapter(this);
		plantListView.setOnItemClickListener(listAdapter);

		Intent intent = getIntent();
		username = intent.getStringExtra("username");
		token = intent.getStringExtra("token");
		// new LoadingPlantsTask().execute();
		onGetPowerList();

		// ///////////////////////////////////////////////////////
		device_xinhao = (TextView) findViewById(R.id.device_xinhao);
		device_bianhao = (TextView) findViewById(R.id.device_bianhao);
		device_company = (TextView) findViewById(R.id.device_company);
		device_beizhu = (TextView) findViewById(R.id.device_beizhu);
		device_addtime = (TextView) findViewById(R.id.device_addtime);

		onGetCompinfo();
	}

	private int getStatusDrawableResource(String status)
	{
		int value = Integer.parseInt(status);
		if (value == 0)
		{
			return R.drawable.green;
		} else
		{
			return R.drawable.red;
		}
	}

	class LoadingPlantsTask extends AsyncTask<Void, Void, Object>
	{

		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
			plantListView.setVisibility(View.GONE);
			loadingLayout.setVisibility(View.VISIBLE);
		}

		@Override
		protected Object doInBackground(Void... params)
		{

			return OmnikUtil.getPlantList(PlantList.this, username, token);
		}

		@Override
		protected void onPostExecute(Object result)
		{
			super.onPostExecute(result);
			loadingLayout.setVisibility(View.GONE);
			plantListView.setVisibility(View.VISIBLE);

			if (result instanceof List)
			{
				listAdapter.mData = (List<Power>) result;
				plantListView.setAdapter(listAdapter);
				listAdapter.notifyDataSetChanged();
			} else if (result instanceof Error)
			{
				Message m = handler.obtainMessage();
				m.what = Config.ERROR_OCCUR;
				m.obj = PlantList.this;
				Bundle b = new Bundle();
				b.putString("error_msg", ((Error) result).errorMessage);
				m.setData(b);
				handler.sendMessage(m);
			}
		}

	}

	class RefreshPlantsTask extends AsyncTask<Void, Void, Object>
	{

		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
			// // plantListView.setVisibility(View.GONE);
			// loadingLayout.setVisibility(View.VISIBLE);
			mListFootLoadingLayout.setVisibility(View.VISIBLE);

		}

		@Override
		protected Object doInBackground(Void... params)
		{

			return OmnikUtil.getPlantList(PlantList.this, username, token);

			// return NetDataProxyManager.getPowerList();
		}

		@Override
		protected void onPostExecute(Object result)
		{
			super.onPostExecute(result);
			loadingLayout.setVisibility(View.GONE);
			plantListView.setVisibility(View.VISIBLE);

			if (result instanceof List)
			{
				listAdapter.mData = (List<Power>) result;
				listAdapter.notifyDataSetChanged();
			} else if (result instanceof Error)
			{
				Message m = handler.obtainMessage();
				m.what = Config.ERROR_OCCUR;
				m.obj = PlantList.this;
				Bundle b = new Bundle();
				b.putString("error_msg", ((Error) result).errorMessage);
				m.setData(b);
				handler.sendMessage(m);
			}

			mListFootLoadingLayout.setVisibility(View.GONE);
		}

	}

	private class LogoutAyncTask extends AsyncTask<Void, Void, Object>
	{
		@Override
		protected Object doInBackground(Void... params)
		{
			return OmnikUtil.doLogout(mContext, username, token);
		}

	}

	class PlantListAdapter extends BaseAdapter implements OnItemClickListener
	{

		private LayoutInflater mInflater;
		public List<Power> mData;

		// private ArrayList<View> views;

		public PlantListAdapter(Context context)
		{
			this.mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount()
		{
			return mData.size();
		}

		@Override
		public Object getItem(int position)
		{
			return mData.get(position);
		}

		@Override
		public long getItemId(int position)
		{
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			ViewHolder holder = null;
			if (convertView == null)
			{

				holder = new ViewHolder();

				convertView = mInflater.inflate(R.layout.view_plant_item, null);
				holder.tv_status = (TextView) convertView.findViewById(R.id.tv_status);
				holder.iv_station = (ImageView) convertView.findViewById(R.id.iv_station);
				holder.tv_station_title = (TextView) convertView.findViewById(R.id.tv_station_title);
				holder.tv_station_power = (TextView) convertView.findViewById(R.id.tv_station_power);
				holder.tv_station_income = (TextView) convertView.findViewById(R.id.tv_station_income);
				holder.tv_station_etoday = (TextView) convertView.findViewById(R.id.tv_station_etoday);
				holder.tv_station_etotal = (TextView) convertView.findViewById(R.id.tv_station_etotal);
				convertView.setTag(holder);
			} else
			{

				holder = (ViewHolder) convertView.getTag();
			}

			Power power = mData.get(position);
			// holder.tv_status
			// .setBackgroundResource(getStatusDrawableResource(power.status));
			// new DownloadImageTask().execute(holder.iv_station, power.pic);
			holder.tv_station_title.setText(power.name);
			holder.tv_station_power.setText(power.bianhao);
			// holder.tv_station_income.setText("Income:" + power.income
			// + power.unit);
			// holder.tv_station_etoday.setText("EToday:" + power.eToday +
			// "kWh");
			// holder.tv_station_etotal.setText("ETotal:" + power.eTotal +
			// "kWh");

			return convertView;
		}

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id)
		{
			Log.v("PlantListActivity", "pos: " + position);
			try
			{
				Class c = Class.forName("com.google.android.maps.MapActivity");
				Intent i = new Intent(PlantList.this, TablesPage.class);
				Power p = mData.get(position);
				i.putExtra("username", username);
				i.putExtra("stationID", p.stationID);
				SolarApplication.getInstance().setStationID(p.bianhao);
				i.putExtra("name", p.name);
				i.putExtra("timezone", p.timeZone);
				i.putExtra("token", token);
				i.putExtra("country", p.country);
				i.putExtra("province", p.province);
				i.putExtra("city", p.city);
				i.putExtra("street", p.street);
				i.putExtra("pic", p.pic);
				i.putExtra("unit", p.unit);
				i.putExtra("sbid", p.bianhao);
				i.putExtra("company", mCompanyId);
				startActivity(i);
			} catch (Exception e)
			{
				Intent i = new Intent(PlantList.this, TablesPage.class);
				Power p = mData.get(position);
				i.putExtra("username", username);
				i.putExtra("stationID", p.stationID);
				SolarApplication.getInstance().setStationID(p.bianhao);

				i.putExtra("name", p.name);
				i.putExtra("timezone", p.timeZone);
				i.putExtra("token", token);
				i.putExtra("country", p.country);
				i.putExtra("province", p.province);
				i.putExtra("city", p.city);
				i.putExtra("street", p.street);
				i.putExtra("pic", p.pic);
				i.putExtra("unit", p.unit);
				i.putExtra("company", mCompanyId);
				startActivity(i);
			}
		}

		private Drawable loadImageFromNetwork(String imageUrl)
		{
			Drawable drawable = null;
			try
			{
				imagePrefs = getPreferences(MODE_PRIVATE);
				// the image has been saved locally, just get it
				if (imagePrefs.contains(imageUrl))
				{
					return OmnikUtil.loadImageFromLocal(PlantList.this, imageUrl);
				}
				drawable = Drawable.createFromStream(new URL(imageUrl).openStream(), imageUrl);
				drawable = zoomDrawable(drawable, 150, 100);
				imagePrefs.edit().putBoolean(imageUrl, true).commit();
				new SaveImageTask().execute(drawable, imageUrl);
				return drawable;

			} catch (IOException e)
			{
				Log.d("PlantListActivity", e.getMessage());
				return getResources().getDrawable(R.drawable.station);
			}

		}

		/** task for saving images to local */
		private class SaveImageTask extends AsyncTask<Object, Void, Void>
		{

			@Override
			protected Void doInBackground(Object... objs)
			{
				saveImage(PlantList.this, (Drawable) objs[0], (String) objs[1]);
				return null;
			}

		}

		private class DownloadImageTask extends AsyncTask<Object, Void, Drawable>
		{
			private ImageView iv;
			private String imageUrl;

			@Override
			protected Drawable doInBackground(Object... objs)
			{
				iv = (ImageView) objs[0];
				imageUrl = (String) objs[1];
				return loadImageFromNetwork(imageUrl);
			}

			@Override
			protected void onPostExecute(Drawable result)
			{
				iv.setImageDrawable(result);
				// listAdapter.notifyDataSetChanged();
			}
		}

		/** zoom the image to the proper size */
		private Drawable zoomDrawable(Drawable drawable, int w, int h)
		{
			Drawable drawable1 = getResources().getDrawable(R.drawable.station_2x);
			w = drawable1.getIntrinsicWidth();
			h = drawable1.getIntrinsicHeight();
			Log.d("PlantListActivity", "w: " + w + " H: " + h);
			int width = drawable.getIntrinsicWidth();
			int height = drawable.getIntrinsicHeight();
			Bitmap oldbmp = drawableToBitmap(drawable);
			Matrix matrix = new Matrix();
			float scaleWidth = ((float) w / width);
			float scaleHeight = ((float) h / height);
			matrix.postScale(scaleWidth, scaleHeight);
			Bitmap newbmp = Bitmap.createBitmap(oldbmp, 0, 0, width, height, matrix, true);
			return new BitmapDrawable(null, newbmp);
		}

		/** convert from drawable to bitmap */
		private Bitmap drawableToBitmap(Drawable drawable)
		{
			int width = drawable.getIntrinsicWidth();
			int height = drawable.getIntrinsicHeight();
			Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
			Bitmap bitmap = Bitmap.createBitmap(width, height, config);
			Canvas canvas = new Canvas(bitmap);
			drawable.setBounds(0, 0, width, height);
			drawable.draw(canvas);
			return bitmap;
		}

		/** convert from bitmap to byte array */
		private byte[] Bitmap2Bytes(Bitmap bm)
		{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
			return baos.toByteArray();
		}

		/** save station image to local cache */
		private void saveImage(Context context, Drawable drawable, String url)
		{
			String localPath = null;
			Bitmap image = ((BitmapDrawable) drawable).getBitmap();
			FileOutputStream fos = null;
			try
			{
				localPath = url.substring(url.lastIndexOf("/") + 1);
				fos = context.openFileOutput(localPath, Context.MODE_PRIVATE);
				fos.write(Bitmap2Bytes(image));
				fos.close();

			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}

	}

	public static final class ViewHolder
	{
		public TextView tv_status;
		public ImageView iv_station;
		public TextView tv_station_title;
		public TextView tv_station_power;
		public TextView tv_station_income;
		public TextView tv_station_etoday;
		public TextView tv_station_etotal;
	}

	@Override
	public void onClick(View v)
	{
		if (v.getId() == R.id.btn_right)
		{
			new LogoutAyncTask().execute();

			Account.getInstance().release();
			TActivityUtils.jumpToNewTopActivity(mContext, Login.class);
			TBroadcastByInner.sentPostEvent(CoreEventUtils.Activity_Self_Destory, 1);

		} else if (v.getId() == R.id.btn_left)
		{
			new RefreshPlantsTask().execute();
		}

	}

	private void onGetPowerList()
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

		mTAsyncHttpClient.post(CoreHttpApi.getPowerList(mCompanyId), null, new AsyncHttpResponseHandler()
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
							String err = jsonObject.getString("err");
							JSONArray jsonArray = jsonObject.getJSONArray("datalist");

							ArrayList<Power> list = new ArrayList<Power>();
							for (int i = 0; i < jsonArray.length(); i++)
							{
								JSONObject obj = (JSONObject) jsonArray.get(i);
								Power power = new Power();
								String deviceid = obj.getString("d_id");
								String xinghao = obj.getString("xinghao");
								String bianhao = obj.getString("bianhao");
								String remark = obj.getString("remark");
								power.stationID = deviceid;
								power.name = xinghao;
								power.bianhao = bianhao;
								power.remark = remark;

								list.add(power);
							}
							listAdapter.mData = list;
							plantListView.setAdapter(listAdapter);
							listAdapter.notifyDataSetChanged();

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
							String err = jsonObject.getString("err");
							JSONObject json2 = (JSONObject) jsonObject.getJSONArray("datalist").get(0);

							String xinhao = json2.getString("cname");
							String bianhao = json2.getString("contact");
							String companyname = json2.getString("address");
							String remark = json2.getString("remark");
							String addtime = json2.getString("area");
							SolarApplication.getInstance().setAddress(companyname);
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
