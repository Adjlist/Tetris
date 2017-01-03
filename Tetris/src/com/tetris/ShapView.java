package com.tetris;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;

import android.util.AttributeSet;

import android.view.View;

public class ShapView extends View {

	public ShapView(Context context, AttributeSet attrs) {
		super(context, attrs);

	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		float width = getWidth();
		float height = getHeight();
		float xdis = getWidth() / 6.0f;
		float ydis = getHeight() / 6.0f;
		Paint m_Paint = new Paint(); // 新建画笔
		m_Paint.setStyle(Style.FILL);
		m_Paint.setColor(Color.WHITE);
		canvas.drawRect(0.0f, 0.0f, width, height, m_Paint);
		for(int i=0;i<4;i++){//画下一个方块
			for(int j=0;j<4;j++){
				if(StateFang.state[Data.a][i][j]!=0){
					m_Paint.setColor(StateFang.color[Data.a/4]);
					canvas.drawRect((j+1)*xdis+2, (i+1)*ydis+2, (j+2)*xdis-1, (i+2)*ydis-1,m_Paint);
				}
			}
		}
		
	}

}
