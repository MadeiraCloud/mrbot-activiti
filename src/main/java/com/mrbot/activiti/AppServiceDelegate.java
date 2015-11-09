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

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

import com.fasterxml.jackson.databind.ObjectMapper;

public class AppServiceDelegate implements JavaDelegate {

	private final Logger log = Logger.getLogger(AppServiceDelegate.class.getName());

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		// http://activiti.org/javadocs/org/activiti/engine/delegate/DelegateExecution.html

		String activityId = execution.getCurrentActivityId();
		String activityName = execution.getCurrentActivityName();

		log.info(String.format("AppServiceDelegate() ��ʱ����ʼִ��: execution id: %s, activityId: %s, activityName: %s "
				, execution.getId(), activityId, activityName ));

		// current time
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ");
		Calendar now = Calendar.getInstance();
		String[] allow_list = {"servicetask-finish"};
		if ( Arrays.asList(allow_list).contains(activityId) ){
			try{
				log.info(String.format("\n----------------------------\n%s(%s)\n----------------------------",
					activityId, activityName));

				// ֱ�ӻ�ȡ��������б���
				//log.info(String.format("\n��ǰʱ��: %s\n�յ�����: %s", df.format(now.getTime()), execution.getVariables()));

				log.info(String.format("\n��ǰʱ��: %s\n�յ���������:", df.format(now.getTime())));
				Map<String, Object> var_map = execution.getVariables(); 
				for (String key : var_map.keySet()) {
					System.out.println("key: "+key+", type: "+execution.getVariable(key).getClass().getName()+", value: "+ var_map.get(key));
				}

				//������������
				@SuppressWarnings("unused")
				String content_json = execution.getVariable("content_json").toString();
				@SuppressWarnings("unused")
				String receiver_detail_json = execution.getVariable("receiver_detail_json").toString();
				@SuppressWarnings("unused")
				String start_time = execution.getVariable("start_time").toString();
				@SuppressWarnings("unused")
				String end_time = execution.getVariable("end_time").toString();
				@SuppressWarnings("unused")
				String receiver_name_list = execution.getVariable("receiver_name_list").toString();
				String sender_name = execution.getVariable("sender_name").toString();
				@SuppressWarnings("unused")
				String sender_uid = execution.getVariable("sender_uid").toString();
				String appservice_token = execution.getVariable("appservice_token").toString();
				String appservice_api = execution.getVariable("appservice_api").toString();
				@SuppressWarnings("rawtypes")
				ArrayList receiver_detail = (ArrayList)execution.getVariable("receiver_detail");
				@SuppressWarnings({ "unchecked", "unused" })
				LinkedHashMap<String, Object> content = (LinkedHashMap<String, Object>)execution.getVariable("content");
				
				@SuppressWarnings("rawtypes")
				Iterator receiver_list = receiver_detail.iterator();
				int total_to_send = receiver_detail.size();
				int index = 0;
			 
				index++;
				//׼������
				@SuppressWarnings("unchecked")
				String process_instance_id = execution.getProcessInstanceId();
				String tran_id = execution.getVariable("tran_id").toString();
				log.info("process_instance_id: "+ process_instance_id+", tran_id: "+ tran_id);

				//���ɴ�post������
				ObjectMapper mapper = new ObjectMapper();
				Map<String, Object> post_map = null;
				post_map = new HashMap<String, Object>();
				post_map.put("state","done");

				// mapתjson string
				String data = mapper.writeValueAsString(post_map);
				log.info(String.format("�����ʱ�䣺%s,\n��post��AppService������: %s",df.format(now.getTime()), data));

				// ����AppService api
				try {
					RESTUtil restUtil = new RESTUtil();
					String result = restUtil.put(appservice_api+"/"+process_instance_id+'/'+tran_id, data, appservice_token);
					log.info("("+index+"/"+total_to_send+")[����AppService api�ɹ�]("+process_instance_id+","+tran_id+"): "+result);
				} catch (Exception e) {
					log.info("("+index+"/"+total_to_send+")[����AppService api�쳣]("+process_instance_id+","+tran_id+"): "+e.getMessage());
				}				 
			}
			catch(Exception e){
				log.info(String.format("���� %s ʱ����: %s\n��������: %s",e.getLocalizedMessage(),execution.getVariables()));
			}
		}
		else{
			log.info(String
					.format("\n----------------------------\nδ֪activity�� %s(%s)\n----------------------------",
							activityId, activityName));
		}
	}

}
