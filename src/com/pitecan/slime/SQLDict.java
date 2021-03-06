//
// 学習辞書をSQLiteで実装する
//
// そもそも学習辞書をSQLiteで実装すべきなのかよくわからないし、
// 仕様が色々面倒なのだがとりあえず...
// (2011/12/10)
//
// 以下のようなページを参考にした。
// AndroidのSQLiteの使い方
// http://android.roof-balcony.com/shori/strage/select/
// サンプル
// http://codezine.jp/article/detail/4814?p=2
// サンプル
// http://ichitcltk.hustle.ne.jp/gudon/modules/pico_rd/index.php?content_id=74
// Helper
// http://android.roof-balcony.com/shori/strage/sqlite/
// http://www.ipentec.com/document/document.aspx?page=android-use-sqlite-simple-app
// Android SQLiteマニュアル
// http://developer.android.com/intl/ja/reference/android/database/sqlite/SQLiteDatabase.html
// http://developer.android.com/intl/ja/reference/android/database/Cursor.html

package com.pitecan.slime;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.ArrayList;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import android.content.Context;

import android.util.Log;

class DBHelper extends SQLiteOpenHelper {
    public DBHelper(Context context) {
	super(context, "learndict", null, 1);
    }
    
    @Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
    
    @Override
	public void onCreate(SQLiteDatabase db) {
	// DBが存在する場合は呼ばれないらしい
	db.execSQL(
		   "create table history("+
		   "   word text not null,"+
		   "   pat text not null,"+
		   "   patind text not null,"+
		   "   date text not null"+
		   ");"
		   );
    }
}

public class SQLDict
{
    SQLiteDatabase db;

    public SQLDict(Context context){
	DBHelper helper = new DBHelper(context);
        db = helper.getWritableDatabase();
    }
	
    public void add(String word, String pat){ // エントリ追加
	// 最初に全部消す
	db.delete("history", "word = '"+word+"' AND pat = '"+pat+"'", null);
	int patind = LocalDict.patInd(pat);
	// SQLite3の日付処理
	// http://www.tamandua-webtools.net/sqlite3-date.html
	db.execSQL("insert into history(word,pat,patind,date) values ('"+word+"', '"+pat+"', "+patind+", datetime('now', 'localtime'));");
    }

    public void limit(int max){ // max個までにDBを制限する
	Cursor cursor;
	String word, pat;
        cursor = db.query("history", new String[] { "word", "pat", "patind", "date" },
			  null, null, null, null, "date desc");
	int count = cursor.getCount();
	for(;max < count;max++){
	    cursor.moveToPosition(max);
	    word = cursor.getString(0);
	    pat = cursor.getString(1);
	    //Log.v("SQLite","delete -> " + word);
	    db.delete("history", "word = '"+word+"' AND pat = '"+pat+"'", null);
	}
	cursor.close();
    }

    public String[][] match(String pat, boolean exactMode){ // 新しいものから検索
	ArrayList<String> words = new ArrayList<String>();
	ArrayList<String> wordpats = new ArrayList<String>();
	Pattern pattern = (exactMode ? Pattern.compile("^"+pat) : Pattern.compile("^"+pat+".*"));
	//Log.v("Slime","pattern="+pattern);

	Cursor cursor = db.query("history", new String[] { "word", "pat", "date" },
				 "patind = " + LocalDict.patInd(pat), null, null, null, "date desc");
        boolean isEof = cursor.moveToFirst();
        while (isEof) {
	    String word = cursor.getString(0);
	    String wordpat = cursor.getString(1);
	    //Log.v("Slime",String.format("word:%s wordpat:%s\r\n", word, wordpat));
	    if(pattern.matcher(wordpat).matches()){
		//Log.v("Slime/SQLite - match",String.format("word:%s pat:%s\r\n", word, wordpat));
		words.add(word);
		wordpats.add(wordpat);
	    }
            isEof = cursor.moveToNext();
        }
        cursor.close();
	//Log.v("Slime","length = "+words.size());
	String[][] res = new String[words.size()][2];
	for(int i=0;i<words.size();i++){
	    res[i][0] = words.get(i);
	    res[i][1] = wordpats.get(i);
	}
	return res;
    }
}
