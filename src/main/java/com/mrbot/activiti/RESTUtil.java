package com.mrbot.activiti;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

public class RESTUtil {

	String _splitter="----------------------------------------------------------------------------------------------------------------";
	
	public String get(String url, String token) throws Exception {
		System.out
				.println(String
						.format("%s\ncurl -X GET -H 'Authorization:Basic %s' -H 'Content-Type: application/json' %s \n%s",
								_splitter,token, url, _splitter));

		URL restURL = new URL(url);
        // ����ƴ�յ�URL�������ӣ�URL.openConnection��������� URL�����ͣ�    
        // ���ز�ͬ��URLConnection����Ķ�������URL��һ��http�����ʵ�ʷ��ص���HttpURLConnection    
		HttpURLConnection conn = (HttpURLConnection) restURL.openConnection();
//		conn.setDoOutput(true);
//		conn.setAllowUserInteraction(false);
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestProperty("Authorization", "Basic " + token);
		conn.setRequestMethod("GET");

  
        // �������ӣ�����ʵ����get requestҪ����һ��� connection.getInputStream()�����вŻ���������    
        // ������    
        conn.connect();    
        // ȡ������������ʹ��Reader��ȡ    
        BufferedReader reader = new BufferedReader(new InputStreamReader(    
                conn.getInputStream()));       
        String lines;
        String resultStr="";
        while ((lines = reader.readLine()) != null) {    
        	resultStr += lines;    
        }    
        reader.close();    
        // �Ͽ�����    
        conn.disconnect(); 
		return resultStr;
	}
	
	public String post(String url, String data, String token) throws Exception {
		System.out
				.println(String
						.format("%s\ncurl -X POST -H 'Authorization:Basic %s' -H 'Content-Type: application/json' %s -d '%s'\n%s",
								_splitter,token, url, data,_splitter));

		URL restURL = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) restURL.openConnection();
		conn.setDoOutput(true);
		conn.setAllowUserInteraction(false);
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestProperty("Accept", "application/json");
		conn.setRequestProperty("Authorization", "Basic " + token);
		conn.setRequestMethod("POST");

		// send data ����1
		byte[] outputBytes = data.getBytes("UTF-8");//�ؼ��������ַ����룬���������յ�����
		OutputStream os = conn.getOutputStream();
		os.write(outputBytes);
		os.close();

		// send data ����2
//		final OutputStreamWriter osw = new OutputStreamWriter(	conn.getOutputStream(), "UTF-8") ; //�ؼ��������ַ����룬���������յ�����
//		osw.write(data);
//		osw.close();

		// receive data
		BufferedReader bReader = new BufferedReader(new InputStreamReader( conn.getInputStream()));
		String line, resultStr = "";
		while (null != (line = bReader.readLine())) {
			resultStr += line;
		}
		bReader.close();
		return resultStr;
	}


	public String put(String url, String data, String token) throws Exception {
		System.out
				.println(String
						.format("%s\ncurl -X PUT -H 'Authorization:Basic %s' -H 'Content-Type: application/json' %s -d '%s'\n%s",
								_splitter,token, url, data,_splitter));

		URL restURL = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) restURL.openConnection();
		conn.setDoOutput(true);
		conn.setAllowUserInteraction(false);
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestProperty("Accept", "application/json");
		conn.setRequestProperty("Authorization", "Basic " + token);
		conn.setRequestMethod("PUT");

		// send data ����1
		byte[] outputBytes = data.getBytes("UTF-8");//�ؼ��������ַ����룬���������յ�����
		OutputStream os = conn.getOutputStream();
		os.write(outputBytes);
		os.close();

		// send data ����2
