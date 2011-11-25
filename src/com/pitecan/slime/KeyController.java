//
//	キー操作に対するアクションメインルーチン
//
package com.pitecan.slime;

import android.view.MotionEvent;
import android.graphics.Canvas;
import android.view.KeyEvent;

import android.content.res.Resources;
import android.util.Log;

import java.util.ArrayList;

import android.os.Handler;

class KeyController {
    // これらはSlime.onCreateInputView()でセットされる
    public KeyView keyView;
    public Keys keys;
    public Dict dict;
    public Slime slime;

    private Key[] keypat;          // 現在のキー配列
    private boolean keyPressed = false;
    private int selectedKey = -1;
    private int selectedCand = -1;
    private boolean shifted = false;

    public ArrayList<String> inputPatArray;
    public ArrayList<String> inputCharArray;

    public KeyController() {
	inputPatArray = new ArrayList<String>();
	inputCharArray = new ArrayList<String>();
    }

    //
    // 座標からキー番号を計算
    //
    private int findKey(Key[] keypat, int x, int y){
	for(int i=0;i<keypat.length;i++){
	    if(keypat[i].rect.in(x,y)){
		Log.v("Slime","findKey - key="+i);
		return i;
	    }
	}
	return -1;
    }

    //
    // 座標から候補ボタン番号を計算
    //
    private int findCand(int x, int y){
	for(int i=0;i<20;i++){
	    if(keyView.candButtons[i].rect.in(x,y)) return i;
	}
	return -1;
    }

    enum Event { UP, DOWN, MOVE, SHIFTTIMER };
    enum State { STATE0, STATE1, STATE2 };
    // STATE0 初期状態
    // STATE1 タップしたときの状態
    // STATE2 スライド後またはタイムアウト後
    private State state = State.STATE0;

    private float mousex, mousey;      // タッチ座標
    private float downx, downy;        // 最初にタッチしたときの座標

    // タイマ処理用
    Handler shiftTimeoutHandler = new Handler();
    Runnable shiftTimeout;

    private void shift(){
	shiftTimeoutHandler.removeCallbacks(shiftTimeout);
	switch(findKey(keys.keypat0, (int)downx, (int)downy)){
	case 0: keypat = keys.keypat1;  break;
	case 1: keypat = keys.keypat2;  break;
	case 2: keypat = keys.keypat3;  break;
	case 3: keypat = keys.keypat4;  break;
	case 4: keypat = keys.keypat5;  break;
	case 5: keypat = keys.keypatbs; break;
	case 6: keypat = keys.keypat6;  break;
	case 7: keypat = keys.keypat7;  break;
	case 8: keypat = keys.keypat8;  break;
	case 9: keypat = keys.keypat9;  break;
	case 10: keypat = keys.keypat10; break;
	case 11: keypat = keys.keypatsp; break;
	}
	keyView.draw(keypat, findKey(keypat, (int)mousex, (int)mousey), -1, true);
	state = State.STATE2;
    }

    private void up(){
	shiftTimeoutHandler.removeCallbacks(shiftTimeout);
	selectedKey = findKey(keypat, (int)mousex, (int)mousey);
	selectedCand = findCand((int)mousex, (int)mousey);
	if(selectedKey >= 0){ // 入力文字処理
	    processKey(keypat[selectedKey]);
	}
	else if(selectedCand >= 0){ // 候補選択
	    fix(keyView.candButtons[selectedCand].text);
	}
	else { // 何もないところをタップしたらキーを隠す
	    if(keyView.candButtons[0].text == ""){
		slime.hide();
	    }
	}
	keypat = keys.keypat0;
	keyView.draw(keypat, -1, -1, false);
	state = State.STATE0;
    }

    //
    // タッチイベント処理
    //
    public boolean onTouchEvent(MotionEvent ev) {
	int action = ev.getAction();
	int pointerCount = ev.getPointerCount();      // マルチタッチの数
	int actionIndex = ev.getActionIndex();        // 今回のアクション番号 (0〜pointerCount-1)
	int pointerId = ev.getPointerId(actionIndex); // 1タッチ目か2タッチ目かの番号のはずなのだがスライドイベントだとうまくいかない?

	Log.v("Slime-ontouch","actionindex="+actionIndex+", pointerid="+pointerId+", action="+action);

	/*
	switch (action & MotionEvent.ACTION_MASK) {
	case MotionEvent.ACTION_DOWN:
	    Log.v("Slime-event", "Action_Down"); break;
	case MotionEvent.ACTION_POINTER_DOWN:
	    Log.v("Slime-event", "Action_PointerDown"); break;
	case MotionEvent.ACTION_UP:
	    Log.v("Slime-event", "Action_Up"); break;
	case MotionEvent.ACTION_POINTER_UP:
	    Log.v("Slime-event", "Action_PointerUp"); break;
	case MotionEvent.ACTION_MOVE:
	    Log.v("Slime-event", "Action_MOVE"); break;
	}

	//Log.v("Slime-TouchEvent", "X:" + ev.getX() + ",Y:" + ev.getY());
	int pointerCount = ev.getPointerCount();
 
	//イベントの発生時刻
	//Log.d("Slime-TouchEvent", "event time: "+ ev.getEventTime());
 
	//ポインタIDの取得、ポインタ座標
	for (int p = 0; p < pointerCount; p++) {
	    Log.d("Slime-TouchEvent", "Pointer ID :"+ ev.getPointerId(p) +
		  " X " + ev.getX(p) + " , " +
		  "Y " + ev.getY(p) + " , " );
	}
	*/

	return true;

	/*
	mousex = ev.getX(0);
	mousey = ev.getY(0);
	switch (action & MotionEvent.ACTION_MASK) {
	case MotionEvent.ACTION_DOWN:
	case MotionEvent.ACTION_POINTER_DOWN:
	    trans(Event.DOWN); break;
	case MotionEvent.ACTION_UP:
	case MotionEvent.ACTION_POINTER_UP:
	    trans(Event.UP); break;
	case MotionEvent.ACTION_MOVE:
	    trans(Event.MOVE); break;
	}
	return true;
	*/
    }

