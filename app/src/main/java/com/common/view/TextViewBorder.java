package com.common.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

public class TextViewBorder extends TextView {

	private Paint paint;
	public TextViewBorder(Context context, AttributeSet attrs) {
		super(context, attrs);
		paint=new Paint();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int w=getWidth();
		int h=getHeight();
		paint.setStrokeWidth(2);
		paint.setColor(0xff4f7c81);
		canvas.drawLine(0, 0, w, 0, paint);//上
		canvas.drawLine(0, h, w, h, paint);//下
		canvas.drawLine(0, 0, 0, h, paint);//左
		canvas.drawLine(w, 0, w, h, paint);//右
	}

}
