package com.mrbot.activiti;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

public class RESTUtil {

	public String post(String url, String data, String token) throws Exception {
		System.out
				.println(String
						.format("curl -H 'Authorization:Basic %s' -H 'Content-Type: application/json' %s -d '%s'",
								token, url, data));

		URL restURL = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) restURL.openConnection();
		conn.setDoOutput(true);
		conn.setAllowUserInteraction(false);
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestProperty("Accept", "application/json");
		conn.setRequestProperty("Authorization", "Basic " + token);
		conn.setRequestMethod("POST");

		// send data 方法1
		/*
		 * byte[] outputBytes = data.getBytes("UTF-8"); OutputStream os =
		 * conn.getOutputStream(); os.write(outputBytes); os.close();
		 */

		// send data 方法2
		final OutputStreamWriter osw = new OutputStreamWriter(
				conn.getOutputStream());
		osw.write(data);
		osw.close();

		// receive data
		BufferedReader bReader = new BufferedReader(new InputStreamReader(
				conn.getInputStream()));
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
						.format("curl -X DELETE -H 'Authorization:Basic %s' -H 'Content-Type: application/json' %s",
								token, url));
		URL restURL = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) restURL.openConnection();
		conn.setDoOutput(true);
		conn.setAllowUserInteraction(false);
		conn.setRequestProperty("Authorization", "Basic " + token);
		conn.setRequestMethod("DELETE"); // type: POST, PUT, DELETE, GET
		// 若response code 返回204， 表示删除成功：则响应执行成功，但没有数据返回
		return String.format("%s %s", conn.getResponseCode(),
				conn.getResponseMessage());
	}

	/*
	 * 以下使用HTTPClient调用REST api
	 */
	@SuppressWarnings("deprecation")
	public String post2(String url, String json, String token) {

		try {
			DefaultHttpClient httpClient = new DefaultHttpClient();
			// POST
			HttpPost postRequest = new HttpPost(url);
			// Header
			postRequest.addHeader("Authorization", "Basic " + token);
			// post data
			StringEntity input = new StringEntity(json);
			input.setContentType("application/json");
			postRequest.setEntity(input);

			// send post
			HttpResponse response = httpClient.execute(postRequest);
			if (response.getStatusLine().getStatusCode() != 201) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatusLine().getStatusCode());
			}
			BufferedReader br = new BufferedReader(new InputStreamReader(
					(response.getEntity().getContent())));
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