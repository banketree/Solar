package com.crystal.solar.ui;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.crystal.solar.R;
import com.crystal.solar.dialogs.LoadingDialog;
import com.crystal.solar.util.CoreEventUtils;
import com.crystal.solar.util.CoreHttpApi;
import com.crystal.solar.util.ScreenShot;
import com.crystal.solar.view.HorizontalPager;
import com.crystal.solar.view.SegmentedRadioGroup;
import com.treecore.TBroadcastByInner;
import com.treecore.http.TAsyncHttpClient;
import com.treecore.http.core.AsyncHttpResponseHandler;
import com.treecore.utils.TToastUtils;
import com.treecore.utils.network.TNetWorkUtil;

public class AllChart extends BaseActivity implements OnClickListener {
	private static final int Type_Day = 0;
	private static final int Type_Week = 1;
	private static final int Type_Month = 2;
	private static final int Type_Year = 3;

	public static final int Table_Capacity = 0;
	public static final int Table_Working = 1;
	public static final int Table_Day_Night = 2;

	private boolean isfrist = true;
	private WebView webview;
	private static Calendar mCurCalendar;

	private int mCurTableIndex = Table_Capacity;// 3个表（产能统计、工况统计、日夜班统计）
	private int mTypes = 0; // 0今日统计1周统计2月统计3年统计
	private int mQuarter = 0; //

	private TextView tv_date;
	private ImageView iv_pre;
	private ImageView iv_next;

	private SegmentedRadioGroup radioGroup_date;

	private HorizontalPager horizontalPager;

	private ImageView sharebtn;

	private Spinner spinner1;

	private Dialog dialog;

	private TAsyncHttpClient mTAsyncHttpClientA;
	private TAsyncHttpClient mTAsyncHttpClientB;
	private TAsyncHttpClient mTAsyncHttpClientC;

	private String mCompanyId = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mCompanyId = getIntent().getStringExtra("company");
		setContentView(R.layout.activity_curve);
		mCurTableIndex = getIntent().getIntExtra("table", 0);

		dialog = LoadingDialog.createLoadingDialog(this,
				getString(R.string.loading_submit));
		init();
		initwebview();

		switchTables();// delay 500
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (mTAsyncHttpClientA != null) {
			mTAsyncHttpClientA.cancelRequests(mContext, true);
			mTAsyncHttpClientA.cancelAllRequests(true);
		}
		mTAsyncHttpClientA = null;

		if (mTAsyncHttpClientB != null) {
			mTAsyncHttpClientB.cancelRequests(mContext, true);
			mTAsyncHttpClientB.cancelAllRequests(true);
		}
		mTAsyncHttpClientB = null;

