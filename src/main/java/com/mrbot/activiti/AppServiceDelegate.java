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

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

import com.fasterxml.jackson.databind.ObjectMapper;

public class AppServiceDelegate implements JavaDelegate {

	private final Logger log = Logger.getLogger(AppServiceDelegate.class
			.getSimpleName());

	String className = "AppServiceDelegate";
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
			System.out.println("");
		}
	}

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		// http://activiti.org/javadocs/org/activiti/engine/delegate/DelegateExecution.html

		Calendar.getInstance().setTimeZone( TimeZone.getTimeZone("GMT+8"));
		
		String activityId = execution.getCurrentActivityId();
		String activityName = execution.getCurrentActivityName();
		
		this.curProcessInstanceId = execution.getProcessInstanceId();
		this.curActivityId = activityId;
		this.curExecutionId = execution.getId();

		show_title(String.format(
				"��ʼִ�ж�ʱ����(execution:%s)=> %s(%s)",
				execution.getId(), activityName, activityId),
				true
		);

		String[] allow_list = { "servicetask-finish", "servicetask-log" };
		if (Arrays.asList(allow_list).contains(activityId)) {
			try {
				// ֱ�ӻ�ȡ��������б���
				// System.out.println(String.format("\n��ǰʱ��: %s\n�յ�����: %s",
				// df.format(now.getTime()), execution.getVariables()));

				Map<String, Object> var_map = execution.getVariables();
				write_log(String.format("�յ�%d����������:", var_map.size()));
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

				write_log("������������");
				// ������������
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
				// ��������
				String appservice_token = execution.getVariable(
						"appservice_token").toString();
				String appservice_api = execution.getVariable("appservice_api")
						.toString();
				@SuppressWarnings({ "unchecked", "unused" })
				LinkedHashMap<String, Object> content = (LinkedHashMap<String, Object>) execution
						.getVariable("content");
				// sender and receiver info
				@SuppressWarnings("unused")
				String sender_name = execution.getVariable("sender_name")
						.toString();
				@SuppressWarnings("rawtypes")
				ArrayList receiver_detail = (ArrayList) execution
						.getVariable("receiver_detail");
				@SuppressWarnings({ "rawtypes", "unused" })
				Iterator receiver_list = receiver_detail.iterator();

				if (activityId.equals("servicetask-finish")) {
					//����Api Service: /activiti/process_instance (PUT)
					// ׼������
					String process_instance_id = execution
							.getProcessInstanceId();
					String tran_id = execution.getVariable("tran_id")
							.toString();
					write_log("process_instance_id: "
							+ process_instance_id + ", tran_id: " + tran_id);

					// ���ɴ�post������
					Map<String, Object> post_map = null;
					post_map = new HashMap<String, Object>();
					post_map.put("state", "done");

					// mapתjson string
					ObjectMapper mapper = new ObjectMapper();
					String data = mapper.writeValueAsString(post_map);
					write_log(String.format("��post��AppService������: %s", data));

					// ����AppService api
					try {
						RESTUtil restUtil = new RESTUtil();
						String result = restUtil.put(appservice_api + "/"
								+ process_instance_id + '/' + tran_id, data,
								appservice_token);
						write_log("[����AppService api�ɹ�](" + process_instance_id
								+ "," + tran_id + "): " + result);
					} catch (Exception e) {
						write_log("[����AppService api�쳣](" + process_instance_id
								+ "," + tran_id + "): " + e.getMessage());
					}
				} else if (activityId.equals("servicetask-log")) {
					@SuppressWarnings("unchecked")
					ArrayList<Object>user_state_array = (ArrayList<Object>) execution.getVariable("user_state");
					String remind_time = execution.getVariable("remind_time").toString();
					String remind_count = execution.getVariable("remind_count").toString();
					write_log(String.format(
							"[remind_time]:%s [remind_count]:%s [user_state]:%s", 
							remind_time,remind_count,user_state_array
						)
					);
					//����Api Service: /activiti/transaction (PUT)
					// ׼������
					String process_instance_id = execution
							.getProcessInstanceId();
					String tran_id = execution.getVariable("tran_id")
							.toString();
					write_log("process_instance_id: "
							+ process_instance_id + ", tran_id: " + tran_id);

					String[] _ary = appservice_api.split("//");
			        String appservice_api_root = _ary[0]+"//"+_ary[1].split("/")[0];
			        String url = appservice_api_root + "/activiti/transaction/";
			        System.out.println("url:"+url);
					
					// ���ɴ�post������
					Map<String, Object> post_map = null;
					post_map = new HashMap<String, Object>();
					post_map.put("tran_id",tran_id);
					post_map.put("remind_time",remind_time);
					post_map.put("remind_count",remind_count);
					post_map.put("user_state", user_state_array);

					// mapתjson string
					ObjectMapper mapper = new ObjectMapper();
					String data = mapper.writeValueAsString(post_map);
					write_log(String.format("��post��AppService������: %s", data));

					// ����AppService api
					try {
						RESTUtil restUtil = new RESTUtil();
						String result = restUtil.put(url , data, appservice_token);
						write_log("[����AppService api�ɹ�](" + process_instance_id
								+ "," + tran_id + "): " + result);
					} catch (Exception e) {
						write_log("[����AppService api�쳣](" + process_instance_id
								+ "," + tran_id + "): " + e.getMessage());
					}
				}
			} catch (Exception e) {
				write_log(String.format("���� %s ʱ����\n����: %s\n��������: %s",
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
			write_log(String
					.format("\n----------------------------\nδ֪activity�� %s(%s)\n----------------------------",
							activityId, activityName));
		}
		show_title(String.format(
				"����ִ�ж�ʱ����(execution:%s)=> %s(%s)",
				execution.getId(), activityId, activityName),
				false
		);
	}

}
