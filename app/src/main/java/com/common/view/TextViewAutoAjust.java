package com.common.view;

import android.content.Context;
import android.graphics.Paint;
import android.text.Html;
import android.util.AttributeSet;
import android.widget.TextView;

public class TextViewAutoAjust extends TextView {

	Paint paint=null;
	Context context;
	public TextViewAutoAjust(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context=context;
		this.paint=new Paint();
		
	}

	public void setText(String text){
		super.setText(Html.fromHtml(text));
	}
	@Override
	public void setText(CharSequence text, BufferType type) {
		super.setText(text, type);
	}
//	@Override
//	protected void onDraw(Canvas canvas) {
//		super.onDraw(canvas);
//		canvas.drawColor(color.white);
//		if(!StringUtils.isEmpty(getText().toString())){
//		    this.paint.setTextSize(getTextSize());
//		    String text=getText().toString().toString();
////		    int len = (int)this.paint.measureText(text);
////		    int w = getWidth();
////		    int h=getHeight();
////		    int line_count=len/w;//行数
////		    for(int i=0;i<line_count;i++){
////		    	String str=text.substring(i,i*line_count);
////		    	canvas.drawText(str, 0, h/line_count*i, paint);
////		    }
//		}
//	}
}
