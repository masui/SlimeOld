package com.pitecan.slime;

import android.util.Log;

public class CandButton {
    public int x,y,w,h;
    String text;
    boolean visible;
    public void setText(String _text){ text = _text; }
    public void setVisible(boolean _visible){ visible = _visible; }

    public CandButton() {
	text = "";
	visible = false;
    }
}
