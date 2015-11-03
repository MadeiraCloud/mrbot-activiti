package org.activiti.designer.test;

import static org.junit.Assert.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.FileInputStream;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.test.ActivitiRule;
import org.junit.Rule;
import org.junit.Test;

public class ProcessTestProcessremindonetime {

	private String filename = "C:\\Users\\jimmy\\workspace\\activiti-mrbot\\src\\main\\resources\\process\\process-remind-onetime.bpmn";

	@Rule
	public ActivitiRule activitiRule = new ActivitiRule();

	@SuppressWarnings("static-access")
	@Test
	public void startProcess() throws Exception {
		RepositoryService repositoryService = activitiRule
				.getRepositoryService();
		repositoryService
				.createDeployment()
				.addInputStream("process-remind-onetime.bpmn20.xml",
						new FileInputStream(filename)).deploy();
		RuntimeService runtimeService = activitiRule.getRuntimeService();
		
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ");
		Calendar now = Calendar.getInstance();

		Map<String, Object> variableMap = new HashMap<String, Object>();
		variableMap.put("content", "test");
		variableMap.put("startTime", df.format( now.getTime()));
		now.add(Calendar.SECOND, 10);
		variableMap.put("endTime", df.format( now.getTime()));
		variableMap.put("senderName", "jimmy");
		variableMap.put("senderUid", "aabcf910-0741-4f48-8a2a-d909c6b38975");
		variableMap.put("receiverName", "@jimmy");
		variableMap.put("receiverUid", "aabcf910-0741-4f48-8a2a-d909c6b38975");
		final ProcessInstance processInstance = runtimeService
				.startProcessInstanceByKey("process-remind-onetime",
						variableMap);
		assertNotNull(processInstance.getId());
		System.out.println("Start process OK :id " + processInstance.getId()
				+ " " + processInstance.getProcessDefinitionId());

		try 
		{ 
			Thread.currentThread().sleep(1000);//毫秒 
		} 
		catch(Exception e){}
		
		try {
			System.out.println("finish wait 1 seconds");
			String processInstanceId = processInstance.getId();
			Execution execution = runtimeService.createExecutionQuery()
					.executionId(processInstanceId).singleResult();// 执行实例
			String activityId = execution.getActivityId();
			ProcessDefinitionEntity processDefinition = (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService)
					.getDeployedProcessDefinition(processInstance
							.getProcessDefinitionId());
			List<ActivityImpl> activitiList = processDefinition
					.getActivities();// 获得当前任务的所有节点
			ActivityImpl activity = null;
			for (ActivityImpl activityImpl : activitiList) {
				String id = activityImpl.getId();
				if (id.equals(activityId)) {// 获得执行到那个节点
					activity = activityImpl;
					break;
				}
			}
			if (activity != null) {
				System.out.println("Current activity Task:"
						+ activity.getId());
			}
		} catch (Exception e) {
			System.out.println("exception:"+e.toString());
		}
	}
}