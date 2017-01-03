package com.tetris;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.View;


public class GameYincangView extends View {	

	public GameYincangView(Context context, AttributeSet attrs) {
		super(context, attrs);

	}

	public void onDraw(Canvas canvas) {// 画图函数，主线程中实现不断刷新，此函数响应刷新
		super.onDraw(canvas);
		
		paintLine(canvas); //绘制线条
		paintBlock(canvas);// 绘制方块
	}

	public void paintLine(Canvas canvas) {
		float width = getWidth();
		float height = getHeight();
		float xdis = getWidth() / 10.0f;
		float ydis = getHeight() / 20.0f;
		Paint m_Paint = new Paint(); // 新建画笔
		m_Paint.setColor(Color.WHITE);//设置背景颜色
		canvas.drawRect(0.0f, 0.0f, width, height, m_Paint);//画白底背景
		m_Paint.setColor(Color.BLACK);
		for(int i = 0;i < 21;i++){//画横线
			canvas.drawLine(0.0f, i*ydis, width, i*ydis, m_Paint);
		}
		for(int j = 0;j < 11;j++){//画竖线
			canvas.drawLine(j*xdis, 0.0f, j*xdis, height, m_Paint);
		}
		/*public void drawLine(float startX,float startY,float stopX,float stopY,Paint paint)
		 * 参数说明
		 * startX:起始端点的X坐标
		 * startY:起始端点的Y坐标
		 * stopX:终止端点的X坐标
		 * stopY:终止端点的Y坐标
		 * paint：绘制直线所使用的画线
		 **/
		
	}
	public void paintBlock(Canvas canvas){
		float xdis = getWidth() / 10.0f;
		float ydis = getHeight() / 20.0f;
		Paint m_Paint = new Paint();
		m_Paint.setStyle(Style.FILL);
		/* 
		 * 设置透明行数
		 * 		减去data.yinchangline；
		 */
		if(GameModel.IS_HINT){
			
			for(int i=0;i<Data.of_Width;i++){//画固定的方块
				for(int j=0;j<Data.of_Height;j++){
					if(Data.m_screen[i][j]!=0){
						m_Paint.setColor(Data.m_screen[i][j]);
						canvas.drawRect(j*xdis+3, (i-4)*ydis+3, (j+1)*xdis-2, (i-3)*ydis-2, m_Paint);
					}
				}
			}
			
		}else {
			for(int i=0;i<Data.of_Width-Data.yinchangline;i++)
			{//画固定的方块
				for(int j=0;j<Data.of_Height;j++)
				{
					if(Data.m_screen[i][j]!=0)
					{
						m_Paint.setColor(Data.m_screen[i][j]);
						canvas.drawRect(j*xdis+3, (i-4)*ydis+3, (j+1)*xdis-2, (i-3)*ydis-2, m_Paint);
					}
				}
			}
			/*
			 * 将最后的data.yinchangline改成白色，即将最后一行隐藏起来
			 */
			for(int i=Data.of_Width;i>Data.of_Width-Data.yinchangline;i--){
				for(int j=0;j<Data.of_Height;j++){
					m_Paint.setColor(Color.WHITE);
					canvas.drawRect(j*xdis+3, (i-4)*ydis+3, (j+1)*xdis-2, (i-3)*ydis-2, m_Paint);
				}
			}
		}
		
		for(int i=0;i<4;i++){//画移动方块
			for(int j=0;j<4;j++){
				if(StateFang.state[Data.k][i][j]!=0){
					m_Paint.setColor(StateFang.color[Data.k/4]);
					canvas.drawRect((j+Data.of_y)*xdis+3, (i+Data.of_x-4)*ydis+3, (j+Data.of_y+1)*xdis-2, (i+Data.of_x-3)*ydis-2, m_Paint);
				}
			}
		}
	}
}