//		final OutputStreamWriter osw = new OutputStreamWriter(	conn.getOutputStream(), "UTF-8") ; //�ؼ��������ַ����룬���������յ�����
//		osw.write(data);
//		osw.close();

		// receive data
		BufferedReader bReader = new BufferedReader(new InputStreamReader( conn.getInputStream()));
		String line, resultStr = "";
		while (null != (line = bReader.readLine())) {
			resultStr += line;
		}
		bReader.close();
		return resultStr;
	}

	
	public String delete(String url, String token) throws Exception {
		System.out
				.println(String
						.format("%s\ncurl -X DELETE -H 'Authorization:Basic %s' -H 'Content-Type: application/json' %s\n%s",
								_splitter,token, url,_splitter));
		URL restURL = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) restURL.openConnection();
		conn.setDoOutput(true);
		conn.setAllowUserInteraction(false);
		conn.setRequestProperty("Authorization", "Basic " + token);
		conn.setRequestMethod("DELETE"); // type: POST, PUT, DELETE, GET
		// ��response code ����204�� ��ʾɾ���ɹ�������Ӧִ�гɹ�����û�����ݷ���
		return String.format("%s %s", conn.getResponseCode(), conn.getResponseMessage());
	}

	/*
	 * ����ʹ��HTTPClient����REST api
	 */
	@SuppressWarnings("deprecation")
	public String post2(String url, String json, String token) {
		System.out
		.println(String
				.format("%s\ncurl -X POST -H 'Authorization:Basic %s' -H 'Content-Type: application/json' %s -d '%s'\n%s",
						_splitter,token, url, json,_splitter));
		try {
			DefaultHttpClient httpClient = new DefaultHttpClient();
			// POST
			HttpPost postRequest = new HttpPost(url);
			// Header
			postRequest.addHeader("Authorization", "Basic " + token);
			// post data
			StringEntity input = new StringEntity(json, "UTF-8");//�ؼ��������ַ����룬���������յ�����
			input.setContentType("application/json");
			postRequest.setEntity(input);

			// send post
			HttpResponse response = httpClient.execute(postRequest);
			if (response.getStatusLine().getStatusCode() != 201 && response.getStatusLine().getStatusCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatusLine().getStatusCode());
			}
			BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent()),"UTF-8"));
			String output;
			System.out.println("Output from Server .... \n");
			StringBuilder sb = new StringBuilder();
			while ((output = br.readLine()) != null) {
				sb.append(output);
			}
			httpClient.getConnectionManager().shutdown();
			return sb.toString();
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return "failed: MalformedURLException";
		} catch (IOException e) {
			e.printStackTrace();
			return "failed: IOException";
		}

	}


	/*
	 * ����ʹ��HTTPClient����REST api
	 */
	@SuppressWarnings("deprecation")
	public String put2(String url, String json, String token) {
		System.out
		.println(String
				.format("%s\ncurl -X PUT -H 'Authorization:Basic %s' -H 'Content-Type: application/json' %s -d '%s'\n%s",
						_splitter,token, url, json,_splitter));
		try {
			DefaultHttpClient httpClient = new DefaultHttpClient();
			// POST
			HttpPut putRequest = new HttpPut(url);
			// Header
			putRequest.addHeader("Authorization", "Basic " + token);
			// post data
			StringEntity input = new StringEntity(json, "UTF-8");//�ؼ��������ַ����룬���������յ�����
			input.setContentType("application/json");
			putRequest.setEntity(input);

			// send post
			HttpResponse response = httpClient.execute(putRequest);
			if (response.getStatusLine().getStatusCode() != 201 && response.getStatusLine().getStatusCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatusLine().getStatusCode());
			}
			BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent()),"UTF-8"));
			String output;
			System.out.println("Output from Server .... \n");
			StringBuilder sb = new StringBuilder();
			while ((output = br.readLine()) != null) {
				sb.append(output);
			}
			httpClient.getConnectionManager().shutdown();
			return sb.toString();
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return "failed: MalformedURLException";
		} catch (IOException e) {
			e.printStackTrace();
			return "failed: IOException";
		}

	}
	
	@SuppressWarnings("deprecation")
	public String delete2(String url, String token) {
		System.out
		.println(String
				.format("%s\ncurl -X DELETE -H 'Authorization:Basic %s' -H 'Content-Type: application/json' %s\n%s",
						_splitter,token, url,_splitter));
		try {
			DefaultHttpClient httpClient = new DefaultHttpClient();
			// delete
			HttpDelete deleteRequest = new HttpDelete(url);
			// Header
			deleteRequest.addHeader("Authorization", "Basic " + token);

			// send post
			HttpResponse response = httpClient.execute(deleteRequest);
			String result = "";
			if (response.getStatusLine().getStatusCode() != 204) {
				result = String.format("%s : %s", response.getStatusLine()
						.getStatusCode(), response.getStatusLine()
						.getReasonPhrase());
			} else {
				BufferedReader br = new BufferedReader(new InputStreamReader(
						(response.getEntity().getContent())));
				String output;
				System.out.println("Output from Server .... \n");
				StringBuilder sb = new StringBuilder();
				while ((output = br.readLine()) != null) {
					sb.append(output);
				}
				result = sb.toString();
			}
			httpClient.getConnectionManager().shutdown();
			return result;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return "failed: MalformedURLException";
		} catch (IOException e) {
			e.printStackTrace();
			return "failed: IOException";
		}

	}

}