package com.crystal.solar.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

import com.crystal.solar.R;

public class Splash extends BaseActivity {
	private static long SPLASH_TIME = 3000;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		new Handler().postDelayed(r, SPLASH_TIME);
	}

	@Override
	public void processEventByInner(Intent intent) {

	}

	private Runnable r = new Runnable() {
		public void run() {
			Intent i = new Intent();
			i.setClass(Splash.this, Login.class);
			startActivity(i);
			overridePendingTransition(android.R.anim.fade_in,
					android.R.anim.fade_out);
			finish();
		}
	};

}