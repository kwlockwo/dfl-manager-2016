package net.dflmngr.webservice;

import java.util.List;
import java.util.Map;

import net.dfl.dflmngrwebservices.DflMngrWebservices;
import net.dfl.dflmngrwebservices.DflMngrWebservicesEndpoint;
import net.dfl.dflmngrwebservices.InsType;
import net.dfl.dflmngrwebservices.JobParamsType;
import net.dfl.dflmngrwebservices.LoadSelectionsRequestType;
import net.dfl.dflmngrwebservices.OutsType;
import net.dfl.dflmngrwebservices.ParamType;
import net.dfl.dflmngrwebservices.ScheduleJobRequestType;

public class CallDflmngrWebservices {
	
	public static void loadSelections(String teamCode, int round, List<Integer> ins, List<Integer> outs) {
		
		LoadSelectionsRequestType request = new LoadSelectionsRequestType();
		
		InsType insXml = new InsType();
		insXml.getIn().addAll(ins);
		OutsType outsXml = new OutsType();
		outsXml.getOut().addAll(outs);
		
		request.setTeam(teamCode);
		request.setRound(round);
		request.setIns(insXml);
		request.setOuts(outsXml);
		
		DflMngrWebservicesEndpoint endpoint = new DflMngrWebservicesEndpoint();
		DflMngrWebservices ws = endpoint.getDflMngrWebservicesPort();
		ws.loadSelections(request);
	}
	
	public static void schedualJob(String jobName, String jobGroup, String jobClassStr, Map<String, Object> jobParams, String cronStr, boolean isImmediate) {
		
		ScheduleJobRequestType request = new ScheduleJobRequestType();
		
		request.setJobName(jobName);
		request.setJobGroup(jobGroup);
		request.setJobClass(jobClassStr);
		
		JobParamsType params = null;
		
		if(jobParams != null && jobParams.size() > 0) {
			params = new JobParamsType();
			
			for (Map.Entry<String, Object> jobParam : jobParams.entrySet()) {
				ParamType param = new ParamType();
				param.setKey(jobParam.getKey());
				param.setValue(jobParam.getValue());
				params.getParam().add(param);
			}
		}
		
		request.setJobParams(params);
		request.setCronString(cronStr);
		request.setImmediateInd(isImmediate);
		
		DflMngrWebservicesEndpoint endpoint = new DflMngrWebservicesEndpoint();
		DflMngrWebservices ws = endpoint.getDflMngrWebservicesPort();
		ws.scheduleJob(request);
	}

}
