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

import org.apache.http.util.EntityUtils;

class GoogleSuggest {
    static String[] suggest(String q){
	//String url_str = "http://google.co.jp/complete/search?output=toolbar&hl=ja&ie=utf-8&q=" + q;
	//String url_str = "http://210.143.110.224/googlesuggest.cgi?q=" + q;
	//String url_str = "http://pitecan.com/googlesuggest.cgi?q=" + q;
	String[] suggestions;
	final int maxSuggestions = 20;
	suggestions = new String[maxSuggestions+1];
	int nsuggest = 0;
	Log.v("Slime","Suggest!");

	String text = new String();

	DefaultHttpClient httpClient = new DefaultHttpClient();
        httpClient.getParams().setParameter("http.protocol.content-charset", "UTF-8");

	String urlstr = "http://google.co.jp/complete/search?output=toolbar&hl=ja&q=" + q;
	//HttpGet request = new HttpGet("http://google.co.jp/complete/search?output=toolbar&hl=ja&q=" + q);
        HttpGet request = new HttpGet(urlstr);

	HttpResponse httpResponse = null;
	try {
	    httpResponse = httpClient.execute(request);
	} catch (Exception e) {
	    Log.d("HttpSampleActivity", "Error Execute");
	}
	int status = httpResponse.getStatusLine().getStatusCode();
	if (HttpStatus.SC_OK == status) {
	    try {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		// httpResponse.getEntity().writeTo(outputStream);
		// text = outputStream.toString();
		text = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
		Log.v("Slime",text);
	    } catch (Exception e) {
		Log.d("Slime HttpSampleActivity", "Error");
	    }
	} else {
	    Log.d("Slime HttpSampleActivity", "Status" + status);
	}
	Pattern pat = Pattern.compile("suggestion data=\"([^\"]*)\"/>"); // Google Suggestのフォーマット
	Matcher matcher = pat.matcher(text);
	while(matcher.find() && nsuggest < maxSuggestions){
	    Log.v("Slime","matcher.group(1) = "+matcher.group(1));
	    suggestions[nsuggest++] = matcher.group(1);
	}


	//	try {
	//	    URL url = new URL(url_str);
	//	    HttpURLConnection http = (HttpURLConnection)url.openConnection();
	//	    http.setRequestMethod("GET");
	//	    Log.v("Slime","get");
	//	    http.connect();
	//	    Log.v("Slime","connect");
	//	    InputStream in = http.getInputStream();
	//	    // HTMLソースを読み出す
	//	    String text = new String();
	//	    byte[] line = new byte[1024];
	//	    int size;
	//	    while(true) {
	//		size = in.read(line);
	//		if(size <= 0) break;
	//		text += new String(line);
	//	    }
	//	    in.close();
	//	    Log.v("Slime","text="+text);
	//	    /*
	//	    BufferedInputStream bis = new BufferedInputStream(http.getInputStream());
	//	    Log.v("Slime","bis");
	//	    String text = "";
	//	    byte[] fbytes = new byte[1024];  
	//	    while ((bis.read(fbytes)) >= 0) {
	//		text += new String(fbytes);  
	//	    }
	//	    Log.v("Slime","text="+text);
	//	    bis.close();
	//	    */
	//	    Pattern pat = Pattern.compile("suggestion data=\"([^\"]*)\"/>"); // Google Suggestのフォーマット
	//	    Matcher matcher = pat.matcher(text);
	//	    while(matcher.find() && nsuggest < maxSuggestions){
	//		Log.v("Slime","matcher.group(1) = "+matcher.group(1));
	//		suggestions[nsuggest++] = matcher.group(1);
	//	    }
	//	    Log.v("Slime","xxxx");
	//	}
	//	catch( IOException e ){
	//	    Log.v("Slime","error = "+e.toString());
	//	}

	suggestions[nsuggest] = "";
	return suggestions;
    }
}

