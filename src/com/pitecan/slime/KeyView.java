//
//	キーと候補を描画するIMEのビュー
//
// FontMetricsは以下を参照
// http://wikiwiki.jp/android/?%A5%C6%A5%AD%A5%B9%A5%C8%A4%CE%C9%C1%B2%E8%28FontMetrics%29
//
package com.pitecan.slime;

import android.view.View;
import android.view.MotionEvent;
import android.graphics.Color;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint.FontMetrics;
import android.content.res.Resources;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

class CandButton {
    Rect rect;
    String text;
    String pat;
    boolean valid;
    boolean visible;
    int row;

    public CandButton() {
	rect = new Rect(0,0,0,0);
	text = "";
	pat = "";
	valid = false;
	visible = false;
	row = 0;
    }
}

public class KeyView extends View {
    private Bitmap keybg32x53, keyfg32x53;
    private Bitmap keybg53x53, keyfg53x53;
    private Bitmap keybg53x106, keyfg53x106;
    private Bitmap keybg24x106, keyfg24x106;
    private Bitmap keybg106x53, keyfg106x53;
    private Bitmap keybg106x106, keyfg106x106;
    private Paint keyPaint;
    private Paint smallKeyPaint;
    private Paint tinyKeyPaint;
    private Paint buttonPaint;
    private Paint buttonTextPaint;
    private final int keyViewWidth =     320;
    private final int buttonMarginX =    5;  // ボタン間の隙間
    private final int buttonMarginY =    6;
    private final int buttonTextMargin = 5;
    private final int buttonHeight =     28;
    private final int buttonTextSize =   18;
    private final int largeKeyTextSize = 36;
    private final int smallKeyTextSize = 24;
    private final int tinyKeyTextSize =  16;

    public int inputWidth = 320; // IMEの幅

    public Keys keys;
    public KeyController keyController = null;
    public int candLines;
    private Key[] keypat = null;
    private Key selectedKey;
    private Key selectedKey2;
    // private boolean showCand = false;
    private int candPage = 0;

    public static CandButton[] candButtons;

    public KeyView(Context context, AttributeSet attrs) {
	super(context,attrs);

	// キー画像の読み込み
	Resources r = context.getResources();
	keybg53x53 =   BitmapFactory.decodeResource(r,R.drawable.keybg53x53);
	keyfg53x53 =   BitmapFactory.decodeResource(r,R.drawable.keyfg53x53);
	keybg53x106 =  BitmapFactory.decodeResource(r,R.drawable.keybg53x106);
	keyfg53x106 =  BitmapFactory.decodeResource(r,R.drawable.keyfg53x106);
	keybg106x53 =  BitmapFactory.decodeResource(r,R.drawable.keybg106x53);
	keyfg106x53 =  BitmapFactory.decodeResource(r,R.drawable.keyfg106x53);
	keybg24x106 =  BitmapFactory.decodeResource(r,R.drawable.keybg24x106);
	keyfg24x106 =  BitmapFactory.decodeResource(r,R.drawable.keyfg24x106);
	keybg106x106 = BitmapFactory.decodeResource(r,R.drawable.keybg106x106);
	keyfg106x106 = BitmapFactory.decodeResource(r,R.drawable.keyfg106x106);
	keybg32x53 =   BitmapFactory.decodeResource(r,R.drawable.keybg32x53);
	keyfg32x53 =   BitmapFactory.decodeResource(r,R.drawable.keyfg32x53);

	/*
	// キートップ色
	keyPaint = new Paint();
	keyPaint.setAntiAlias(true);
        keyPaint.setTextSize(largeKeyTextSize);
        keyPaint.setColor(0xff000000); // argb 黒
	smallKeyPaint = new Paint();
	smallKeyPaint.setAntiAlias(true);
	smallKeyPaint.setTextSize(smallKeyTextSize);
        smallKeyPaint.setColor(0xff000000); // 黒
	tinyKeyPaint = new Paint();
	tinyKeyPaint.setAntiAlias(true);
	tinyKeyPaint.setTextSize(tinyKeyTextSize);
        tinyKeyPaint.setColor(0xff000000); // 黒
	*/

	// 背景色
	buttonPaint = new Paint();
        buttonPaint.setColor(0xffc0c0c0);
	
	/*
	// 候補ボタンのテキスト色
	buttonTextPaint = new Paint();
	buttonTextPaint.setAntiAlias(true);
        buttonTextPaint.setTextSize(buttonTextSize);
        buttonTextPaint.setColor(0xff000000); // 黒
	*/

	// 候補「ボタン」の初期化
	candButtons = new CandButton[Slime.MAXCANDS];
	for(int i=0;i<candButtons.length;i++){
	    CandButton button = new CandButton();
	    candButtons[i] = button;
	}
    }

    // イベント処理はkeyControllerに丸投げする
    public boolean onTouchEvent(MotionEvent ev) {
	if(keyController != null){
	    keyController.onTouchEvent(ev);
	}
	return true;
    }