		if (mTAsyncHttpClientC != null) {
			mTAsyncHttpClientC.cancelRequests(mContext, true);
			mTAsyncHttpClientC.cancelAllRequests(true);
		}
		mTAsyncHttpClientC = null;
	}

	@Override
	protected void onResume() {
		if (isfrist) {
			isfrist = false;
		} else {
			updateDateDisplay();
		}
		super.onResume();
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.iv_pre) {
			onPreOrNextDate(-1);
			updateDateDisplay();
			return;
		} else if (v.getId() == R.id.iv_next) {
			onPreOrNextDate(1);
			updateDateDisplay();
			return;
		} else if (v.getId() == R.id.tv_date) {
			new DatePickerDialog(AllChart.this, new OnDateSetListener() {
				public void onDateSet(DatePicker view, int year,
						int monthOfYear, int dayOfMonth) {
					mCurCalendar.set(Calendar.YEAR, year);
					mCurCalendar.set(Calendar.MONTH, monthOfYear);
					mCurCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

					updateDateDisplay();
				}
			}, mCurCalendar.get(Calendar.YEAR),
					mCurCalendar.get(Calendar.MONTH),
					mCurCalendar.get(Calendar.DAY_OF_MONTH)).show();
			return;
		}
	}

	@Override
	public void processEventByInner(Intent intent) {
		super.processEventByInner(intent);

		int mainEvent = intent.getIntExtra(TBroadcastByInner.MAINEVENT, 0);
		int event = intent.getIntExtra(TBroadcastByInner.EVENT, 0);

		if (mainEvent == CoreEventUtils.Chart_Refresh) {
			if (mCurTableIndex == event) {
				updateDateDisplay();
			}
		}
	}

	public void init() {
		if (mCurTableIndex == 0) {
			mCurCalendar = Calendar.getInstance();
		}

		if (mTAsyncHttpClientA == null) {
			mTAsyncHttpClientA = new TAsyncHttpClient();
			mTAsyncHttpClientA.setTimeout(20 * 1000);
		}
		if (mTAsyncHttpClientB == null) {
			mTAsyncHttpClientB = new TAsyncHttpClient();
			mTAsyncHttpClientB.setTimeout(20 * 1000);
		}
		if (mTAsyncHttpClientC == null) {
			mTAsyncHttpClientC = new TAsyncHttpClient();
			mTAsyncHttpClientC.setTimeout(20 * 1000);
		}

		mQuarter = getCurQuarter(mCurCalendar);
		tv_date = (TextView) findViewById(R.id.tv_date);
		iv_pre = (ImageView) findViewById(R.id.iv_pre);
		iv_next = (ImageView) findViewById(R.id.iv_next);
		iv_pre.setOnClickListener(this);
		iv_next.setOnClickListener(this);
		tv_date.setOnClickListener(this);

		horizontalPager = (HorizontalPager) findViewById(R.id.day_curveViewFlipper);

		radioGroup_date = (SegmentedRadioGroup) findViewById(R.id.radioGroup_date);
		radioGroup_date
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						switch (checkedId) {
						case R.id.radio_day: {
							mTypes = Type_Day;
							updateDateDisplay();
							break;
						}
						case R.id.radio_week: {
							mTypes = Type_Week;
							updateDateDisplay();
							break;
						}
						case R.id.radio_month: {
							mTypes = Type_Month;
							updateDateDisplay();
							break;
						}
						case R.id.radio_year: {
							mTypes = Type_Year;
							updateDateDisplay();
							break;
						}
						}
					}
				});

		sharebtn = (ImageView) findViewById(R.id.sharebtn);
		sharebtn.setOnClickListener(clickListener);

		// spinner
		spinner1 = (Spinner) findViewById(R.id.spinner1);
		String[] names = new String[] { "产能状态统计", "运行状态运行时间", "运行状态等待时间",
				"运行状态停止时间" };
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, names);
		spinner1.setAdapter(adapter);
	}

	private OnClickListener clickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.sharebtn:
				share();
				break;

			}
		}
	};

	public String getStartDateString() {
		Calendar calendar = (Calendar) mCurCalendar.clone();
		String formatString = "";
		switch (mTypes) {
		case Type_Day:
			formatString = formatDate(calendar.get(Calendar.YEAR),
					calendar.get(Calendar.MONTH),
					calendar.get(Calendar.DAY_OF_MONTH), "yyyy-MM-dd");
			break;
		case Type_Week:
			calendar = getMondayOfThisWeek(calendar);
			formatString = formatDate(calendar.get(Calendar.YEAR),
					calendar.get(Calendar.MONTH),
					calendar.get(Calendar.DAY_OF_MONTH), "yyyy-MM-dd");
			break;
		case Type_Month:
			formatString = formatDate(calendar.get(Calendar.YEAR),
					calendar.get(Calendar.MONTH),
					calendar.get(Calendar.DAY_OF_MONTH), "yyyy-MM-01");
			break;
		case Type_Year:
			formatString = formatDate(calendar.get(Calendar.YEAR),
					calendar.get(Calendar.MONTH),
					calendar.get(Calendar.DAY_OF_MONTH), "yyyy-01-01");
			break;
		}
		return formatString;
	}

	public String getEndDateString() {
		Calendar calendar = (Calendar) mCurCalendar.clone();
		String formatString = "";
		switch (mTypes) {
		case Type_Day:
			formatString = formatDate(calendar.get(Calendar.YEAR),
					calendar.get(Calendar.MONTH),
					calendar.get(Calendar.DAY_OF_MONTH), "yyyy-MM-dd");
			break;
		case Type_Week:
			calendar = getSundayOfThisWeek(calendar);
			formatString = formatDate(calendar.get(Calendar.YEAR),
					calendar.get(Calendar.MONTH),
					calendar.get(Calendar.DAY_OF_MONTH), "yyyy-MM-dd");
			break;
		case Type_Month:
			calendar.add(Calendar.MONTH, 1);
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			calendar.add(Calendar.DAY_OF_MONTH, -1);
			formatString = formatDate(calendar.get(Calendar.YEAR),
					calendar.get(Calendar.MONTH),
					calendar.get(Calendar.DAY_OF_MONTH), "yyyy-MM-dd");
			break;
		case Type_Year:
			formatString = formatDate(calendar.get(Calendar.YEAR),
					calendar.get(Calendar.MONTH),
					calendar.get(Calendar.DAY_OF_MONTH), "yyyy-12-31");
			break;
		}
		return formatString;
	}

	private void onPreOrNextDate(int interval) {
		switch (mTypes) {
		case Type_Day:
			mCurCalendar.add(Calendar.DATE, interval);
			break;
		case Type_Month:
			mCurCalendar.add(Calendar.MONTH, interval);
			break;
		case Type_Year:
			if (mCurTableIndex == Table_Working) {
				mQuarter = mQuarter + interval;
				if (mQuarter < 0 || mQuarter > 3) {// +
					mQuarter = 0;
					mCurCalendar.add(Calendar.YEAR, interval);
				}

			} else {
				mCurCalendar.add(Calendar.YEAR, interval);
			}
			break;
		case Type_Week:
			mCurCalendar.add(Calendar.DAY_OF_WEEK_IN_MONTH, interval);
			break;
		}
	}

	/** update date and graph */
	private void updateDateDisplay() {

		StringBuffer sb = new StringBuffer();
		String formatString = "";
		switch (mTypes) {
		case Type_Day:
		case Type_Week:
			formatString = formatDate(mCurCalendar.get(Calendar.YEAR),
					mCurCalendar.get(Calendar.MONTH),
					mCurCalendar.get(Calendar.DAY_OF_MONTH), "yyyy-MM-dd");

			sb.append(formatString);
			break;
		case Type_Month:
			formatString = formatDate(mCurCalendar.get(Calendar.YEAR),
					mCurCalendar.get(Calendar.MONTH),
					mCurCalendar.get(Calendar.DAY_OF_MONTH), "yyyy-MM");
			sb.append(formatString);

			break;
		case Type_Year:
			formatString = formatDate(mCurCalendar.get(Calendar.YEAR),
					mCurCalendar.get(Calendar.MONTH),
					mCurCalendar.get(Calendar.DAY_OF_MONTH), "yyyy");
			sb.append(formatString);

			if (mCurTableIndex == Table_Working) {
				sb.append(" Q" + (mQuarter + 1));
			}

			break;
		}
		tv_date.setText(sb);
		iv_next.setEnabled(true);
		iv_next.setAlpha(255);

		switch (mCurTableIndex) {
		case Table_Capacity: {
			onGetChannengTongjiData("124", getStartDateString(),
					getEndDateString(), mTypes + "");

			break;
		}
		case Table_Working: {
			if (mTypes == Type_Year) {
				onGetRuntime(getQuarterStartDate(mQuarter),
						getQuarterEndDate(mQuarter));
			} else {
				onGetRuntime(getStartDateString(), getEndDateString());
			}

			break;
		}
		case Table_Day_Night: {
			onGetStackedbarData("", getStartDateString());
			break;
		}
		}
	}

	/** format the date with pattern */
	private static String formatDate(int year, int month, int day,
			String pattern) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		Date date = new Date(year - 1900, month, day);
		return sdf.format(date);
	}

	// webview
	public static final String ASSERT_HTML_PATH = "file:///android_asset";

	public void initwebview() {
		View v = LayoutInflater.from(this)
				.inflate(R.layout.activity_main, null);
		horizontalPager.removeAllViews();
		horizontalPager.addView(v);
		webview = (WebView) v.findViewById(R.id.mainWebView);
		webview.getSettings().setJavaScriptEnabled(true);
		webview.getSettings().setBuiltInZoomControls(false);
		webview.getSettings().setAllowFileAccess(true);
		webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

		webview.getSettings().setDatabaseEnabled(true);
		String dir = this.getApplicationContext()
				.getDir("database", Context.MODE_PRIVATE).getPath();
		webview.getSettings().setDatabasePath(dir);
		webview.getSettings().setDomStorageEnabled(true);
		webview.getSettings().setGeolocationEnabled(true);
		webview.getSettings().setAllowFileAccess(true);
		//
		//
		try {
			if (Build.VERSION.SDK_INT >= 16) {
				Class<?> clazz = webview.getSettings().getClass();
				Method method = clazz.getMethod(
						"setAllowUniversalAccessFromFileURLs", boolean.class);
				if (method != null) {
					method.invoke(webview.getSettings(), true);
				}
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		webview.addJavascriptInterface(this, AllChart.class.getSimpleName());

		int screenDensity = getResources().getDisplayMetrics().densityDpi;
		WebSettings.ZoomDensity zoomDensity = WebSettings.ZoomDensity.MEDIUM;
		switch (screenDensity) {
		case DisplayMetrics.DENSITY_LOW:
			zoomDensity = WebSettings.ZoomDensity.CLOSE;
			break;
		case DisplayMetrics.DENSITY_MEDIUM:
			zoomDensity = WebSettings.ZoomDensity.MEDIUM;
			break;
		case DisplayMetrics.DENSITY_HIGH:
			zoomDensity = WebSettings.ZoomDensity.FAR;
			break;
		}
		webview.getSettings().setDefaultZoom(zoomDensity);
		// webview.getSettings().setUseWideViewPort(true);
		// webview.getSettings().setSupportZoom(true);

		webview.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onConsoleMessage(String message, int lineNumber,
					String sourceID) {
				// Log.i(TAG,message+ " line:"+lineNumber + "source:"+sourceID);
			}
		});

		loadUrl();
	}

	private void loadUrl() {
		if (mCurTableIndex == Table_Capacity) {
			webview.loadUrl(ASSERT_HTML_PATH + "/columnindex.htm");
		} else if (mCurTableIndex == Table_Working) {
			webview.loadUrl(ASSERT_HTML_PATH + "/pieindex.htm");
		} else if (mCurTableIndex == Table_Day_Night) {
			webview.loadUrl(ASSERT_HTML_PATH + "/stackbarindex.htm");
		}
	}

	public void switchTables() {
		switch (mCurTableIndex) {
		case Table_Capacity: {
			radioGroup_date.setVisibility(View.VISIBLE);
			findViewById(R.id.radio_day).setVisibility(View.GONE);
			radioGroup_date.check(R.id.radio_week);
			break;
		}
		case Table_Working: {

			radioGroup_date.setVisibility(View.VISIBLE);
			findViewById(R.id.radio_day).setVisibility(View.VISIBLE);
			radioGroup_date.check(R.id.radio_day);
			break;
		}
		case Table_Day_Night: {
			radioGroup_date.setVisibility(View.INVISIBLE);
			radioGroup_date.check(R.id.radio_day);
			break;
		}

		}
	}

	private void onGetRuntime(final String startDate, final String endDate) {
		if (!TNetWorkUtil.isNetworkConnected()) {
			loadUrl();
			makeText(getResString(R.string.not_network));
			if (dialog != null)
				dialog.dismiss();
			return;
		}

		dialog.show();

		mTAsyncHttpClientA.get(CoreHttpApi.getRuntime(startDate, endDate),
				null, new AsyncHttpResponseHandler() {

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							byte[] responseBody) {
						// err：0成功1 接口类型参数为空2.登陆账号为空3.登陆密码为空4. 帐号不存在
						// shyxsj:【设备运行时间-单位小时】
						// shddsj:【设备等待时间-单位小时】
						// shtzsj:【设备停止时间-单位小时】
						// 返回数据格式查看：
						// http://115.29.172.235/subweb_csarks/m/index.php?act=
						// 00720075006e00740069006d0065&uname=00610064006d0069006e003800380038&upwd=00610064006d0069006e003800380038&sbid=124&times=2014-09-27
						try {
							if (statusCode == 200) {
								dialog.dismiss();
								JSONObject jsonObject = CoreHttpApi
										.checkResponseStatus(responseBody);

								try {
									String err = jsonObject.getString("err");
									Double shyxsj = jsonObject
											.getDouble("shyxsj");
									Double shddsj = jsonObject
											.getDouble("shddsj");
									Double shtzsj = jsonObject
											.getDouble("shtzsj");
									StringBuffer sb = new StringBuffer();

									JSONArray jsonarray = new JSONArray();

									String n1 = getString(R.string.device_runtime);
									String n2 = getString(R.string.device_waittime);
									String n3 = getString(R.string.device_stoptime);

									JSONObject j1 = new JSONObject();
									j1.put("name", n1);
									j1.put("y", shyxsj);
									jsonarray.put(j1);

									JSONObject j2 = new JSONObject();
									j2.put("name", n2);
									j2.put("y", shddsj);
									jsonarray.put(j2);

									JSONObject j3 = new JSONObject();
									j3.put("name", n3);
									j3.put("y", shtzsj);
									jsonarray.put(j3);
									webview.loadUrl("javascript:refreshPie('"
											+ jsonarray.toString() + "')");

									if (shyxsj == 0 && shddsj == 0
											&& shtzsj == 0) {
										Toast.makeText(AllChart.this,
												getString(R.string.nodatatips),
												Toast.LENGTH_SHORT).show();
									}
								} catch (Exception e) {
									makeText(getResString(R.string.operation_error));
									loadUrl();
								}
							} else {
								TToastUtils.makeText("服务器异常");
								loadUrl();
							}
						} catch (Exception e) {
							loadUrl();
							makeText(getResString(R.string.operation_error));
							// if (e == null
							// || TStringUtils.isEmpty(e.getMessage()))
							// return;
							// TToastUtils.makeText(e.getMessage());
						}
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							byte[] responseBody, Throwable error) {
						loadUrl();
						makeText(getResString(R.string.operation_error));
					}

					@Override
					public void onProgress(int bytesWritten, int totalSize) {
						super.onProgress(bytesWritten, totalSize);
					}

					@Override
					public void onRetry(int retryNo) {
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

	private void onGetChannengTongjiData(final String sbid,
			final String startDate, final String endDate, final String types) {

		if (!TNetWorkUtil.isNetworkConnected()) {
			makeText(getResString(R.string.not_network));
			loadUrl();
			if (dialog != null)
				dialog.dismiss();
			return;
		}

		dialog.show();

		mTAsyncHttpClientB.get(CoreHttpApi.getChannengTongjiData(sbid,
				startDate, endDate, types, mCompanyId), null,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							byte[] responseBody) {
						// err：0成功1 接口类型参数为空2.登陆账号为空3.登陆密码为空4. 帐号不存在
						// datalist:【设备集合】
						// pstate:【产能】
						// pstate:【日期】
						// reddata:【停止时间】
						// yellowdata:【等待时间】
						// greendata:【运行时间】
						// 返回数据格式查看：
						// http://115.29.172.235/subweb_csarks/m/index.php?act=006300680061006e006e0065006e00670074006f006e0067006a0069&types=0&uname=00610064006d0069006e003800380038&upwd=00610064006d0069006e003800380038&bh=124&Time=2014-10-13&Etime=2014-10-14
						try {
							if (statusCode == 200) {
								dialog.dismiss();
								JSONObject jsonObject = CoreHttpApi
										.checkResponseStatus(responseBody);

								JSONArray array = jsonObject
										.getJSONArray("datalist");

								JSONArray powervalues = new JSONArray();
								JSONArray datevalues = new JSONArray();
								JSONArray seriesvalues = new JSONArray();

								JSONArray redvalues = new JSONArray();
								JSONArray yellowvalues = new JSONArray();
								JSONArray greenvalues = new JSONArray();

								for (int i = 0; i < array.length(); i++) {
									JSONObject obj = array.getJSONObject(i);
									powervalues.put(obj.get("pstate") + "k");
									datevalues.put(obj.get("categories"));
									String reddata = obj.getString("reddata");
									String yellowdata = obj
											.getString("yellowdata");
									String greendata = obj
											.getString("greendata");
									if ("".equals(reddata)) {
										reddata = "0";
									}
									if ("".equals(yellowdata)) {
										yellowdata = "0";
									}
									if ("".equals(greendata)) {
										greendata = "0";
									}

									redvalues.put(Double.parseDouble(reddata));
									yellowvalues.put(Double
											.parseDouble(yellowdata));
									greenvalues.put(Double
											.parseDouble(greendata));
								}

								String n1 = getString(R.string.device_runtime);
								String n2 = getString(R.string.device_waittime);
								String n3 = getString(R.string.device_stoptime);

								JSONObject reddata = new JSONObject();
								reddata.put("type", "column");
								reddata.put("name", n3);
								reddata.put("color", "#F00");
								reddata.put("data", redvalues);

								JSONObject yellowdata = new JSONObject();
								yellowdata.put("type", "column");
								yellowdata.put("name", n2);
								yellowdata.put("color", "#FF0");
								yellowdata.put("data", yellowvalues);

								JSONObject greendata = new JSONObject();
								greendata.put("type", "column");
								greendata.put("name", n1);
								greendata.put("color", "#060");
								greendata.put("data", greenvalues);

								seriesvalues.put(greendata);
								seriesvalues.put(yellowdata);
								seriesvalues.put(reddata);
								webview.loadUrl("javascript:refreshStackbar('"
										+ powervalues.toString() + "','"
										+ datevalues.toString() + "','"
										+ seriesvalues.toString() + "')");
								if (array.length() == 0) {
									Toast.makeText(AllChart.this,
											getString(R.string.nodatatips),
											Toast.LENGTH_SHORT).show();
								}

							} else {
								loadUrl();
								TToastUtils.makeText("服务器异常");
							}
						} catch (Exception e) {
							loadUrl();
							makeText(getResString(R.string.operation_error));
						}
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							byte[] responseBody, Throwable error) {
						loadUrl();
						makeText(getResString(R.string.operation_error));
					}

					@Override
					public void onProgress(int bytesWritten, int totalSize) {
						super.onProgress(bytesWritten, totalSize);
					}

					@Override
					public void onRetry(int retryNo) {
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

	private void onGetStackedbarData(final String sbid, final String date) {
		if (!TNetWorkUtil.isNetworkConnected()) {
			loadUrl();
			makeText(getResString(R.string.not_network));
			return;
		}

		dialog.show();

		mTAsyncHttpClientC.get(
				CoreHttpApi.getStackedbarData(sbid, date, mCompanyId), null,
				new AsyncHttpResponseHandler() {

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							byte[] responseBody) {
						// err：0成功1 接口类型参数为空2.登陆账号为空3.登陆密码为空4. 帐号不存在
						// datalist:【数据集合】
						// runtime:【运行时间】
						// runstate:【运行状态102. 运行时间103.等待时间104.停机时间】
						// 返回数据格式查看：
						// http://115.29.172.235/subweb_csarks/m/index.php?act=0074006f00640061007900700072006f00730074006100740065&uname=00610064006d0069006e003800380038&upwd=00610064006d0069006e003800380038&sbid=124&times=2014-09-27
						try {
							if (statusCode == 200) {
								dialog.dismiss();
								JSONObject jsonObject = CoreHttpApi
										.checkResponseStatus(responseBody);

								JSONArray array0 = jsonObject
										.getJSONArray("zblist");
								JSONArray array1 = jsonObject
										.getJSONArray("wblist");
								JSONArray date1 = jsonObject
										.getJSONArray("zbdian");
								JSONArray date2 = jsonObject
										.getJSONArray("wbdian");

								JSONArray jsonarray = new JSONArray();
								JSONArray jsonarray2 = new JSONArray();
								for (int i = array0.length() - 1; i >= 0; i--) {
									JSONObject tempobj = array0
											.getJSONObject(i);
									String code = tempobj.getString("state");
									double data = tempobj.getDouble("data");

									JSONObject j1 = new JSONObject();
									j1.put("name", getLabelName(code));
									j1.put("color", getLabelColor(code));
									j1.put("data", new JSONArray().put(data));
									jsonarray.put(j1);
								}
								for (int i = array1.length() - 1; i >= 0; i--) {
									JSONObject tempobj = array1
											.getJSONObject(i);
									String code = tempobj.getString("state");
									double data = tempobj.getDouble("data");

									JSONObject j1 = new JSONObject();
									j1.put("name", getLabelName(code));
									j1.put("color", getLabelColor(code));
									j1.put("data", new JSONArray().put(data));
									jsonarray2.put(j1);
								}
								webview.loadUrl("javascript:refreshStackbar('"
										+ jsonarray.toString() + "','"
										+ jsonarray2.toString() + "','"
										+ date1.toString() + "','"
										+ date2.toString() + "')");

								if (array0.length() == 0
										&& array1.length() == 0) {
									Toast.makeText(AllChart.this,
											getString(R.string.nodatatips),
											Toast.LENGTH_SHORT).show();
								}
							} else {
								loadUrl();
								TToastUtils.makeText("服务器异常");
							}
						} catch (Exception e) {
							loadUrl();
							makeText(getResString(R.string.operation_error));
						}
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							byte[] responseBody, Throwable error) {
						loadUrl();
						makeText(getResString(R.string.operation_error));
					}

					@Override
					public void onProgress(int bytesWritten, int totalSize) {
						super.onProgress(bytesWritten, totalSize);
					}

					@Override
					public void onRetry(int retryNo) {
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

	// share
	public void share() {
		ScreenShot.shoot(AllChart.this);
		Intent localIntent = new Intent("android.intent.action.SEND");
		localIntent.putExtra(
				"android.intent.extra.STREAM",
				Uri.fromFile(new File(Environment.getExternalStorageDirectory()
						+ "/shot.png")));
		localIntent.setType("image/*");
		startActivity(Intent.createChooser(localIntent, "分享"));
	}

	public String getLabelColor(String code) {
		if ("0".equals(code)) {
			return "#ffffff";
		} else if ("102".equals(code)) {
			return "#086b08";
		} else if ("103".equals(code)) {
			return "#f6e132";
		} else if ("104".equals(code)) {
			return "#ff0000";
		}
		return "#ffffff";
	}

	public String getLabelName(String code) {
		String n1 = getString(R.string.device_runtime);
		String n2 = getString(R.string.device_waittime);
		String n3 = getString(R.string.device_stoptime);
		if ("0".equals(code)) {
			return "";
		} else if ("102".equals(code)) {
			return n1;
		} else if ("103".equals(code)) {
			return n2;
		} else if ("104".equals(code)) {
			return n3;
		}
		return "";
	}

	/**
	 * 得到本周周一
	 * 
	 * @return yyyy-MM-dd
	 */
	public static Calendar getMondayOfThisWeek(Calendar calendar) {
		// int day_of_week = calendar.get(Calendar.DAY_OF_WEEK) - 1;
		// if (day_of_week == 0)
		// day_of_week = 7;
		// calendar.add(Calendar.DATE, -day_of_week + 1);
		// return calendar;
		int day_of_week = 7;
		calendar.add(Calendar.DATE, -day_of_week + 1);
		return calendar;
	}

	/**
	 * 得到本周周日
	 * 
	 * @return yyyy-MM-dd
	 */
	public static Calendar getSundayOfThisWeek(Calendar calendar) {
		// int day_of_week = calendar.get(Calendar.DAY_OF_WEEK) - 1;
		// if (day_of_week == 0)
		// day_of_week = 7;
		// calendar.add(Calendar.DATE, -day_of_week + 7);
		// return calendar;
		return calendar;
	}

	public static int getCurQuarter(Calendar calendar) {
		int quarter = 0;
		int currentMonth = calendar.get(Calendar.MONTH) + 1;
		if (currentMonth >= 1 && currentMonth <= 3)
			quarter = 0;
		else if (currentMonth >= 4 && currentMonth <= 6)
			quarter = 1;
		else if (currentMonth >= 7 && currentMonth <= 9)
			quarter = 2;
		else if (currentMonth >= 10 && currentMonth <= 12)
			quarter = 3;

		return quarter;
	}

	public static String getQuarterStartDate(int quarter) {
		Calendar curCalendar = (Calendar) mCurCalendar.clone();
		if (quarter == 0)
			curCalendar.set(Calendar.MONTH, 0);
		else if (quarter == 1)
			curCalendar.set(Calendar.MONTH, 3);
		else if (quarter == 2)
			curCalendar.set(Calendar.MONTH, 6);
		else if (quarter == 3)
			curCalendar.set(Calendar.MONTH, 9);
		curCalendar.set(Calendar.DATE, 1);
		return formatDate(curCalendar.get(Calendar.YEAR),
				curCalendar.get(Calendar.MONTH),
				curCalendar.get(Calendar.DAY_OF_MONTH), "yyyy-MM-dd");
	}

	public static String getQuarterEndDate(int quarter) {
		Calendar curCalendar = (Calendar) mCurCalendar.clone();
		if (quarter == 0) {
			curCalendar.set(Calendar.MONTH, 2);
			curCalendar.set(Calendar.DATE, 31);
		} else if (quarter == 1) {
			curCalendar.set(Calendar.MONTH, 5);
			curCalendar.set(Calendar.DATE, 30);
		} else if (quarter == 2) {
			curCalendar.set(Calendar.MONTH, 8);
			curCalendar.set(Calendar.DATE, 30);
		} else if (quarter == 3) {
			curCalendar.set(Calendar.MONTH, 11);
			curCalendar.set(Calendar.DATE, 31);
		}
		return formatDate(curCalendar.get(Calendar.YEAR),
				curCalendar.get(Calendar.MONTH),
				curCalendar.get(Calendar.DAY_OF_MONTH), "yyyy-MM-dd");
	}

}
