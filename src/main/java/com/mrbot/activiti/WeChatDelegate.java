package com.mrbot.activiti;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.task.TaskQuery;

import com.fasterxml.jackson.databind.ObjectMapper;

public class WeChatDelegate implements JavaDelegate {

	private final Logger log = Logger.getLogger(WeChatDelegate.class.getName());  
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		//http://activiti.org/javadocs/org/activiti/engine/delegate/DelegateExecution.html
		
		String activityId = execution.getCurrentActivityId();
		String activityName = execution.getCurrentActivityName();
		/*
		log.info(String.format("Executing Java Service - WeChatDelegate(): execution id: %s, activityId: %s, activityName: %s ", 
				execution.getId(), activityId, activityName
		));
		*/
		//current time
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ");
		Calendar now = Calendar.getInstance();
		if (activityId.equals("servicetask-remind")){
			log.info(String.format("----------------------------\n%s(%s)\n----------------------------",activityId,activityName));
			
			//获取map方法1
			Map<String, Object> map = null;
			map = new HashMap<String, Object>();
			map.put("content", execution.getVariable("content"));
			map.put("endTime", execution.getVariable("endTime"));
			map.put("receiverName", execution.getVariable("receiverName"));
			map.put("receiverUid", execution.getVariable("receiverUid"));
			map.put("senderName", execution.getVariable("senderName"));
			map.put("senderUid", execution.getVariable("senderUid"));
			map.put("startTime", execution.getVariable("startTime"));
			
			//获取map方法2
			map = execution.getVariables();
			
			//map转json string
			ObjectMapper mapper = new ObjectMapper();
			String json = mapper.writeValueAsString(map);

			//output currentTime and json string
			log.info(String.format("currentTime: %s\njson: %s", df.format( now.getTime()) ,json));
		}
		else if (activityId.equals("servicetask-confirm")){
			log.info(String.format("----------------------------\n%s(%s)\n----------------------------",activityId,activityName));			
			//直接获取和输出所有变量
			log.info(String.format("currentTime: %s\nvariables=: %s", df.format( now.getTime()) ,execution.getVariables()));
		}
		else{
			log.info(String.format("----------------------------\n未知activity： %s(%s)\n----------------------------",activityId,activityName));
		}
	}
}
