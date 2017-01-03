package com.tetris;


import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


@SuppressLint("HandlerLeak")
public class GameActivity extends Activity {//游戏运行类
	boolean isstop = false;//暂停判断标志位
	boolean longLeft = false;//左移长按标志位
	boolean longRight = false;//右移长按标志位
	boolean longDown = false;//下移长按标志位
	boolean isTouched = false;//触控标志位,判断是点击还是移动
	static final int MSG_START = 12580;//每隔一定时间发送的消息
	static long count = 0;//用于取余运算实现定时操作
	static int level = 0;
	int speed = 10;//游戏速度
	long a=0;//消除flag
	Button m_ButtonLeft = null; // 按钮
	Button m_ButtonRight = null;
	Button m_ButtonDown = null;
	Button m_ButtonPause = null;
	Button m_ButtonRotatae = null;
	Button m_Buttonhint = null;
	Button m_ButtonFastDown =null;
	ShapView m_ShapView = null;
	View m_GameView = null;
	TextView m_TextDone = null;
	TextView m_TextScore = null;
	TextView m_TextLevel=null;
	Handler m_handler = null;//负责接收消息
	Timer timer = null;//负责发送消息
	Data data = null;//存放游戏的数据，即二维数组，并负责二维数组的运算
    private SoundPool sp;//声明一个SoundPool
    private int music;//定义一个整型用load（）；来设置suondID
    private int music_fastdown;//定义速降的声音
    private int music_cleanline;//定义消除的声音
	MediaPlayer m_MediaPlayer = null;//音乐播放器
	GestureDetector m_detector = null;//手势
	SharedPreferences m_sp = null;//用于存储最高分等
	String fileName = "Data";//存放游戏一些数据，最高分、难度、音乐、触摸屏
	final String HIGH_SCORE = "highscore";
	final String GAME_HARD = "gamehard";//存放游戏难度
	final String GAME_MUSIC = "gamemusic";//游戏音乐
	final String TOUCH_SCREEM = "touchscreem";//触屏开关
	final String HIGH_SCORE_YC="high_score_yc";//隐藏模式的最高分
	final String HIGH_SCORE_CG="high_score_cg";//闯关模式的最高分
	final String LEVEL_CG="level";//闯关模式的等级
	final String GAME_YINCANG = "yincang"; 	//存储隐藏行数
	private class MyGestureDetector extends GestureDetector.SimpleOnGestureListener{//设置手势响应
		@Override
		public boolean onScroll(MotionEvent e1,MotionEvent e2,
				float distanceX,float distanceY){
			if(e1.getX()-e2.getX() > 30){
				if(isstop==true){
					return false;
				}
				if(m_sp.getBoolean(TOUCH_SCREEM, true))//先判断是否开启触控
				longLeft = true;
			}
			else if(e1.getX()-e2.getX() < -30){
				if(isstop==true){
					return false;
				}
				if(m_sp.getBoolean(TOUCH_SCREEM, true))
				longRight = true;
			}
			else if(e1.getY()-e2.getY() < -30){
				if(isstop==true){
					return false;
				}
				if(m_sp.getBoolean(TOUCH_SCREEM, true))
				longDown = true;
			}
			return false;
		}
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/*
		 *  MODEL_CLASSCIAL=0;	//经典模式
		 	MODEL_YINCANG=1;	//隐藏模式
			MODEL_CHUANGGUAN=2;	//闯关模式
		 */
		switch (GameModel.MODEL) {
		case 0://经典模式
			setContentView(R.layout.gamerun_classical);
			break;
		case 1://隐藏模式
			setContentView(R.layout.gamerun_yincang);
			break;
		default://闯关模式
			setContentView(R.layout.gamerun_chuangguan);
			
			break;
		}
		inItTools();// 初始化
		
	}
	
