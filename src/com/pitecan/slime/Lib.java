//
//	
//
package com.pitecan.slime;

class Position {
    int x,y;
    public Position(int _x,int _y){
	x = _x;
	y = _y;
    }
}

class Size {
    int w,h;
    public Size(int _w,int _h){
	w = _w;
	h = _h;
    }
}

class Rectangle {
    Position pos;
    Size size;
    public Rectangle(int _x,int _y, int _w, int _h){
	pos = new Position(_x,_y);
	size = new Size(_w,_h);
    }
    public boolean in(int x,int y){
	return x >= pos.x && x <= pos.x + size.w &&
	    y >= pos.y && y <= pos.y + size.h ;
    }
}
