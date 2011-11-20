//
//	接続辞書を使った単語検索
//
//	iPhone用にJSで書いてあったものをAndroid用に書き直し
//	2010/9/20
//
package com.pitecan.slime;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import android.content.res.AssetManager;
import android.util.Log;
import android.text.TextUtils;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;

class DictEntry {
    String pat, word;
    int inConnection, outConnection;
    int hashLink;
    int connectionLink;

    public DictEntry(String p, String w, int o, int i){
	pat = p;
	word = w;
	outConnection = o;
	inConnection = i;
    }
}

public class Dict {
    static DictEntry[] dict;
    static int[] hashLink = new int[10];
    static int[] connectionLink = new int[2000];

    public static String[] candWords = new String[20];      // 候補単語リスト
    static String[] candPatterns = new String[20];   // その読み
    public static int ncands = 0;

    static Pattern[] regexp = new Pattern[50];       // パタンの部分文字列にマッチするRegExp
    static int[] cslength = new int[50];             // regexp[n]に完全マッチするパタンの長さ

    static String[] wordStack = new String[20];
    static String[] patStack = new String[20];

    static boolean exactMode = false;

    static final int maxCands = 20;

    //
    // assetsディレクトリの中のdict.txtを使用
    //
    public Dict(AssetManager as){
	try {
	    InputStream is;
	    InputStreamReader in;
	    BufferedReader br;

	    String line;
	    int i, entries = 0;

	    // エントリ数を数えてから配列を作って初期化しているが、時間がかかってしまう

	    // 辞書エントリを数える
	    is = as.open("dict.txt");
	    in = new InputStreamReader(is);
	    br = new BufferedReader(in);
            while ((line = br.readLine()) != null) {
		int c = line.charAt(0);
		if(c == '#' || c == ' ' || c == '\t') continue; // コメント行
		entries++;
            }
	    br.close();
	    in.close();
	    is.close();
	    Log.v("Slime","entries="+entries);

	    dict = new DictEntry[entries];

	    // 辞書エントリを読み込む
	    is = as.open("dict.txt");
	    in = new InputStreamReader(is);
	    br = new BufferedReader(in);
	    i = 0;
            while ((line = br.readLine()) != null) {
		int c = line.charAt(0);
		if(c == '#' || c == ' ' || c == '\t') continue; // コメント行
		String[] a = TextUtils.split(line,"\t");
		dict[i++] = new DictEntry(a[0],a[1],Integer.valueOf(a[2]),Integer.valueOf(a[3]));
            }
            br.close();
	    in.close();
	    is.close();

	    //	    as.close(); IMEの場合これがあると死ぬ
	} catch (IOException e) {  
	    //e.printStackTrace();  
	}
	Log.v("Slime","Dict read end");
	initLink(); // 辞書エントリ間のリンク設定
	Log.v("Slime","initLink end");
    }

    private static void initLink(){
	//
	// 先頭読みが同じ単語のリスト
	//
	int[] cur;
	cur = new int[10];
	for(int i=0;i<10;i++){
	    hashLink[i] = -1;
	}
	for(int i=0;i<dict.length;i++){
	    int ind = patInd(dict[i].pat);
	    if(hashLink[ind] < 0){
		cur[ind] = i;
		hashLink[ind] = i;
	    }
	    else {
		dict[cur[ind]].hashLink = i;
		cur[ind] = i;
	    }
	    dict[i].hashLink = -1; // リンクの末尾
	}
	//
	// コネクションつながりのリスト
	//
	cur = new int[2000];
	for(int i=0;i<2000;i++){
	    connectionLink[i] = -1;
	}
	for(int i=0;i<dict.length;i++){
	    int ind = dict[i].inConnection;
	    if(connectionLink[ind] < 0){
		cur[ind] = i;
		connectionLink[ind] = i;
	    }
	    else {
		dict[cur[ind]].connectionLink = i;
		cur[ind] = i;
	    }
	    dict[i].connectionLink = -1; // リンクの末尾
	}
    }

    static String patInit(String pat, int level){
	String p = "";
	String top = "";
	Pattern re;
	Matcher m;

	cslength[level] = 0;
	if(pat.length() > 0){
	    re = Pattern.compile("^(\\[[^\\]]+\\])(.*)$");
	    m = re.matcher(pat);
	    if(m.find()){
		top = m.group(1);
		p = patInit(m.group(2),level+1);
	    }
	    else {
		re = Pattern.compile("^(.)(.*)$");
		m = re.matcher(pat);
		m.find();
		top = m.group(1);
		p = patInit(m.group(2),level+1);
	    }
	    cslength[level] = cslength[level+1]+1;
	}

	top += (p.length() > 0 ? "("+p+")?" : "");
	regexp[level] = Pattern.compile("^("+top+")");
	return top;
    }

