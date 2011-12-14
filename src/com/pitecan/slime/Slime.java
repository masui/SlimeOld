//
//	Slime for Android
//
//	Toshiyuki Masui 2010/9/22 (Lexierra.Android)
//      Slime��Ȓ����ƒ��ĥ��� 2011/11/20
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

import android.widget.AbsoluteLayout; // �����쒤������뒤Ȓ�����ђ����钤˒ʸ��璤����쒤�

import android.content.Context;
import android.text.ClipboardManager;
// Android3.0��ʒ�咤Β�쒹璤�������
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
	  KeyController�����钥�������¾��˒�������������ǒ����뒤蒤���˒����Ò�Ȓ�����
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
	getCurrentInputConnection().commitText(s,1); // �����ϒŽ����Ւ��
    }

    public void keyDownUp(int keyEventCode) { // ������������
        getCurrentInputConnection().sendKeyEvent(
                new KeyEvent(KeyEvent.ACTION_DOWN, keyEventCode));
        getCurrentInputConnection().sendKeyEvent(
                new KeyEvent(KeyEvent.ACTION_UP, keyEventCode));
    }

    public void showComposingText(){
	String composingText = "";
	for(int i=0;i<keyController.inputPatArray.size();i++){
	    //Log.v("Slime","pat=" + keyController.inputPatArray.get(i));
	    // �ۣ��撥������Β�쒹� "[" ��� "]" ��ǒ�ϒ��
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
	requestHideSelf(0); // IME���View��򒱣���
    }

    //
    // ��������ВϿ��ђ�˒����꒥Ò�ג�ܒ����ɒ�Βñ��쒤��֒��
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
