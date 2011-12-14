//
//	Slime for Android
//
//	Toshiyuki Masui 2010/9/22 (Lexierra.Android)
//      Slime’¤È’¤·’¤Æ’´è’Ä¥’¤ë 2011/11/20
//

package com.pitecan.slime;

import android.inputmethodservice.InputMethodService;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;

import android.os.Bundle;
import android.view.View;
import android.view.KeyEvent;
import android.widget.Button;
import android.util.Log;

import android.widget.AbsoluteLayout; // ’¤³’¤ì’¤¬’¤¢’¤ë’¤È’¥³’¥ó’¥Ñ’¥¤’¥é’¤Ë’Ê¸’¶ç’¤ò’¸À’¤ï’¤ì’¤ë

import android.content.Context;
import android.text.ClipboardManager;
// Android3.0’°Ê’¾å’¤Î’¾ì’¹ç’¤³’¤Á’¤é
// import android.content.ClipData;
// import android.content.ClipboardManager;


public class Slime extends InputMethodService 
{
    private Keys keys;
    private KeyView keyView;
    private KeyController keyController;
    private Dict dict;

    private SQLDict sqlDict;
    private ClipboardManager cm;
    private String clipboardText;

    static final int MAXCANDS = 20;

    /**
     * Main initialization of the input method component.  Be sure to call
     * to super class.
     */
    @Override
    public void onCreate()
    {
        super.onCreate();
	dict = new Dict(getResources().getAssets());
	sqlDict = new SQLDict(this);
	cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
	clipboardText = cm.getText().toString();
    }

    /**
     * This is the point where you can do all of your UI initialization.  It
     * is called after creation and any configuration change.
     */
    @Override public void onInitializeInterface() {
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
	/*
	  KeyController’¥¯’¥é’¥¹’¤«’¤é’Â¾’¤Ë’¥¢’¥¯’¥»’¥¹’¤Ç’¤­’¤ë’¤è’¤¦’¤Ë’¥»’¥Ã’¥È’¤¹’¤ë
	 */
        keyView = (KeyView) getLayoutInflater().inflate(R.layout.input, null);
	keyView.keys = keys;
	keyView.keyController = keyController;
	keyController.keyView = keyView;
	keyController.keys = keys;
	keyController.dict = dict;
	keyController.sqlDict = sqlDict;
	keyController.slime = this;
	keyController.reset();
        return keyView;
    }

    @Override public void onStartInputView(EditorInfo info, boolean restarting) {
    }

    @Override public void onFinishInput() {
	super.onFinishInput();
    }

    public void input(String s){
	getCurrentInputConnection().commitText(s,1); // ’Æþ’ÎÏ’Å½’¤ê’ÉÕ’¤±
    }

    public void keyDownUp(int keyEventCode) { // ’¥­’¡¼’Æþ’ÎÏ
        getCurrentInputConnection().sendKeyEvent(
                new KeyEvent(KeyEvent.ACTION_DOWN, keyEventCode));
        getCurrentInputConnection().sendKeyEvent(
                new KeyEvent(KeyEvent.ACTION_UP, keyEventCode));
    }

    public void showComposingText(){
	String composingText = "";
	for(int i=0;i<keyController.inputPatArray.size();i++){
	    //Log.v("Slime","pat=" + keyController.inputPatArray.get(i));
	    // ’Û£’Ëæ’¥­’¡¼’¤Î’¾ì’¹ç "[" ’¤È "]" ’¤Ç’°Ï’¤à
	    if(keyController.inputPatArray.get(i).matches(".*\\[..*")){
		composingText += "(" + keyController.inputCharArray.get(i) + ")";
	    }
	    else {
		composingText += keyController.inputCharArray.get(i);
	    }
	}
	getCurrentInputConnection().setComposingText(composingText,1);
    }

    public void hide(){
	requestHideSelf(0); // IME’¤ÎView’¤ò’±£’¤¹
    }

    //
    // ’¿·’µ¬’ÅÐ’Ï¿’ÍÑ’¤Ë’¥¯’¥ê’¥Ã’¥×’¥Ü’¡¼’¥É’¤Î’Ã±’¸ì’¤ò’ÊÖ’¤¹
    //
    public void clearRegWord(){
	clipboardText = cm.getText().toString();
    }

    public String getRegWord(){
	String s = cm.getText().toString();
	if(s.equals(clipboardText)){
	    return "";
	}
	else {
	    return s;
	}
    }
}
