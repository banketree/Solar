package com.crystal.solar.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.conn.util.InetAddressUtils;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.crystal.solar.R;
import com.crystal.solar.bean.Power;
import com.crystal.solar.config.Config;
import com.crystal.solar.xmlhandler.DataXmlHandler;
import com.crystal.solar.xmlhandler.ErrorXmlHandler;
import com.crystal.solar.xmlhandler.GraphXmlHandler;
import com.crystal.solar.xmlhandler.LoginXmlHandler;
import com.crystal.solar.xmlhandler.MapXmlHandler;
import com.crystal.solar.xmlhandler.PlantListXmlHandler;

public class OmnikUtil {

	private static ProgressDialog waitingDialog;
	private static ShowDialogHandler handler = new ShowDialogHandler();

	/** do login */
	public static Object doLogin(Context context, String username, String pswd) {

		SAXParserFactory spf = SAXParserFactory.newInstance();
		try {
			SAXParser sp = spf.newSAXParser();
			LoginXmlHandler xmlHandler = new LoginXmlHandler();

			URL url = new URL(Config.API_HOST + "?method=Login" + "&username="
					+ username + "&password=" + pswd + "&key=" + Config.API_KEY
					+ "&client=" + Config.API_CLIENT);
			URLConnection conn = url.openConnection();
			conn.setConnectTimeout(Config.TIMEOUT);

			InputStream is = conn.getInputStream();
			Log.d("OmnikUtil", is.toString());
			sp.parse(is, xmlHandler);

			return xmlHandler.getResult();
		} catch (UnknownHostException e) {
			showError(handler, context,
					context.getResources().getString(R.string.err_unknown_host));
			return null;
		} catch (MalformedURLException e) {
			showError(handler, context,
					context.getResources()
							.getString(R.string.err_malformed_url));
			return null;
		} catch (Exception e) {
			showError(handler, context,
					context.getResources().getString(R.string.err_unknown));
			return null;
		}
	}

	/** do login */
	public static Object doLogout(Context context, String username, String token) {
		SAXParserFactory spf = SAXParserFactory.newInstance();
		try {
			SAXParser sp = spf.newSAXParser();
			LoginXmlHandler xmlHandler = new LoginXmlHandler();

			URL url = new URL(Config.API_HOST + "?method=Logout" + "&username="
					+ username + "&token=" + token);
			URLConnection conn = url.openConnection();
			conn.setConnectTimeout(Config.TIMEOUT);

			InputStream is = conn.getInputStream();
			sp.parse(is, xmlHandler);

			return xmlHandler.getResult();
		} catch (UnknownHostException e) {
			showError(handler, context,
					context.getResources().getString(R.string.err_unknown_host));
			return null;
		} catch (MalformedURLException e) {
			showError(handler, context,
					context.getResources()
							.getString(R.string.err_malformed_url));
			return null;
		} catch (Exception e) {
			showError(handler, context,
					context.getResources().getString(R.string.err_unknown));
			return null;
		}
	}

	/** get Plant List */
	public static Object getPlantList(Context context, String username,
			String token) {
		SAXParserFactory spf = SAXParserFactory.newInstance();

		try {
			// SAXParser sp = spf.newSAXParser();
			// PlantListXmlHandler xmlHandler = new PlantListXmlHandler();
			//
			// URL url = new URL(Config.API_HOST + "?method=Powerstationslist"
			// + "&username=" + username + "&token=" + token + "&key="
			// + Config.API_KEY);
			// URLConnection conn = url.openConnection();
			// conn.setConnectTimeout(Config.TIMEOUT);
			//
			// InputStream is = conn.getInputStream();
			// sp.parse(is, xmlHandler);
			//
			// return xmlHandler.getResult();

			// } catch (UnknownHostException e) {
			// showError(handler, context,
			// context.getResources().getString(R.string.err_unknown_host));
			// return null;
			// } catch (MalformedURLException e) {
			// showError(handler, context,
			// context.getResources()
			// .getString(R.string.err_malformed_url));
			// return null;

			List<Power> powerList = new ArrayList<Power>();
			String[] names = new String[] { "设备一", "设备二", "设备三" };
			for (int i = 0; i < 3; i++) {
				Power p = new Power();
				p.status = "0";
				p.name = names[i];
				p.actualPower = "10";
				p.unit = "W";
				p.income = "10";
				p.eToday = "20";
				p.eTotal = "20";
				powerList.add(p);
			}
			return powerList;

		} catch (Exception e) {
			showError(handler, context,
					context.getResources().getString(R.string.err_unknown));
			return null;
		}
	}

