//
// いろんな辞書を検索するものをまとめる
// その後でまとめてAsyncTaskにする。
// 
package com.pitecan.slime;

import java.util.Arrays;
import android.util.Log;
import android.os.AsyncTask;

class Candidate {
    String pat, word;
    int weight;

    public Candidate(String p, String w, int weight){
    }
}

public class Search {
    LocalDict localDict;
    static SQLDict sqlDict;
    static Slime slime;
    static int nbuttons;

    public static Candidate[] candidates = new Candidate[Slime.MAXCANDS];  // 候補単語リスト
    public static int ncands = 0;

    public Search(LocalDict localDict, SQLDict sqlDict, Slime slime){
	this.localDict = localDict;
	this.sqlDict = sqlDict;
	this.slime = slime;
	for(int i=0;i<Slime.MAXCANDS;i++){
	    candidates[i] = new Candidate("","",0);
	}
    }

    //
    // いろんな辞書を使った検索!
    //
    public static Candidate[] search(String pat, String word, boolean useGoogle, SearchTask searchTask){
	Log.v("Slime","Search - pat="+pat+", word="+word);
	ncands = 0;
	KeyController.candPage = 1;

	// ひらがな/カタカナ
	if(LocalDict.exactMode){
	    String hira = word;
	    String p = Keys.hira2pat(hira); // 無理矢理ひらがなをローマ字パタンに変換
	    addCandidateWithLevel(hira,p,-100);
	    addCandidateWithLevel(h2k(hira),p,-99);
	}

	// コピーした単語を候補に出す (新規登録用)
	if(!LocalDict.exactMode){
	    String s = slime.getRegWord();
	    if(s != "" && s.length() < 10){ // コピー文字列が短い場合だけ候補にする
		addCandidate(s,Keys.hira2pat(word));
	    }
	}

	// 学習辞書を検索
	String[][] s = sqlDict.match(pat,LocalDict.exactMode);
	for(int k=0;k<s.length;k++){
	    addCandidateWithLevel(s[k][0],s[k][1],-50+k);
	}

	// 通常のローカル辞書を検索
	LocalDict.search(pat,searchTask);

	// Google Suggest
	if(useGoogle){
	    String[] suggestions = GoogleSuggest.suggest(word);
	    for(int i=0;suggestions[i] != "";i++){
		Log.v("Slime","Use Google ... suggestions = "+suggestions[i]);
		addCandidateWithLevel(suggestions[i],Keys.hira2pat(word),50);
	    }
	}

	// 優先度に従って候補を並べなおし
	for(int j=ncands;j<Slime.MAXCANDS;j++){
	    candidates[j].weight = 100;
	}
	Arrays.sort(candidates, new CandidateComparator());

	return candidates;
    }

    public static void addCandidate(String word, String pat){
	addCandidateWithLevel(word,pat,0);
    }

    public static void addCandidateWithLevel(String word, String pat, int level){
	int i;
	Log.v("Slime","addCandidate: word="+word+" pat="+pat+" ncands="+ncands+" level="+level);
	if(ncands >= Slime.MAXCANDS) return;
	for(i=0;i<ncands;i++){
	    if(candidates[i].word.equals(word)) break;
	}
	if(i >= ncands){
	    candidates[ncands].pat = pat;
	    candidates[ncands].word = word;
	    candidates[ncands].weight = level;
	    Log.v("Slime", "Add "+word+" to candidates");
	    ncands++;
	}
    }

    // ひらがな ⇒カタカナ
    // http://www7a.biglobe.ne.jp/~java-master/samples/string/ZenkakuHiraganaToZenkakuKatakana.html
    private static String h2k(String s){
	StringBuffer sb = new StringBuffer(s);
	for (int i = 0; i < sb.length(); i++) {
	    char c = sb.charAt(i);
	    if (c >= 'ぁ' && c <= 'ん') {
		sb.setCharAt(i, (char)(c - 'ぁ' + 'ァ'));
	    }
	}
	return sb.toString();    
    }

}
