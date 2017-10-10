package com.ledble.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;

import com.ledble.R;

public class TabButton extends LinearLayout {
    
	@Bind(R.id.ivTabImage) ImageView ivTabImage;
	@Bind(R.id.tvTabName) TextView tvTabName;
	@Bind(R.id.ivTabImage_above) ImageView ivTabImage_above;
	@Bind(R.id.tvTabName_above) TextView tvTabName_above;
	@Bind(R.id.llBelow) LinearLayout llBelow;
	@Bind(R.id.llAbove) LinearLayout llAbove;
	@Bind(R.id.tvCount) TextView tvCount;// 数量控件


    private int imageResId;
    private String tabName;
    private boolean isSelect;
    private int backgroundResId;
    private int selectedBackgroundResId;
    private int selImageResId;

    public TabButton(Context context) {
        super(context);
    }

    public TabButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (isInEditMode()) return;
        View view = LayoutInflater.from(context).inflate(R.layout.layout_tabbtn, this);
        ButterKnife.bind(this, view);
    }

    /**
     * @param imageResId    tab图片
     * @param selImageResId 选中状态的图片
     * @param tabName       tab 名字
     * @param isSelect      是否选中状态
     * @param context       上下文
     * @return
     */
    public TabButton init(int imageResId, int selImageResId, String tabName, boolean isSelect,
                          Context context) {
        this.imageResId = imageResId;
        this.selImageResId = selImageResId;
        this.tabName = tabName;
        this.isSelect = isSelect;
        
        ivTabImage.setImageResource(imageResId);
        tvTabName.setText(tabName);

        ivTabImage_above.setImageResource(selImageResId);
        tvTabName_above.setText(tabName);
        
        tvTabName.setTextColor(context.getResources().getColor(R.color.white));
        tvTabName_above.setTextColor(context.getResources().getColor(R.color.colorPrimary));

        if (isSelect) {
        	llAbove.setAlpha(1);
            llBelow.setAlpha(0);
        } else {
        	llAbove.setAlpha(0);
            llBelow.setAlpha(1);
        }
        postInvalidate();
        return this;
    }

    public int getImageResId() {
        return imageResId;
    }

    public TabButton setImageResId(int imageResId) {
        this.imageResId = imageResId;
        return this;
    }

    public String getTabName() {
        return tabName;
    }

    public TabButton setTabName(String tabName) {
        this.tabName = tabName;
        return this;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean isSelect) {
        this.isSelect = isSelect;
        if (isSelect) {
        	llAbove.setAlpha(1);
            llBelow.setAlpha(0);
        } else {
        	llAbove.setAlpha(0);
            llBelow.setAlpha(1);
        }
        postInvalidate();
    }

    public int getBackgroundResId() {
        return backgroundResId;
    }

    public void setBackgroundResId(int backgroundResId) {
        this.backgroundResId = backgroundResId;
    }

    public int getSelectedBackgroundResId() {
        return selectedBackgroundResId;
    }

    public void setSelectedBackgroundResId(int selectedBackgroundResId) {
        this.selectedBackgroundResId = selectedBackgroundResId;
    }

    public int getSelImageResId() {
        return selImageResId;
    }

    public void setSelImageResId(int selImageResId) {
        this.selImageResId = selImageResId;
    }

    public ImageView getIvTabImage() {
        return ivTabImage;
    }

    public void setIvTabImage(ImageView ivTabImage) {
        this.ivTabImage = ivTabImage;
    }

    public TextView getTvTabName() {
        return tvTabName;
    }

    public void setTvTabName(TextView tvTabName) {
        this.tvTabName = tvTabName;
    }

    public LinearLayout getLlBelow() {
        return llBelow;
    }

    public void setLlBelow(LinearLayout llBelow) {
        this.llBelow = llBelow;
    }

    public LinearLayout getLlAbove() {
        return llAbove;
    }

    public void setLlAbove(LinearLayout llAbove) {
        this.llAbove = llAbove;
    }

	public TextView getTvCount() {
		return tvCount;
	}

	public void setTvCount(TextView tvCount) {
		this.tvCount = tvCount;
	}

}
