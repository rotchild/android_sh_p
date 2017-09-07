package cx.mobilechecksh.mvideo.picc.ui;


import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cx.mobilechecksh.R;


public class Titlebar extends RelativeLayout {

	private final String LEFT_TAG = "left";
	private final String CENTER_TAG = "center";
	private final String RIGHT_TAG = "right";

	private Context mContext;
	private Resources mResources;

	private View mLeftLayout;
	private ImageView mLeftImg;
	private TextView mLeftTxt;
	private ImageView mLeftBackImg;
	
	private View mCenterLayout;
	private TextView mCenterText;
	private ImageView centerButton;

	private View mRightLayout;
	private ImageView mRightImg;
	private TextView mRightTxt;
	
	private int bgId = 0;
	private int leftBgId = 0;
	private int rightBgId = 0;
	private int leftImageSrc = 0;
	private int rightImageSrc = 0;
	private int titlebarTitleId = 0;
	
	private LinearLayout titleBarLinear;

	public Titlebar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.mContext = context;
		  TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.UItitlebar, defStyle, 0);
		  bgId =  a.getResourceId(R.styleable.UItitlebar_titlebarBackground, 0);
		  leftBgId = a.getResourceId(R.styleable.UItitlebar_titlebarLeftBackground, 0);
		  rightBgId = a.getResourceId(R.styleable.UItitlebar_titlebarRightBackground, 0);
		  leftImageSrc = a.getResourceId(R.styleable.UItitlebar_titlebarLeftImageSrc, 0);
		  rightImageSrc = a.getResourceId(R.styleable.UItitlebar_titlebarRightImageSrc, 0);
		  titlebarTitleId = a.getResourceId(R.styleable.UItitlebar_titlebarTitle, 0);
	      a.recycle();
	     setAlwaysDrawnWithCacheEnabled(false);
		initView();
	}

	public Titlebar(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		this.mContext = context;

	}

	public Titlebar(Context context) {
		super(context);
		this.mContext = context;
		initView();
	}

	private void initView() {
		mResources = mContext.getResources();
		LayoutInflater.from(mContext).inflate(R.layout.ui_titlebar, this, true);

		mLeftLayout = findViewById(R.id.titlebar_leftLayout);
		mLeftImg = (ImageView) findViewById(R.id.titlebar_leftBtn);
		mLeftBackImg = (ImageView)findViewById(R.id.titlebar_leftBackBtn);
		mLeftTxt = (TextView) findViewById(R.id.titlebar_leftTxt);

		mCenterLayout = findViewById(R.id.titlebar_centerLayout);
		mCenterText = (TextView) findViewById(R.id.titlebar_center_text);
		centerButton = (ImageView) findViewById(R.id.titlebar_centerBtn);

		mRightLayout = findViewById(R.id.titlebar_rightLayout);
		// mLeftImg = (ImageView) findViewById(R.id.titlebar_rightBtn);
		mRightImg = (ImageView) findViewById(R.id.titlebar_rightBtn);
		mRightTxt = (TextView) findViewById(R.id.titlebar_rightTxt);
		
		titleBarLinear = (LinearLayout) findViewById(R.id.titlebar_layout);
		if (bgId!=0) {
			titleBarLinear.setBackgroundResource(bgId);
		}
		if (leftBgId!=0) {
			mLeftLayout.setBackgroundResource(leftBgId);
		}
		if (rightBgId!=0) {
			mRightLayout.setBackgroundResource(rightBgId);
		}
		if (leftImageSrc!=0) {
			setLeftImageRes(leftImageSrc);
		}
		if (rightImageSrc!=0) {
			setRightImageRes(rightImageSrc);
		}
		if (titlebarTitleId !=0) {
			setCenterText(titlebarTitleId);
		}

		mLeftLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				((Activity) mContext).finish();
			}
		});

	}

	public void setCenterText(int txtResId) {
		String text = mResources.getString(txtResId);
		setCenterText(text);
	}

	public void setCenterText(String text) {
		mCenterText.setText(text);
	}

	public void showLeft() {
		layoutVisibility(LEFT_TAG);
	}

	public void showLeft(OnClickListener l) {
		mLeftLayout.setOnClickListener(l);
		layoutVisibility(LEFT_TAG);
	}
	
	public void setLeftBackground(Drawable drawable){
		mLeftLayout.setBackgroundDrawable(drawable);
	}

	public void setRightBackground(Drawable drawable){
		mRightLayout.setBackgroundDrawable(drawable);
	}
	
	public void setLeftText(int redId) {
		String text = mResources.getString(redId);
		setLeftText(text);
	}

	public void setLeftText(String text) {
		mLeftTxt.setText(text);
	}

	public void setLeftImageRes(int redId) {
		mLeftImg.setImageResource(redId);
	}

	public void setLeftBackImagesRes(int redId){
		mLeftBackImg.setImageResource(redId);
	}
	
	public void showRight() {
		layoutVisibility(RIGHT_TAG);
	}

	public void showRight(OnClickListener l) {
		mRightLayout.setOnClickListener(l);
		layoutVisibility(RIGHT_TAG);
	}

	public void setRightText(int redId) {
		String text = mResources.getString(redId);
		setRightText(text);
	}

	public void setRightText(String text) {
		mRightTxt.setText(text);
	}

	public void setRightImageRes(int redId) {
		mRightImg.setImageResource(redId);
	}

	public void setOnRightButtonClickListener(OnClickListener l) {
		mRightImg.setOnClickListener(l);
		layoutVisibility(RIGHT_TAG);
	}
	public void setOnLeftButtonClickListener(OnClickListener l) {
		mLeftLayout.setOnClickListener(l);
		layoutVisibility(LEFT_TAG);
	}
	

	public void layoutVisibility(String tag) {
		if (tag.equals(LEFT_TAG)) {
			mLeftLayout.setVisibility(View.VISIBLE);
		} else if (tag.equals(CENTER_TAG)) {
			mCenterLayout.setVisibility(View.VISIBLE);
		} else if (tag.equals(RIGHT_TAG)) {
			mRightLayout.setVisibility(View.VISIBLE);
		}
	}
	
	public void setRightButtonVisbility(int visibility){
		mRightLayout.setVisibility(visibility);
	}
	
	public void setcenterEnable(boolean enable){
		mCenterLayout.setClickable(enable);
	}
	
	
	public void setCenterButtonImage(int resid){
		centerButton.setVisibility(View.VISIBLE);
		centerButton.setBackgroundResource(resid);
	}
	
	
	public void setCenterButtonVisibility(int visibility){
		centerButton.setVisibility(visibility);
	}
	
	public void setOncenterButtonClickListener(OnClickListener clickListener){
		if (clickListener != null) {
//			centerButton.setOnClickListener(clickListener);
			mCenterLayout.setOnClickListener(clickListener);
		}
		
	}

	

}