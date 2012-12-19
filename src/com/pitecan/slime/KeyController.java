//
//	キー操作に対するアクションメインルーチン
//
package com.pitecan.slime;

import android.view.MotionEvent;
import android.view.KeyEvent;
import android.graphics.Canvas;
import android.graphics.Rect;

import android.content.res.Resources;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;

import java.util.regex.Pattern;

import android.os.Handler;

// 候補に重みづけするための比較ルーチン
class CandidateComparator implements java.util.Comparator {
    public int compare(Object o1, Object o2){
	int w1 = ((Candidate)o1).weight;
	int w2 = ((Candidate)o2).weight;
	int l1 = ((Candidate)o1).word.length();
	int l2 = ((Candidate)o2).word.length();
	if(w1 != w2) return w1 - w2;
	return l1 - l2;
    }
}

class KeyController {
    // これらはSlime.onCreateInputView()でセットされる
    public KeyView keyView;
    public Keys keys;
    public LocalDict dict;
    public SQLDict sqlDict;
    public Search search;
    public Slime slime;

    public static Key[] keypat;          // 現在のキー配列
    private int selectedCand = -1;
    private Key downKey = null;
    private Key selectedKey = null;
    private Key secondKey = null;
    public static int candPage = 1;   // 候補の何ページ目か

