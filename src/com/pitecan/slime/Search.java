//
// いろんな辞書を検索するものをまとめる
// その後でまとめてAsyncTaskにする。
// 
package com.pitecan.slime;

class Candidate {
    String pat, word;
    int weight;

    public Candidate(String p, String w, int weight){
    }
}

public class Search {
    LocalDict localDict;
    SQLDict sqlDict;

    public static Candidate[] candidates = new Candidate[Slime.MAXCANDS];  // 候補単語リスト
    public static int ncands = 0;

    public Search(LocalDict localDict, SQLDict sqlDict){
	this.localDict = localDict;
	this.sqlDict = sqlDict;
	for(int i=0;i<Slime.MAXCANDS;i++){
	    candidates[i] = new Candidate("","",0);
	}
    }

    public static void search(String pat){
	LocalDict.search(pat);
    }

    public static void addCandidate(String word, String pat){
	addCandidateWithLevel(word,pat,0);
    }

    public static void addCandidateWithLevel(String word, String pat, int level){
	int i;
	// Log.v("Slime","addCandidate: word="+word+" pat="+pat+" ncands="+ncands+" level="+level);
	if(ncands >= Slime.MAXCANDS) return;
	for(i=0;i<ncands;i++){
	    if(candidates[i].word.equals(word)) break;
	}
	if(i >= ncands){
	    candidates[ncands].pat = pat;
	    candidates[ncands].word = word;
	    candidates[ncands].weight = level;
	    //Log.v("Slime", "Add "+word+" to candidates");
	    ncands++;
	}
    }
}
