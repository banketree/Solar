package com.crystal.solar.util;

import java.io.File;
import java.io.FileOutputStream;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Environment;
import android.view.View;

public class ScreenShot
{

	private static void savePic(Bitmap mBitmap, String path)
	{
		try
		{
			File file = new File(path);
			if (file.exists())
				file.delete();
			file.createNewFile();
			FileOutputStream fOut = null;
			fOut = new FileOutputStream(file);
			mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
			fOut.flush();
			fOut.close();

		} catch (Exception e)
		{
		}
	}

	public static void shoot(Activity paramActivity)
	{
		savePic(takeScreenShot(paramActivity), Environment.getExternalStorageDirectory() + "/shot.png");
	}

	private static Bitmap takeScreenShot(Activity paramActivity)
	{
		View localView = paramActivity.getWindow().getDecorView();
		localView.setDrawingCacheEnabled(true);
		localView.buildDrawingCache();
		Bitmap localBitmap1 = localView.getDrawingCache();
		Rect localRect = new Rect();
		paramActivity.getWindow().getDecorView().getWindowVisibleDisplayFrame(localRect);
		int i = localRect.top;
		Bitmap localBitmap2 = Bitmap.createBitmap(localBitmap1, 0, i, paramActivity.getWindowManager().getDefaultDisplay().getWidth(), paramActivity.getWindowManager().getDefaultDisplay().getHeight() - i);
		localView.destroyDrawingCache();
		return localBitmap2;
	}
}