    static void search(String pat){
	int linkInd = patInd(pat);
	patInit(pat,0);
	ncands = 0;
	generateCand(maxCands, linkInd); // 接続辞書を使って20個まで候補を生成
    }

    static void generateCand(int maxcands, int linkInd){
	generateCand0(0, 0, "", "", 0, maxcands, linkInd);
    }

    // パタンのlen文字目からのマッチを調べる
    static void generateCand0(int connection, int len, String word, String pat, int level, int maxcands, int linkInd){
	//Log.v("Slime","GenerateCand("+word+","+pat+","+level+")");
	wordStack[level] = word;
	patStack[level] = pat;
	int patlen, matchlen;
	Matcher m;
    
	if(connection == 0){
	    patlen = cslength[0];
	    int d = hashLink[linkInd];
	    for(;d >= 0 && ncands < maxcands;d = dict[d].hashLink){
		//Log.v("Slime","matcher="+regexp[level]+", pat="+dict[d].pat);
		m = regexp[len].matcher(dict[d].pat);
		if(m.find()){
		    matchlen = m.group(1).length();
		    //Log.v("Slime","find success. m.group(1)="+m.group(1)+" patlen="+patlen);
		    if(matchlen == patlen && (!exactMode || exactMode && dict[d].pat.length() == matchlen)){ // 最後までマッチ
			//Log.v("Slime","match success");
			ncands = addCandidate(dict[d].word, dict[d].pat, dict[d].outConnection, ncands, level, matchlen);
		    }
		    else if(matchlen == dict[d].pat.length() && dict[d].outConnection != 0){ // とりあえずその単語まではマッチ
			generateCand0(dict[d].outConnection, len+matchlen, dict[d].word, dict[d].pat, level+1, maxcands, 0);
		    }
		}
	    }
	}
	else {
	    patlen = cslength[len];
	    int d = connectionLink[connection];
	    for(;d >= 0 && ncands < maxcands;d = dict[d].connectionLink){
		m = regexp[len].matcher(dict[d].pat);
		if(m.find()){
		    matchlen = m.group(1).length();
		    if(matchlen == patlen && (!exactMode || exactMode && dict[d].pat.length() == matchlen)){ // 最後までマッチ
			ncands = addCandidate(dict[d].word, dict[d].pat, dict[d].outConnection, ncands, level, matchlen);
		    }
		    else if(matchlen == dict[d].pat.length() && dict[d].outConnection != 0){ // とりあえずその単語まではマッチ
			generateCand0(dict[d].outConnection, len+matchlen, dict[d].word, dict[d].pat, level+1, maxcands, 0);
		    }
		}
	    }
	}
    }

    static int addCandidate(String word, String pat, int connection, int n, int level, int matchlen){ // 候補追加
	int i;
	//Log.v("Slime","addCandidate word="+word);
	if(word == "") return n; // 2011/11/3
	if(word.charAt(0) == '*') return n;

	String p = "";
	for(i=0;i<level+1;i++){
	    p += patStack[i];
	}
	p += pat;
	String w = "";
	for(i=0;i<level+1;i++){
	    w += wordStack[i];
	}
	w += word;
	//Log.v("Slime","addCandidate! word="+word);

	w = w.replaceAll("\\*","");

	////  if(w[0] == '*') return n;
	// w = w.replace(/\*/g,''); // 全ての'*'を消すのはまずいはず。後で修正必要。
	//  w = w.replace(/\-/g,'');

	//  //
	//  // "ode"入力で「おディズニーランド」が候補にならないように、接続辞書で生成された候補の長さが入力文字列を大幅に上回るものははじくようにする!
	//  //
	//  var totalinputlen = patStack.slice(0,level+1).join('').length + matchlen;
	//  if(level > 0 && totalinputlen <= p.length / 2){
	//    return n;
	//  }

	for(i=0;i<ncands;i++){
	    if(candWords[i].equals(w)) break;
	}
	if(i >= ncands){
	    candPatterns[ncands] = p;
	    candWords[ncands] = w;
	    //Log.v("Slime", "Add "+w+" to candidates");
	    ncands++;
	}
	//Log.v("Slime","ncands="+ncands);
	return ncands;
    }

    private static int patInd(String str){
	if(Pattern.matches("\\[?[aiueoAIUEO].*",str)) return 0;
	if(Pattern.matches("\\[?[kg].*",str)) return 1;
	if(Pattern.matches("\\[?[sz].*",str)) return 2;
	if(Pattern.matches("\\[?[tdT].*",str)) return 3;
	if(Pattern.matches("\\[?[hbp].*",str)) return 4;
	if(Pattern.matches("\\[?[n].*",str)) return 5;
	if(Pattern.matches("\\[?[m].*",str)) return 6;
	if(Pattern.matches("\\[?[yY].*",str)) return 7;
	if(Pattern.matches("\\[?[r].*",str)) return 8;
	return 9;
    }
}
