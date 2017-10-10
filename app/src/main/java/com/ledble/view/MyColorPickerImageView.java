package com.ledble.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.common.uitl.LogUtil;
import com.ledble.R;
import com.ledble.base.LedBleApplication;

public class MyColorPickerImageView extends ImageView {

	private Bitmap bitmap;
	private Bitmap txmap;
	private OnTouchPixListener onTouchPixListener;

	private double tx;
	private double ty;

	private int kx;
	private int ky;

	private int w;
	private int h;

	private Paint p;
	private Context context;

	private float innerCircle = 0;
	private static final int MIN_POINT = 2;
	private int borderWidth = 0;

	public MyColorPickerImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		p = new Paint();
		this.context = context;
		setDrawingCacheEnabled(true);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		w = getWidth();
		h = getHeight();

		if (null == txmap) {
			txmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.bg_picker_knob);
			kx = txmap.getWidth();
			ky = txmap.getHeight();
		}

		int r = w / 2;
		int centerX = w / 2;
		int centerY = h / 2;

		if (tx == 0 && ty == 0) {
			if (tx == 0) {
				tx = w / 2;
			}
			if (ty == 0) {
				ty = h / 2;
			}
			canvas.drawBitmap(txmap, (float) (tx - kx / 2), (float) (ty - ky / 2), p);
		}

		if (inCircle((int) tx, (int) ty, r, centerX, centerY)) {
			canvas.drawBitmap(txmap, (float) (tx - kx / 2), (float) (ty - ky / 2), p);
		} else {
			LogUtil.i(LedBleApplication.tag, "not in circle:" + r);
		}

	}

	public void move2Ege(double angle) {
		int cx = w / 2;
		int cy = h / 2;
		float r = cx - borderWidth;
		if (angle == 0) {
			tx = w - borderWidth;
			ty = h / 2;
		} else if (angle == Math.PI / 3) {
			tx = r + r * 0.5;
			ty =r-r*0.5*Math.sqrt(3);
		} else if (angle == Math.PI * 2 / 3) {
			tx =(int)(r - r * 0.5);
			ty =(int)(r-r*0.5*Math.sqrt(3));
		} else if (angle == Math.PI) {
			tx = borderWidth;
			ty = h / 2;
		} else if (angle == Math.PI * 4 / 3) {
			tx = r - r * 0.5;
			ty = r + r * (1.0f / Math.sqrt(3));
		} else if (angle == Math.PI * 5 / 3) {
			tx = r + r * 0.5;
			ty = r+r*0.5*Math.sqrt(3);
		}
		LogUtil.i(LedBleApplication.tag, "tx:" + tx + " ty:" + ty + " w:" + w);
		invalidate();
	}

	public int getBolorWidth() {
		return borderWidth;
	}

	public void setBolorWidth(int bolorWidth) {
		this.borderWidth = bolorWidth;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		w = getWidth();
		h = getHeight();

		int r = w / 2;
		tx = (int) event.getX();
		ty = (int) event.getY();

		int centerX = w / 2;
		int centerY = h / 2;

		if (null == bitmap || bitmap.isRecycled()) {
			bitmap = getDrawingCache();
		}
		if (null != bitmap) {
			if ((tx * ty > 0) && (tx < bitmap.getWidth()) && (ty < bitmap.getHeight())
					&& inInnerCircle((int) tx, (int) ty, r, centerX, centerY, innerCircle)) {

				Point eventPoint = new Point((int) tx, (int) ty);
				Point centerPoint = new Point(centerX, centerY);
				float angle = angleFrom0(eventPoint, centerPoint);

				if (null != onTouchPixListener) {
					onTouchPixListener.onColorSelect(bitmap.getPixel((int) tx, (int) ty), angle);
				}
				invalidate();
			}
		}

		return true;
	}

	private boolean inCircle(int x, int y, int r, int centerX, int centerY) {
		int d = (x - centerX) * (x - centerX) + (y - centerY) * (y - centerY);
		int dx = (int) Math.sqrt(d);
		if (dx <= (r - borderWidth)) {
			return true;
		}
		return false;
	}

	private boolean inInnerCircle(int x, int y, int r, int centerX, int centerY, float innerCircle) {
		int d = (x - centerX) * (x - centerX) + (y - centerY) * (y - centerY);
		int dx = (int) Math.sqrt(d);
		if (dx < r && dx > r * innerCircle) {
			return true;
		}
		return false;
	}

	public OnTouchPixListener getOnTouchPixListener() {
		return onTouchPixListener;
	}

	public void setOnTouchPixListener(OnTouchPixListener onTouchPixListener) {
		this.onTouchPixListener = onTouchPixListener;
	}

	public float getInnerCircle() {
		return innerCircle;
	}

	public void setInnerCircle(float innerCircle) {
		this.innerCircle = innerCircle;
	}

	// 从0度开始顺时针旋转的角度
	protected float angleFrom0(Point eventPoint, Point center) {
		int x = eventPoint.x - center.x;
		int y = eventPoint.y - center.y;
		double z = Math.hypot(Math.abs(x), Math.abs(y));
		double sa = (double) Math.abs(y) / z;
		int xpoint = 0;
		if (xpoint > MIN_POINT) {
			z = Math.pow(z, 2);
		}
		double ts = Math.asin(sa);
		float d = (float) (ts / 3.14f * 180f);// 角度
		float rd = 0;
		if (x <= 0 && y <= 0) {
			rd = 180 + d;
		} else if (x >= 0 && y <= 0) {
			rd = 360 - d;
		} else if (x <= 0 && y >= 0) {
			rd = 180 - d;
		} else {
			rd = d;
		}
		return rd;
	}

	public static interface OnTouchPixListener {
		public void onColorSelect(int color, float angle);
	}
}
