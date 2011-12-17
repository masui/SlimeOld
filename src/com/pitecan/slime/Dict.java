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
    int keyLink;
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
    static int[] keyLink = new int[10];
    static int[] connectionLink = new int[2000];

    public static String[] candWords = new String[Slime.MAXCANDS];      // 候補単語リスト
    public static String[] candPatterns = new String[Slime.MAXCANDS];   // その読み
    public static int[] candWeight = new int[Slime.MAXCANDS];
    public static int ncands = 0;

    static Pattern[] regexp = new Pattern[50];       // パタンの部分文字列にマッチするRegExp
    static int[] cslength = new int[50];             // regexp[n]に完全マッチするパタンの長さ

    static String[] wordStack = new String[20];
    static String[] patStack = new String[20];

    public static boolean exactMode = false;

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
	    //Log.v("Slime","entries="+entries);

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
	//Log.v("Slime","Dict read end");
	initLink(); // 辞書エントリ間のリンク設定
	//Log.v("Slime","initLink end");
    }

    private static void initLink(){
	//
	// 先頭読みが同じ単語のリスト
	//
	int[] cur;
	cur = new int[10];
	for(int i=0;i<10;i++){
	    keyLink[i] = -1;
	}
	for(int i=0;i<dict.length;i++){
	    if(dict[i].word.startsWith("*")) continue;
	    if(dict[i].inConnection < 1000) continue; // 活用の接続の場合
	    int ind = patInd(dict[i].pat);
	    if(keyLink[ind] < 0){
		cur[ind] = i;
		keyLink[ind] = i;
	    }
	    else {
		dict[cur[ind]].keyLink = i;
		cur[ind] = i;
	    }
	    dict[i].keyLink = -1; // リンクの末尾
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
	patInit(pat,0);
	generateCand(0, patInd(pat), 0, "", "", 0); // 接続辞書を使って候補を生成
    }

    // パタンのlen文字目からのマッチを調べる
    // 接続リンクを深さ優先検索してマッチするものを候補に加えていく
    static void generateCand(int connection, int keylink, int len, String word, String pat, int level){
	//Log.v("Slime","GenerateCand("+word+","+pat+","+level+")");
	wordStack[level] = word;
	patStack[level] = pat;
    
	int patlen = cslength[len];
	int d = (connection != 0 ? connectionLink[connection] : keyLink[keylink]);
	for(;d >= 0 && ncands < Slime.MAXCANDS;d = (connection != 0 ? dict[d].connectionLink : dict[d].keyLink)){
	    Matcher m = regexp[len].matcher(dict[d].pat);
	    if(m.find()){
		int matchlen = m.group(1).length();
		if(matchlen == patlen && (!exactMode || exactMode && dict[d].pat.length() == matchlen)){ // 最後までマッチ
		    addConnectedCandidate(dict[d].word, dict[d].pat, dict[d].outConnection, level, matchlen);
		}
		else if(matchlen == dict[d].pat.length() && dict[d].outConnection != 0){ // とりあえずその単語まではマッチ
		    generateCand(dict[d].outConnection, 0, len+matchlen, dict[d].word, dict[d].pat, level+1);
		}
	    }
	}
    }

    static void addConnectedCandidate(String word, String pat, int connection, int level, int matchlen){ // 候補追加
	int i;
	if(word == "") return; // 2011/11/3
	if(word.charAt(0) == '*') return; // 単語活用の途中

	String p = "";
	for(i=0;i<level+1;i++) p += patStack[i];
	p += pat;
	String w = "";
	for(i=0;i<level+1;i++) w += wordStack[i];
	w += word;

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

	//addCandidate(w,p);
	addCandidateWithLevel(w,p,level);
    }

    public static void addCandidate(String word, String pat){
	addCandidateWithLevel(word,pat,0);
    }

    private static void addCandidateWithLevel(String word, String pat, int level){
	int i;
	//Log.v("Slime","addCandidate: word="+word+" pat="+pat+" ncands="+ncands);
	if(ncands >= Slime.MAXCANDS) return;
	for(i=0;i<ncands;i++){
	    if(candWords[i].equals(word)) break;
	}
	if(i >= ncands){
	    candPatterns[ncands] = pat;
	    candWords[ncands] = word;
	    candWeight[ncands] = level;
	    //Log.v("Slime", "Add "+word+" to candidates");
	    ncands++;
	}
    }

    private static Pattern[] patIndPattern = new Pattern[10];
    private static boolean patIndInitialized = false;

    public static int patInd(String str){
	if(! patIndInitialized){
	    patIndPattern[0] = Pattern.compile("\\[?[aiueoAIUEO].*");
	    patIndPattern[1] = Pattern.compile("\\[?[kg].*");
	    patIndPattern[2] = Pattern.compile("\\[?[sz].*");
	    patIndPattern[3] = Pattern.compile("\\[?[tdT].*");
	    patIndPattern[4] = Pattern.compile("\\[?[n].*");
	    patIndPattern[5] = Pattern.compile("\\[?[hbp].*");
	    patIndPattern[6] = Pattern.compile("\\[?[m].*");
	    patIndPattern[7] = Pattern.compile("\\[?[yY].*");
	    patIndPattern[8] = Pattern.compile("\\[?[r].*");
	    patIndInitialized = true;
	}
	for(int i=0;i<9;i++){
	    Matcher matcher = patIndPattern[i].matcher(str);
	    if(matcher.find()) return i;
	}
	return 9;
    }
}
