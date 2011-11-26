package com.pitecan.slime;

import android.util.Log;

class Key {
    Rectangle rect;
    String str;
    String pat;
    Key[] shiftKeypat;
    public Key(int _x,int _y,int _w,int _h,String _str,String _pat,Key[] _shiftKeypat){
	rect = new Rectangle(_x,_y,_w,_h);
	str = _str;
	pat = _pat;
	shiftKeypat = _shiftKeypat;
    }
}

class Keys {
    public final Key[] keypat11 =  {
	new Key(1,   3,   53, 53,  "ぁ", "A", null),
	new Key(54,  3,   53, 53,  "ぃ", "I", null),
	new Key(107, 3,   53, 53,  "ぅ", "U", null),
	new Key(160, 3,   53, 53,  "ぇ", "E", null),
	new Key(213, 3,   53, 53,  "ぉ", "O", null),
	new Key(1,   56,  53, 106, "あ", "a", null),
	new Key(54,  56,  53, 106, "い", "i", null),
	new Key(107, 56,  53, 106, "う", "u", null),
	new Key(160, 56,  53, 106, "え", "e", null),
	new Key(213, 56,  53, 106, "お", "o", null),
    };
    public final Key[] keypat1 =  {
	new Key(1,   3,   53, 53,  "ぁ", "A", keypat11),
	new Key(54,  3,   53, 53,  "ぃ", "I", keypat11),
	new Key(107, 3,   53, 53,  "ぅ", "U", keypat11),
	new Key(160, 3,   53, 53,  "ぇ", "E", keypat11),
	new Key(213, 3,   53, 53,  "ぉ", "O", keypat11),
	new Key(1,   56,  53, 106, "あ", "a", keypat11),
	new Key(54,  56,  53, 106, "い", "i", keypat11),
	new Key(107, 56,  53, 106, "う", "u", keypat11),
	new Key(160, 56,  53, 106, "え", "e", keypat11),
	new Key(213, 56,  53, 106, "お", "o", keypat11),
    };
    public final Key[] keypat12 =  {
	new Key(1,   3,   53, 53,  "が", "ga", null),
	new Key(54,  3,   53, 53,  "ぎ", "gi", null),
	new Key(107, 3,   53, 53,  "ぐ", "gu", null),
	new Key(160, 3,   53, 53,  "げ", "ge", null),
	new Key(213, 3,   53, 53,  "ご", "go", null),
	new Key(1,   56,  53, 106, "か", "ka", null),
	new Key(54,  56,  53, 106, "き", "ki", null),
	new Key(107, 56,  53, 106, "く", "ku", null),
	new Key(160, 56,  53, 106, "け", "ke", null),
	new Key(213, 56,  53, 106, "こ", "ko", null),
    };
    public final Key[] keypat2 =  {
	new Key(1,   3,   53, 53,  "が", "ga", keypat12),
	new Key(54,  3,   53, 53,  "ぎ", "gi", keypat12),
	new Key(107, 3,   53, 53,  "ぐ", "gu", keypat12),
	new Key(160, 3,   53, 53,  "げ", "ge", keypat12),
	new Key(213, 3,   53, 53,  "ご", "go", keypat12),
	new Key(1,   56,  53, 106, "か", "ka", keypat12),
	new Key(54,  56,  53, 53,  "き", "ki", keypat12),
	new Key(107, 56,  53, 106, "く", "ku", keypat12),
	new Key(160, 56,  53, 106, "け", "ke", keypat12),
	new Key(213, 56,  53, 106, "こ", "ko", keypat12),
	new Key(54,  109, 53, 53,  "か", "ka", keypat12),
    };
    public final Key[] keypat13 =  {
	new Key(1,   3,   53, 53,  "ざ", "za", null),
	new Key(54,  3,   53, 53,  "じ", "zi", null),
	new Key(107, 3,   53, 53,  "ず", "zu", null),
	new Key(160, 3,   53, 53,  "ぜ", "ze", null),
	new Key(213, 3,   53, 53,  "ぞ", "zo", null),
	new Key(1,   56,  53, 106, "さ", "sa", null),
	new Key(54,  56,  53, 106, "し", "si", null),
	new Key(107, 56,  53, 106, "す", "su", null),
	new Key(160, 56,  53, 106, "せ", "se", null),
	new Key(213, 56,  53, 106, "そ", "so", null),
    };
    public final Key[] keypat3 =  {
	new Key(1,   3,   53, 53,  "ざ", "za", keypat13),
	new Key(54,  3,   53, 53,  "じ", "zi", keypat13),
	new Key(107, 3,   53, 53,  "ず", "zu", keypat13),
	new Key(160, 3,   53, 53,  "ぜ", "ze", keypat13),
	new Key(213, 3,   53, 53,  "ぞ", "zo", keypat13),
	new Key(1,   56,  53, 106, "さ", "sa", keypat13),
	new Key(54,  56,  53, 106, "し", "si", keypat13),
	new Key(107, 56,  53, 53,  "す", "su", keypat13),
	new Key(160, 56,  53, 106, "せ", "se", keypat13),
	new Key(213, 56,  53, 106, "そ", "so", keypat13),
	new Key(107, 109, 53, 53,  "さ", "sa", keypat13),
    };
    public final Key[] keypat14 =  {
	new Key(1,   3,   53, 53,  "だ", "da", null),
	new Key(54,  3,   53, 53,  "ぢ", "di", null),
	new Key(107, 3,   53, 53,  "づ", "du", null),
	new Key(160, 3,   53, 53,  "で", "de", null),
	new Key(213, 3,   53, 53,  "ど", "do", null), 
	new Key(1,   56,  53, 106, "た", "ta", null),
	new Key(54,  56,  53, 106, "ち", "ti", null),
	new Key(107, 56,  53, 106, "つ", "tu", null),
	new Key(160, 56,  53, 106, "て", "te", null),
	new Key(213, 56,  53, 106, "と", "to", null),
	new Key(107, 162, 53, 53,  "っ", "Tu", null),
    };
    public final Key[] keypat4 =  {
	new Key(1,   3,   53, 53,  "だ", "da", keypat14),
	new Key(54,  3,   53, 53,  "ぢ", "di", keypat14),
	new Key(107, 3,   53, 53,  "づ", "du", keypat14),
	new Key(160, 3,   53, 53,  "で", "de", keypat14),
	new Key(213, 3,   53, 53,  "ど", "do", keypat14), 
	new Key(1,   56,  53, 106, "た", "ta", keypat14),
	new Key(54,  56,  53, 106, "ち", "ti", keypat14),
	new Key(107, 56,  53, 106, "つ", "tu", keypat14),
	new Key(160, 56,  53, 53,  "て", "te", keypat14),
	new Key(213, 56,  53, 106, "と", "to", keypat14),
	new Key(160, 109, 53, 53,  "た", "ta", keypat14),
	new Key(107, 162, 53, 53,  "っ", "Tu", keypat14),
    };
    public final Key[] keypat15 =  {
	new Key(1,   56,  53, 106, "な", "na", null),
	new Key(54,  56,  53, 106, "に", "ni", null),
	new Key(107, 56,  53, 106, "ぬ", "nu", null),
	new Key(160, 56,  53, 106, "ね", "ne", null),
	new Key(213, 56,  53, 106, "の", "no", null),
	new Key(267, 109, 53, 53, "←", "", null),
    };
    public final Key[] keypat5 =  {
	new Key(1,   56,  53, 106, "な", "na", keypat15),
	new Key(54,  56,  53, 106, "に", "ni", keypat15),
	new Key(107, 56,  53, 106, "ぬ", "nu", keypat15),
	new Key(160, 56,  53, 106, "ね", "ne", keypat15),
	new Key(213, 56,  53, 53,  "の", "no", keypat15),
	new Key(214, 109, 53, 53,  "な", "na", keypat15),
	new Key(267, 109, 53, 53,  "←", "", null),
    };
    public final Key[] keypat16 =  {
	new Key(1,   3,   53, 53,  "ぱ", "pa", null),
	new Key(54,  3,   53, 53,  "ぴ", "pi", null),
	new Key(107, 3,   53, 53,  "ぷ", "pu", null),
	new Key(160, 3,   53, 53,  "ぺ", "pe", null),
	new Key(213, 3,   53, 53,  "ぽ", "po", null),
	new Key(1,   56,  53, 53,  "ば", "ba", null),
	new Key(54,  56,  53, 53,  "び", "bi", null),
	new Key(107, 56,  53, 53,  "ぶ", "bu", null),
	new Key(160, 56,  53, 53,  "べ", "be", null),
	new Key(213, 56,  53, 53,  "ぼ", "bo", null),
	new Key(1,   109, 53, 106, "は", "ha", null),
	new Key(54,  109, 53, 106, "ひ", "hi", null),
	new Key(107, 109, 53, 106, "ふ", "hu", null),
	new Key(160, 109, 53, 106, "へ", "he", null),
	new Key(213, 109, 53, 106, "ほ", "ho", null),
    };
    public final Key[] keypat6 =  {
	new Key(1,   3,   53, 53,  "ぱ", "pa", keypat16),
	new Key(54,  3,   53, 53,  "ぴ", "pi", keypat16),
	new Key(107, 3,   53, 53,  "ぷ", "pu", keypat16),
	new Key(160, 3,   53, 53,  "ぺ", "pe", keypat16),
	new Key(213, 3,   53, 53,  "ぽ", "po", keypat16),
	new Key(1,   56,  53, 53,  "ば", "ba", keypat16),
	new Key(54,  56,  53, 53,  "び", "bi", keypat16),
	new Key(107, 56,  53, 53,  "ぶ", "bu", keypat16),
	new Key(160, 56,  53, 53,  "べ", "be", keypat16),
	new Key(213, 56,  53, 53,  "ぼ", "bo", keypat16),
	new Key(1,   109, 53, 106, "は", "ha", keypat16),
	new Key(54,  109, 53, 106, "ひ", "hi", keypat16),
	new Key(107, 109, 53, 106, "ふ", "hu", keypat16),
	new Key(160, 109, 53, 106, "へ", "he", keypat16),
	new Key(213, 109, 53, 106, "ほ", "ho", keypat16),
    };
    public final Key[] keypat17 =  {
	new Key(1,   3,   53, 53,  "0",  "",   null),
	new Key(54,  3,   53, 53,  "1",  "",   null),
	new Key(107, 3,   53, 53,  "2",  "",   null),
	new Key(160, 3,   53, 53,  "3",  "",   null),
	new Key(213, 3,   53, 53,  "4",  "",   null),
	new Key(267, 3,   53, 53,  "=",  "",   null),
	new Key(267, 3,   53, 53,  "←",  "",   null),
	new Key(1,   56,  53, 53,  "5",  "",   null),
	new Key(54,  56,  53, 53,  "6",  "",   null),
	new Key(107, 56,  53, 53,  "7",  "",   null),
	new Key(160, 56,  53, 53,  "8",  "",   null),
	new Key(213, 56,  53, 53,  "9",  "",   null),
	new Key(267, 56,  53, 53,  " ",  "",   null),
	new Key(1,   109, 53, 106, "ま", "ma", null),
	new Key(54,  109, 53, 106, "み", "mi", null),
	new Key(107, 109, 53, 106, "む", "mu", null),
	new Key(160, 109, 53, 106, "め", "me", null),
	new Key(213, 109, 53, 106, "も", "mo", null),
	new Key(267, 109, 53, 53,  "+",  "",   null),
	new Key(267, 162, 53, 53,  "-",  "",   null),
    };
    public final Key[] keypat7 =  {
	new Key(1,   3,   53, 53,  "0",  "",   keypat17),
	new Key(54,  3,   53, 53,  "1",  "",   keypat17),
	new Key(107, 3,   53, 53,  "2",  "",   keypat17),
	new Key(160, 3,   53, 53,  "3",  "",   keypat17),
	new Key(213, 3,   53, 53,  "4",  "",   keypat17),
	new Key(267, 3,   53, 53,  "←",  "",   keypat17),
	new Key(1,   56,  53, 53,  "5",  "",   keypat17),
	new Key(54,  56,  53, 53,  "6",  "",   keypat17),
	new Key(107, 56,  53, 53,  "7",  "",   keypat17),
	new Key(160, 56,  53, 53,  "8",  "",   keypat17),
	new Key(213, 56,  53, 53,  "9",  "",   keypat17),
	new Key(267, 56,  53, 53,  " ",  "",   keypat17),
	new Key(1,   109, 53, 106, "ま", "ma", keypat17),
	new Key(54,  109, 53, 53,  "み", "mi", keypat17),
	new Key(107, 109, 53, 106, "む", "mu", keypat17),
	new Key(160, 109, 53, 106, "め", "me", keypat17),
	new Key(213, 109, 53, 106, "も", "mo", keypat17),
	new Key(54,  162, 53, 53,  "ま", "ma", keypat17),
	new Key(267, 109, 53, 53,  "+",  "",   keypat17),
	new Key(267, 162, 53, 53,  "-",  "",   keypat17),
    };
    public final Key[] keypat18 =  {
	new Key(1,   56,  106, 53,  "ゃ", "Ya", null),
	new Key(107, 56,  53,  53,  "ゅ", "Yu", null),
	new Key(160, 56,  106, 53,  "ょ", "Yo", null),
	new Key(1,   109, 106, 106, "や", "ya", null),
	new Key(107, 109, 53,  106, "ゆ", "yu", null),
	new Key(160, 109, 106, 106, "よ", "yo", null),
    };
    public final Key[] keypat8 =  {
	new Key(1,   56,  106, 53,  "ゃ", "Ya", keypat18),
	new Key(107, 56,  53,  53,  "ゅ", "Yu", keypat18),
	new Key(160, 56,  106, 53,  "ょ", "Yo", keypat18),
	new Key(1,   109, 106, 106, "や", "ya", keypat18),
	new Key(107, 109, 53,  53,  "ゆ", "yu", keypat18),
	new Key(160, 109, 106, 106, "よ", "yo", keypat18),
	new Key(107, 162, 53,  53,  "や", "ya", keypat18),
    };
    public final Key[] keypat19 =  {
	new Key(1,   3,   53, 53,  "'",  "",   null),
	new Key(54,  3,   53, 53,  "\"", "",   null),
	new Key(107, 3,   53, 53,  "^",  "",   null),
	new Key(160, 3,   53, 53,  "|",  "",   null),
	new Key(213, 3,   53, 53,  "%",  "",   null),
	new Key(267, 3,   53, 53,  "=",  "",   null),
	new Key(1,   56,  53, 53,  "`",  "",   null),
	new Key(54,  56,  53, 53,  "<",  "",   null),
	new Key(107, 56,  53, 53,  ">",  "",   null),
	new Key(160, 56,  53, 53,  "_",  "",   null),
	new Key(213, 56,  53, 53,  "$",  "",   null),
	new Key(267, 56,  53, 53,  "*",  "",   null),
	new Key(267, 109, 53, 53,  "+",  "",   null),
	new Key(267, 162, 53, 53,  "-",  "",   null),
	new Key(1,   109, 53, 106, "ら", "ra", null),
	new Key(54,  109, 53, 106, "り", "ri", null),
	new Key(107, 109, 53, 106, "る", "ru", null),
	new Key(160, 109, 53, 106, "れ", "re", null),
	new Key(213, 109, 53, 106, "ろ", "ro", null),
    };
    public final Key[] keypat9 =  {
	new Key(1,   3,   53, 53, "'",   "",   keypat19),
	new Key(54,  3,   53, 53, "\"",  "",   keypat19),
	new Key(107, 3,   53, 53, "^",   "",   keypat19),
	new Key(160, 3,   53, 53, "|",   "",   keypat19),
	new Key(213, 3,   53, 53, "%",   "",   keypat19),
	new Key(267, 3,   53, 53, "=",   "",   keypat19),
	new Key(1,   56,  53, 53, "`",   "",   keypat19),
	new Key(54,  56,  53, 53, "<",   "",   keypat19),
	new Key(107, 56,  53, 53, ">",   "",   keypat19),
	new Key(160, 56,  53, 53, "_",   "",   keypat19),
	new Key(213, 56,  53, 53, "$",   "",   keypat19),
	new Key(267, 56,  53, 53, "*",   "",   keypat19),
	new Key(267, 109, 53, 53, "+",   "",   keypat19),
	new Key(267, 162, 53, 53, "-",   "",   keypat19),
	new Key(1,   109, 53, 106, "ら", "ra", keypat19),
	new Key(54,  109, 53, 106, "り", "ri", keypat19),
	new Key(107, 109, 53, 106, "る", "ru", keypat19),
	new Key(160, 109, 53, 53,  "れ", "re", keypat19),
	new Key(213, 109, 53, 106, "ろ", "ro", keypat19),
	new Key(160, 162, 53, 53,  "ら", "ra", keypat19),
    };
    public final Key[] keypat20 = {
	new Key(1,   3,   53, 53, ":",  "", null),
	new Key(54,  3,   53, 53, ";",  "", null),
	new Key(107, 3,   53, 53, "#",  "", null),
	new Key(160, 3,   53, 53, "&",  "", null),
	new Key(213, 3,   53, 53, "~",  "", null),
	new Key(267, 3,   53, 53, "!",  "", null),
	new Key(1,   56,  53, 53, "{",  "", null),
	new Key(54,  56,  53, 53, "}",  "", null),
	new Key(107, 56,  53, 53, "@",  "", null),
	new Key(160, 56,  53, 53, "/",  "", null),
	new Key(213, 56,  53, 53, "\\", "", null),
	new Key(267, 56,  53, 53, "?",  "", null),
	new Key(1,   109, 53, 53, "[",  "", null),
	new Key(54,  109, 53, 53, "]",  "", null),
	new Key(267, 109, 53, 53, ",",  "", null),
	new Key(1,   162, 53, 53, "(",  "", null),
	new Key(54,  162, 53, 53, ")",  "", null),
	new Key(267, 162, 53, 53, ".",  "", null),
	new Key(107, 109, 53, 53,  "ゎ", "", null),
	new Key(107, 162, 53, 53,  "わ", "w", null),
	new Key(160, 109, 53, 106, "を", "W", null),
	new Key(213, 109, 53, 106, "ん", "N", null),
    };
    public final Key[] keypat10 = {
	new Key(1,   3,   53, 53, ":",  "", keypat20),
	new Key(54,  3,   53, 53, ";",  "", keypat20),
	new Key(107, 3,   53, 53, "#",  "", keypat20),
	new Key(160, 3,   53, 53, "&",  "", keypat20),
	new Key(213, 3,   53, 53, "~",  "", keypat20),
	new Key(267, 3,   53, 53, "!",  "", keypat20),
	new Key(1,   56,  53, 53, "{",  "", keypat20),
	new Key(54,  56,  53, 53, "}",  "", keypat20),
	new Key(107, 56,  53, 53, "@",  "", keypat20),
	new Key(160, 56,  53, 53, "/",  "", keypat20),
	new Key(213, 56,  53, 53, "\\", "", keypat20),
	new Key(267, 56,  53, 53, "?",  "", keypat20),
	new Key(1,   109, 53, 53, "[",  "", keypat20),
	new Key(54,  109, 53, 53, "]",  "", keypat20),
	new Key(267, 109, 53, 53, ",",  "", keypat20),
	new Key(1,   162, 53, 53, "(",  "", keypat20),
	new Key(54,  162, 53, 53, ")",  "", keypat20),
	new Key(267, 162, 53, 53, ".",  "", keypat20),
	new Key(107, 109, 53, 53,  "ゎ", "", keypat20),
	new Key(107, 162, 53, 53,  "わ", "w", keypat20),
	new Key(160, 109, 53, 106, "を", "W", keypat20),
	new Key(213, 109, 53, 106, "ん", "N", keypat20),
    };
    public final Key[] keypatbs_shift = {
	new Key(0,   3,   32, 53, "Q", "", null),
	new Key(32,  3,   32, 53, "W", "", null),
	new Key(64,  3,   32, 53, "E", "", null),
	new Key(96,  3,   32, 53, "R", "", null),
	new Key(128, 3,   32, 53, "T", "", null),
	new Key(160, 3,   32, 53, "Y", "", null),
	new Key(192, 3,   32, 53, "U", "", null),
	new Key(224, 3,   32, 53, "I", "", null),
	new Key(256, 3,   32, 53, "O", "", null),
	new Key(288, 3,   32, 53, "P", "", null),
	new Key(10,  56,  32, 53, "A", "", null),
	new Key(42,  56,  32, 53, "S", "", null),
	new Key(74,  56,  32, 53, "D", "", null),
	new Key(106, 56,  32, 53, "F", "", null),
	new Key(138, 56,  32, 53, "G", "", null),
	new Key(170, 56,  32, 53, "H", "", null),
	new Key(202, 56,  32, 53, "J", "", null),
	new Key(234, 56,  32, 53, "K", "", null),
	new Key(266, 56,  32, 53, "L", "", null),
	new Key(20,  109, 32, 53, "Z", "", null),
	new Key(52,  109, 32, 53, "X", "", null),
	new Key(84,  109, 32, 53, "C", "", null),
	new Key(116, 109, 32, 53, "V", "", null),
	new Key(148, 109, 32, 53, "B", "", null),
	new Key(180, 109, 32, 53, "N", "", null),
	new Key(212, 109, 32, 53, "M", "", null),
	new Key(80,  162, 106, 53, " ", "", null),
	new Key(267, 109, 53, 53, "←", "", null),
    };
    public final Key[] keypatbs = {
	new Key(0,   3,   32,  53, "Q", "", keypatbs_shift),
	new Key(32,  3,   32,  53, "W", "", keypatbs_shift),
	new Key(64,  3,   32,  53, "E", "", keypatbs_shift),
	new Key(96,  3,   32,  53, "R", "", keypatbs_shift),
	new Key(128, 3,   32,  53, "T", "", keypatbs_shift),
	new Key(160, 3,   32,  53, "Y", "", keypatbs_shift),
	new Key(192, 3,   32,  53, "U", "", keypatbs_shift),
	new Key(224, 3,   32,  53, "I", "", keypatbs_shift),
	new Key(256, 3,   32,  53, "O", "", keypatbs_shift),
	new Key(288, 3,   32,  53, "P", "", keypatbs_shift),
	new Key(10,  56,  32,  53, "A", "", keypatbs_shift),
	new Key(42,  56,  32,  53, "S", "", keypatbs_shift),
	new Key(74,  56,  32,  53, "D", "", keypatbs_shift),
	new Key(106, 56,  32,  53, "F", "", keypatbs_shift),
	new Key(138, 56,  32,  53, "G", "", keypatbs_shift),
	new Key(170, 56,  32,  53, "H", "", keypatbs_shift),
	new Key(202, 56,  32,  53, "J", "", keypatbs_shift),
	new Key(234, 56,  32,  53, "K", "", keypatbs_shift),
	new Key(266, 56,  32,  53, "L", "", keypatbs_shift),
	new Key(20,  109, 32,  53, "Z", "", keypatbs_shift),
	new Key(52,  109, 32,  53, "X", "", keypatbs_shift),
	new Key(84,  109, 32,  53, "C", "", keypatbs_shift),
	new Key(116, 109, 32,  53, "V", "", keypatbs_shift),
	new Key(148, 109, 32,  53, "B", "", keypatbs_shift),
	new Key(180, 109, 32,  53, "N", "", keypatbs_shift),
	new Key(212, 109, 32,  53, "M", "", keypatbs_shift),
	new Key(80,  162, 106, 53, " ", "", keypatbs_shift),
	new Key(267, 109, 53,  53, "←", "", keypatbs_shift),
    };
    public final Key[] keypatsp_shift = {
	new Key(0,   3,   32,  53, "q", "", null),
	new Key(32,  3,   32,  53, "w", "", null),
	new Key(64,  3,   32,  53, "e", "", null),
	new Key(96,  3,   32,  53, "r", "", null),
	new Key(128, 3,   32,  53, "t", "", null),
	new Key(160, 3,   32,  53, "y", "", null),
	new Key(192, 3,   32,  53, "u", "", null),
	new Key(224, 3,   32,  53, "i", "", null),
	new Key(256, 3,   32,  53, "o", "", null),
	new Key(288, 3,   32,  53, "p", "", null),
	new Key(10,  56,  32,  53, "a", "", null),
	new Key(42,  56,  32,  53, "s", "", null),
	new Key(74,  56,  32,  53, "d", "", null),
	new Key(106, 56,  32,  53, "f", "", null),
	new Key(138, 56,  32,  53, "g", "", null),
	new Key(170, 56,  32,  53, "h", "", null),
	new Key(202, 56,  32,  53, "j", "", null),
	new Key(234, 56,  32,  53, "k", "", null),
	new Key(266, 56,  32,  53, "l", "", null),
	new Key(20,  109, 32,  53, "z", "", null),
	new Key(52,  109, 32,  53, "x", "", null),
	new Key(84,  109, 32,  53, "c", "", null),
	new Key(116, 109, 32,  53, "v", "", null),
	new Key(148, 109, 32,  53, "b", "", null),
	new Key(180, 109, 32,  53, "n", "", null),
	new Key(212, 109, 32,  53, "m", "", null),
	new Key(80,  162, 106, 53, " ", "", null),
	new Key(267, 162, 53,  53, "", "", null),
    };
    public final Key[] keypatsp = {
	new Key(0,   3,   32,  53, "q", "", keypatsp_shift),
	new Key(32,  3,   32,  53, "w", "", keypatsp_shift),
	new Key(64,  3,   32,  53, "e", "", keypatsp_shift),
	new Key(96,  3,   32,  53, "r", "", keypatsp_shift),
	new Key(128, 3,   32,  53, "t", "", keypatsp_shift),
	new Key(160, 3,   32,  53, "y", "", keypatsp_shift),
	new Key(192, 3,   32,  53, "u", "", keypatsp_shift),
	new Key(224, 3,   32,  53, "i", "", keypatsp_shift),
	new Key(256, 3,   32,  53, "o", "", keypatsp_shift),
	new Key(288, 3,   32,  53, "p", "", keypatsp_shift),
	new Key(10,  56,  32,  53, "a", "", keypatsp_shift),
	new Key(42,  56,  32,  53, "s", "", keypatsp_shift),
	new Key(74,  56,  32,  53, "d", "", keypatsp_shift),
	new Key(106, 56,  32,  53, "f", "", keypatsp_shift),
	new Key(138, 56,  32,  53, "g", "", keypatsp_shift),
	new Key(170, 56,  32,  53, "h", "", keypatsp_shift),
	new Key(202, 56,  32,  53, "j", "", keypatsp_shift),
	new Key(234, 56,  32,  53, "k", "", keypatsp_shift),
	new Key(266, 56,  32,  53, "l", "", keypatsp_shift),
	new Key(20,  109, 32,  53, "z", "", keypatsp_shift),
	new Key(52,  109, 32,  53, "x", "", keypatsp_shift),
	new Key(84,  109, 32,  53, "c", "", keypatsp_shift),
	new Key(116, 109, 32,  53, "v", "", keypatsp_shift),
	new Key(148, 109, 32,  53, "b", "", keypatsp_shift),
	new Key(180, 109, 32,  53, "n", "", keypatsp_shift),
	new Key(212, 109, 32,  53, "m", "", keypatsp_shift),
	new Key(80,  162, 106, 53, " ", "", keypatsp_shift),
	new Key(267, 162, 53,  53, "", "", keypatsp_shift),
    };
    public final Key[] keypat0 = {
	new Key(1,   109, 53, 53, "あ", "[aiueoAIUEO]", keypat1),
	new Key(54,  109, 53, 53, "か", "[kg][aiueo]", keypat2),
	new Key(107, 109, 53, 53, "さ", "[sz][aiueo]", keypat3),
	new Key(160, 109, 53, 53, "た", "[tdT][aiueo]", keypat4),
	new Key(213, 109, 53, 53, "な", "n[aiueo]", keypat5),
	new Key(267, 109, 53, 53, "←", "", keypatbs),
	new Key(1,   162, 53, 53, "は", "[hbp][aiueo]", keypat6),
	new Key(54,  162, 53, 53, "ま", "m[aiueo]", keypat7),
	new Key(107, 162, 53, 53, "や", "[yY][auo]", keypat8),
	new Key(160, 162, 53, 53, "ら", "r[aiueo]", keypat9),
	new Key(213, 162, 53, 53, "わ", "[wWN]", keypat10),
	new Key(267, 162, 53, 53, "↴", "", keypatsp),
    };

    public Keys(){
    }
}