	protected void onGameOver() {//处理游戏结束事件,弹出对话框提示
		
		timer.cancel();
		if(m_MediaPlayer!=null){
			m_MediaPlayer.pause();
			m_MediaPlayer.release();
			m_MediaPlayer=null;
		}
		SharedPreferences.Editor editor = m_sp.edit();
		switch (GameModel.MODEL) {
		case 0://经典模式
			if(m_sp.getLong(HIGH_SCORE, 0)<Data.score)
			{//存储最高分				
				editor.putLong(HIGH_SCORE, Data.score);
				editor.commit();
			}

			AlertDialog.Builder bulider = new AlertDialog.Builder(this);//设置游戏结束对话框
			bulider.setMessage("游戏结束，是否重新开始？");
			bulider.setTitle("游戏结束！");
			if(m_MediaPlayer!=null){
				m_MediaPlayer.pause();
				m_MediaPlayer.release();
				m_MediaPlayer=null;
			}
			m_MediaPlayer = MediaPlayer.create(this, R.raw.loss);
			if(m_sp.getBoolean(GAME_MUSIC, true))//判断音乐是否开启
				m_MediaPlayer.start();
			bulider.setPositiveButton("重新开始", new DialogInterface.OnClickListener()
			{
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					inItTools();
				}
				
			});
			bulider.setNegativeButton("退出游戏", new DialogInterface.OnClickListener() 
			{
				
				@Override
				public void onClick(DialogInterface dialog, int which) 
				{
					finish();
				}
			});
			bulider.create();
			bulider.show();
			
			break;
		case 1://隐藏模式
			if(m_sp.getLong(HIGH_SCORE, 0)<Data.score)
			{//存储最高分				
				editor.putLong(HIGH_SCORE, Data.score);
				editor.commit();
			}
			AlertDialog.Builder bulider1 = new AlertDialog.Builder(this);//设置游戏结束对话框
			bulider1.setMessage("游戏结束，是否重新开始？");
			bulider1.setTitle("游戏结束！");
			if(m_MediaPlayer!=null){
				m_MediaPlayer.pause();
				m_MediaPlayer.release();
				m_MediaPlayer=null;
			}
			m_MediaPlayer = MediaPlayer.create(this, R.raw.loss);
			if(m_sp.getBoolean(GAME_MUSIC, true))//判断音乐是否开启
				m_MediaPlayer.start();
			bulider1.setPositiveButton("重新开始", new DialogInterface.OnClickListener()
			{
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
					inItTools();
				}
				
			});
			bulider1.setNegativeButton("退出游戏", new DialogInterface.OnClickListener() 
			{
				
				@Override
				public void onClick(DialogInterface dialog, int which) 
				{
					
					finish();
				}
			});
			bulider1.create();
			bulider1.show();
			
