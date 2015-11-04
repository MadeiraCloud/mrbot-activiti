package com.mrbot.activiti.test;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mrbot.activiti.RESTUtil;

public class RESTUtilTest {

	@Test
	public void testCreateUser() {
		/* create user with activit-rest api */

		Map<String, Object> map = null;
		map = new HashMap<String, Object>();
		map.put("id", "test");
		map.put("firstName", "test");
		map.put("lastName", "test");
		map.put("email", "test@mc2.io");
		map.put("password", "aaa123aa");

		// map转json string
		String json = "{}";
		try {
			ObjectMapper mapper = new ObjectMapper();
			json = mapper.writeValueAsString(map);
			System.out.println(":) json:" + json);
		} catch (JsonProcessingException e1) {
			System.out.println(":( generate json error");
			e1.printStackTrace();
		}

		try {
			RESTUtil util = new RESTUtil();
			String url = "http://192.168.22.133:8888/activiti-rest/service/identity/users";
			String token = "kermit:kermit";
			String token_b64 = Base64.encodeBase64String(token.getBytes());

			// 方法1： 使用HttpURLConnection
			System.out
			.println(":) testCreateUser() with HttpURLConnection. result:\n"
					+ util.post(url, json, token_b64));

			// 方法2： 使用HTTPClient
			System.out
			.println(":) testCreateUser() with DefaultHttpClient. result:\n"
					+ util.post2(url, json, token_b64));
		} catch (Exception e) {
			System.out.println(":( call REST api error\n"
					+ e.getLocalizedMessage());
		}
	}

	@Test
	public void testDelUser() {
		/* delete user with activit-rest api */
		try {
			RESTUtil util = new RESTUtil();
			String url = "http://192.168.22.133:8888/activiti-rest/service/identity/users/test";
			String token = "kermit:kermit";
			String token_b64 = Base64.encodeBase64String(token.getBytes());

			// 方法1：使用HttpURLConnection
			System.out
			.println(":) testDelUser() with HttpURLConnection result:\n"
					+ util.delete(url, token_b64));

			// 方法2：使用HTTPClient
			System.out.println(":) testDelUser() with HTTPClient result:\n"
					+ util.delete2(url, token_b64));
		} catch (Exception e) {
			System.out.println(":( call REST api error");
			e.printStackTrace();
		}
	}


	@Test
	public void testNotify() {
		/* call WeChat api */
		ObjectMapper mapper = new ObjectMapper();
		String json = "{}";

		//json for content
		Map<String, Object> content_map = null;
		content_map = new HashMap<String, Object>();

		Map<String, Object> _map = new HashMap<String, Object>();
		_map.put("value", "test data"); _map.put("color", "#ccc");
		content_map.put("first", _map);

		_map = new HashMap<String, Object>();
		_map.put("value", "this is a test"); _map.put("color", "#0f0");
		content_map.put("keyword1", _map);

		_map = new HashMap<String, Object>();
		_map.put("value", "today"); _map.put("color", "#00f"); content_map.put("keyword2", _map);

		_map = new HashMap<String, Object>();
		_map.put("value", "finish description"); _map.put("color", "#000"); content_map.put("remark", _map);

		try {
			// map转json string
			json = mapper.writeValueAsString(content_map);
			System.out.println(":) json:" + json);
		} catch (JsonProcessingException e2) {
			System.out.println(":( generate notify_map json error");
			e2.printStackTrace();
		}

		//json for notify
		Map<String, Object> notify_map = null;
		notify_map = new HashMap<String, Object>();
		notify_map.put("open_id", "oKqDPsw0ZJDDI37fqoQq7wWYnJj0");
		notify_map.put("content", content_map );
		try {
			// map转json string
			json = mapper.writeValueAsString(notify_map);
			System.out.println(":) json:" + json);
		} catch (JsonProcessingException e1) {
			System.out.println(":( generate json error");
			e1.printStackTrace();
		}

		try {
			RESTUtil util = new RESTUtil();
			String url = "http://wxbot.jiecao.pw/api/notify";
			String token = "Ig8dian2lI9griRd3I6eM7Pet1yaRc6e4tWo2harb1Ov2Nich9";
			String token_b64 = Base64.encodeBase64String(token.getBytes());

			// 方法1： 使用HttpURLConnection
			System.out
			.println(":) testNotify() with HttpURLConnection. result:\n"
					+ util.post(url, json, token_b64));

			// 方法2： 使用HTTPClient
			//			System.out
			//					.println(":) testNotify() with DefaultHttpClient. result:\n"
			//							+ util.post2(url, json, token_b64));
		} catch (Exception e) {
			System.out.println(":( call REST api error\n"
					+ e.getLocalizedMessage());
		}
	}


	@Test
	public void testNotify2() {
		/* call WeChat api (read json from notify.json) */
		String encoding="UTF-8";
		String json_file = "D:\\Project\\github\\mr.bot\\source\\mrbot-activiti\\src\\test\\java\\com\\mrbot\\activiti\\test\\json\\notify.json";
		File file=new File(json_file);
		InputStreamReader read = null;
		StringBuilder sb = new StringBuilder();
		if(file.isFile() && file.exists()){ //判断文件是否存在
			try {
				read = new InputStreamReader(new FileInputStream(file),encoding);
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				while((lineTxt = bufferedReader.readLine()) != null){
					sb.append(lineTxt);
				}
				read.close();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else{
			System.out.println("找不到指定的文件:"+json_file);
		}

		String json = sb.toString();
		try {
			RESTUtil util = new RESTUtil();
			String url = "http://wxbot.jiecao.pw/api/notify";
			String token = "Ig8dian2lI9griRd3I6eM7Pet1yaRc6e4tWo2harb1Ov2Nich9";
			String token_b64 = Base64.encodeBase64String(token.getBytes());

			// 方法1： 使用HttpURLConnection
			System.out
			.println(":) testNotify() with HttpURLConnection. result:\n"
					+ util.post(url, json, token_b64));

			// 方法2： 使用HTTPClient
//			System.out
//					.println(":) testNotify() with DefaultHttpClient. result:\n"
//							+ util.post2(url, json, token_b64));
		} catch (Exception e) {
			System.out.println(":( call REST api error\n"
					+ e.getLocalizedMessage());
		}
	}


}
