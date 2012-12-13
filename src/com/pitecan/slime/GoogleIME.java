package com.pitecan.slime;

import android.util.Log;

import org.json.*;

import java.io.*;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import org.apache.http.params.HttpParams;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams; 

import org.apache.http.util.EntityUtils;

class GoogleIME {
    static String[] ime(String q){
	// Google日本語入力のURLは "http://google.co.jp/transliterate?langpair=ja-Hira|ja&text=かんじ" のような形式だが
	// "|" を "%7c" にしておかないと new HttpGet() が失敗する
	String urlstr = "http://google.co.jp/transliterate?langpair=ja-Hira%7cja&text="+q;
	String[] suggestions;
	final int maxSuggestions = 20;
	suggestions = new String[maxSuggestions+1];
	int nsuggest = 0;

	String jsonText = "[[\"\",[]]]";

	//Log.d("Slime", urlstr);

	try {
	    // http://stackoverflow.com/questions/693997/how-to-set-httpresponse-timeout-for-android-in-java
	    HttpParams httpParameters = new BasicHttpParams();
	    //Log.d("Slime", "parameters = " + httpParameters);
	    // Set the timeout in milliseconds until a connection is established.
	    int timeoutConnection = 1500;
	    HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
	    // Set the default socket timeout (SO_TIMEOUT) 
	    // in milliseconds which is the timeout for waiting for data.
	    int timeoutSocket = 1500;
	    HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
	    DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);
	    //Log.d("Slime", "defaulthttpclient = " + httpClient);

	    //DefaultHttpClient httpClient = new DefaultHttpClient();

	    httpClient.getParams().setParameter("http.protocol.content-charset", "UTF-8");
	    //Log.d("Slime", "setParameter");

	    HttpGet request = new HttpGet(urlstr);
	    //Log.d("Slime", "request = " + request);
	    HttpResponse httpResponse = null;
	    try {
		//Log.d("Slime", "Google Execute");
		httpResponse = httpClient.execute(request);
		//Log.d("Slime", "Response get");
	    } catch (Exception e) {
		//Log.d("HttpSampleActivity", "Error Execute");
	    }
	    int status = httpResponse.getStatusLine().getStatusCode();
	    if (HttpStatus.SC_OK == status) {
		try {
		    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		    jsonText = EntityUtils.toString(httpResponse.getEntity(), "UTF-8"); // これが大事らしいが...
		} catch (Exception e) {
		    //Log.d("Slime HttpSampleActivity", "Error");
		}
	    } else {
		//Log.d("Slime HttpSampleActivity", "Status" + status);
	    }

	    // http://www.google.co.jp/ime/cgiapi.html
	    // "ここではきものをぬぐ" のようなパタンを与えたとき、
	    // Google日本語入力は以下のようなJSONテキストを返す
	    // [
	    //   ["ここでは",
	    //     ["ここでは", "個々では", "此処では"],
	    //   ],
	    //   ["きものを",
	    //     ["着物を", "きものを", "キモノを"],
	    //   ],
	    //   ["ぬぐ",
	    //     ["脱ぐ", "ぬぐ", "ヌグ"],
	    //   ],
	    // ]
	    // これを読んで適当に候補を生成する
	    try {
		JSONArray ja1, ja2, ja3;
		int len1, len3;
		ja1 = new JSONArray(jsonText);
		len1 = ja1.length();
		int i = 0;
		ja2 = ja1.getJSONArray(i);
		ja3 = ja2.getJSONArray(1); // 第2要素 = 変換候補
		len3 = ja3.length();
		for(nsuggest=0; nsuggest<len3 && nsuggest < maxSuggestions; nsuggest++){
		    suggestions[nsuggest] = ja3.getString(nsuggest);
		}
		suggestions[nsuggest] = "";
		for(i=1; i<len1; i++){
		    ja2 = ja1.getJSONArray(i);
		    // String s = ja2.getString(0); // 第1要素 = 元の文字列
		    ja3 = ja2.getJSONArray(1); // 第2要素 = 変換候補
		    for(int j=0; j<nsuggest; j++){
			suggestions[j] += ja3.getString(0); // ふたつめ以降は最初の候補を連結する
		    }
		}
	    }
	    catch(JSONException e){
		Log.e("Slime", "JSON Exception " + e);
	    }
	} catch (Exception e){
	    Log.v("Slime","GoogleIME error");
	}
	return suggestions;
    }
}
