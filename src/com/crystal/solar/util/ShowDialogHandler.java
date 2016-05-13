package com.crystal.solar.util;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.crystal.solar.config.Config;

public class ShowDialogHandler extends Handler
{

	@Override
	public void handleMessage(Message msg)
	{
		// TODO Auto-generated method stub
		super.handleMessage(msg);

		switch (msg.what)
		{
		case Config.ERROR_OCCUR:
			String message = msg.getData().getString("error_msg");
			OmnikUtil.showErrorDialog((Context) msg.obj, message);
			break;
		default:
			break;
		}
	}
}
