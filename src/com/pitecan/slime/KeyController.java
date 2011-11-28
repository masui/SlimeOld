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
    private int selectedCand = -1;
    private Key downKey = null;
    private Key selectedKey = null;
    private Key secondKey = null;

    public ArrayList<String> inputPatArray;
    public ArrayList<String> inputCharArray;

    public KeyController() {
	inputPatArray = new ArrayList<String>();
	inputCharArray = new ArrayList<String>();
    }

    //
    // 座標からキー番号を計算
    //
    private Key findKey(Key[] keypat, int x, int y){
	for(int i=0;i<keypat.length;i++){
	    if(keypat[i].rect.in(x,y)){
		return keypat[i];
	    }
	}
	return null;
    }

    //
    // 座標から候補ボタン番号を計算
    //
    private int findCand(int x, int y){
	for(int i=0;i<keyView.candButtons.length;i++){
	    if(! keyView.candButtons[i].visible) continue;
	    if(keyView.candButtons[i].rect.in(x,y)) return i;
	}
	return -1;
    }

    enum Event { UP1, UP2, DOWN1, DOWN2, MOVE, SHIFTTIMER }; // MOVE1, MOVE2の区別がつかないかも
    enum State { STATE0, STATE1, STATE2, STATE3, STATE4, STATE5, STATEC };
    // STATE0 初期状態
    // STATE1 タップしたときの状態
    // STATE2 スライド後またはタイムアウト後
    private State state = State.STATE0;

    private float mousex, mousey;      // タッチ座標
    private float downx, downy;        // 最初にタッチしたときの座標

    // タイマ処理用
    Handler shiftTimeoutHandler = new Handler();
    Runnable shiftTimeout;

    //
    // タッチイベント処理
    //
    public boolean onTouchEvent(MotionEvent ev) {
	int action = ev.getAction();
	int pointerCount = ev.getPointerCount();      // マルチタッチの数
	int actionIndex = ev.getActionIndex();        // 今回のイベントのアクション番号 (0〜pointerCount-1)
	int pointerId = ev.getPointerId(actionIndex);
	// 1タッチ目か2タッチ目かの番号のはずなのだがスライドイベントだとうまくいかない?
	// 2タッチ目の指をスライドさせてもpointerIdが0のままになってしまうっぽい
	// 2タッチでスライドがあれば全部2タッチ目のスライドとして扱うことにする

	//Log.v("Slime-ontouch","actionindex="+actionIndex+", pointerid="+pointerId+", action="+action);

	mousex = ev.getX(pointerId);
	mousey = ev.getY(pointerId);
	switch (action & MotionEvent.ACTION_MASK) {
	case MotionEvent.ACTION_DOWN:
	case MotionEvent.ACTION_POINTER_DOWN:
	    if(pointerId == 0){
		//Log.v("Slime","DOWN1 - "+mousex);
		trans(Event.DOWN1);
	    }
	    else {
		//Log.v("Slime","DOWN2 - "+mousex);
		trans(Event.DOWN2);
	    }
	    break;
	case MotionEvent.ACTION_UP:
	case MotionEvent.ACTION_POINTER_UP:
	    if(pointerId == 0){
		//Log.v("Slime","UP1 - "+mousex);
		trans(Event.UP1);
	    }
	    else {
		//Log.v("Slime","UP2 - "+mousex);
		trans(Event.UP2);
	    }
	    break;
	case MotionEvent.ACTION_MOVE:
	    if(pointerCount == 2){ // 2本指でタッチしているときは2本目の座標を取得
		mousex = ev.getX(1);
		mousey = ev.getY(1);
	    }
	    //Log.v("Slime","MOVE - "+mousex);
	    trans(Event.MOVE);
	    break;
	}
	return true;
    }

    //
    // 状態遷移本体
    //
    private void trans(Event e){
	selectedKey = findKey(keypat, (int)mousex, (int)mousey);
	switch(state){
	case STATE0:
	    switch(e){
	    case DOWN1:
		downx = mousex;
		downy = mousey;
		downKey = findKey(keypat, (int)downx, (int)downy);
		if(downKey != null){ // キーの上を押した
		    keyView.draw(keypat, downKey, null, false);
		    // タイマ設定
		    shiftTimeout = new Runnable(){
			    public void run() {
				trans(Event.SHIFTTIMER);
			    }
			};
		    shiftTimeoutHandler.postDelayed(shiftTimeout,300);
		    state = State.STATE1;
		}
		else { // 候補の上かも
		    state = State.STATEC;
		}
	    }
	    break;
	case STATEC:
	    selectedCand = findCand((int)mousex, (int)mousey);
	    switch(e){
	    case UP1:
		if(selectedCand >= 0){ // 候補選択
		    fix(keyView.candButtons[selectedCand].text);
		}
		else { // 何もないところをタップしたらキーを隠す
		    if(keyView.candButtons[0].text == ""){
			slime.hide();
		    }
		}
		Dict.exactMode = false;
		keypat = keys.keypat0;
		keyView.draw(keypat, null, null, false);
		state = State.STATE0;
	    }
	    break;
	case STATE1:
	    switch(e){
	    case UP1:
		if(selectedKey != null){ // 入力文字処理
		    processKey(selectedKey);
		}
		keypat = keys.keypat0;
		keyView.draw(keypat, null, null, true);
		state = State.STATE0;
		shiftTimeoutHandler.removeCallbacks(shiftTimeout);
		break;
	    case MOVE:
		if(Math.hypot(mousex-downx, mousey-downy) >= 30.0){
		    keypat = downKey.shiftKeypat;

		    downKey = findKey(keypat, (int)downx, (int)downy);
		    keypat = downKey.shiftKeypat; //!!!!!

		    selectedKey = findKey(keypat, (int)mousex, (int)mousey);
		    keyView.draw(keypat, selectedKey, null, false);
		    state = State.STATE3;
		    shiftTimeoutHandler.removeCallbacks(shiftTimeout);
		}
		break;
	    case DOWN2:
		keypat = downKey.shiftKeypat;
		downKey = findKey(keypat, (int)downx, (int)downy);
		selectedKey = findKey(keypat, (int)mousex, (int)mousey);
		secondKey = selectedKey;
		keyView.draw(keypat, downKey, selectedKey, false);
		state = State.STATE4;
		shiftTimeoutHandler.removeCallbacks(shiftTimeout);
		break;
	    case SHIFTTIMER:
		keypat = downKey.shiftKeypat;
		downKey = findKey(keypat, (int)downx, (int)downy);
		keyView.draw(keypat, downKey, null, false);
		state = State.STATE2;
		shiftTimeoutHandler.removeCallbacks(shiftTimeout);
		break;
	    }
	    break;
	case STATE2:
	    switch(e){
	    case UP1:
		if(selectedKey != null){ // 入力文字処理
		    processKey(selectedKey);
		}
		keypat = keys.keypat0;
		keyView.draw(keypat, null, null, true);
		state = State.STATE0;
		break;
	    case DOWN2:
		keyView.draw(keypat, downKey, selectedKey, false);
		secondKey = selectedKey;
		state = State.STATE4;
		break;
	    case MOVE:
		if(Math.hypot(mousex-downx, mousey-downy) >= 30.0){
		    downKey = findKey(keypat, (int)downx, (int)downy);
		    keypat = downKey.shiftKeypat;

		    selectedKey = findKey(keypat, (int)mousex, (int)mousey);
		    keyView.draw(keypat, selectedKey, null, false);
		    state = State.STATE3;
		}
		//keyView.draw(keypat, selectedKey, null, true);
		break;
	    }
	    break;
	case STATE3:
	    switch(e){
	    case UP1:
	    case UP2:
		if(selectedKey != null){ // 入力文字処理
		    processKey(selectedKey);
		}
		keypat = keys.keypat0;
		keyView.draw(keypat, null, null, true);
		state = State.STATE0;
		break;
	    case MOVE:
		keyView.draw(keypat, selectedKey, null, false);
		break;
	    }
	    break;
	case STATE4:
	    switch(e){
	    case UP1:
		if(secondKey != null){ // 入力文字処理
		    processKey(secondKey);
		}
		keypat = keys.keypat0;
		keyView.draw(keypat, null, null, true);
		state = State.STATE0;
		break;
	    case UP2:
		if(selectedKey != null){ // 入力文字処理
		    processKey(selectedKey);
		}
		keyView.draw(keypat, downKey, null, false);
		state = State.STATE5;
		break;
	    case MOVE:
		keyView.draw(keypat, downKey, selectedKey, false);
		secondKey = selectedKey;
		break;
	    }
	    break;
	case STATE5:
	    switch(e){
	    case UP1:
		keypat = keys.keypat0;
		keyView.draw(keypat, null, null, true);
		state = State.STATE0;
		break;
	    case DOWN2:
		keyView.draw(keypat, downKey, selectedKey, false);
		state = State.STATE4;
		break;
	    case MOVE:
		/*
		selectedKey = findKey(keypat, (int)down, (int)down);
		keyView.draw(keypat, selectedKey, null, true);
		*/
		break;
	    }
	    break;
	}
    }

    //
    // 文字が入力されたときの処理
    // 独立させたいものだが
    //

    private void resetInput(){
	inputPatArray = new ArrayList<String>();
	inputCharArray = new ArrayList<String>();
	for(int i=0;i<keyView.candButtons.length;i++){
	    keyView.candButtons[i].text = "";
	    keyView.candButtons[i].visible = false;
	}
    }

    public void reset(){
	resetInput();
	keypat = keys.keypat0;
	keyView.draw(keypat, null, null, false);
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

    // ひらがな ⇒カタカナ
    // http://www7a.biglobe.ne.jp/~java-master/samples/string/ZenkakuHiraganaToZenkakuKatakana.html
    private String h2k(String s){
	StringBuffer sb = new StringBuffer(s);
	for (int i = 0; i < sb.length(); i++) {
	    char c = sb.charAt(i);
	    if (c >= 'ぁ' && c <= 'ん') {
		sb.setCharAt(i, (char)(c - 'ぁ' + 'ァ'));
	    }
	}
	return sb.toString();    
    }

    private void searchAndDispCand(){
	int i=0;
	int buttonInd = 0;
	dict.search(inputPat());
	if(Dict.exactMode){
	    String hira = inputWord();
	    keyView.candButtons[buttonInd++].text = hira;
	    keyView.candButtons[buttonInd++].text = h2k(hira);
	}
	if(dict.ncands > 0){
	    for(;buttonInd<keyView.candButtons.length && i <dict.ncands;i++,buttonInd++){
		keyView.candButtons[buttonInd].text = dict.candWords[i];
	    }
	}
	if(buttonInd < keyView.candButtons.length){ // まだ余裕あり
	    String[] suggestions = GoogleSuggest.suggest(inputWord());
	    for(i=0;buttonInd < keyView.candButtons.length && suggestions[i] != "";i++,buttonInd++){
		keyView.candButtons[buttonInd].text = suggestions[i];
	    }
	}
	for(;buttonInd<keyView.candButtons.length;buttonInd++){
	    keyView.candButtons[buttonInd].text = "";
	}
    }
    
    private void processKey(Key key){
	String c = key.str;
	String p = key.pat;
	int inputlen = inputCharArray.size();
	boolean toExact = false;
	if(c == "←"){
	    if(inputlen == 0){
                slime.keyDownUp(KeyEvent.KEYCODE_DEL);
	    }
	    else {
		inputCharArray.remove(inputlen-1);
		inputPatArray.remove(inputlen-1);
		searchAndDispCand();
		state = State.STATE0;
		keyView.draw(keypat, null, null, false);
		slime.showComposingText();
	    }
	}
	else if(c == "↴"){
	    if(inputlen == 0){
                slime.keyDownUp(KeyEvent.KEYCODE_ENTER);
		resetInput();
	    }
	    else {
		if(Dict.exactMode){
		    fix(inputWord());
		    resetInput();
		}
		else {
		    toExact = true;
		    Dict.exactMode = true;
		    searchAndDispCand();
		}
	    }
	}
	else if(keypat == keys.keypatbs || keypat == keys.keypatsp || c.matches("[a-zA-Z0-9]") || key.pat == ""){
	    if(inputlen != 0){
		fix(inputWord());
	    }
	    fix(c);
	    resetInput();
	}
	else {
	    inputPatArray.add(p);
	    inputCharArray.add(c);
	    searchAndDispCand();
	    slime.showComposingText();
	}
	Dict.exactMode = toExact;
    }

    public void fix(String s){
	slime.input(s);
	resetInput();
    }
}
