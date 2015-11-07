package com.mrbot.activiti;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.delegate.DelegateExecution;
import com.fasterxml.jackson.databind.ObjectMapper;

public class WeChatDelegate implements JavaDelegate {

	private final Logger log = Logger.getLogger(WeChatDelegate.class.getName());

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		// http://activiti.org/javadocs/org/activiti/engine/delegate/DelegateExecution.html

		String activityId = execution.getCurrentActivityId();
		String activityName = execution.getCurrentActivityName();

		log.info(String.format("WeChatDelegate() 定时任务开始执行: execution id: %s, activityId: %s, activityName: %s "
				, execution.getId(), activityId, activityName ));

		// current time
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ");
		Calendar now = Calendar.getInstance();
		String[] allow_list = {"servicetask-remind","servicetask-confirm"};
		if ( Arrays.asList(allow_list).contains(activityId) ){
			try{
				log.info(String.format("\n----------------------------\n%s(%s)\n----------------------------",
					activityId, activityName));

				// 直接获取和输出所有变量
				//log.info(String.format("\n当前时间: %s\n收到数据: %s", df.format(now.getTime()), execution.getVariables()));

				log.info(String.format("\n当前时间: %s\n收到数据如下:", df.format(now.getTime())));
				Map<String, Object> var_map = execution.getVariables(); 
				for (String key : var_map.keySet()) {
					System.out.println("key: "+key+", type: "+execution.getVariable(key).getClass().getName()+", value: "+ var_map.get(key));
				}

				//解析输入数据
				@SuppressWarnings("unused")
				String content_json = execution.getVariable("content_json").toString();
				@SuppressWarnings("unused")
				String start_time = execution.getVariable("start_time").toString();
				@SuppressWarnings("unused")
				String end_time = execution.getVariable("end_time").toString();
				@SuppressWarnings("unused")
				String receiver_name_list = execution.getVariable("receiver_name_list").toString();
				@SuppressWarnings("unused")
				String sender_name = execution.getVariable("sender_name").toString();
				@SuppressWarnings("unused")
				String sender_uid = execution.getVariable("sender_uid").toString();
				String token = execution.getVariable("token").toString();
				String url = execution.getVariable("url").toString();
				@SuppressWarnings("rawtypes")
				ArrayList receiver_detail = (ArrayList)execution.getVariable("receiver_detail");
				@SuppressWarnings({ "unchecked" })
				LinkedHashMap<String, Object> content = (LinkedHashMap<String, Object>)execution.getVariable("content");

				@SuppressWarnings("rawtypes")
				Iterator receiver_list = receiver_detail.iterator();
				int total_to_send = receiver_detail.size();
				int index = 0;
				while(receiver_list.hasNext()){
					index++;
					//准备参数
					@SuppressWarnings("unchecked")
					Map<String, Object> receiver = (Map<String, Object>)receiver_list.next();
					String alias = receiver.get("alias").toString();
					String uid = receiver.get("uid").toString();
					String open_id = receiver.get("open_id").toString();
					System.out.println("[Receiver] alias: "+alias+", uid: "+ uid+", open_id: "+ open_id);
				
					//生成待post的数据
					ObjectMapper mapper = new ObjectMapper();
					Map<String, Object> post_map = null;
					post_map = new HashMap<String, Object>();
					post_map.put("open_id", open_id);
					post_map.put("content", content);

					//添加type参数到待post的数据中
					if (activityId.equals("servicetask-remind")) {	
						post_map.put("type","remind");
					} else if (activityId.equals("servicetask-confirm")) {
						post_map.put("type","confirm");
					}

					// map转json string
					String data = mapper.writeValueAsString(post_map);
					System.out.println(String.format("服务端时间：%s,\n待post到微信的数据: %s",df.format(now.getTime()), data));

					// 调用微信api
					try {
						RESTUtil restUtil = new RESTUtil();
						String result = restUtil.post(url, data, token);
						System.out.println("("+index+"/"+total_to_send+")[调用微信api成功]("+sender_name+"->"+alias+"): "+result);
					} catch (Exception e) {
						System.out.println("("+index+"/"+total_to_send+")[调用微信api异常]("+sender_name+"->"+alias+"): "+e.getMessage());
						continue;
					}
				}
			}
			catch(Exception e){
				log.info(String.format("处理 %s 时出错: %s\n输入数据: %s",e.getLocalizedMessage(),execution.getVariables()));
			}
		}
		else{
			log.info(String
					.format("\n----------------------------\n未知activity： %s(%s)\n----------------------------",
							activityId, activityName));
		}
	}
}
