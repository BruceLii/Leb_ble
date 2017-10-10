package com.common.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.ledble.R;

public class Segment extends View {

	private OnSegMentClickListener onSegMentClickListener;
	private int index = 2;

	private Paint paint = null;
	private int pic_width = 0;
	private int pic_height = 1;
	private Drawable[] bgDrawable = new Drawable[3];
	private String[] tags = { "0", "1", "2" };
	private String[] text = { "进港", "出港", "进出港" };

	private int seg_text_size = 20;
	private int normal_color = 0x7e868e;
	private int press_color = 0xffffff;

	public Segment(Context context, AttributeSet attrs) {
		super(context, attrs);
		paint = new Paint();
		paint.setAntiAlias(true);
		
		seg_text_size = (int) context.getResources().getDimension(R.dimen.label_text_size_third);// 默认值
		
		// ========配置值
		TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.Segment);

		seg_text_size = (int) array.getDimension(R.styleable.Segment_seg_text_size, seg_text_size);// 标题文字大小
		paint.setTextSize(seg_text_size);
		text[0] = array.getString(R.styleable.Segment_seg1_text);
		text[1] = array.getString(R.styleable.Segment_seg2_text);
		text[2] = array.getString(R.styleable.Segment_seg3_text);

		normal_color = array.getColor(R.styleable.Segment_normal_text_color, normal_color);
		press_color = array.getColor(R.styleable.Segment_check_text_color, press_color);

		bgDrawable[0] = array.getDrawable(R.styleable.Segment_bgseg1);
		bgDrawable[1] = array.getDrawable(R.styleable.Segment_bgseg2);
		bgDrawable[2] = array.getDrawable(R.styleable.Segment_bgseg3);

		pic_width = bgDrawable[2].getIntrinsicWidth();
		pic_height = bgDrawable[2].getIntrinsicHeight();
		array.recycle();
		refresh();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(pic_width, pic_height);
	}

	public OnSegMentClickListener getOnSegMentClickListener() {
		return onSegMentClickListener;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		int w = getWidth();
		float x = event.getX();
		int x1 = w / 3;
		int x2 = (w / 3) * 2;
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			if (x <= x1) {
				index = 0;
			}
			if (x > x1 && x < x2) {
				index = 1;
			}
			if (x > x2) {
				index = 2;
			}
			refresh();
			return true;
		case MotionEvent.ACTION_UP:
			if (null != onSegMentClickListener) {
				onSegMentClickListener.onSelect(index, tags[index],text[index]);
			}
			break;
		}
		refresh();
		return super.onTouchEvent(event);
	}

	public void setIndex(int index){
		this.index=index;
		refresh();
	}
	private void refresh() {
		setBackgroundDrawable(bgDrawable[index]);
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int w = getWidth();
		int h = getHeight();

		Rect bounds = new Rect();
		paint.getTextBounds(text[0], 0, text[0].length(), bounds);
		int delTaYBounds = bounds.bottom - bounds.top;
		int top = h / 2 + delTaYBounds / 2 - delTaYBounds / 5;

		for (int i = 0; i < text.length; i++) {
			int color=((i==index)? press_color:normal_color );
			paint.setColor(color);
			int x=(int)((w / 3 / 2 - paint.measureText(text[i]) / 2) +( w / 3)*i);
			canvas.drawText(text[i], x, top, paint);
			//===========
		}
	}

	public String[] getTags() {
		return tags;
	}

	public void setTags(String[] tags) {
		this.tags = tags;
	}

	
	public int getIndex() {
		return index;
	}

	// 获取字体高度
	public int getFontHeight(float fontSize) {
		paint.setTextSize(fontSize);
		FontMetrics fm = paint.getFontMetrics();
		return (int) Math.ceil(fm.descent - fm.top) + 2;
	}

	public void setOnSegMentClickListener(OnSegMentClickListener onSegMentClickListener) {
		this.onSegMentClickListener = onSegMentClickListener;
	}

	public static interface OnSegMentClickListener {
		public void onSelect(int index, String tag,String text);
	}
}