			break;
		default://闯关模式
			if(m_sp.getLong(HIGH_SCORE_CG, 0)<=Data.score_chuangguan)
			{//存储最高分
				editor.putLong(HIGH_SCORE_CG, Data.score_chuangguan);
				editor.commit();
			}
			if(Data.level_chuangguan*Data.CHUANGGUAN_TOP_SCORE<=Data.score_chuangguan){
				int a=(int)(Data.score_chuangguan/Data.CHUANGGUAN_TOP_SCORE)+1;
				editor.putInt(LEVEL_CG,a);
				Data.level_chuangguan=a;
				editor.commit();
				AlertDialog.Builder bulider2 = new AlertDialog.Builder(this);//设置游戏结束对话框
				bulider2.setMessage("是否进行下一关？");
				bulider2.setTitle("恭喜您，过关了！");
				if(m_MediaPlayer!=null){
					m_MediaPlayer.pause();
					m_MediaPlayer.release();
					m_MediaPlayer=null;
				}
				m_MediaPlayer = MediaPlayer.create(this, R.raw.victory);
				if(m_sp.getBoolean(GAME_MUSIC, true))//判断音乐是否开启
					m_MediaPlayer.start();
				bulider2.setPositiveButton("下一关", new DialogInterface.OnClickListener() 
				{
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
						inItTools();
					}
				});
				bulider2.setNegativeButton("退出游戏", new DialogInterface.OnClickListener() 
				{
					
					@Override
					public void onClick(DialogInterface dialog, int which) 
					{
						
						finish();
					}
				});
				bulider2.create();
				bulider2.show();				
				
			}
			else {
				
				Data.score_chuangguan=(Data.level_chuangguan-1)*Data.CHUANGGUAN_TOP_SCORE;
				
				AlertDialog.Builder bulider2 = new AlertDialog.Builder(this);//设置游戏结束对话框
				bulider2.setMessage("是否重新开始？");
				bulider2.setTitle("您输了...");
				
				m_MediaPlayer=null;
				m_MediaPlayer = MediaPlayer.create(this, R.raw.loss);
				if(m_sp.getBoolean(GAME_MUSIC, true))//判断音乐是否开启
					m_MediaPlayer.start();
				bulider2.setPositiveButton("重新开始", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
						inItTools();
					}
				});
				bulider2.setNegativeButton("退出游戏", new DialogInterface.OnClickListener() 
				{
					
					@Override
					public void onClick(DialogInterface dialog, int which) 
					{
						
						finish();
					}
				});
				bulider2.create();
				bulider2.show();
				
			}
			
			break;
		}
		
	}
	
	public void inItTools() {// 初始化
		m_sp = getSharedPreferences(fileName, MODE_PRIVATE);//设置文件
		count = 0;//定时器重置
		isstop = false;//标志位重置
		longLeft = false;
		longRight = false;
		longDown = false;
		data = new Data();//创建一个新的Data对象		
		sp= new SoundPool(10, AudioManager.STREAM_SYSTEM, 3);//第一个参数为同时播放数据流的最大个数，第二数据流类型，第三为声音质量
        music = sp.load(this, R.raw.button_click, 1); //把你的声音素材放到res/raw里，第2个参数即为资源文件，第3个为音乐的优先级
		music_fastdown=sp.load(this, R.raw.fastdown, 1);
		music_cleanline=sp.load(this, R.raw.delete1, 1);
		Data.setYinchangline(m_sp.getInt(GAME_YINCANG, 1));//设置隐藏
		if(m_MediaPlayer!=null){
			m_MediaPlayer=null;
		}
		switch (GameModel.MODEL) {
		case 0://经典模式
			level = m_sp.getInt(GAME_HARD, 0);
			a=Data.line;
			speed=Math.abs(20-level*7);//游戏速度
			m_TextDone = (TextView) findViewById(R.id.doneclassical);
			m_TextScore = (TextView) findViewById(R.id.scoreclassical);
			break;
		case 1://隐藏模式
			a=Data.line;
			level = m_sp.getInt(GAME_HARD, 0);
			speed=Math.abs(20-level*7);//游戏速度
			m_TextDone = (TextView) findViewById(R.id.done);
			m_TextScore = (TextView) findViewById(R.id.score);
			break;
		default://闯关模式
			a=Data.line;
			level=m_sp.getInt(LEVEL_CG, 1);			
			speed=Math.abs(40-level*4);
			m_TextDone = (TextView) findViewById(R.id.done_chuangguan);
			m_TextScore = (TextView) findViewById(R.id.score_chuangguan);
			m_TextLevel=(TextView) findViewById(R.id.level_chuangguan);
			break;
		}
		
		m_handler = new Handler(){//接收消息

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch(msg.what){
					case MSG_START:	
						if(count%speed==0){//每隔一定时间(1s)下降一行
							data.goDown();
							
							setText();
							m_GameView.invalidate();
							m_ShapView.invalidate();
							if(data.isGameOver()){
								onGameOver();
							}
						}
						if(longLeft==true){//判断左移长按是否被按下
							if(count%2==0){
								
								data.goLeft();
								m_GameView.invalidate();
								m_ShapView.invalidate(); 
							}
						}
						if(longRight==true){//判断右移长按是否被按下
							if(count%2==0){
								data.goRight();
								m_GameView.invalidate();
								m_ShapView.invalidate(); 
							}
						}
						if(longDown==true){//判断下移长按是否被按下
							if(count%2==0){
								data.goDown();
								m_GameView.invalidate();
								m_ShapView.invalidate(); 
							}
						}
						break;
				}
			}
			
		};
		timer= new Timer();
		timer.schedule(new TimerTask() {//发送消息
			
			@Override
			public void run() {
				if(isstop==true){
					return;
				}
				count++;
				m_handler.sendEmptyMessage(MSG_START);
			}
		}, 1000, 50);
		
		switch (GameModel.MODEL) {
		case 0://经典模式
			m_ButtonLeft = (Button) findViewById(R.id.leftclassical);//设置左移按钮
			break;
		case 1://隐藏模式
			m_ButtonLeft = (Button) findViewById(R.id.left);//设置左移按钮
			break;
		default://闯关模式
			m_ButtonLeft=(Button) findViewById(R.id.left_chuangguan);
			break;
		}
		
		
		m_ButtonLeft.setOnClickListener(new View.OnClickListener() {
			@Override
			
			public void onClick(View v) {
				if(isstop==true){//判断暂停
					return;
				}
				if(longLeft == true){//当按钮别放开时，将左移长按标志位置为false
					longLeft=false;
				}
				if(m_sp.getBoolean(GAME_MUSIC, true))//判断音乐是否开启
					sp.play(music, 1, 1, 0, 0, 1);
				data.goLeft();
				m_GameView.invalidate();
				m_ShapView.invalidate(); 
				
			}
		});
		
		m_ButtonLeft.setOnLongClickListener(new View.OnLongClickListener() {//设置左移长按按钮
			
			@Override
			public boolean onLongClick(View v) {
				longLeft = true;//修改左移长按标志位
				return false;
			}
		});
		
		switch (GameModel.MODEL) {
		case 0://经典模式
			m_ButtonRight = (Button) findViewById(R.id.rightclassical);//设置右移按钮
			break;
		case 1://隐藏模式
			m_ButtonRight = (Button) findViewById(R.id.right);//设置右移按钮
			break;
		default://闯关模式
			m_ButtonRight = (Button) findViewById(R.id.right_chuangguan);//设置右移按钮
			break;
		}
		
		
		m_ButtonRight.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(isstop==true){
					return;
				}
				if(longRight == true){//当按钮别放开时，将右移长按标志位置为false
					longRight=false;
				}
				if(m_sp.getBoolean(GAME_MUSIC, true))//判断音乐是否开启
					sp.play(music, 1, 1, 0, 0, 1);
				data.goRight();
				m_GameView.invalidate();
				m_ShapView.invalidate(); 
				
			}
		});
		
		m_ButtonRight.setOnLongClickListener(new View.OnLongClickListener() {//设置右移长按按钮
			
			@Override
			public boolean onLongClick(View v) {
				longRight = true;//修改左移长按标志位
				return false;
			}
		});
		
		switch (GameModel.MODEL) {
		case 0://经典模式
			m_ButtonDown = (Button) findViewById(R.id.downclassical);//设置下降按钮
			break;
		case 1://隐藏模式
			m_ButtonDown = (Button) findViewById(R.id.down);//设置下降按钮
			break;
		default://闯关模式
			m_ButtonDown = (Button) findViewById(R.id.down_chuangguan);//设置下降按钮
			break;
		}
		
		
		m_ButtonDown.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(isstop==true){
					return;
				}
				if(longDown == true){//当按钮别放开时，将下移长按标志位置为false
					longDown=false;
				}
				data.goDown();
				setText();
				if(m_sp.getBoolean(GAME_MUSIC, true))//判断音乐是否开启
					sp.play(music, 1, 1, 0, 0, 1);
				m_GameView.invalidate();
				m_ShapView.invalidate(); 
				
			}
		});
		
		m_ButtonDown.setOnLongClickListener(new View.OnLongClickListener() {//设置下移长按按钮
			
			@Override
			public boolean onLongClick(View v) {
				longDown = true;//修改下移长按标志位
				return false;
			}
		});
		
		switch (GameModel.MODEL) {
		case 0://经典模式
			m_ButtonFastDown = (Button) findViewById(R.id.fastdownclassical);//设置下降按钮
			break;
		case 1://隐藏模式
			m_ButtonFastDown = (Button) findViewById(R.id.fastdown);//设置下降按钮
			break;
		default://闯关模式
			m_ButtonFastDown = (Button) findViewById(R.id.fastdown_chuangguan);//设置下降按钮
			break;
		}
		
		
		m_ButtonFastDown.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(isstop==true){
					return;
				}
				if(longDown == true){//当按钮别放开时，将下移长按标志位置为false
					longDown=false;
				}
				data.goFastDown();
				setText();
				if(m_sp.getBoolean(GAME_MUSIC, true))//判断音乐是否开启
					sp.play(music_fastdown, 1, 1, 0, 0, 1);
				m_GameView.invalidate();
				m_ShapView.invalidate(); 
				
			}
		});
		
		m_ButtonFastDown.setOnLongClickListener(new View.OnLongClickListener() {//设置下移长按按钮
			
			@Override
			public boolean onLongClick(View v) {
				longDown = true;//修改下移长按标志位
				return false;
			}
		});
		
		
		switch (GameModel.MODEL) {
		case 0://经典模式
			m_ButtonRotatae =(Button) findViewById(R.id.rotateclassical);
			break;
		case 1://隐藏模式
			m_ButtonRotatae =(Button) findViewById(R.id.rotate);
			break;
		default://闯关模式
			m_ButtonRotatae =(Button) findViewById(R.id.rotate_chuangguan);
			break;
		}
		
		
		m_ButtonRotatae.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO 自动生成的方法存根
				longLeft = false;
		    	longRight = false;
		    	longDown = false;
		    	if(isstop==true){
					return;
				}
		    	data.goTurn();
		    	if(m_sp.getBoolean(GAME_MUSIC, true))//判断音乐是否开启
		    		sp.play(music, 1, 1, 0, 0, 1);
				m_GameView.invalidate();
				m_ShapView.invalidate();
			}
		});
		
		
		switch (GameModel.MODEL) {
		case 0://经典模式
			m_ButtonPause = (Button) findViewById(R.id.stopclassical);//设置暂停按钮
			break;
		case 1://隐藏模式
			m_ButtonPause = (Button) findViewById(R.id.stop);//设置暂停按钮
			break;
		default://闯关模式
			m_ButtonPause = (Button) findViewById(R.id.stop_chuangguan);//设置暂停按钮
			break;
		}
		
		
		m_ButtonPause.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				if(isstop==false){
					m_MediaPlayer.pause();//暂停音乐
					isstop=true;
					m_ButtonPause.setText("继续");
				}
				else{
					if(m_sp.getBoolean(GAME_MUSIC, true))//判断音乐是否开启
					   m_MediaPlayer.start();
					isstop=false;
					m_ButtonPause.setText("暂停");
				}
				
			}
			
		});
		
		/*
		 * 设置隐藏模式的提示
		 */
		switch (GameModel.MODEL) {
		case 0://经典模式
			
			break;
		case 1://隐藏模式
			
			m_Buttonhint=(Button) findViewById(R.id.hint);
			
			m_Buttonhint.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO 自动生成的方法存根
					if(GameModel.IS_HINT){
						GameModel.IS_HINT=false;	//不提示
						
						m_Buttonhint.setText("提示");
					}else {
						GameModel.IS_HINT=true;		//提示
						
						m_Buttonhint.setText("不提示");
					}
				}
			});
			
			break;
		default://闯关模式
			
			break;
		}
		
		
		
		switch (GameModel.MODEL) {
		case 0://经典模式
			m_ShapView = (ShapView) findViewById(R.id.svclassical);
			m_GameView = (GameClassicalView)findViewById(R.id.tetvclassical);
			break;
		case 1://隐藏模式
			m_ShapView = (ShapView) findViewById(R.id.sv);
			m_GameView = (GameYincangView) findViewById(R.id.tetv);
			break;
		default://闯关模式
			m_ShapView = (ShapView) findViewById(R.id.sv_chuangguan);
			m_GameView = (GameChuangGuanView)findViewById(R.id.tetv_chuangguan);
			break;
		}
		
		
		m_detector = new GestureDetector(this,new MyGestureDetector());//设置触控
		m_GameView.setOnClickListener(new View.OnClickListener() {//必须设置，因为OnTouchListener继承了它
			@Override
			public void onClick(View v) {
				
			}
		});
		m_GameView.setOnTouchListener(new View.OnTouchListener() {//设置触控监听
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch(event.getAction()&MotionEvent.ACTION_MASK){
				    case MotionEvent.ACTION_DOWN:
				    	isTouched = false;
				    	break;
				    case MotionEvent.ACTION_POINTER_DOWN:
				    	break;
				    case MotionEvent.ACTION_UP:
				    	longLeft = false;
				    	longRight = false;
				    	longDown = false;
				    	if(!isTouched){//设置点击视图旋转方块
				    		if(isstop==true){
								return false;
							}
				    		data.goTurn();
							m_GameView.invalidate();
							m_ShapView.invalidate();
				    	}
				    	break;
				    case MotionEvent.ACTION_MOVE:
				    	isTouched = true;
				    	break;
				  }
				return m_detector.onTouchEvent(event);
			}
		});
		m_MediaPlayer=null;
		m_MediaPlayer = MediaPlayer.create(this, R.raw.music1);//设置音乐播放
		m_MediaPlayer.setLooping(true);
		
		if(m_sp.getBoolean(GAME_MUSIC, true))//判断音乐是否开启
				m_MediaPlayer.start();
	}
	
	public void setText(){//更新分数
		
		switch (GameModel.MODEL) {
		case 0://经典模式	

			m_TextScore.setText("\n得分\n\n"+Data.score);
			m_TextDone.setText("\n消除行数\n\n"+Data.line);
			if(a!=Data.line){
				
				if(m_sp.getBoolean(GAME_MUSIC, true))//判断音乐是否开启
					sp.play(music_cleanline, 1, 1, 0, 0, 1);
			}
			a=Data.line;
			break;
		case 1://隐藏模式

			m_TextScore.setText("\n得分\n\n"+Data.score_yincang);
			m_TextDone.setText("\n消除行数\n\n"+Data.line);
			if(a!=Data.line){
				
				if(m_sp.getBoolean(GAME_MUSIC, true))//判断音乐是否开启
					sp.play(music_cleanline, 1, 1, 0, 0, 1);
			}
			a=Data.line;
			break;
		default://闯关模式

			m_TextLevel.setText("\n关卡\n"+Data.level_chuangguan);
			m_TextScore.setText("\n得分\n"+Data.score_chuangguan);
			m_TextDone.setText("\n消除行数\n"+Data.line);
			if(a!=Data.line){
			
				if(m_sp.getBoolean(GAME_MUSIC, true))//判断音乐是否开启
					sp.play(music_cleanline, 1, 1, 0, 0, 1);
			}
			a=Data.line;
			break;
		}
	}

	@Override
	protected void onDestroy() {//释放资源
		super.onDestroy();
		SharedPreferences.Editor editor = m_sp.edit();
		switch (GameModel.MODEL) {
		case 0://经典模式	
			if(m_sp.getLong(HIGH_SCORE, 0)<Data.score){//存储最高分				
				editor.putLong(HIGH_SCORE, Data.score);
				editor.commit();
			}
			break;
		case 1://隐藏模式
			if(m_sp.getLong(HIGH_SCORE_YC, 0)<Data.score_yincang){//存储最高分				
				editor.putLong(HIGH_SCORE_YC, Data.score_yincang);
				editor.commit();
			}			
			break;
		default://闯关模式
			if(m_sp.getLong(HIGH_SCORE_CG, 0)<Data.score_chuangguan)
			{
				
				editor.putLong(HIGH_SCORE_CG, Data.score_chuangguan);
				if(Data.level_chuangguan*Data.CHUANGGUAN_TOP_SCORE<=Data.score_chuangguan)
				{					
						int a=(int)(Data.score_chuangguan/Data.CHUANGGUAN_TOP_SCORE);
						editor.putInt(LEVEL_CG,++a);
						Data.level_chuangguan=m_sp.getInt(LEVEL_CG, 1);
						editor.commit();
				}
				editor.commit();
			}			
			break;
		}
		m_MediaPlayer.stop();
		m_MediaPlayer.release();
		m_MediaPlayer=null;
		timer.cancel();
	}

	
	@Override
	protected void onStop() {
		// TODO 自动生成的方法存根
		if(m_MediaPlayer!=null){
			m_MediaPlayer.pause();
		}
		super.onStop();
		
	}
	
	private long lastPressTime;//双击退出
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if(lastPressTime<=0)
		{
			Toast.makeText(this, "再按一次后退键退出", Toast.LENGTH_SHORT).show();
			lastPressTime=System.currentTimeMillis();
		}
		else
		{
			long currentPress=System.currentTimeMillis();
			if((currentPress-lastPressTime)<=1500){
			    Data.score=0;
			    Data.score_chuangguan=0;
			    Data.level_chuangguan=1;			   
				super.onBackPressed();
			}
			else{
				Toast.makeText(this, "再按一次后退键退出", Toast.LENGTH_SHORT).show();
				lastPressTime=currentPress;
			}
		}

	}
}