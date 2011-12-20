package com.pitecan.slime;

import android.os.AsyncTask;
import android.util.Log;

//                                       <Params, Progress, Result> で型を指定する
public class SearchTask extends AsyncTask<String, Integer, Candidate[]> {

    private KeyView keyView;
    private boolean useGoogle;

    public SearchTask(KeyView keyView, boolean useGoogle){
	this.keyView = keyView;
	this.useGoogle = useGoogle;
    }

    protected Candidate[] doInBackground(String... searchParams){ // Result の型を返す 引数はParamsの型
	Candidate[] res;
	String pat = searchParams[0];
	String word = searchParams[1];
	res = Search.search(pat,word,useGoogle,this); // this.cancel()が呼ばれるとthis.isCancelled()がtrueになる
	// Log.v("Slime","doInBackground end");
	return res;
    }

    //    protected void onProgressUpdate(Integer... progress) { // Progressの型
    //	// setProgressPercent(progress[0]);
    //    }

    @Override
    protected void onPostExecute(Candidate[] candidates) { // Result の型の値が引数に入る doInBackgroundの返り値
	Log.v("Slime","onPostExecute");
	// ここで候補を表示する
	int i = 0;
	int nbuttons = 0;
	if(Search.ncands > 0){
	    for(;nbuttons<KeyView.candButtons.length && i <Search.ncands;i++,nbuttons++){
		KeyView.candButtons[nbuttons].text = candidates[i].word;
		KeyView.candButtons[nbuttons].pat = candidates[i].pat;
	    }
	}
	for(int j = nbuttons;j<KeyView.candButtons.length;j++){
	    KeyView.candButtons[j].text = "";
	    KeyView.candButtons[j].pat = "";
	}

	keyView.drawDefault();
    }

    protected void onCancelled(Candidate[] candidates){
	Log.v("Slime","onCancelled");
    }
}