	/** get Plant Data */
	public static Object getPlantData(Context context, String username,
			String stationID, String token) {
		SAXParserFactory spf = SAXParserFactory.newInstance();

		try {
			SAXParser sp = spf.newSAXParser();
			DataXmlHandler xmlHandler = new DataXmlHandler();

			URL url = new URL(Config.API_HOST + "?method=Data" + "&key="
					+ Config.API_KEY + "&username=" + username + "&stationid="
					+ stationID + "&token=" + token);
			URLConnection conn = url.openConnection();
			conn.setConnectTimeout(Config.TIMEOUT);

			InputStream is = conn.getInputStream();
			sp.parse(is, xmlHandler);

			return xmlHandler.getResult();
		} catch (UnknownHostException e) {
			showError(handler, context,
					context.getResources().getString(R.string.err_unknown_host));
			return null;
		} catch (MalformedURLException e) {
			showError(handler, context,
					context.getResources()
							.getString(R.string.err_malformed_url));
			return null;
		} catch (Exception e) {
			showError(handler, context,
					context.getResources().getString(R.string.err_unknown));
			return null;
		}
	}

	/** get Plant Error */
	public static Object getPlantError(Context context, String username,
			String stationID, String token, int page, int perPage) {
		SAXParserFactory spf = SAXParserFactory.newInstance();

		try {
			SAXParser sp = spf.newSAXParser();
			ErrorXmlHandler xmlHandler = new ErrorXmlHandler();

			URL url = new URL(Config.API_HOST + "?method=Error" + "&key="
					+ Config.API_KEY + "&username=" + username + "&stationid="
					+ stationID + "&token=" + token + "&page=" + page
					+ "&perPage=" + perPage);
			URLConnection conn = url.openConnection();
			conn.setConnectTimeout(Config.TIMEOUT);

			InputStream is = conn.getInputStream();
			sp.parse(is, xmlHandler);

			return xmlHandler.getResult();
		} catch (UnknownHostException e) {
			showError(handler, context,
					context.getResources().getString(R.string.err_unknown_host));
			return null;
		} catch (MalformedURLException e) {
			showError(handler, context,
					context.getResources()
							.getString(R.string.err_malformed_url));
			return null;
		} catch (Exception e) {
			showError(handler, context,
					context.getResources().getString(R.string.err_unknown));
			return null;
		}
	}