    private int bgcolor;

    public void draw2(Key keypat[], Key selectedKey, Key selectedKey2, int candPage){
	this.keypat = keypat;
	this.selectedKey = selectedKey;
	this.selectedKey2 = selectedKey2;
	this.candPage = candPage;
	bgcolor = 0xffd0d0d0;
	invalidate();
    }

    public void draw(Key keypat[], Key selectedKey, Key selectedKey2, int candPage){
	this.keypat = keypat;
	this.selectedKey = selectedKey;
	this.selectedKey2 = selectedKey2;
	this.candPage = candPage;
	bgcolor = 0xfff0f0f0;
	if(keyController.useGoogle && !keyController.googleDisplayed){
	    bgcolor = 0xffd0d0d0;
	    keyController.googleDisplayed = true;
	}
	invalidate();
    }

    public void drawDefault(){
	this.keypat = keys.keypat0;
	this.selectedKey = null;
	this.selectedKey2 = null;
	this.candPage = 1;
	bgcolor = 0xfff0f0f0;
	invalidate();
    }

    public void drawDefault2(){
	this.keypat = keys.keypat0;
	this.selectedKey = null;
	this.selectedKey2 = null;
	this.candPage = 1;
	bgcolor = 0xffd0d0d0;
	invalidate();
    }

    //
    // 現在の版は1〜3行しか候補を表示しないが、1〜12行ぐらい用意しておいて
    // 3行ずつ表示するように変更したい
    //
    private void layoutCandButtons(){
	float x, y, w, h;   // 候補ボタンの矩形
	int buttonIndex = 0;
	//int[] leftlimit = {0, 0, 0, 24, 24, 24, 24, 24, 24, 24, 24, 24};
	int[] leftlimit = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
	//int[] rightlimit = {296, 296, 296, 296, 296, 296, 296, 296, 296, 296, 296, 296}; 
	int[] rightlimit = {320, 268, 268, 320, 268, 268, 320, 268, 268, 320, 268, 268};
	int[] curright = new int[12];
	for(int i=0;i<12;i++){
	    curright[i] = leftlimit[i] + buttonMarginX;
	}

	for(;buttonIndex<candButtons.length;buttonIndex++){
	    CandButton button = candButtons[buttonIndex];
	    String s = button.text;
	    if(s == "") break;
	    float textWidth = buttonTextPaint.measureText(s) * 320 / inputWidth;
	    w = textWidth + ((float)buttonMarginX * 2); // ボタン幅
	    h = buttonHeight;                           // ボタン高さ
	    // 空いている候補領域に候補ボタンを詰める
	    for(int i=0;i<12;i++){
		if(curright[i] + w + buttonMarginX <= rightlimit[i]){
		    x = curright[i];
		    y = buttonMarginY + (i % 3) * (buttonHeight+buttonMarginY);
		    /*
		    button.rect.pos.x = (int)x;
		    button.rect.pos.y = (int)y;
		    button.rect.size.w = (int)w;
		    button.rect.size.h = (int)h;
		    */
		    button.rect.left = (int)x;
		    button.rect.top = (int)y;
		    button.rect.right = (int)(x+w);
		    button.rect.bottom = (int)(y+h);
		    button.row = i;
		    button.valid = true;
		    curright[i] += (w + buttonMarginX);
		    break;
		}
	    }
	}
	for(candLines=0;candLines<12;candLines++){
	    if(curright[candLines] == leftlimit[candLines] + buttonMarginX) break;
	}
	for(;buttonIndex<candButtons.length;buttonIndex++){
	    CandButton button = candButtons[buttonIndex];
	    button.valid = false;
	}
    }

