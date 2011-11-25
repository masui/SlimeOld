//
//	キーと候補を描画するIMEのビュー
//
package com.pitecan.slime;

import android.view.View;
import android.view.MotionEvent;
import android.graphics.Color;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
    private Bitmap keybg, keyfg;
    private float mousex, mousey;
    private Paint keyPaint;
    private Paint buttonPaint;
    private Paint buttonTextPaint;

    public Keys keys;
    public KeyController keyController = null;
    private Key[] keypat = null;
    private Key selectedKey;
    private Key selectedKey2;
    private boolean shifted;

    public CandButton[] candButtons;

    public KeyView(Context context, AttributeSet attrs) {
	super(context,attrs);

	// キー画像の読み込み
	Resources r=context.getResources();
	keybg=BitmapFactory.decodeResource(r,R.drawable.keybg);
	keyfg=BitmapFactory.decodeResource(r,R.drawable.keyfg);

	// キートップ色
	keyPaint = new Paint();
	keyPaint.setAntiAlias(true);
        keyPaint.setTextSize(36);
        keyPaint.setColor(0xff000000); // argb 黒

	// 背景色
	buttonPaint = new Paint();
        buttonPaint.setColor(0xffc0c0c0);

	// 候補ボタンのテキスト色
	buttonTextPaint = new Paint();
	buttonTextPaint.setAntiAlias(true);
        buttonTextPaint.setTextSize(18);
        buttonTextPaint.setColor(0xff000000); // 黒

	// 候補「ボタン」の初期化
	candButtons = new CandButton[20];
	for(int i=0;i<20;i++){
	    CandButton button = new CandButton();
	    candButtons[i] = button;
	}
    }

    // keyControllerに丸投げする
    public boolean onTouchEvent(MotionEvent ev) {
	if(keyController != null){
	    keyController.onTouchEvent(ev);
	}
	return true;
    }

    public void draw(Key _keypat[], Key _selectedKey, Key _selectedKey2, boolean _shifted){
	keypat = _keypat;
	selectedKey = _selectedKey;
	selectedKey2 = _selectedKey2;
	shifted = _shifted;
	invalidate();
    }

    private void layoutCandButtons(){
	int x, y, w, h;
	String s;
	int i;
	x = 5; y = 6;
	for(i=0;i<20;i++){
	    CandButton button = candButtons[i];
	    s = button.text;
	    if(s == "") break;
	    h = 28;
	    w = s.length() * 15 + 20;
	    if(x + w > 315){
		x = 5;
		y += 34;
	    }
	    button.rect.pos.x = x;
	    button.rect.pos.y = y;
	    button.rect.size.w = w;
	    button.rect.size.h = h;
	    if(y >= 6+34*3) break;
	    button.visible = true;
	    x += (w + 5);
	    //Log.v("Slime","Layout "+x+" "+y);
	}
	for(;i<20;i++){
	    CandButton button = candButtons[i];
	    button.visible = false;
	}
    }

    @Override public void onDraw(Canvas canvas) {
	Bitmap image;
			    
	if(keypat == null) keypat = keys.keypat0; // Viewを作った瞬間はkeypatが設定されてない

	Log.v("Slime","onDraw - length="+keypat.length);

	canvas.drawColor(0xfff0f0f0);
	for(int i=0;i<keypat.length;i++){
	    Key key = keypat[i];
	    image = ((selectedKey != null && key.str == selectedKey.str) ||
		     (selectedKey2 != null && key.str == selectedKey2.str) ?
		     keyfg : keybg);
	    canvas.drawBitmap(image,key.rect.pos.x,key.rect.pos.y,null);
	    canvas.drawText(key.str,key.rect.pos.x+10,key.rect.pos.y+40,keyPaint);
	}
	if(!shifted){
	    layoutCandButtons();
	    for(int i=0;i<20 && candButtons[i].visible;i++){
		CandButton button = candButtons[i];
		canvas.drawRect((float)button.rect.pos.x,(float)button.rect.pos.y,
				(float)(button.rect.pos.x+button.rect.size.w),(float)(button.rect.pos.y+button.rect.size.h),
				buttonPaint);
		canvas.drawText(button.text,button.rect.pos.x+7,button.rect.pos.y+19,buttonTextPaint);
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

