package com.crystal.solar.ui.pages;

import org.apache.http.Header;
import org.json.JSONObject;

import android.app.Dialog;
import android.os.Bundle;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.crystal.solar.R;
import com.crystal.solar.dialogs.LoadingDialog;
import com.crystal.solar.ui.BaseActivity;
import com.crystal.solar.util.CoreHttpApi;
import com.treecore.http.TAsyncHttpClient;
import com.treecore.http.core.AsyncHttpResponseHandler;
import com.treecore.utils.TStringUtils;
import com.treecore.utils.TToastUtils;
import com.treecore.utils.network.TNetWorkUtil;

public class MapLocate extends BaseActivity implements OnGetGeoCoderResultListener
{
	GeoCoder mSearch = null;
	double lat = 39.897445;
	double lang = 116.331398;

	private MapView mMapView;
	private BaiduMap mBaiduMap;
	private Marker mMarkerA;
	BitmapDescriptor bdA = BitmapDescriptorFactory.fromResource(R.drawable.ac8);
	// 定位相关
	LocationClient mLocClient;
	public MyLocationListenner myListener = new MyLocationListenner();
	private Dialog dialog;

	private TAsyncHttpClient mTAsyncHttpClient, mUploadGPSClient;
	private String mCompanyId = "";

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		mCompanyId = getIntent().getStringExtra("company");

		setContentView(R.layout.activity_map_layout);
		dialog = LoadingDialog.createLoadingDialog(this, getString(R.string.loadmoredata));

		mMapView = (MapView) findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();
		mBaiduMap.getUiSettings();
		mBaiduMap.setMyLocationEnabled(true);
		// 开启定位图层
		// 定位初始化
		mBaiduMap.setOnMarkerClickListener(listener);
		mLocClient = new LocationClient(getApplicationContext());
		mLocClient.registerLocationListener(myListener);

		mSearch = GeoCoder.newInstance();
		mSearch.setOnGetGeoCodeResultListener(this);
		onGetCompinfo();
	}

	OnMarkerClickListener listener = new OnMarkerClickListener()
	{
		public boolean onMarkerClick(Marker marker)
		{
			if (marker == mMarkerA)
			{
				onUploadGPS("124", lat + "", lang + "");
			}
			return false;
		}
	};

	@Override
	protected void onPause()
	{
		mMapView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume()
	{
		mMapView.onResume();
		super.onResume();
	}

	@Override
	protected void onDestroy()
	{
		if (mTAsyncHttpClient != null)
		{
			mTAsyncHttpClient.cancelRequests(mContext, true);
			mTAsyncHttpClient.cancelAllRequests(true);
		}
		mTAsyncHttpClient = null;

		if (mUploadGPSClient != null)
		{
			mUploadGPSClient.cancelRequests(mContext, true);
			mUploadGPSClient.cancelAllRequests(true);
		}
		mUploadGPSClient = null;

		mLocClient.stop();
		mBaiduMap.setMyLocationEnabled(false);
		mMapView.onDestroy();
		mMapView = null;
		super.onDestroy();
		bdA.recycle();

	}

	/**
	 * 定位SDK监听函数
	 */
	public class MyLocationListenner implements BDLocationListener
	{
		public void onReceiveLocation(BDLocation location)
		{
			if (location == null || mMapView == null || mBaiduMap == null)
				return;

			MyLocationData locData = new MyLocationData.Builder().accuracy(location.getRadius()).direction(100).latitude(location.getLatitude()).longitude(location.getLongitude()).build();
			mBaiduMap.setMyLocationData(locData);
			LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
			MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
			mBaiduMap.animateMapStatus(u);

			mBaiduMap.clear();
			LatLng point = new LatLng(location.getLatitude(), location.getLongitude());
			OverlayOptions option = new MarkerOptions().position(point).icon(bdA);
			mMarkerA = (Marker) mBaiduMap.addOverlay(option);

			lat = location.getLatitude();
			lang = location.getLongitude();
			mLocClient.stop();
		}

		public void onReceivePoi(BDLocation poiLocation)
		{
		}
	}

	public void addMarkertest()
	{
		LatLng ll = new LatLng(lat, lang);
		MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
		mBaiduMap.animateMapStatus(u);

		mBaiduMap.clear();
		LatLng point = new LatLng(lat, lang);
		OverlayOptions option = new MarkerOptions().position(point).icon(bdA);
		mMarkerA = (Marker) mBaiduMap.addOverlay(option);
	}

	public void onGetGeoCodeResult(GeoCodeResult result)
	{
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR)
		{
			Toast.makeText(mContext, "抱歉，未能找到结果", Toast.LENGTH_LONG).show();
			return;
		}

		LatLng ll = new LatLng(result.getLocation().latitude, result.getLocation().longitude);
		MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
		mBaiduMap.animateMapStatus(u);

		mBaiduMap.clear();
		LatLng point = new LatLng(result.getLocation().latitude, result.getLocation().longitude);
		OverlayOptions option = new MarkerOptions().position(point).icon(bdA);
		mMarkerA = (Marker) mBaiduMap.addOverlay(option);

		lat = result.getLocation().latitude;
		lang = result.getLocation().longitude;
	}

	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult arg0)
	{

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
							// companyname = "上海浦东新区";
							mSearch.geocode(new GeoCodeOption().city("").address(companyname));
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

	private void onUploadGPS(final String sbid, final String lat, final String lang)
	{
		if (!TNetWorkUtil.isNetworkConnected())
		{
			if (dialog != null)
				dialog.dismiss();
			makeText(getResString(R.string.not_network));
			return;
		}

		if (mUploadGPSClient == null)
		{
			mUploadGPSClient = new TAsyncHttpClient();
		}

		dialog.show();

		mUploadGPSClient.get(CoreHttpApi.getUploadGPS(sbid, lat, lang, mCompanyId), null, new AsyncHttpResponseHandler()
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

						Toast.makeText(mContext, getString(R.string.submit_success), Toast.LENGTH_SHORT).show();
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
