package com.ledble.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.common.uitl.DrawTool;
import com.common.uitl.LogUtil;
import com.ledble.R;
import com.ledble.base.LedBleApplication;

public class MyColorPickerImageView4RGB extends ImageView {

	private Bitmap bitmap;
	private Bitmap bitmapBg;

	private Bitmap txmap;
	private OnTouchPixListener onTouchPixListener;

	private double tx;
	private double ty;

	private int kx;
	private int ky;

	private int w;
	private int h;

	private float r;

	private Paint p;
	private Context context;

	private float innerCircle = 0;
	private static final int MIN_POINT = 2;
	private int borderWidth = 0;

	public MyColorPickerImageView4RGB(Context context, AttributeSet attrs) {
		super(context, attrs);
		p = new Paint();
		p.setAntiAlias(true);
		p.setColor(Color.RED);
//		p.setStrokeWidth(2);
		p.setStyle(Style.STROKE);
		this.context = context;
		setDrawingCacheEnabled(true);
	}

	double lx = 0;
	double ly = 0;

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		w = getWidth();
		h = getHeight();

		int centerX = w / 2;
		int centerY = h / 2;
		r = Math.min(w, h) / 2 - borderWidth / 2;
		LogUtil.i(LedBleApplication.tag, "r:" + r);
		// ======
		if (null == txmap || txmap.isRecycled()) {
			txmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.bg_picker_knob);
			kx = txmap.getWidth();
			ky = txmap.getHeight();
		}
		if (null == bitmapBg || bitmapBg.isRecycled()) {
			bitmapBg = BitmapFactory.decodeResource(context.getResources(), R.drawable.bg_collor_picker);
			float f=2*r/bitmapBg.getHeight();
			bitmapBg=DrawTool.scale(bitmapBg, f);
//			bitmapBg = DrawTool.getRoundedBitmap(bitmapBg, r);
		}
		LogUtil.i(LedBleApplication.tag, "bgmap:"+bitmapBg.getWidth()+" "+bitmapBg.getHeight()+" r:"+r);
//		canvas.drawCircle(centerX, centerY, r, p);
		canvas.drawBitmap(bitmapBg, centerX-r, centerY-r, p);

		if (tx == 0 && ty == 0) {
			if (tx == 0) {
				tx = w / 2;
			}
			if (ty == 0) {
				ty = h / 2;
			}
			canvas.drawBitmap(txmap, (float) (tx - kx / 2), (float) (ty - ky / 2), p);
		}

		if (inCircle(tx, ty, r, centerX, centerY)) {
			lx = tx;
			ly = ty;
			canvas.drawBitmap(txmap, (float) (tx - kx / 2), (float) (ty - ky / 2), p);
		} else {
			LogUtil.i(LedBleApplication.tag, "not in circle:" + r);
			canvas.drawBitmap(txmap, (float) (lx - kx / 2), (float) (ly - ky / 2), p);
		}
		// canvas.drawCircle(centerX, centerY, 5, p);
	}

	public void move2Ege(double angle) {

		if (angle == 0) {
			tx = w - borderWidth;
			ty = h / 2;
		} else if (angle == Math.PI / 3) {
			tx = r + r * 0.5;
			ty = h / 2 - r * (Math.sqrt(3) / 2);
		} else if (angle == Math.PI * 2 / 3) {
			tx = r - r * 0.5;
			ty = h / 2 - r * (Math.sqrt(3) / 2);
		} else if (angle == Math.PI) {
			tx = borderWidth;
			ty = h / 2;
		} else if (angle == Math.PI * 4 / 3) {
			tx = r - r * 0.5;
			ty = h / 2 + r * (Math.sqrt(3) / 2);
		} else if (angle == Math.PI * 5 / 3) {
			tx = r + r * 0.5;
			ty = h / 2 + r * (Math.sqrt(3) / 2);
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
		tx = (int) event.getX();
		ty = (int) event.getY();

		int centerX = w / 2;
		int centerY = h / 2;

		LogUtil.i(LedBleApplication.tag, "w:" + w + " h:" + h + " r:" + r + " c(x,y):" + centerX + "," + centerY + "");

		if (null == bitmap || bitmap.isRecycled()) {
			bitmap = getDrawingCache();
		}
		if (null != bitmap) {
			if ((tx * ty > 0) && inCircle((int) tx, (int) ty, r, centerX, centerY)) {
				Point eventPoint = new Point((int) tx, (int) ty);
				Point centerPoint = new Point(centerX, centerY);
				float angle = angleFrom0(eventPoint, centerPoint);
				invalidate();
				if (null != onTouchPixListener) {
					int c = bitmap.getPixel((int) tx, (int) ty);
					onTouchPixListener.onColorSelect(c, angle);
				}

			}
		}

		return true;
	}

	private boolean inCircle(double x, double y, double r, double centerX, double centerY) {
		double d = (x - centerX) * (x - centerX) + (y - centerY) * (y - centerY);
		int dx = (int) Math.sqrt(d);
		if (dx < (r-5)) {
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
