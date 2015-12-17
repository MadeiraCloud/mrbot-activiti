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
import java.util.TimeZone;
import java.util.logging.Logger;

import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.delegate.DelegateExecution;

import com.fasterxml.jackson.databind.ObjectMapper;

public class WeChatDelegate implements JavaDelegate {

	private final Logger log = Logger.getLogger(WeChatDelegate.class
			.getSimpleName());
	String className = "WeChatDelegate";
	DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	String curActivityId = "";
	String curExecutionId = "";
	String curProcessInstanceId = "";

	private void write_log(String message) {
		System.out.println(String.format("[%s]->%s()->%s->%s->%s: %s",
				df.format(Calendar.getInstance().getTime()), className,
				this.curActivityId, this.curProcessInstanceId, this.curExecutionId,
				message));
	}

	private void show_title(String title, boolean isHead) {
		if (isHead){
			System.out.println("\n");
		}
		System.out
				.println(String
						.format("----------------------------------------------------------------------------------------------------------------\n[%s] - %s(): %s\n----------------------------------------------------------------------------------------------------------------",
								df.format(Calendar.getInstance().getTime()),
								className, title));
		if (!isHead){
			System.out.println("\n");
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		// http://activiti.org/javadocs/org/activiti/engine/delegate/DelegateExecution.html

		String activityId = execution.getCurrentActivityId();
		String activityName = execution.getCurrentActivityName();

		this.curProcessInstanceId = execution.getProcessInstanceId();
		this.curActivityId = activityId;
		this.curExecutionId = execution.getId();

		show_title(String.format(
				"开始执行定时任务(execution:%s)=> %s(%s)",
				execution.getId(), activityName, activityId),
				true
		);

		String[] allow_list = { "servicetask-remind", "servicetask-confirm" };
		if (Arrays.asList(allow_list).contains(activityId)) {
			try {
				// 直接获取和输出所有变量
				// System.out.println(String.format("\n当前时间: %s\n收到数据: %s",
				// df.format(now.getTime()), execution.getVariables()));

				Map<String, Object> var_map = execution.getVariables();
				write_log(String.format("收到%d个参数如下:", var_map.size()));
				for (String key : var_map.keySet()) {
					if ("|receiver_name_list|sender_name|tran_id|duetime_type|cycle_time|raw_duetime|remind_time|remind_count|cycle_time|"
							.contains("|" + key + "|")) {
						System.out.println(
							String.format("%-20s=> %s", 
								key,
								//execution.getVariable(key).getClass().getName(),
								var_map.get(key)
							)
						);
					}
				}

				write_log("解析输入数据");
				// 解析输入数据
				@SuppressWarnings("unused")
				String duetime_type = execution.getVariable("duetime_type")
						.toString();
				@SuppressWarnings("unused")
				String cycle_time = execution.getVariable("cycle_time")
						.toString();
				@SuppressWarnings("unused")
				String end_time = execution.getVariable("end_time").toString();
				/*
				 * @SuppressWarnings("unused") String content_json =
				 * execution.getVariable("content_json").toString();
				 * 
				 * @SuppressWarnings("unused") String receiver_detail_json =
				 * execution.getVariable("receiver_detail_json").toString();
				 * 
				 * @SuppressWarnings("unused") String receiver_name_list =
				 * execution.getVariable("receiver_name_list").toString();
				 * 
				 * @SuppressWarnings("unused") String sender_uid =
				 * execution.getVariable("sender_uid").toString();
				 */
				// 解析参数
				String raw_duetime = execution.getVariable("raw_duetime")
						.toString();
				String wechat_token = execution.getVariable("wechat_token")
						.toString();
				String wechat_api = execution.getVariable("wechat_api")
						.toString();
				String appservice_token = execution.getVariable(
						"appservice_token").toString();
				String appservice_api = execution.getVariable("appservice_api")
						.toString();
				String tran_id = execution.getVariable("tran_id")
						.toString();
				
				@SuppressWarnings({ "unchecked" })
				LinkedHashMap<String, Object> content = (LinkedHashMap<String, Object>) execution
						.getVariable("content");
				// sender and receiver info
				String sender_name = execution.getVariable("sender_name")
						.toString();
				@SuppressWarnings("rawtypes")
				ArrayList receiver_detail = (ArrayList) execution
						.getVariable("receiver_detail");
				@SuppressWarnings("rawtypes")
				Iterator receiver_list = receiver_detail.iterator();

				// write_log("解析remind_time");
				//设置时区
				DateFormat _df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				_df.setTimeZone(TimeZone.getTimeZone("GMT+8"));
				String remind_time = _df.format(Calendar.getInstance().getTime());
				// write_log("解析remind_count");
				long remind_count = Long.parseLong(execution.getVariable(
						"remind_count").toString());
				remind_count++;

				write_log("获取有效接收者列表");
				String[] _ary = appservice_api.split("//");
		        String appservice_api_root = _ary[0]+"//"+_ary[1].split("/")[0];
		        String url = appservice_api_root + "/activiti/transaction/" + tran_id;
		        System.out.println("url:"+url);
		        HashMap<String,Object> user_map = null;
		        try {
					RESTUtil restUtil = new RESTUtil();
					String result_str = restUtil.get(url, appservice_token);
					//System.out.println("[调用AppService api成功](" + "," + tran_id + "): " + result_str);
					//json string to map
					user_map = new ObjectMapper().readValue(result_str, HashMap.class);
					System.out.println( ":) testGetTransaction() result(json to map):\n"+ user_map );
					System.out.println("user state:");
					for (String key : user_map.keySet()) {
						@SuppressWarnings("unchecked")
						HashMap<String,Object> _map = (HashMap<String, Object>) user_map.get(key);
						System.out.println( key +"=> "
								+ _map.get("is_valid_user").toString() + ", "
								+ _map.get("receive_enabled").toString() + ", " 
								+ _map.get("is_friend").toString()
						);
					}
				} catch (Exception e) {
					System.out.println("[调用AppService api异常](" + "," + tran_id + "): " + e.getMessage());
				}
		        
				write_log("逐个向接收者推送提醒");
				int total_to_send = receiver_detail.size();
				int index = 0;
				int index_send = 0;
				ArrayList<Object> user_state_array = new ArrayList<Object>();
				while (receiver_list.hasNext()) {
					index++;
					// 准备参数
					@SuppressWarnings("unchecked")
					Map<String, Object> receiver = (Map<String, Object>) receiver_list.next();
					String alias = receiver.get("alias").toString();
					String uid = receiver.get("uid").toString();
					String open_id = receiver.get("open_id").toString();

					//get user state
					HashMap<String,Object> _map = (HashMap<String, Object>) user_map.get(open_id);
					String receive_enabled = _map.get("receive_enabled").toString();
					String is_valid_user = _map.get("is_valid_user").toString();
					String is_friend = _map.get("is_friend").toString();

					if (receive_enabled.equals("true") && is_valid_user.equals("true") && is_friend.equals("true")){
						write_log("[send][Receiver] alias: " + alias + ", uid: " + uid + ", open_id: " + open_id + ", receive_enabled:" + receive_enabled + ", is_valid_user:" + is_valid_user + ", is_friend:" +is_friend);
						index_send++;
						// 生成待post的数据
						@SuppressWarnings("unchecked")
						Map<String, Object> keyword2 = (Map<String, Object>) content
								.get("keyword2");
						if (duetime_type.equals("end_time")){
							keyword2.put("value", String.format("%s",
									raw_duetime));
						}
						else{
							keyword2.put("value", String.format("%s(%s,第%d次)",
									raw_duetime, remind_time, remind_count));
						}
						Map<String, Object> post_map = null;
						post_map = new HashMap<String, Object>();
						post_map.put("content", content);
						post_map.put("type", activityId);
						post_map.put("open_id", open_id);
	
						// map转json string
						ObjectMapper mapper = new ObjectMapper();
						String data = mapper.writeValueAsString(post_map);
						// write_log(String.format("待post到微信的数据: %s", data));
	
						// 调用微信api
						try {
							RESTUtil restUtil = new RESTUtil();
							String result = restUtil.post(wechat_api, data,
									wechat_token);
							write_log("(" + index + "/" + total_to_send
									+ ")[调用微信api成功](" + sender_name + "->" + alias
									+ "): " + result);
							_map.put("send_result", "succeed");
						} catch (Exception e) {
							write_log("(" + index + "/" + total_to_send
									+ ")[调用微信api异常](" + sender_name + "->" + alias
									+ "): " + e.getMessage());
							_map.put("send_result", "failed");
						}
					}
					else{
						write_log("[skip][Receiver] alias: " + alias + ", uid: " + uid + ", open_id: " + open_id + ", receive_enabled:" + receive_enabled + ", is_valid_user:" + is_valid_user + ", is_friend:" +is_friend);
						_map.put("send_result", "skipped");
					}
					_map.put("user_id", uid);
					_map.put("alias", alias);
					_map.put("open_id", open_id);
					user_state_array.add(_map);
				}
				//提醒记录
				execution.setVariable("user_state", user_state_array);
				execution.setVariable("remind_time", remind_time);
				execution.setVariable("remind_count", remind_count);
				write_log(String.format(
						"[remind_time]:%s [remind_count]:%s [sent/total]:%d/%d", 
						execution.getVariable("remind_time").toString(),
						execution.getVariable("remind_count").toString(),
						index_send,index
				));
			} catch (Exception e) {
				write_log(String.format("处理 %s 时出错\n错误: %s\n输入数据: %s",
						activityId, e.getMessage(), execution.getVariables()));

				StackTraceElement[] trace = e.getStackTrace();
				StackTraceElement ste = trace[0];
				System.err.println("error occurred in method: "
						+ ste.getMethodName());
				System.err.println("                    file: "
						+ ste.getFileName());
				System.err.println("             line number: "
						+ ste.getLineNumber());
				System.out.println("");
			}
		} else {
			System.out
					.println(String
							.format("\n----------------------------\n未知activity： %s(%s)\n----------------------------",
									activityId, activityName));
		}
		show_title(String.format(
				"结束执行定时任务(execution:%s)=> %s(%s)",
				execution.getId(), activityId, activityName),
				false
		);
	}
}
