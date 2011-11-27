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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint.FontMetrics;
import android.content.res.Resources;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

class CandButton {
    Rectangle rect;
    String text;
    boolean visible;

    public CandButton() {
	rect = new Rectangle(0,0,0,0);
	text = "";
	visible = false;
    }
}

public class KeyView extends View {
    private Bitmap keybg32x53, keyfg32x53;
    private Bitmap keybg53x53, keyfg53x53;
    private Bitmap keybg53x106, keyfg53x106;
    private Bitmap keybg106x53, keyfg106x53;
    private Bitmap keybg106x106, keyfg106x106;
    private float mousex, mousey;
    private Paint keyPaint;
    private Paint smallKeyPaint;
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

    public Keys keys;
    public KeyController keyController = null;
    private Key[] keypat = null;
    private Key selectedKey;
    private Key selectedKey2;
    private boolean showCand = false;

    public CandButton[] candButtons;

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
	keybg106x106 = BitmapFactory.decodeResource(r,R.drawable.keybg106x106);
	keyfg106x106 = BitmapFactory.decodeResource(r,R.drawable.keyfg106x106);
	keybg32x53 =   BitmapFactory.decodeResource(r,R.drawable.keybg32x53);
	keyfg32x53 =   BitmapFactory.decodeResource(r,R.drawable.keyfg32x53);

	// キートップ色
	keyPaint = new Paint();
	keyPaint.setAntiAlias(true);
        keyPaint.setTextSize(largeKeyTextSize);
        keyPaint.setColor(0xff000000); // argb 黒
	smallKeyPaint = new Paint();
	smallKeyPaint.setAntiAlias(true);
	smallKeyPaint.setTextSize(smallKeyTextSize);
        smallKeyPaint.setColor(0xff000000); // 黒

	// 背景色
	buttonPaint = new Paint();
        buttonPaint.setColor(0xffc0c0c0);

	// 候補ボタンのテキスト色
	buttonTextPaint = new Paint();
	buttonTextPaint.setAntiAlias(true);
        buttonTextPaint.setTextSize(buttonTextSize);
        buttonTextPaint.setColor(0xff000000); // 黒

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

    public void draw(Key _keypat[], Key _selectedKey, Key _selectedKey2, boolean _showCand){
	keypat = _keypat;
	selectedKey = _selectedKey;
	selectedKey2 = _selectedKey2;
	showCand = _showCand;
	invalidate();
    }

    private void layoutCandButtons(){
	float x, y, w, h;   // 候補ボタンの矩形
	int i;
	int line = 0;
	x = buttonMarginX;
	for(i=0;i<candButtons.length;i++){
	    CandButton button = candButtons[i];
	    String s = button.text;
	    if(s == "") break;
	    float textWidth = buttonTextPaint.measureText(s);
	    h = buttonHeight;
	    w = textWidth + ((float)buttonMarginX * 2); // ボタン幅
	    if(x + w + buttonMarginX > keyViewWidth){
		x = buttonMarginX;
		if(++line >= 3) break; // 候補は3行まで
	    }
	    y = buttonMarginY + line * (buttonHeight+buttonMarginY);
	    button.rect.pos.x = (int)x;
	    button.rect.pos.y = (int)y;
	    button.rect.size.w = (int)w;
	    button.rect.size.h = (int)h;
	    button.visible = true;
	    x += (w + buttonMarginX);
	}
	for(;i<candButtons.length;i++){
	    CandButton button = candButtons[i];
	    button.visible = false;
	}
    }

    @Override public void onDraw(Canvas canvas) {
	Bitmap image;
			    
	if(keypat == null) keypat = keys.keypat0; // Viewを作った瞬間はkeypatが設定されてない

	//Log.v("Slime","onDraw - length="+keypat.length);

	canvas.drawColor(0xfff0f0f0);
	for(int i=0;i<keypat.length;i++){
	    Key key = keypat[i];
	    Paint paint = (key.rect.size.w == 32 ? smallKeyPaint : keyPaint);
	    FontMetrics fontMetrics = paint.getFontMetrics();
	    float ascent = fontMetrics.ascent; // これはマイナス値
	    float descent = fontMetrics.descent;
	    float shadewidth = 6;

	    image = ((selectedKey != null && key.str == selectedKey.str) ||
		     (selectedKey2 != null && key.str == selectedKey2.str)
		     ?
		     (key.rect.size.w == 32 ? keyfg32x53 :
		      (key.rect.size.h == 106 ?
		       (key.rect.size.w == 106 ? keyfg106x106 : keyfg53x106) :
		       (key.rect.size.w == 106 ? keyfg106x53 : keyfg53x53)
		       )
		      )
		     :
		     (key.rect.size.w == 32 ? keybg32x53 :
		      (key.rect.size.h == 106 ?
		       (key.rect.size.w == 106 ? keybg106x106 : keybg53x106) :
		       (key.rect.size.w == 106 ? keybg106x53 : keybg53x53)
		       )
		      )
		     );
	    canvas.drawBitmap(image,key.rect.pos.x,key.rect.pos.y,null);
	    //
	    // キー文字描画
	    //
	    float textWidth = paint.measureText(key.str);
	    float baseX = key.rect.pos.x + (key.rect.size.w-shadewidth - textWidth)/2;
	    float baseY = key.rect.pos.y + (key.rect.size.h-shadewidth - (ascent+descent))/2;
	    canvas.drawText(key.str,baseX,baseY,paint);
	}
	if(showCand){
	    FontMetrics fontMetrics = buttonTextPaint.getFontMetrics();
	    float ascent = fontMetrics.ascent;
	    float descent = fontMetrics.descent;
	    layoutCandButtons();
	    for(int i=0;i<candButtons.length && candButtons[i].visible;i++){
		CandButton button = candButtons[i];
		canvas.drawRect((float)button.rect.pos.x,
				(float)button.rect.pos.y,
				(float)(button.rect.pos.x+button.rect.size.w),
				(float)(button.rect.pos.y+button.rect.size.h),
				buttonPaint);
		canvas.drawText(button.text,
				button.rect.pos.x+buttonTextMargin,
				button.rect.pos.y + (buttonHeight-(ascent+descent))/2,
				buttonTextPaint);
	    }
	}
    }

    ///////////////////////////////////////////////////////////////////////////////////////

    public void setButton(String word, int index){
	CandButton button = candButtons[index];
	button.text = word;
    }

    ///////////////////////////////////////////////////////////////////////////////////////

    // よくわからないがこれを設定するとViewの大きさを決められるようだ...
    @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(320,216);
    }
}

