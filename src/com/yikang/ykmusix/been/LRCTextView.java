package com.yikang.ykmusix.been;



import java.io.File;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;



public class LRCTextView extends TextView {
	private Paint mPaint;
	private Paint mPaintBG;
	private float mX;
	private Paint mCurPaint;
	public String test = "test";
	
	public float mTouchHistoryY;
	private int mY;
	private float middleY;// y轴中间
	private static final int DY = 65; // 每一行的间隔
	
	Map<Long, String> lrcMap ;
	Object[] key  ;
	public int index = 0;
	public LRCTextView(Context context) {
		super(context);
		init();
	}
	public LRCTextView(Context context, AttributeSet attr) {
		super(context, attr);
		init();
	}
	public LRCTextView(Context context, AttributeSet attr, int i) {
		super(context, attr, i);
		init();
	}
	private void init() {
		setFocusable(true);
	
	   mPaintBG= new Paint();
	   mPaintBG.setColor(0xEFeffff);
		// 非高亮部分
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setTextSize(26);
		mPaint.setColor(Color.GRAY);
		mPaint.setTypeface(Typeface.SERIF);
		// 高亮部分 当前歌词
		mCurPaint = new Paint();
		mCurPaint.setAntiAlias(true);
		mCurPaint.setColor(Color.RED);
		mCurPaint.setTextSize(36);
		mCurPaint.setTypeface(Typeface.SANS_SERIF);
	}
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawPaint(mPaintBG);
		Paint p = mPaint;
		Paint p2 = mCurPaint;
		p.setTextAlign(Paint.Align.CENTER);
		p2.setTextAlign(Paint.Align.CENTER);
		
		if (index<=0){
			return;
		}
		
		if(lrcMap==null||key==null){
			return ;
		}
		if(lrcMap.isEmpty()||key.length==0){
			return ;
		}
		
		//当前句
		canvas.drawText(lrcMap.get(key[index-1])+"", mX, middleY, p2);
		float tempY = middleY;
		// 画出本句之前的句子
		for (int i = index - 2; i >= 0; i--) {
			// Sentence sen = list.get(i);
			// 向上推移
			tempY = tempY - DY;
			if (tempY < 0) {
				break;
			}
			canvas.drawText(lrcMap.get(key[i])+"", mX, tempY, p);
			// canvas.translate(0, DY);
		}
		tempY = middleY;
		// 画出本句之后的句子
		for (int i = index ; i < key.length-1; i++) {
			// 往下推移
			tempY = tempY + DY;
			if (tempY > mY) {
				break;
			}
			canvas.drawText(lrcMap.get(key[i])+"", mX, tempY, p);
			// canvas.translate(0, DY);
		}
	}
	protected void onSizeChanged(int w, int h, int ow, int oh) {
		super.onSizeChanged(w, h, ow, oh);
		mX = w * 0.5f; // remember the center of the screen
		mY = h;
		middleY = h * 0.5f;
	}
	public void updata(int index,Map<Long, String> lrcMap ,Object[] key){
		this.index=index;
		this.lrcMap =lrcMap;
		this.key=key  ;
	}
	
}