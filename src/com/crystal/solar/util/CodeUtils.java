package com.crystal.solar.util;

import java.util.Locale;

public class CodeUtils
{

	public static String codeString(String str)
	{
		if (str == null || "".equals(str))
		{
			return null;
		}

		char[] chars = str.toCharArray();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < chars.length; i++)
		{
			String hex = Integer.toHexString(char2ASCII(chars[i]));
			if (hex.length() <= 4)
			{
				hex = "00".concat(hex);
			}
			sb.append(hex);
		}
		return sb.toString();
	}

	public static int char2ASCII(char c)
	{
		return (int) c;
	}

	public static String hextoString(String hex)
	{
		return new String(hexStringToBytes(hex));
	}

	private static byte[] hexStringToBytes(String hexString)
	{
		if (hexString == null || hexString.equals(""))
		{
			return null;
		}
		hexString = hexString.toUpperCase();
		int length = hexString.length() / 2;
		char[] hexChars = hexString.toCharArray();
		byte[] d = new byte[length];
		for (int i = 0; i < length; i++)
		{
			int pos = i * 2;
			d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));

		}
		return d;
	}

	private static byte charToByte(char c)
	{
		return (byte) "0123456789ABCDEF".indexOf(c);
	}

	private final static String mHexStr = "0123456789ABCDEF";
	private final static char[] mChars = "0123456789ABCDEF".toCharArray();

	public static String hexStr2Str(String hexStr)
	{
		hexStr = hexStr.toString().trim().replace(" ", "").toUpperCase(Locale.US);
		char[] hexs = hexStr.toCharArray();
		byte[] bytes = new byte[hexStr.length() / 2];
		int iTmp = 0x00;
		for (int i = 0; i < bytes.length; i++)
		{
			iTmp = mHexStr.indexOf(hexs[2 * i]) << 4;
			iTmp |= mHexStr.indexOf(hexs[2 * i + 1]);
			bytes[i] = (byte) (iTmp & 0xFF);
		}
		return new String(bytes);
	}

	public static String str2HexStr(String str)
	{
		StringBuilder sb = new StringBuilder();
		byte[] bs = str.getBytes();

		for (int i = 0; i < bs.length; i++)
		{
			sb.append(mChars[(bs[i] & 0xFF) >> 4]);
			sb.append(mChars[bs[i] & 0x0F]);
			sb.append(' ');
		}
		return sb.toString().trim();
	}
}
