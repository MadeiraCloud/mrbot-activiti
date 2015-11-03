package com.mrbot.activiti;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.delegate.DelegateExecution;
import org.apache.commons.codec.binary.Base64;

import com.fasterxml.jackson.databind.ObjectMapper;

public class WeChatDelegate implements JavaDelegate {

	private final Logger log = Logger.getLogger(WeChatDelegate.class.getName());

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		// http://activiti.org/javadocs/org/activiti/engine/delegate/DelegateExecution.html

		String activityId = execution.getCurrentActivityId();
		String activityName = execution.getCurrentActivityName();
		/*
		 * log.info(String.format(
		 * "Executing Java Service - WeChatDelegate(): execution id: %s, activityId: %s, activityName: %s "
		 * , execution.getId(), activityId, activityName ));
		 */
		// current time
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ");
		Calendar now = Calendar.getInstance();
		if (activityId.equals("servicetask-remind")) {
			log.info(String
					.format("----------------------------\n%s(%s)\n----------------------------",
							activityId, activityName));

			// 获取map方法1
			Map<String, Object> map = null;
			map = new HashMap<String, Object>();
			map.put("content", execution.getVariable("content"));
			map.put("endTime", execution.getVariable("endTime"));
			map.put("receiverName", execution.getVariable("receiverName"));
			map.put("receiverUid", execution.getVariable("receiverUid"));
			map.put("senderName", execution.getVariable("senderName"));
			map.put("senderUid", execution.getVariable("senderUid"));
			map.put("startTime", execution.getVariable("startTime"));

			// 获取map方法2
			map = execution.getVariables();

			// map转json string
			ObjectMapper mapper = new ObjectMapper();
			String json = mapper.writeValueAsString(map);

			// output currentTime and json string
			log.info(String.format("currentTime: %s\njson: %s",
					df.format(now.getTime()), json));

			// 调用微信api
			// 方法1
			/*
			 * Client client = ClientBuilder.newClient(); WebTarget target =
			 * client.target("http://wxbot.jiecao.pw").path("notify");
			 * Invocation.Builder invocationBuilder =
			 * target.request(MediaType.APPLICATION_JSON); Response response =
			 * invocationBuilder.header("Authorization",
			 * String.format("Basic %s",token_b64)).post();
			 */
			try {
				RESTUtil restUtil = new RESTUtil();
				String url = "http://wxbot.jiecao.pw/notify";
				String data = "{'open_id':'okqdpsw0zjddi37fqoqq7wwynjj0','url':'custom_url','content':{'data':''}}";
				String token = "Ig8dian2lI9griRd3I6eM7Pet1yaRc6e4tWo2harb1Ov2Nich9";
				String token_b64 = Base64.encodeBase64String(token.getBytes());
				String result = restUtil.post(url, data, token_b64);
			} catch (Exception e) {
				System.out.print(e.getMessage());
			}
		} else if (activityId.equals("servicetask-confirm")) {
			log.info(String
					.format("----------------------------\n%s(%s)\n----------------------------",
							activityId, activityName));
			// 直接获取和输出所有变量
			log.info(String.format("currentTime: %s\nvariables=: %s",
					df.format(now.getTime()), execution.getVariables()));
		} else {
			log.info(String
					.format("----------------------------\n未知activity： %s(%s)\n----------------------------",
							activityId, activityName));
		}
	}
}