	/** get Plant's Location */
	public static Object getPlantGraph(Context context, String username,
			String stationID, String token, String dateTime, int type) {
		SAXParserFactory spf = SAXParserFactory.newInstance();

		try {
			SAXParser sp = spf.newSAXParser();
			GraphXmlHandler xmlHandler = new GraphXmlHandler();

			URL url = new URL(Config.API_HOST + "?method=Graph" + "&key="
					+ Config.API_KEY + "&username=" + username + "&stationid="
					+ stationID + "&token=" + token + "&datetime=" + dateTime
					+ "&type=" + type);
			URLConnection conn = url.openConnection();
			conn.setConnectTimeout(Config.TIMEOUT);

			InputStream is = conn.getInputStream();
			sp.parse(is, xmlHandler);

			return xmlHandler.getResult();
		} catch (UnknownHostException e) {
			showError(handler, context,
					context.getResources().getString(R.string.err_unknown_host));
			return null;
		} catch (MalformedURLException e) {
			showError(handler, context,
					context.getResources()
							.getString(R.string.err_malformed_url));
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/** get Plant's Location */
	public static Object getPlantLocation(Context context, String username,
			String stationID, String token) {
		SAXParserFactory spf = SAXParserFactory.newInstance();

		try {
			SAXParser sp = spf.newSAXParser();
			MapXmlHandler xmlHandler = new MapXmlHandler();

			URL url = new URL(Config.API_HOST + "?method=Map" + "&key="
					+ Config.API_KEY + "&username=" + username + "&stationid="
					+ stationID + "&token=" + token);
			URLConnection conn = url.openConnection();
			conn.setConnectTimeout(Config.TIMEOUT);

			InputStream is = conn.getInputStream();
			sp.parse(is, xmlHandler);

			return xmlHandler.getResult();
		} catch (UnknownHostException e) {
			showError(handler, context,
					context.getResources().getString(R.string.err_unknown_host));
			return null;
		} catch (MalformedURLException e) {
			showError(handler, context,
					context.getResources()
							.getString(R.string.err_malformed_url));
			return null;
		} catch (Exception e) {
			showError(handler, context,
					context.getResources().getString(R.string.err_unknown));
			return null;
		}
	}

	public static void showError(ShowDialogHandler handler, Context context,
			String errorMessage) {
		Message m = handler.obtainMessage();
		m.what = Config.ERROR_OCCUR;
		m.obj = context;
		Bundle b = new Bundle();
		b.putString("error_msg", errorMessage);
		m.setData(b);
		handler.sendMessage(m);
	}

	// show error dialog
	public static void showErrorDialog(Context context, String msg) {
		new AlertDialog.Builder(context)
				.setMessage(msg)
				.setNegativeButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub

							}
						}).create().show();
	}

	public static void showWaitingDialog(Context context, String msg) {
		waitingDialog = ProgressDialog.show(context, null, msg);
	}

	public static void hideWaitingDialog() {
		if (waitingDialog != null) {
			waitingDialog.cancel();
		}
	}

	public static String formatDate(String secondString, String format,
			int timeZoneOffset) {
		long milliSeconds = Long.parseLong(secondString) * 1000;
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		TimeZone timeZone = null;
		if (timeZoneOffset >= 0) {
			timeZone = TimeZone.getTimeZone("GMT+" + timeZoneOffset + ":00");
		} else {
			timeZone = TimeZone.getTimeZone("GMT" + timeZoneOffset + ":00");
		}
		sdf.setTimeZone(timeZone);
		return sdf.format(milliSeconds);
	}

	/** get station Image from cache */
	public static Bitmap getImage(Context context, String url) {
		Bitmap image = null;
		FileInputStream fis = null;
		try {
			fis = context
					.openFileInput(url.substring(url.lastIndexOf("/") + 1));
			image = BitmapFactory.decodeStream(fis);
			fis.close();

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return image;
	}

	public static Drawable loadImageFromLocal(Context context, String imageUrl) {
		Bitmap bitmap = OmnikUtil.getImage(context, imageUrl);
		BitmapDrawable bitmapDrawable = new BitmapDrawable(bitmap);
		return bitmapDrawable;
	}

	public static void setListViewHeightBasedOnChildren(ListView listView) {
		ListAdapter adapter = listView.getAdapter();
		if (adapter == null) {
			return;
		}

		int totalHeight = 0;
		for (int i = 0; i < adapter.getCount(); i++) {
			View listItem = adapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (adapter.getCount() - 1));
		params.height += 15;// if without this statement,the listview will be a
							// little short
		listView.setLayoutParams(params);
	}

	/**
	 * Convert byte array to hex string
	 * 
	 * @param bytes
	 * @return
	 */
	public static String bytesToHex(byte[] bytes) {
		StringBuilder sbuf = new StringBuilder();
		for (int idx = 0; idx < bytes.length; idx++) {
			int intVal = bytes[idx] & 0xff;
			if (intVal < 0x10)
				sbuf.append("0");
			sbuf.append(Integer.toHexString(intVal).toUpperCase());
		}
		return sbuf.toString();
	}

	/**
	 * Get utf8 byte array.
	 * 
	 * @param str
	 * @return array of NULL if error was found
	 */
	public static byte[] getUTF8Bytes(String str) {
		try {
			return str.getBytes("UTF-8");
		} catch (Exception ex) {
			return null;
		}
	}

	/**
	 * Load UTF8withBOM or any ansi text file.
	 * 
	 * @param filename
	 * @return
	 * @throws java.io.IOException
	 */
	public static String loadFileAsString(String filename)
			throws java.io.IOException {
		final int BUFLEN = 1024;
		BufferedInputStream is = new BufferedInputStream(new FileInputStream(
				filename), BUFLEN);
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream(BUFLEN);
			byte[] bytes = new byte[BUFLEN];
			boolean isUTF8 = false;
			int read, count = 0;
			while ((read = is.read(bytes)) != -1) {
				if (count == 0 && bytes[0] == (byte) 0xEF
						&& bytes[1] == (byte) 0xBB && bytes[2] == (byte) 0xBF) {
					isUTF8 = true;
					baos.write(bytes, 3, read - 3); // drop UTF8 bom marker
				} else {
					baos.write(bytes, 0, read);
				}
				count += read;
			}
			return isUTF8 ? new String(baos.toByteArray(), "UTF-8")
					: new String(baos.toByteArray());
		} finally {
			try {
				is.close();
			} catch (Exception ex) {
			}
		}
	}

	/**
	 * Returns MAC address of the given interface name.
	 * 
	 * @param interfaceName
	 *            eth0, wlan0 or NULL=use first interface
	 * @return mac address or empty string
	 */
	@SuppressLint("NewApi")
	public static String getMACAddress(String interfaceName) {
		try {
			List<NetworkInterface> interfaces = Collections
					.list(NetworkInterface.getNetworkInterfaces());
			for (NetworkInterface intf : interfaces) {
				if (interfaceName != null) {
					if (!intf.getName().equalsIgnoreCase(interfaceName))
						continue;
				}
				byte[] mac = intf.getHardwareAddress();
				if (mac == null)
					return "";
				StringBuilder buf = new StringBuilder();
				for (int idx = 0; idx < mac.length; idx++)
					buf.append(String.format("%02X:", mac[idx]));
				if (buf.length() > 0)
					buf.deleteCharAt(buf.length() - 1);
				return buf.toString();
			}
		} catch (Exception ex) {
		} // for now eat exceptions
		return "";
		/*
		 * try { // this is so Linux hack return
		 * loadFileAsString("/sys/class/net/" +interfaceName +
		 * "/address").toUpperCase().trim(); } catch (IOException ex) { return
		 * null; }
		 */
	}

	/**
	 * Get IP address from first non-localhost interface
	 * 
	 * @param ipv4
	 *            true=return ipv4, false=return ipv6
	 * @return address or empty string
	 */
	public static String getIPAddress(boolean useIPv4) {
		try {
			List<NetworkInterface> interfaces = Collections
					.list(NetworkInterface.getNetworkInterfaces());
			for (NetworkInterface intf : interfaces) {
				List<InetAddress> addrs = Collections.list(intf
						.getInetAddresses());
				for (InetAddress addr : addrs) {
					if (!addr.isLoopbackAddress()) {
						String sAddr = addr.getHostAddress().toUpperCase();
						boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
						if (useIPv4) {
							if (isIPv4)
								return sAddr;
						} else {
							if (!isIPv4) {
								int delim = sAddr.indexOf('%'); // drop ip6 port
																// suffix
								return delim < 0 ? sAddr : sAddr.substring(0,
										delim);
							}
						}
					}
				}
			}
		} catch (Exception ex) {
		} // for now eat exceptions
		return "";
	}

	public static String intToIp(int i) {
		return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF)
				+ "." + ((i >> 24) & 0xFF);
	}
}
