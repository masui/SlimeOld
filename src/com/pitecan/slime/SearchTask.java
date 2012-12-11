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

    public void progress(int i){
	publishProgress(0);
    }

    protected Candidate[] doInBackground(String... searchParams){ // Result の型を返す 引数はParamsの型
	Candidate[] res;
	String pat = searchParams[0];
	String word = searchParams[1];
	res = Search.search(pat,word,useGoogle,this); // this.cancel()が呼ばれるとthis.isCancelled()がtrueになる
	// Log.v("Slime","doInBackground end");
	return res;
    }

    private void updateView(){
	int nbuttons = 0;
	if(Search.ncands > 0){
	    for(;nbuttons<KeyView.candButtons.length && nbuttons <Search.ncands;nbuttons++){
		KeyView.candButtons[nbuttons].text = Search.candidates[nbuttons].word;
		KeyView.candButtons[nbuttons].pat = Search.candidates[nbuttons].pat;
	    }
	}
	for(int i = nbuttons;i<KeyView.candButtons.length;i++){
	    KeyView.candButtons[i].text = "";
	    KeyView.candButtons[i].pat = "";
	}
	keyView.drawDefault();
    }

    @Override
    protected void onProgressUpdate(Integer... progress) { // Progressの型
	// setProgressPercent(progress[0]);
	//
	// 検索ルーチンで候補がみつかったとき適宜publishProgress()を呼ぶことにより候補表示を速くする。
	// publishProgress()が呼ばれるとこのスレッドのonProgressUpdate()が呼ばれる。
	//
	updateView();
    }

    @Override
    protected void onPostExecute(Candidate[] candidates) { // Result の型の値が引数に入る doInBackgroundの返り値
	// Log.v("Slime","onPostExecute");
	// ここで候補を表示する
	updateView();
    }

    protected void onCancelled(Candidate[] candidates){
	// Log.v("Slime","onCancelled");
    }
}