    //
    // 状態遷移本体
    //
    private void trans(Event e){
	switch(state){
	case STATE0:
	    switch(e){
	    case DOWN:
		downx = mousex;
		downy = mousey;
		selectedKey = findKey(keypat, (int)mousex, (int)mousey);
		selectedCand = findCand((int)mousex, (int)mousey);
		Log.v("Slime","STATE0: selectedkey="+selectedKey);
		// タイマ設定
		shiftTimeout = new Runnable(){
			public void run() {
			    trans(Event.SHIFTTIMER);
			}
		    };
		keyView.draw(keypat, selectedKey, selectedCand, false);
		shiftTimeoutHandler.postDelayed(shiftTimeout,300);
		state = State.STATE1;
		Log.v("Slime","STATE0!!!!: selectedkey="+selectedKey);
	    }
	    break;
	case STATE1:
	    switch(e){
	    case UP:
		up();
		break;
	    case MOVE:
		selectedKey = findKey(keypat, (int)mousex, (int)mousey);
		selectedCand = findCand((int)mousex, (int)mousey);
		if((mousex - downx) * (mousex - downx) +
		   (mousey - downy) * (mousey - downy) >= 30.0 * 30.0){
		    shift();
		}
		break;
	    case SHIFTTIMER:
		shift();
		break;
	    }
	    break;
	case STATE2:
	    switch(e){
	    case UP:
		up();
		break;
	    case MOVE:
		selectedKey = findKey(keypat, (int)mousex, (int)mousey);
		selectedCand = findCand((int)mousex, (int)mousey);
		keyView.draw(keypat, selectedKey, selectedCand, true);
		break;
	    }
	}
    }

    //
    // 文字が入力されたときの処理
    // 独立させたいものだが
    //

    public void reset(){
	inputPatArray = new ArrayList<String>();
	inputCharArray = new ArrayList<String>();
	for(int i=0;i<20;i++){
	    keyView.candButtons[i].text = "";
	    keyView.candButtons[i].visible = false;
	}
	keypat = keys.keypat0;
	keyView.draw(keypat, -1, -1, shifted);
    }

    public String inputPat(){
	String pat = "";
	for(int i=0;i<inputPatArray.size();i++){
	    pat = pat + inputPatArray.get(i);
	}
	return pat;
    }
    
    public String inputWord(){
	String s = "";
	for(int i=0;i<inputCharArray.size();i++){
	    s = s + inputCharArray.get(i);
	}
	return s;
    }

    private void searchAndDispCand(){
	int i=0;
	dict.search(inputPat());
	keyView.setButton(inputWord(),i++);
	if(dict.ncands > 0){
	    for(;i<20-1 && i-1 <dict.ncands;i++){
		keyView.setButton(dict.candWords[i-1],i);
	    }
	}
	keyView.draw(keypat, -1, -1, shifted);
    }
    
    private void processKey(Key key){
	String c = key.str;
	String p = key.pat;
	int inputlen = inputCharArray.size();
	if(c == "←"){
	    if(inputlen == 0){
                slime.keyDownUp(KeyEvent.KEYCODE_DEL);
	    }
	    else {
		inputCharArray.remove(inputlen-1);
		inputPatArray.remove(inputlen-1);
		searchAndDispCand();
		slime.showComposingText();
	    }
	}
	else if(c == "↴"){
	    if(inputlen == 0){
                slime.keyDownUp(KeyEvent.KEYCODE_ENTER);
	    }
	    else {
		fix(inputWord());
	    }
	    reset();
	}
	else if(keypat == keys.keypatbs || keypat == keys.keypatsp ||
		c.matches("[a-zA-Z0-9]")){
	    if(inputlen != 0){
		fix(inputWord());
	    }
	    fix(c);
	    reset();
	}
	else {
	    inputPatArray.add(p);
	    inputCharArray.add(c);
	    searchAndDispCand();
	    slime.showComposingText();
	}
    }

    public void fix(String s){
	slime.input(s);
	reset();
    }
}