    private SearchTask searchTask = null;

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
	    if(keypat[i].rect.contains(x,y)){
		return keypat[i];
	    }
	}
	return null;
    }

    //
    // 座標から候補ボタン番号を計算
    //
    private int findCand(int x, int y){
	// Log.v("Slime","findCand("+x+","+y+")");
	for(int i=0;i<keyView.candButtons.length;i++){
	    if(! keyView.candButtons[i].visible) continue;
	    Rect rect = keyView.candButtons[i].rect;
	    int xx = rect.left;
	    int yy = rect.top;
	    int ww = rect.right - rect.left;
	    int hh = rect.bottom - rect.top;
	    if(xx <= 5){
		xx -= 5;
		ww += (5+3);
	    }
	    else {
		xx -= 2;
		ww += (2+3);
	    }
	    if(yy <= 6){
		yy -= 6;
		hh += (6+3);
	    }
	    else {
		yy -= 3;
		hh += (3+3);
	    }
	    Rect extendedRect = new Rect(xx,yy,xx+ww,yy+hh);
	    // Log.v("Slime","extendedrect = "+extendedRect.pos.x+", "+extendedRect.pos.y+", "+extendedRect.size.w+", "+extendedRect.size.h);
	    if(extendedRect.contains(x,y)) return i;
	}
	return -1;
    }

    enum Event { UP1, UP2, DOWN1, DOWN2, MOVE, SHIFTTIMER, SHIFTLOCKTIMER }; // MOVE1, MOVE2の区別がつかないかも
    enum State { STATE0, STATE1, STATE2, STATE3, STATE4, STATE5, STATE6, STATE7, STATEC, STATEFB };
    // STATE0 初期状態
    // STATE1 タップしたときの状態
    // STATE2 スライド後またはタイムアウト後
    private State state = State.STATE0;

    private float mousex, mousey;      // タッチ座標
    private float mousex2, mousey2;    // (Desireのバグ回避用)
    private float downx, downy;        // 最初にタッチしたときの座標

    // タイマ処理用
    Handler shiftTimeoutHandler = new Handler();
    Runnable shiftTimeout;
    private final int SHIFTTIME = 150;      // 長押しタイムアウト

    Handler shiftLockTimeoutHandler = new Handler();
    Runnable shiftLockTimeout;
    private final int SHIFTLOCKTIME = 1500; // 英数字シフトロックタイムアウト
    
    public boolean googleDisplayed = false; // Google検索中を示す表示
    public boolean useGoogle = false;       // GoogleSuggestを呼ぶかどうか

    //
    // 戻るキーが押されたとき
    //
    public void backKey(){
	//Log.v("Slime","BACKKEY state = "+state);
	if(state == State.STATE7){
	    // 英数字モードのときは日本語モードに戻る
	    reset();
	}
	else {
	    slime.hide();
	}
    }

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

	int pointerIndex = ev.findPointerIndex(pointerId);
	//Log.v("Slime-ontouch","count="+pointerCount+", actionindex="+actionIndex+", pointerid="+pointerId+", pointerIndex="+pointerIndex+", action="+action);

	mousex = ev.getX(pointerIndex) / keyView.expand;
	mousey = ev.getY(pointerIndex) / keyView.expand;
	//Log.v("Slime","mousex="+mousex+", mousey="+mousey);

	switch (action & MotionEvent.ACTION_MASK) {
	case MotionEvent.ACTION_DOWN:
	case MotionEvent.ACTION_POINTER_DOWN:
	    mousex2 = mousex;
	    mousey2 = mousey;
	    if(pointerId == 0){
		// Log.v("Slime","DOWN1 - "+mousex);
		trans(Event.DOWN1);
	    }
	    else {
		// Log.v("Slime","DOWN2 - "+mousex);
		trans(Event.DOWN2);
	    }
	    break;
	case MotionEvent.ACTION_UP:
	case MotionEvent.ACTION_POINTER_UP:
	    if(pointerId == 0){
		// Log.v("Slime","UP1 - "+mousex);
		trans(Event.UP1);
	    }
	    else {
		// Log.v("Slime","UP2 - "+mousex);
		trans(Event.UP2);
	    }
	    break;
	case MotionEvent.ACTION_MOVE:
	    mousex2 = mousex;
	    mousey2 = mousey;
	    // このあたり微妙に機種依存してるかも
	    if(pointerCount == 2){ // 2本指でタッチしているときは2本目の座標を取得
		// mousex = ev.getX(1);
		mousex = ev.getX(1) / keyView.expand;
		// mousey = ev.getY(1);
		mousey = ev.getY(1) / keyView.expand;
	    }
	    // Log.v("Slime","MOVE - "+mousex);
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
	//Log.v("Slime","trans - selectedKey = "+selectedKey);
	switch(state){
	case STATE0:
	    //Log.v("Slime","STATE0 e="+e);
	    switch(e){
	    case DOWN1:
		downx = mousex;
		downy = mousey;
		downKey = findKey(keypat, (int)downx, (int)downy);
		// Log.v("Slime","downKey... downkey="+downKey);
		if(downKey != null){ // キーの上を押した
		    if(downKey.str == "→"){
			// 候補が1面しか無くて「→」が押されたときだけGoogleSuggestを呼ぶようにする
			if(!useGoogle && !googleDisplayed){
			    useGoogle = true;

			    if(keyView.candLines >= 3){
				candPage++;
				//Log.v("Slime","Draw in trans");
				keyView.draw(keypat, downKey, null, candPage);
			    }
			    else {
				keyView.draw2(keypat, downKey, null, candPage);
				searchAndDispCand();
			    }
			}
			else {
			    candPage++;
			    keyView.draw(keypat, downKey, null, candPage);
			}
			state = State.STATEFB;
		    }
		    else if(downKey.str == "←" && candPage > 1){
			if(candPage > 1) candPage--;
			keyView.draw(keypat, downKey, null, candPage);
			state = State.STATEFB;
		    }
		    else {
			keyView.draw(keypat, downKey, null, 0);
			// タイマ設定
			shiftTimeout = new Runnable(){
				public void run() {
				    trans(Event.SHIFTTIMER);
				}
			    };
			// 長押し判定時間
			// 以前は300msだったが長すぎたので変更 (2012/12/10 22:20:13)
			shiftTimeoutHandler.postDelayed(shiftTimeout,SHIFTTIME);
			state = State.STATE1;
		    }
		}
		else { // 候補の上かも
		    state = State.STATEC;
		}
	    }
	    break;
	case STATEFB:
	    switch(e){
	    case UP1:
		keyView.draw(keypat, null, null, candPage);
		state = State.STATE0;
	    }
	    break;
	case STATEC:
	    selectedCand = findCand((int)mousex, (int)mousey);
	    switch(e){
	    case UP1:
		if(selectedCand >= 0){ // 候補選択
		    searchTask.cancel(true); // !!!!!
		    fix(keyView.candButtons[selectedCand].text,
			keyView.candButtons[selectedCand].pat);

		    searchTask = new SearchTask(keyView,useGoogle);
		    searchTask.execute("","");
		}
		else {
		    // if(keyView.candButtons[0].text == ""){ // 何もないところをタップしたらキーを隠す
		    // slime.hide();
		    // }
		}
		LocalDict.exactMode = false;
		keypat = keys.keypat0;
		if(selectedCand >= 0)
		    keyView.draw(keypat, null, null, 0);
		else
		    keyView.draw(keypat, null, null, candPage);
		state = State.STATE0;
		break;
	    }
	    break;
	case STATE1:
	    //Log.v("Slime","STATE1 e="+e);
	    switch(e){
	    case UP1:
		//Log.v("Slime","STATE1-UP1");
		if(selectedKey != null){ // 入力文字処理
		    processKey(selectedKey);
		}
		keypat = keys.keypat0;
		keyView.draw(keypat, null, null, candPage);
		state = State.STATE0;
		shiftTimeoutHandler.removeCallbacks(shiftTimeout);
		break;
	    case MOVE:
		//Log.v("Slime","STATE1-MOVE");
		if(Math.hypot(mousex-downx, mousey-downy) >= 20.0){
		    keypat = downKey.shiftKeypat;

		    downKey = findKey(keypat, (int)downx, (int)downy);
		    keypat = downKey.shiftKeypat; //!!!!!

		    selectedKey = findKey(keypat, (int)mousex, (int)mousey);
		    keyView.draw(keypat, selectedKey, null, 0);
		    state = State.STATE3;
		    shiftTimeoutHandler.removeCallbacks(shiftTimeout);
		}
		break;
	    case DOWN2:
		//Log.v("Slime","STATE1-DOWN2");
		keypat = downKey.shiftKeypat;
		downKey = findKey(keypat, (int)downx, (int)downy);
		selectedKey = findKey(keypat, (int)mousex, (int)mousey);
		secondKey = selectedKey;
		//Log.v("Slime","STATE1-DOWN2-secondkey="+secondKey);
		keyView.draw(keypat, downKey, selectedKey, 0);
		state = State.STATE4;
		shiftTimeoutHandler.removeCallbacks(shiftTimeout);
		break;
	    case SHIFTTIMER:
		//Log.v("Slime","STATE1-TIMER");
		keypat = downKey.shiftKeypat;
		downKey = findKey(keypat, (int)downx, (int)downy);
		keyView.draw(keypat, downKey, null, 0);
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
		keyView.draw(keypat, null, null, candPage);
		state = State.STATE0;
		break;
	    case DOWN2:
		keyView.draw(keypat, downKey, selectedKey, 0);
		secondKey = selectedKey;
		// 2本指で英数字をタッチしたときはロックモードに移行する
		if(secondKey != null && Pattern.matches("[a-zA-Z0-9]",secondKey.str)){
		    state = State.STATE6;
		}
		else {
		    state = State.STATE4;
		}
		break;
	    case MOVE:
		if(Math.hypot(mousex-downx, mousey-downy) >= 20.0){
		    downKey = findKey(keypat, (int)downx, (int)downy);
		    keypat = downKey.shiftKeypat;

		    selectedKey = findKey(keypat, (int)mousex, (int)mousey);
		    keyView.draw(keypat, selectedKey, null, 0);
		    state = State.STATE3;
		}
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
		if(selectedKey != null && Pattern.matches("[a-zA-Z0-9]",selectedKey.str)){
		    shiftLockTimeout = new Runnable(){
			    public void run() {
				trans(Event.SHIFTLOCKTIMER);
			    }
			};
		    shiftLockTimeoutHandler.postDelayed(shiftLockTimeout,SHIFTLOCKTIME);
		    keyView.draw(keypat, null, null, 0);
		    state = State.STATE7;
		}
		else {
		    keypat = keys.keypat0;
		    keyView.draw(keypat, null, null, candPage);
		    state = State.STATE0;
		}
		break;
	    case MOVE:
		keyView.draw(keypat, selectedKey, null, 0);
		break;
	    }
	    break;
	case STATE4:
	    //Log.v("Slime","STATE4 e="+e);
	    //Log.v("Slime","STATE4 secondKey="+secondKey);
	    switch(e){
	    case UP1:
		//Log.v("Slime","STATE4 UP1 e="+e);
		//Log.v("Slime","STATE4 UP1 secondKey="+secondKey);
		if(secondKey != null){ // 入力文字処理
		    processKey(secondKey);
		}
		keypat = keys.keypat0;
		keyView.draw(keypat, null, null, candPage);
		state = State.STATE0;
		break;
	    case UP2:
		if(selectedKey != null){ // 入力文字処理
		    processKey(selectedKey);
		}
		keyView.draw(keypat, downKey, null, 0);
		state = State.STATE5;
		break;
	    case MOVE:
		keyView.draw(keypat, downKey, selectedKey, 0);
		// secondKey = selectedKey; !!!!!!!
		break;
	    }
	    break;
	case STATE5:
	    switch(e){
	    case UP1:
		keypat = keys.keypat0;
		keyView.draw(keypat, null, null, candPage);
		state = State.STATE0;
		break;
	    case DOWN2:
		keyView.draw(keypat, downKey, selectedKey, 0);
		state = State.STATE6;
		break;
	    case MOVE:
		break;
	    }
	    break;
	case STATE6:
	    switch(e){
	    case UP1:
	    case UP2:
		if(selectedKey != null){ // 入力文字処理
		    processKey(selectedKey);
		}

		shiftLockTimeout = new Runnable(){
			public void run() {
			    trans(Event.SHIFTLOCKTIMER);
			}
		    };
		shiftLockTimeoutHandler.postDelayed(shiftLockTimeout,SHIFTLOCKTIME);

		keyView.draw(keypat, downKey, null, 0);
		state = State.STATE7;
		break;
	    case MOVE:
		keyView.draw(keypat, downKey, selectedKey, 0);
		secondKey = selectedKey;
		break;
	    }
	    break;
	case STATE7:
	    switch(e){
	    case DOWN1:
	    case DOWN2:
		shiftLockTimeoutHandler.removeCallbacks(shiftLockTimeout);
		keyView.draw(keypat, downKey, selectedKey, 0);
		secondKey = selectedKey;
		state = State.STATE6;
		break;
	    case SHIFTLOCKTIMER:
		keypat = keys.keypat0;
		keyView.draw(keypat, null, null, candPage);
		state = State.STATE0;
		break;
	    }
	    break;
	}
    }

    //
    // 文字が入力されたときの処理
    // 独立させたいものだが
    //
    public void resetInput(){
	inputPatArray = new ArrayList<String>();
	inputCharArray = new ArrayList<String>();
	for(int i=0;i<keyView.candButtons.length;i++){
	    keyView.candButtons[i].text = "";
	    keyView.candButtons[i].visible = false;
	}
	candPage = 0;
	googleDisplayed = false;
	useGoogle = false;
    }

    public void reset(){
	shiftLockTimeoutHandler.removeCallbacks(shiftLockTimeout);
	shiftTimeoutHandler.removeCallbacks(shiftTimeout);
	state = State.STATE0;
	resetInput();
	keypat = keys.keypat0;
	keyView.draw(keypat, null, null, 0);
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
	searchTask = new SearchTask(keyView,useGoogle);
	searchTask.execute(inputPat(),inputWord());
    }

    private void processKey(Key key){
	//Log.v("Slime","ProcessKey");
	if(searchTask != null){
	    boolean cancelres =
	    searchTask.cancel(true);
	    //Log.v("Slime","onTouchEvent - cancel res="+cancelres);
	}

	String c = key.str;
	String p = key.pat;
	int inputlen = inputCharArray.size();
	boolean toExact = false;
	if(c == "←"){
	    googleDisplayed = false;
	    useGoogle = false;
	    if(inputlen == 0){
                slime.keyDownUp(KeyEvent.KEYCODE_DEL);
	    }
	    else {
		inputCharArray.remove(inputlen-1);
		inputPatArray.remove(inputlen-1);
		if(inputlen > 1){
		    searchAndDispCand();
		}
		else {
		    resetInput();
		    keyView.draw(keypat, null, null, 0);
		}
		state = State.STATE0;
		slime.showComposingText();
	    }
	}
	else if(c == "↴"){
	    if(inputlen == 0){
                slime.keyDownUp(KeyEvent.KEYCODE_ENTER);
		resetInput();
	    }
	    else {
		if(LocalDict.exactMode){
		    fix(inputWord(),inputPat());
		    resetInput();
		}
		else {
		    toExact = true;
		    LocalDict.exactMode = true;
		    searchAndDispCand();
		}
	    }
	}
	else if(keypat == keys.keypatbs || keypat == keys.keypatsp || c.matches("[a-zA-Z0-9]") || key.pat == ""){
	    if(inputlen != 0){
		fix(inputWord(),inputPat());
	    }
	    fix(c,inputPat());
	    resetInput();
	}
	else { // 普通の文字入力
	    if(LocalDict.exactMode){
		LocalDict.exactMode = false;
		fix(inputWord(),inputPat());
		resetInput();
	    }
	    inputPatArray.add(p);
	    inputCharArray.add(c);
	    searchAndDispCand();
	    slime.showComposingText();
	}
	LocalDict.exactMode = toExact;
    }

    public void fix(String s,String p){
	sqlDict.add(s,p);
	sqlDict.limit(1000); // 1000個以上になれば古いエントリを消す
	slime.input(s);
	resetInput();
	slime.clearRegWord();
    }
}
