package com.tetris;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		Toast.makeText(this, "Loading...", Toast.LENGTH_SHORT).show();
		Thread th = new Thread(new Runnable(){//游戏启动界面，停顿2秒后关闭并进入MenuActivity

			@Override
			public void run() {
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, MenuActivity.class);
				startActivity(intent);
			}
		});
		th.start();
	}

	@Override
	protected void onStop() {
		super.onStop();
		finish();
	}
	
	
}