//
//	
//
package com.pitecan.slime;

class Position {
    int x,y;
    public Position(int x,int y){
	this.x = x;
	this.y = y;
    }
}

class Size {
    int w,h;
    public Size(int w,int h){
	this.w = w;
	this.h = h;
    }
}

class Rectangle {
    Position pos;
    Size size;
    public Rectangle(int x,int y, int w, int h){
	pos = new Position(x,y);
	size = new Size(w,h);
    }
    public boolean in(int x,int y){
	return x >= pos.x && x <= pos.x + size.w &&
	    y >= pos.y && y <= pos.y + size.h ;
    }
}
