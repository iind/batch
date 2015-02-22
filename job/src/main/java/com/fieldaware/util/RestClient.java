package com.fieldaware.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class RestClient {
	
	private String url;
	private String authToken;
	
	public RestClient(){}
	
	public RestClient(String url, String authToken){
		this.url = url;
		this.authToken = authToken;
	}
	
	private static SSLContext buildSSLContext()
			throws NoSuchAlgorithmException, KeyManagementException,
			KeyStoreException {
		SSLContext sslcontext = SSLContexts.custom()
				.setSecureRandom(new SecureRandom())
				.loadTrustMaterial(null, new TrustStrategy() {

					public boolean isTrusted(X509Certificate[] chain,
							String authType) throws CertificateException {
						return true;
					}
				}).build();
		return sslcontext;
	}
	
	public JsonElement getMethod(String reqUrl) throws KeyManagementException, NoSuchAlgorithmException, 
			KeyStoreException, ClientProtocolException, IOException{
		
		// Trust all certs
		SSLContext sslcontext = buildSSLContext();

		// Allow TLSv1 protocol only
		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
				sslcontext, new String[] { "TLSv1" }, null,
				SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

		CloseableHttpClient httpclient = HttpClients.custom()
				.setSSLSocketFactory(sslsf).build();
		try {

			HttpGet httpget = new HttpGet(url + reqUrl);
			httpget.addHeader("Authorization", authToken);
			
			CloseableHttpResponse response = httpclient.execute(httpget);
			try {
				HttpEntity entity = response.getEntity();
				BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
				 
				StringBuffer result = new StringBuffer();
				String line = "";
				while ((line = rd.readLine()) != null) {
					result.append(line);
				}
				JsonElement root = new JsonParser().parse(result.toString());
				EntityUtils.consume(entity);
				return root;
			} finally {
				response.close();
			}
		} finally {
			httpclient.close();
		}
	}
	
	public JsonElement postMethod(String reqUrl, String json) throws KeyManagementException, NoSuchAlgorithmException, 
			KeyStoreException, ClientProtocolException, IOException{
	
		// Trust all certs
		SSLContext sslcontext = buildSSLContext();
		
		// Allow TLSv1 protocol only
		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
				sslcontext, new String[] { "TLSv1" }, null,
				SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		
		CloseableHttpClient httpclient = HttpClients.custom()
				.setSSLSocketFactory(sslsf).build();
		try {
			StringEntity params =new StringEntity(json);
			HttpPost httpPost = new HttpPost(url + reqUrl);
			httpPost.addHeader("Authorization", authToken);
			httpPost.addHeader("content-type", "application/json");
			httpPost.setEntity(params);
			
			CloseableHttpResponse response = httpclient.execute(httpPost);
			try {
				HttpEntity entity = response.getEntity();
				JsonElement root = null;
				if(entity != null){
					BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
					 
					StringBuffer result = new StringBuffer();
					String line = "";
					while ((line = rd.readLine()) != null) {
						result.append(line);
					}
					 new JsonParser().parse(result.toString());
					EntityUtils.consume(entity);
				}
				return root;
			} finally {
				response.close();
			}
		} finally {
			httpclient.close();
		}
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getAuthToken() {
		return authToken;
	}

	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}
}
