package com.tetris;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;

public class MenuActivity extends Activity{//开始菜单类
	
	
	Button startClasscialBtn = null;
	Button startChuangGuanBtn = null;
	
	
	Button startYincangBtn = null;//开始游戏按钮
	
	Button hscoreB = null;//高分榜按钮
	Button settingsB = null;//设置按钮
	Button exitB = null;//退出按钮
	SharedPreferences m_sp = null;//用于存储最高分
	String fileName = "Data";//存放游戏一些数据，最高分、难度、音乐、触摸屏
	LayoutInflater inflater = null;
	Spinner spinner = null;//难度spinner
	
	Spinner spinnerYC = null;//隐藏 spinner
	
	CheckBox checkBoxM = null;//音乐开关
	CheckBox checkBoxT = null;//触屏开关
	final String HIGH_SCORE = "highscore";//存放最高分
	final String GAME_HARD = "gamehard";//存放游戏难度
	final String GAME_MUSIC = "gamemusic";//游戏音乐
	final String TOUCH_SCREEM = "touchscreem";//触屏开关
	
	final String GAME_YINCANG = "yincang"; 	//存储隐藏行数
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.menu);
		
		startClasscialBtn=(Button) findViewById(R.id.startClassical);
		startChuangGuanBtn=(Button) findViewById(R.id.startCuangGuan);
		
		startYincangBtn = (Button)findViewById(R.id.start);//开始游戏
		hscoreB = (Button)findViewById(R.id.high_score);//最高分
		settingsB = (Button)findViewById(R.id.settings);//设置
		exitB = (Button)findViewById(R.id.exit);//退出按钮
		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				this,android.R.layout.simple_list_item_1,
				new String[]{"简单","一般","困难","变态"});//用于设置难度
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);//设置spinner样式
		
		/*
		 * 增加选择隐藏行数
		 */
		final ArrayAdapter<String> adapterYC =new ArrayAdapter<String>(
				this, android.R.layout.simple_spinner_dropdown_item,
				new String []{"0","1","2","3","4","5","6","7","8","9","10"});
		adapterYC.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		startYincangBtn.setOnClickListener(new OnClickListener() {//设置开始按钮，跳到GameActivity
			
			@Override
			public void onClick(View v) {
				
				
				AlertDialog.Builder bulider = new AlertDialog.Builder(MenuActivity.this);
				bulider.setTitle("隐藏数目");
				if(inflater==null){//加载设置布局
					inflater = LayoutInflater.from(MenuActivity.this);
				}
				
				View view = (View) inflater.inflate(R.layout.setting_yc, null);
				spinnerYC = (Spinner) view.findViewById(R.id.spinerYC);	//隐藏
				
				spinnerYC.setAdapter(adapterYC);
				spinnerYC.setSelection(m_sp.getInt(GAME_YINCANG, 0));	//隐藏
				
				
				bulider.setView(view);
				
				bulider.setPositiveButton("确定", new DialogInterface.OnClickListener(){
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
						SharedPreferences.Editor editor = m_sp.edit();
						editor.putInt(GAME_YINCANG, spinnerYC.getSelectedItemPosition());
						
						editor.commit();						
						
						GameModel.MODEL=GameModel.MODEL_YINCANG;//隐藏模式
				
						Intent intent = new Intent();
						intent.setClass(MenuActivity.this, GameActivity.class);
						startActivity(intent);
					}
				});
				
				bulider.setNeutralButton("取消", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO 自动生成的方法存根
						
					}
				});
				
				
				bulider.create();
				bulider.show();
			}
		});
		
		startClasscialBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO 自动生成的方法存根
				
				
				
				GameModel.MODEL=GameModel.MODEL_CLASSCIAL;//经典模式
				
				Intent intent = new Intent();
				intent.setClass(MenuActivity.this, GameActivity.class);
				startActivity(intent);
				
			}
		});
		
		startChuangGuanBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO 自动生成的方法存根
				GameModel.MODEL=GameModel.MODEL_CHUANGGUAN;//闯关模式
				
				Intent intent = new Intent();
				intent.setClass(MenuActivity.this, GameActivity.class);
				startActivity(intent);
			}
		});
		
		m_sp = getSharedPreferences(fileName, MODE_PRIVATE);//设置文件的访问形式
		
		hscoreB.setOnClickListener(new OnClickListener() {//显示最高分
			
			@Override
			public void onClick(View v) {
				AlertDialog.Builder bulider = new AlertDialog.Builder(MenuActivity.this);
				bulider.setMessage("最高分："+m_sp.getLong(HIGH_SCORE, 0));
				bulider.setTitle("最高分");
				bulider.setPositiveButton("重置最高分", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						SharedPreferences.Editor editor = m_sp.edit();
						editor.putLong(HIGH_SCORE, 0);
						editor.commit();
					}
				});
				bulider.setNegativeButton("退出", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				});
				bulider.create();
				bulider.show();
			}
		});
		
		settingsB.setOnClickListener(new OnClickListener() {//设置按钮
			
			@Override
			public void onClick(View v) {
				AlertDialog.Builder bulider = new AlertDialog.Builder(MenuActivity.this);
				bulider.setTitle("游戏设置");
				if(inflater==null){//加载设置布局
					inflater = LayoutInflater.from(MenuActivity.this);
				}
				View view = (View) inflater.inflate(R.layout.setting, null);
				spinner = (Spinner)view.findViewById(R.id.spiner);//定义dialog布局中的控件
				
				
				
				checkBoxM = (CheckBox)view.findViewById(R.id.musicbox);
				checkBoxT = (CheckBox)view.findViewById(R.id.touchbox);
				spinner.setAdapter(adapter);
				spinner.setSelection(m_sp.getInt(GAME_HARD, 0));//为控件赋值
				
				
				
				checkBoxM.setChecked(m_sp.getBoolean(GAME_MUSIC, true));
				checkBoxT.setChecked(m_sp.getBoolean(TOUCH_SCREEM, true));
				bulider.setView(view);
				
				bulider.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {//保存修改结果
						SharedPreferences.Editor editor = m_sp.edit();
						editor.putInt(GAME_HARD, spinner.getSelectedItemPosition());
						
						//editor.putInt(GAME_YINCANG, spinnerYC.getSelectedItemPosition());
						
						editor.putBoolean(GAME_MUSIC, checkBoxM.isChecked());
						editor.putBoolean(TOUCH_SCREEM, checkBoxT.isChecked());
						editor.commit();
					}
				});
				bulider.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				});
				bulider.create();
				bulider.show();
				
			}
		});
		
		exitB.setOnClickListener(new OnClickListener() {//设置退出按钮
			
			@Override
			public void onClick(View v) {
				AlertDialog.Builder bulider = new AlertDialog.Builder(MenuActivity.this);
				bulider.setMessage("真的要退出吗？再玩一会儿嘛！");
				bulider.setTitle("退出游戏！");
				bulider.setPositiveButton("再玩一会儿", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				});
				bulider.setNegativeButton("退出游戏", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				});
				bulider.create();
				bulider.show();
			}
		});
	}
	

}
