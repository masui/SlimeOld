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
    private KeyView keyView;
    private Keys keys;
    private Dict dict;
    private Slime slime;
    public void setKeyView(KeyView _keyView){ keyView = _keyView; }
    public void setKeys(Keys _keys){ keys = _keys; }
    public void setDict(Dict _dict){ dict = _dict; }
    public void setSlime(Slime _slime){ slime = _slime; }

    private float mousex, mousey;      // タッチ座標
    private float downx, downy;        // 最初にタッチしたときの座標

    private String[][] keypat;          // 現在のキー配列
    private boolean keyPressed = false;
    private int selectedKey = -1;
    private int selectedCand = -1;
    private boolean shifted = false;

    // タイマ処理用
    Handler handler = new Handler();
    Runnable keyDisp;

    public ArrayList<String> inputPatArray;
    public ArrayList<String> inputCharArray;

    public KeyController() {
	inputPatArray = new ArrayList<String>();
	inputCharArray = new ArrayList<String>();
    }

    //
    // 座標からキー番号を計算
    //
    private int findKey(String[][] pat, float x, float y){
	for(int i=0;i<24;i++){
	    int[] rect = keys.keypos[i];
	    int rectx = rect[0];
	    int recty = rect[1];
	    int rectwidth = rect[2];
	    int rectheight = rect[3];
	    if(rectx <= x && x < rectx+rectwidth  &&
	       recty <= y && y < recty+rectheight &&
	       pat[i][0] != ""){
		return i;
	    }
	}
	return -1;
    }

    //
    // 座標から候補ボタン番号を計算
    //
    private int findCand(float x, float y){
	for(int i=0;i<20;i++){
	    CandButton button = keyView.candButtons[i];
	    if(button.x <= x && x < button.x+button.w  &&
	       button.y <= y && y < button.y+button.h &&
	       button.text != "" && button.visible){
		return i;
	    }
	}
	return -1;
    }

    private void shift(){
	shifted = true;
	handler.removeCallbacks(keyDisp);
	switch(findKey(keys.keypat0, downx, downy)){
	case 12: keypat = keys.keypat1;  break;
	case 13: keypat = keys.keypat2;  break;
	case 14: keypat = keys.keypat3;  break;
	case 15: keypat = keys.keypat4;  break;
	case 16: keypat = keys.keypat5;  break;
	case 17: keypat = keys.keypatbs; break;
	case 18: keypat = keys.keypat6;  break;
	case 19: keypat = keys.keypat7;  break;
	case 20: keypat = keys.keypat8;  break;
	case 21: keypat = keys.keypat9;  break;
	case 22: keypat = keys.keypat10; break;
	case 23: keypat = keys.keypatsp; break;
	}
	keyView.draw(keypat, findKey(keypat, mousex, mousey), -1, shifted);
    }

    //
    // タッチのメイン処理
    //
    public boolean onTouchEvent(MotionEvent ev) {
	int action = ev.getAction();
	int i = 0;
	switch (action & MotionEvent.ACTION_MASK) {
	case MotionEvent.ACTION_UP:
	case MotionEvent.ACTION_POINTER_UP:
	    if(selectedKey >= 0){ // 入力文字処理
		processKey(keypat[selectedKey][0],keypat[selectedKey][1]);
	    }
	    else if(selectedCand >= 0){ // 候補選択
		fix(keyView.candButtons[selectedCand].text);
	    }
	    else { // 何もないところをタップしたらキーを隠す
		if(keyView.candButtons[0].text == ""){
		    slime.hide();
		}
	    }
	    handler.removeCallbacks(keyDisp);
	    keypat = keys.keypat0;
	    shifted = false;
	    keyPressed = false;
	    keyView.draw(keypat, -1, -1, shifted);
	    break;
	case MotionEvent.ACTION_MOVE:
	    if(!keyPressed) break;
	case MotionEvent.ACTION_DOWN:
	case MotionEvent.ACTION_POINTER_DOWN:
	    mousex = ev.getX(i);
	    mousey = ev.getY(i);

	    selectedKey = findKey(keypat, mousex, mousey);
	    selectedCand = findCand(mousex, mousey);
	    if(!shifted && keyPressed &&
		    (mousex - downx) * (mousex - downx) +
		    (mousey - downy) * (mousey - downy) >= 30.0 * 30.0){
		shift();
	    }
	    if(keyPressed){ // 五十音文字ドラッグ状態
		keyView.draw(keypat, selectedKey, selectedCand, shifted);
	    }
	    else if(! keyPressed){
		if(selectedKey >= 0){ // 五十音キーを押した
		    downx = mousex;
		    downy = mousey;
		    //
		    // タイマを設定
		    // http://www.bpsinc.jp/blog/archives/1342
		    // http://magpad.jugem.jp/?eid=108
		    //
		    // Handler reference
		    // http://developer.android.com/reference/android/os/Handler.html
		    //
		    keyDisp = new Runnable(){
			    public void run() {
				shift();
			    }
			};
		    handler.postDelayed(keyDisp,300);
		    keyPressed = true;
		}
		keyView.draw(keypat, selectedKey, selectedCand, shifted);
	    }
	    break;
	}
	return true;
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
    
    private void processKey(String c, String p){
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
