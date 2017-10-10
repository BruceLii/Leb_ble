package com.ledble.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.common.uitl.LogUtil;
import com.ledble.R;
import com.ledble.base.LedBleApplication;

public class BlackWiteSelectView extends View {

	int[] mRectColors = new int[] { 0xFF000000, 0xFFFFFFFF };
	private Paint paint;
	private Shader shader;
	int w;
	int h;
	private OnSelectColor onSelectColor;
	private float tx;
	private float ty;
	private int kx;
	private int ky;

	private Context context;
	private Bitmap txmap;

	private int progress;

	public BlackWiteSelectView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		init();
	}

	private void init() {
		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setStrokeWidth(5);
	}

	public void setStartColor(int color) {
		mRectColors[1] = color;
		// tx = w;
		invalidate();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		tx = event.getX();
		ty = h / 2;

		// int color = interpRectColor(mRectColors, tx, w);
		int color = getColor(mRectColors, tx, w);
		LogUtil.i(LedBleApplication.tag, " ttttouch:" + Integer.toHexString(color));
		progress = (int) ((tx / w) * 100);
		if (null != onSelectColor) {
			onSelectColor.onColorSelect(color, progress);
		}
		if (inRectangle((int) tx, (int) ty, w, h)) {
			invalidate();
		}
		return true;
	}

	public int getProgress() {
		return progress;
	}

	public void setProgress(int progress) {
		this.progress = progress;
		this.tx=(int)(w*(progress/100.0));
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawColor(Color.TRANSPARENT);
		w = getWidth();
		h = getHeight();
		if (null == txmap) {
			txmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.bg_picker_knob);
			kx = txmap.getWidth();
			ky = txmap.getHeight();
		}

		float round = 10;
		shader = new LinearGradient(0, h / 2, w, h / 2, mRectColors, null, Shader.TileMode.MIRROR);
		paint.setShader(shader);
		paint.setStyle(Style.FILL);
		RectF rect = new RectF(0, 0, w, h);
		paint.setColor(Color.BLACK);
		canvas.drawRoundRect(rect, round, round, paint);

		// 画白色的带子

		Paint paint2 = new Paint();
		paint2.setAntiAlias(true);
		paint2.setStrokeWidth(5);
		paint2.setStyle(Style.STROKE);
		paint2.setColor(Color.WHITE);
		canvas.drawRoundRect(rect, round, round, paint2);

		if (ty == 0) {
			ty = h / 2;
		}
		canvas.drawBitmap(txmap, tx - kx / 2, ty - ky / 2, paint);

		// paint.setStyle(Style.FILL);
		// paint.setStrokeWidth(10);
		// paint.setShader(null);
		// paint.setColor(0x88ffffff);
		// canvas.drawRoundRect(rect, round, round, paint);
		// canvas.drawCircle(w/2, h/2, 20, paint);
	}

	public void moveEge(int degree) {

	}

	private boolean inRectangle(int tx, int ty, int w, int h) {
		if ((tx >= 0 && tx <= w)) {
			return true;
		}
		return false;
	}

	private int getColor(int colors[], float x, int w) {
		int a, r, g, b, c0, c1;
		if (x < 0) {
			x = 0;
		}
		if (x > w) {
			x = w;
		}
		float p = (float) x / w;
		c0 = colors[0];
		c1 = colors[1];

		a = ave(Color.alpha(c0), Color.alpha(c1), p);
		r = ave(Color.red(c0), Color.red(c1), p);
		g = ave(Color.green(c0), Color.green(c1), p);
		b = ave(Color.blue(c0), Color.blue(c1), p);

		return Color.argb(a, r, g, b);
	}

	private int interpRectColor(int colors[], float x, int rectRight) {
		int a, r, g, b, c0, c1;
		float p;
		if (x < 0) {
			c0 = colors[0];
			c1 = colors[1];
			p = (x + rectRight) / rectRight;
		} else {
			c0 = colors[0];
			c1 = colors[1];
			p = x / rectRight;
		}
		a = ave(Color.alpha(c0), Color.alpha(c1), p);
		r = ave(Color.red(c0), Color.red(c1), p);
		g = ave(Color.green(c0), Color.green(c1), p);
		b = ave(Color.blue(c0), Color.blue(c1), p);
		return Color.argb(a, r, g, b);
	}

	private int ave(int s, int d, float p) {
		return s + Math.round(p * (d - s));
	}

	public OnSelectColor getOnSelectColor() {
		return onSelectColor;
	}

	public void setOnSelectColor(OnSelectColor onSelectColor) {
		this.onSelectColor = onSelectColor;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	public static interface OnSelectColor {
		public void onColorSelect(int color, int progress);
	}
}
