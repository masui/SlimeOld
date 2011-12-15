package com.pitecan.slime;

import android.util.Log;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.*;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;

import org.apache.http.params.HttpParams;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams; 

import org.apache.http.util.EntityUtils;

class GoogleSuggest {
    static String[] suggest(String q){
	String urlstr = "http://google.co.jp/complete/search?output=toolbar&hl=ja&q=" + q;
	String[] suggestions;
	final int maxSuggestions = 20;
	suggestions = new String[maxSuggestions+1];
	int nsuggest = 0;

	String text = new String();

	try {
	    // http://stackoverflow.com/questions/693997/how-to-set-httpresponse-timeout-for-android-in-java
	    HttpParams httpParameters = new BasicHttpParams();
	    // Set the timeout in milliseconds until a connection is established.
	    int timeoutConnection = 1500;
	    HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
	    // Set the default socket timeout (SO_TIMEOUT) 
	    // in milliseconds which is the timeout for waiting for data.
	    int timeoutSocket = 1500;
	    HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
	    DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);

	    //DefaultHttpClient httpClient = new DefaultHttpClient();

	    httpClient.getParams().setParameter("http.protocol.content-charset", "UTF-8");

	    HttpGet request = new HttpGet(urlstr);
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
		    text = EntityUtils.toString(httpResponse.getEntity(), "UTF-8"); // これが大事らしいが...
		} catch (Exception e) {
		    //Log.d("Slime HttpSampleActivity", "Error");
		}
	    } else {
		//Log.d("Slime HttpSampleActivity", "Status" + status);
	    }
	    Pattern pat = Pattern.compile("suggestion data=\"([^\"]*)\"/>"); // Google Suggestのフォーマット
	    Matcher matcher = pat.matcher(text);
	    while(matcher.find() && nsuggest < maxSuggestions){
		//Log.v("Slime","matcher.group(1) = "+matcher.group(1));
		suggestions[nsuggest++] = matcher.group(1);
	    }
	} catch (Exception e){
	    Log.v("Slime","GoogleSuggest error");
	}

	suggestions[nsuggest] = "";
	return suggestions;
    }
}

