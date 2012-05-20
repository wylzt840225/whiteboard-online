package com.me.whiteboard.http;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class JsonTransfer {
	static String url0 = "http://whiteboard.aliapp.com";

	static public String httpTransfor(String url, boolean post, String Data) {
		url = url0 + url;
		HttpUriRequest request;
		if (post) {
			request = new HttpPost(url);

			try {
				((HttpPost) request).setEntity(new StringEntity(Data));
			} catch (UnsupportedEncodingException e) {
				return "!error parsing";
			}
		} else {
			request = new HttpGet(url);
		}
		String resultString = "";
		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpResponse httpResponse = httpClient.execute(request);
			int p = httpResponse.getStatusLine().getStatusCode();
			if (p != 200)
				return "!error";
			resultString = EntityUtils.toString(httpResponse.getEntity(),
					"UTF-8");

		} catch (IOException e) {
			resultString = "!error";
		}
		return resultString;
	}

	static public String httpTransfor(String url) {
		return httpTransfor(url, false, null);
	}

}
