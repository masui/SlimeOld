package com.pitecan.slime;

import android.util.Log;

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

class Key {
    Rectangle rect;
    String str;
    String pat;
    public Key(int _x,int _y,int _w,int _h,String _str,String _pat){
	rect = new Rectangle(_x,_y,_w,_h);
	str = _str;
	pat = _pat;
    }
}

class Keys {
    /*
	{1,     3, 53, 53}, {54,    3, 53, 53}, {107,   3, 53, 53}, {160,   3, 53, 53}, {213,   3, 53, 53}, {267,   3, 53, 53},
	{1,    56, 53, 53}, {54,   56, 53, 53}, {107,  56, 53, 53}, {160,  56, 53, 53}, {213,  56, 53, 53}, {267,  56, 53, 53},
	{1,   109, 53, 53}, {54,  109, 53, 53}, {107, 109, 53, 53}, {160, 109, 53, 53}, {213, 109, 53, 53}, {267, 109, 53, 53},
	{1,   162, 53, 53}, {54,  162, 53, 53}, {107, 162, 53, 53}, {160, 162, 53, 53}, {213, 162, 53, 53}, {267, 162, 53, 53}
    };
    */
    public final Key[] keypat0 = {
	new Key(1,   109, 53, 53, "あ", "[aiueoAIUEO]"),
	new Key(54,  109, 53, 53, "か", "[kg][aiueo]"),
	new Key(107, 109, 53, 53, "さ", "[sz][aiueo]"),
	new Key(160, 109, 53, 53, "た", "[tdT][aiueo]"),
	new Key(213, 109, 53, 53, "は", "[hbp][aiueo]"),
	new Key(267, 109, 53, 53, "←", ""),
	new Key(1,   162, 53, 53, "な", "n[aiueo]"),
	new Key(54,  162, 53, 53, "ま", "m[aiueo]"),
	new Key(107, 162, 53, 53, "や", "[yY][auo]"),
	new Key(160, 162, 53, 53, "ら", "r[aiueo]"),
	new Key(213, 162, 53, 53, "わ", "[wWN]"),
	new Key(267, 162, 53, 53, "↴", ""),
    };
    public final Key[] keypat1 =  {
	new Key(1,   56,  53, 53, "ぁ", "A"),
	new Key(54,  56,  53, 53, "ぃ", "I"),
	new Key(107, 56,  53, 53, "ぅ", "U"),
	new Key(160, 56,  53, 53, "ぇ", "E"),
	new Key(213, 56,  53, 53, "ぉ", "O"),
	new Key(1,   109, 53, 53, "あ", "a"),
	new Key(54,  109, 53, 53, "い", "i"),
	new Key(107, 109, 53, 53, "う", "u"),
	new Key(160, 109, 53, 53, "え", "e"),
	new Key(213, 109, 53, 53, "お", "o"),
    };
    public final Key[] keypat2 =  {
	new Key(1,   109, 53, 53, "か", "ka"),
	new Key(54,  109, 53, 53, "き", "ki"),
	new Key(107, 109, 53, 53, "く", "ku"),
	new Key(160, 109, 53, 53, "け", "ke"),
	new Key(213, 109, 53, 53, "こ", "ko"),
	new Key(1,   162, 53, 53, "が", "ga"),
	new Key(54,  162, 53, 53, "ぎ", "gi"),
	new Key(107, 162, 53, 53, "ぐ", "gu"),
	new Key(160, 162, 53, 53, "げ", "ge"),
	new Key(213, 162, 53, 53, "ご", "go"),
    };
    public final Key[] keypat3 =  {
	new Key(1,   109, 53, 53, "さ", "sa"),
	new Key(54,  109, 53, 53, "し", "si"),
	new Key(107, 109, 53, 53, "す", "su"),
	new Key(160, 109, 53, 53, "せ", "se"),
	new Key(213, 109, 53, 53, "そ", "so"),
	new Key(1,   162, 53, 53, "ざ", "za"),
	new Key(54,  162, 53, 53, "じ", "zi"),
	new Key(107, 162, 53, 53, "ず", "zu"),
	new Key(160, 162, 53, 53, "ぜ", "ze"),
	new Key(213, 162, 53, 53, "ぞ", "zo"),
    };
    public final Key[] keypat4 =  {
	new Key(107, 56,  53, 53, "っ", "Tu"),
	new Key(1,   109, 53, 53, "た", "ta"),
	new Key(54,  109, 53, 53, "ち", "ti"),
	new Key(107, 109, 53, 53, "つ", "tu"),
	new Key(160, 109, 53, 53, "て", "te"),
	new Key(213, 109, 53, 53, "と", "to"),
	new Key(1,   162, 53, 53, "だ", "da"),
	new Key(54,  162, 53, 53, "ぢ", "di"),
	new Key(107, 162, 53, 53, "づ", "du"),
	new Key(160, 162, 53, 53, "で", "de"),
	new Key(213, 162, 53, 53, "ど", "do"), 
    };
    public final Key[] keypat5 =  {
	new Key(1,   56,  53, 53, "ぱ", "pa"),
	new Key(54,  56,  53, 53, "ぴ", "pi"),
	new Key(107, 56,  53, 53, "ぷ", "pu"),
	new Key(160, 56,  53, 53, "ぺ", "pe"),
	new Key(213, 56,  53, 53, "ぽ", "po"),
	new Key(1,   109, 53, 53, "は", "ha"),
	new Key(54,  109, 53, 53, "ひ", "hi"),
	new Key(107, 109, 53, 53, "ふ", "hu"),
	new Key(160, 109, 53, 53, "へ", "he"),
	new Key(213, 109, 53, 53, "ほ", "ho"),
	new Key(1,   162, 53, 53, "ば", "ba"),
	new Key(54,  162, 53, 53, "び", "bi"),
	new Key(107, 162, 53, 53, "ぶ", "bu"),
	new Key(160, 162, 53, 53, "べ", "be"),
	new Key(213, 162, 53, 53, "ぼ", "bo"),
    };
    public final Key[] keypat6 =  {
	new Key(1,   56,  53, 53, "5", "5"),
	new Key(54,  56,  53, 53, "6", "6"),
	new Key(107, 56,  53, 53, "7", "7"),
	new Key(160, 56,  53, 53, "8", "8"),
	new Key(213, 56,  53, 53, "9", "9"),
	new Key(1,   109, 53, 53, "0", "0"),
	new Key(54,  109, 53, 53, "1", "1"),
	new Key(107, 109, 53, 53, "2", "2"),
	new Key(160, 109, 53, 53, "3", "3"),
	new Key(213, 109, 53, 53, "4", "4"),
	new Key(1,   162, 53, 53, "な", "na"),
	new Key(54,  162, 53, 53, "に", "ni"),
	new Key(107, 162, 53, 53, "ぬ", "nu"),
	new Key(160, 162, 53, 53, "ね", "ne"),
	new Key(213, 162, 53, 53, "の", "no"),
    };
    public final Key[] keypat7 =  {
	new Key(1,   3,   53, 53, "q", "q"),
	new Key(54,  3,   53, 53, "w", "w"),
	new Key(107, 3,   53, 53, "e", "e"),
	new Key(160, 3,   53, 53, "r", "r"),
	new Key(213, 3,   53, 53, "t", "t"),
	new Key(1,   56,  53, 53, "a", "a"),
	new Key(54,  56,  53, 53, "s", "s"),
	new Key(107, 56,  53, 53, "d", "d"),
	new Key(160, 56,  53, 53, "f", "f"),
	new Key(213, 56,  53, 53, "g", "g"),
	new Key(1,   109, 53, 53, "z", "z"),
	new Key(54,  109, 53, 53, "x", "x"),
	new Key(107, 109, 53, 53, "c", "c"),
	new Key(160, 109, 53, 53, "v", "v"),
	new Key(213, 109, 53, 53, "b", "b"),
	new Key(1,   162, 53, 53, "ま", "ma"),
	new Key(54,  162, 53, 53, "み", "mi"),
	new Key(107, 162, 53, 53, "む", "mu"),
	new Key(160, 162, 53, 53, "め", "me"),
	new Key(213, 162, 53, 53, "も", "mo"),
    };
    public final Key[] keypat8 =  {
	new Key(1,   3,   53, 53, "y", "y"),
	new Key(54,  3,   53, 53, "u", "u"),
	new Key(107, 3,   53, 53, "i", "i"),
	new Key(160, 3,   53, 53, "o", "o"),
	new Key(213, 3,   53, 53, "p", "p"),
	new Key(1,   56,  53, 53, "h", "h"),
	new Key(54,  56,  53, 53, "j", "j"),
	new Key(107, 56,  53, 53, "k", "k"),
	new Key(160, 56,  53, 53, "l", "l"),
	new Key(1,   109, 53, 53, "n", "n"),
	new Key(54,  109, 53, 53, "m", "m"),
	new Key(107, 109, 53, 53, "ゃ", "Ya"),
	new Key(160, 109, 53, 53, "ゅ", "Yu"),
	new Key(213, 109, 53, 53, "ょ", "Yo"),
	new Key(1,   162, 53, 53, "や", "ya"),
	new Key(54,  162, 53, 53, "や", "ya"),
	new Key(107, 162, 53, 53, "や", "ya"),
	new Key(160, 162, 53, 53, "ゆ", "yu"),
	new Key(213, 162, 53, 53, "よ", "yo"),
    };
    public final Key[] keypat9 =  {
	new Key(1,   3,   53, 53, "Q", "Q"),
	new Key(54,  3,   53, 53, "W", "W"),
	new Key(107, 3,   53, 53, "E", "E"),
	new Key(160, 3,   53, 53, "R", "R"),
	new Key(213, 3,   53, 53, "T", "T"),
	new Key(1,   56,  53, 53, "A", "A"),
	new Key(54,  56,  53, 53, "S", "S"),
	new Key(107, 56,  53, 53, "D", "D"),
	new Key(160, 56,  53, 53, "F", "F"),
	new Key(213, 56,  53, 53, "G", "G"),
	new Key(1,   109, 53, 53, "Z", "Z"),
	new Key(54,  109, 53, 53, "X", "X"),
	new Key(107, 109, 53, 53, "C", "C"),
	new Key(160, 109, 53, 53, "V", "V"),
	new Key(213, 109, 53, 53, "B", "B"),
	new Key(1,   162, 53, 53, "ら", "ra"),
	new Key(54,  162, 53, 53, "り", "ri"),
	new Key(107, 162, 53, 53, "る", "ru"),
	new Key(160, 162, 53, 53, "れ", "re"),
	new Key(213, 162, 53, 53, "ろ", "ro"),
    };
    public final Key[] keypat10 = {
	new Key(1,   3,   53, 53, "Y", "Y"),
	new Key(54,  3,   53, 53, "U", "U"),
	new Key(107, 3,   53, 53, "I", "I"),
	new Key(160, 3,   53, 53, "O", "O"),
	new Key(213, 3,   53, 53, "P", "P"),
	new Key(1,   56,  53, 53, "H", "H"),
	new Key(54,  56,  53, 53, "J", "J"),
	new Key(107, 56,  53, 53, "K", "K"),
	new Key(160, 56,  53, 53, "L", "L"),
	new Key(1,   109, 53, 53, "N", "N"),
	new Key(54,  109, 53, 53, "M", "M"),
	new Key(160, 109, 53, 53, "ゎ", ""),
	new Key(213, 109, 53, 53, "を", "W"),
	new Key(1,   162, 53, 53, "わ", "W"),
	new Key(54,  162, 53, 53, "ー", "-"),
	new Key(107, 162, 53, 53, "ー", "-"),
	new Key(160, 162, 53, 53, "わ", "w"),
	new Key(213, 162, 53, 53, "ん", "N"),
    };
    public final Key[] keypatbs = {
	new Key(54,  3,   53, 53, "", ""),
	new Key(107, 3,   53, 53, "`", ""),
	new Key(160, 3,   53, 53, "~", ""),
	new Key(213, 3,   53, 53, "|", ""),
	new Key(267, 3,   53, 53, "_", ""),
	new Key(1,   56,  53, 53, "<", ""),
	new Key(54,  56,  53, 53, ">", ""),
	new Key(107, 56,  53, 53, "[", ""),
	new Key(160, 56,  53, 53, "]", ""),
	new Key(213, 56,  53, 53, "{", ""),
	new Key(267, 56,  53, 53, "}", ""),
	new Key(1,   109, 53, 53, "/", ""),
	new Key(54,  109, 53, 53, "\\", ""),
	new Key(107, 109, 53, 53, ":", ""),
	new Key(160, 109, 53, 53, ";", ""),
	new Key(213, 109, 53, 53, "'", "'"),
	new Key(267, 109, 53, 53, "←", ""),
	new Key(107, 162, 53, 53, "+", ""),
	new Key(160, 162, 53, 53, "-", ""),
	new Key(213, 162, 53, 53, "=", ""),
	new Key(267, 162, 53, 53, "\"", ""),
    };
    public final Key[] keypatsp = {
	new Key(1,   3,   53, 53, "♨", ""),
	new Key(54,  3,   53, 53, "〠", ""),
	new Key(107, 3,   53, 53, "電", ""),
	new Key(160, 3,   53, 53, "♫", ""),
	new Key(213, 3,   53, 53, "", ""),
	new Key(267, 3,   53, 53, "", ""),
	new Key(1,   56,  53, 53, "!", ""),
	new Key(54,  56,  53, 53, "@", ""),
	new Key(107, 56,  53, 53, "#", ""),
	new Key(160, 56,  53, 53, "$", ""),
	new Key(213, 56,  53, 53, "%", ""),
	new Key(267, 56,  53, 53, ".", ""),
	new Key(1,   109, 53, 53, "^", ""),
	new Key(54,  109, 53, 53, "&", ""),
	new Key(107, 109, 53, 53, "*", ""),
	new Key(160, 109, 53, 53, "(", ""),
	new Key(213, 109, 53, 53, ")", ""),
	new Key(267, 109, 53, 53, ",", ""),
	new Key(1,   162, 53, 53, ",", ""),
	new Key(54,  162, 53, 53, ".", ""),
	new Key(107, 162, 53, 53, "?", ""),
	new Key(160, 162, 53, 53, "、", ""),
	new Key(213, 162, 53, 53, "。", ""),
	new Key(267, 162, 53, 53, " ", ""),
    };

    public Keys(){
    }
}