    @Override public void onDraw(Canvas canvas) {
	Bitmap image;
			    
	if(keypat == null) keypat = keys.keypat0; // Viewを作った瞬間はkeypatが設定されてない

	//Log.v("Slime","onDraw - length="+keypat.length);

	//canvas.drawColor(0x80f0f0f0); 背景を透明にしたい場合 - ブラウザしか有効じゃないみたいだけど
	canvas.drawColor(bgcolor);
	for(int i=0;i<keypat.length;i++){
	    Key key = keypat[i];
	    int width = key.rect.right - key.rect.left;
	    int height = key.rect.bottom - key.rect.top;
	    Paint paint = (width <= 24 ? tinyKeyPaint : width <= 32 ? smallKeyPaint : keyPaint);
	    FontMetrics fontMetrics = paint.getFontMetrics();
	    float ascent = fontMetrics.ascent; // これはマイナス値
	    float descent = fontMetrics.descent;
	    float shadewidth = 6;

	    image = ((selectedKey != null && key.str == selectedKey.str) ||
		     (selectedKey2 != null && key.str == selectedKey2.str)
		     ?
		     (width == 32 ? keyfg32x53 :
		      (height == 106 ?
		       (width == 106 ? keyfg106x106 : keyfg53x106) :
		       (width == 106 ? keyfg106x53 : keyfg53x53)
		       )
		      )
		     :
		     (width == 32 ? keybg32x53 :
		      (height == 106 ?
		       (width == 106 ? keybg106x106 : keybg53x106) :
		       (width == 106 ? keybg106x53 : keybg53x53)
		       )
		      )
		     );
	    if(! (key.str == "→" && (candPage == 0 || candPage == 4))){
		int left = key.rect.left * inputWidth / 320;
		int top = key.rect.top * inputWidth / 320;
		int right = key.rect.right * inputWidth / 320;
		int bottom = key.rect.bottom * inputWidth / 320;
		Rect dst = new Rect(left,top,right,bottom);
		canvas.drawBitmap(image,null,dst,null); // 何故か第2引数がkey.rectだとうまくいかない
		//
		// キー文字描画
		//
		float textWidth = paint.measureText(key.str);
		float baseX = left + (right-left-(shadewidth*inputWidth/320) - textWidth)/2;
		float baseY = top + (bottom-top-(shadewidth*inputWidth/320) - (ascent+descent))/2;
		canvas.drawText(key.str,baseX,baseY,paint);
	    }
	}
	if(candPage > 0){
	    CandButton button;
	    FontMetrics fontMetrics = buttonTextPaint.getFontMetrics();
	    float ascent = fontMetrics.ascent * 320 / inputWidth;
	    float descent = fontMetrics.descent * 320 / inputWidth;
	    layoutCandButtons();
	    for(int i=0;i<candButtons.length;i++){
		button = candButtons[i];
		candButtons[i].visible = button.valid && ((candPage-1) * 3 <= button.row && button.row < candPage * 3);
	    }
	    for(int i=0;i<candButtons.length;i++){
		button = candButtons[i];
		if(! button.visible) continue;
		float y = buttonMarginY + (button.row % 3) * (buttonHeight+buttonMarginY);
		canvas.drawRect((float)button.rect.left * inputWidth / 320,
				(float)button.rect.top * inputWidth / 320,
				(float)button.rect.right * inputWidth / 320,
				(float)(y+button.rect.bottom-button.rect.top) * inputWidth / 320,
				buttonPaint);
		canvas.drawText(button.text,
				(button.rect.left + buttonTextMargin) * inputWidth / 320,
				(button.rect.top + (buttonHeight-(ascent+descent))/2) * inputWidth / 320,
				buttonTextPaint);
	    }
	}
    }

    ///////////////////////////////////////////////////////////////////////////////////////

    //public void setButton(String word, int index){
    //	CandButton button = candButtons[index];
    //	button.text = word;
    //}

    ///////////////////////////////////////////////////////////////////////////////////////

    // よくわからないがこれを設定するとViewの大きさを決められるようだ...
    @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	Log.v("Slime","onMeasure = width = "+widthMeasureSpec+" height="+heightMeasureSpec);
	int specMode = MeasureSpec.getMode(widthMeasureSpec);
	int specSize = MeasureSpec.getSize(widthMeasureSpec);
	Log.v("Slime","modeandsize = "+specMode+", "+specSize);
	specMode = MeasureSpec.getMode(heightMeasureSpec);
	specSize = MeasureSpec.getSize(heightMeasureSpec);
	Log.v("Slime","modeandsize = "+specMode+", "+specSize);

	// Android.manifestで以下のような記述をしておけば勝手にスケールしてくれる
	// http://y-anz-m.blogspot.com/2010/02/andro
	// SDK version 4以降でこれが必要らしい
	//    <supports-screens  
	//       android:smallScreens="true"  
	//       android:normalScreens="true"  
	//       android:largeScreens="true"  
	//       android:anyDensity="false" />  
	//
	
	inputWidth = MeasureSpec.getSize(widthMeasureSpec);
        setMeasuredDimension(320 * inputWidth / 320,216 * inputWidth / 320);

	// キートップ色
	keyPaint = new Paint();
	keyPaint.setAntiAlias(true);
        keyPaint.setTextSize(largeKeyTextSize * inputWidth / 320);
        keyPaint.setColor(0xff000000); // argb 黒
	smallKeyPaint = new Paint();
	smallKeyPaint.setAntiAlias(true);
	smallKeyPaint.setTextSize(smallKeyTextSize * inputWidth / 320);
        smallKeyPaint.setColor(0xff000000); // 黒
	tinyKeyPaint = new Paint();
	tinyKeyPaint.setAntiAlias(true);
	tinyKeyPaint.setTextSize(tinyKeyTextSize * inputWidth / 320);
        tinyKeyPaint.setColor(0xff000000); // 黒

	buttonTextPaint = new Paint();
	buttonTextPaint.setAntiAlias(true);
        buttonTextPaint.setTextSize(buttonTextSize * inputWidth / 320);
        buttonTextPaint.setColor(0xff000000); // 黒
   }
}

