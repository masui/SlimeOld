//
//	Slime for Android
//
//	Toshiyuki Masui 2010/9/22 (Lexierra.Android)
//      Slimeとして頑張る 2011/11/20
//

package com.pitecan.slime;

import android.inputmethodservice.InputMethodService;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;

import android.os.Bundle;
import android.os.Build; // for Build.VERSION.SDK_INT
import android.view.View;
import android.view.KeyEvent;
import android.widget.Button;
import android.util.Log;

import android.widget.AbsoluteLayout; // これがあるとコンパイラに文句を言われる

import android.content.Context;
import android.text.ClipboardManager;
// Android3.0以上の場合こちら
// import android.content.ClipData;
// import android.content.ClipboardManager;


public class Slime extends InputMethodService 
{
    private Keys keys;
    private KeyView keyView;
    private KeyController keyController;
    private LocalDict dict;
    private SQLDict sqlDict;
    private Search search;

    private ClipboardManager cm;
    private String clipboardText;

    static final int MAXCANDS = 50;

    /**
     * Main initialization of the input method component.  Be sure to call
     * to super class.
     */
    @Override
    public void onCreate()
    {
        super.onCreate();
	dict = new LocalDict(getResources().getAssets());
	sqlDict = new SQLDict(this);
	search = new Search(dict,sqlDict,this);
	cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
	CharSequence seq = cm.getText();
	clipboardText = (seq == null ? "" : seq.toString());

	//
	// IMEで「戻るボタン」を扱えるようにするためのもの
	// android-11 以降のAPIじゃないと動かない模様
	//
	// Log.v("Slime","version="+Build.VERSION.SDK_INT);
	if(Build.VERSION.SDK_INT >= 11){
	    setBackDisposition(InputMethodService.BACK_DISPOSITION_WILL_NOT_DISMISS);
	}
    }

    /**
     * This is the point where you can do all of your UI initialization.  It
     * is called after creation and any configuration change.
     */
    @Override public void onInitializeInterface() {
	super.onInitializeInterface(); // 必要??
	keys = new Keys();
	keyController = new KeyController();
    }
    
    /**
     * This is the main point where we do our initialization of the input method
     * to begin operating on an application.  At this point we have been
     * bound to the client, and are now receiving all of the detailed information
     * about the target of our edits.
     */
    @Override public void onStartInput(EditorInfo attribute, boolean restarting) {
        super.onStartInput(attribute, restarting);
    }

    /**
     * Called by the framework when your view for creating input needs to
     * be generated.  This will be called the first time your input method
     * is displayed, and every time it needs to be re-created such as due to
     * a configuration change.
     */
    @Override public View onCreateInputView() {
	super.onCreateInputView(); // 必要??
	/*
	  KeyControllerクラスから他にアクセスできるようにセットする
	 */
        keyView = (KeyView) getLayoutInflater().inflate(R.layout.input, null);
	keyView.keys = keys;
	keyView.keyController = keyController;
	keyController.search = search;
	keyController.keyView = keyView;
	keyController.keys = keys;
	keyController.dict = dict;
	keyController.sqlDict = sqlDict;
	keyController.slime = this;
	keyController.reset();
        return keyView;
    }

    @Override public void onStartInputView(EditorInfo info, boolean restarting) {
	super.onStartInputView(info,restarting); // 必要??
	//
	// 別アプリに切り替わったときなど初期化する
	// InputConnection ic = getCurrentInputConnection();
	// if(ic != null) ic.commitText("",1);
	// !!!! このコードがあるせいでブラウザのURL枠をタップしたときURLが消えてしまっていた
	// 何故こういうコードを書いたのか全く覚えていない。 (2012/12/13 21:40:11)
	//

	onFinishInput();
	keyController.resetInput();
    }

    @Override public void onFinishInput() {
	super.onFinishInput();
    }

    public void input(String s){
	InputConnection ic = getCurrentInputConnection();
	if(ic != null) ic.commitText(s,1); // 入力貼り付け
    }

    public void keyDownUp(int keyEventCode) { // キー入力
	InputConnection ic = getCurrentInputConnection();
	if(ic != null){
	    ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, keyEventCode));
	    ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, keyEventCode));
	}
    }

    public void showComposingText(){
	String composingText = "";
	for(int i=0;i<keyController.inputPatArray.size();i++){
	    //Log.v("Slime","pat=" + keyController.inputPatArray.get(i));
	    // 曖昧キーの場合 "[" と "]" で囲む
	    if(keyController.inputPatArray.get(i).matches(".*\\[..*")){
		composingText += "(" + keyController.inputCharArray.get(i) + ")";
	    }
	    else {
		composingText += keyController.inputCharArray.get(i);
	    }
	}
	InputConnection ic = getCurrentInputConnection();
	if(ic != null){
	    ic.setComposingText(composingText,1);
	}
    }

    public void hide(){
	requestHideSelf(0); // IMEのViewを隠す
    }

    //
    // 新規登録用にクリップボードの単語を返す
    //
    public void clearRegWord(){
	CharSequence seq = cm.getText();
	clipboardText = (seq == null ? "" : seq.toString());
    }

    public String getRegWord(){
	CharSequence seq = cm.getText();
	String s = (seq == null ? "" : seq.toString());
	if(s.equals(clipboardText)){
	    return "";
	}
	else {
	    return s;
	}
    }

    // Backボタン処理
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
	//Log.v("Slime","onKeyDown - keyCode = "+keyCode);
	if(Build.VERSION.SDK_INT < 11 || keyCode != KeyEvent.KEYCODE_BACK || !isInputViewShown()){
	    return super.onKeyDown(keyCode,event);
	}
	else {
	    keyController.backKey();
	    return true;
	}
    }
}
