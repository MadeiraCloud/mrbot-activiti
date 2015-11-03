package com.mrbot.activiti.test;

import static org.junit.Assert.*;

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
					.println(":) testLoad() with DefaultHttpClient. result:\n"
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

}
