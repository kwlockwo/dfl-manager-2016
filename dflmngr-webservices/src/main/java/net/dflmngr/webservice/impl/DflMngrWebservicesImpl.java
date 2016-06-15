package net.dflmngr.webservice.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.jws.WebService;
import javax.servlet.ServletContext;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import net.dfl.dflmngrwebservices.DflMngrWebservices;
import net.dfl.dflmngrwebservices.LoadSelectionsRequestType;
import net.dfl.dflmngrwebservices.LoadSelectionsResponseType;
import net.dfl.dflmngrwebservices.ParamType;
import net.dfl.dflmngrwebservices.ScheduleJobRequestType;
import net.dfl.dflmngrwebservices.ScheduleJobResponseType;
import net.dflmngr.handlers.TeamSelectionLoaderHandler;
import net.dflmngr.logging.LoggingUtils;
import net.dflmngr.scheduler.JobScheduler;


@WebService(targetNamespace = "http://www.dfl.net/services/DflMngrWebservices", 
            portName="DflMngrWebservicesPort",
            serviceName="DflMngrWebservicesEndpoint", 
            endpointInterface="net.dfl.dflmngrwebservices.DflMngrWebservices")
public class DflMngrWebservicesImpl implements DflMngrWebservices {
	

	@Resource
	private WebServiceContext context;
	
	@Override
	public LoadSelectionsResponseType loadSelections(LoadSelectionsRequestType parameters) {
		
		LoggingUtils loggerUtils = new LoggingUtils("online-logger", "online.name", "Webservices");
		
		try {
			loggerUtils.log("info", "Load selections request received");
			loggerUtils.log("info", "XML Data: {}", parameters);
			
			TeamSelectionLoaderHandler handler = new TeamSelectionLoaderHandler();
			
			String teamCode = parameters.getTeam();
			int round = parameters.getRound();
			List<Integer> ins = parameters.getIns().getIn();
			List<Integer> outs = parameters.getOuts().getOut();
			
			loggerUtils.log("info", "Load selections data: teamCode={}; round={}; ins={}; outs={}", teamCode, round, ins, outs);
			
			handler.execute(teamCode, round, ins, outs, false);
			
			loggerUtils.log("info", "Load selections request completed");
		} catch (Exception ex) {
			loggerUtils.log("error", "Error in ... ", ex);
		}
		
		return null;
	}

	@Override
	public ScheduleJobResponseType scheduleJob(ScheduleJobRequestType parameters) {
		
		LoggingUtils loggerUtils = new LoggingUtils("online-logger", "online.name", "Webservices");
		
		try {			
			loggerUtils.log("info", "Job schedule request received");
			loggerUtils.log("info", "XML Data: {}", parameters);
			
			ServletContext servletContext = (ServletContext) context.getMessageContext().get(MessageContext.SERVLET_CONTEXT);
			
			JobScheduler scheduleJob = new JobScheduler();
			
			String jobName = parameters.getJobName();
			String jobGroup = parameters.getJobGroup();
			String jobClassStr = parameters.getJobClass();
			
			Map<String, Object> jobParams = null;
			
			if(parameters.getJobParams() != null) {
				jobParams = new HashMap<>();
				List<ParamType> params = parameters.getJobParams().getParam();
				
				for(ParamType param : params) {
					String key = param.getKey();
					Object value = param.getValue();
					
					jobParams.put(key, value);
				}
			}
			
			String cronStr = parameters.getCronString();
			boolean isImmediate = parameters.isImmediateInd();
			
			loggerUtils.log("info", "Job details: jobName=" + jobName + "; jobGroup=" + jobGroup + "; jobClass=" + jobClassStr + "; jobParams=" + jobParams + "; cron=" + cronStr + "; isImmediate=" + isImmediate);
			
			scheduleJob.schedule(jobName, jobGroup, jobClassStr, jobParams, cronStr, isImmediate, servletContext);
			
			loggerUtils.log("info", "Job schedule request completed");
		} catch (Exception ex) {
			loggerUtils.log("error", "Error in ... ", ex);
		}
		
		return null;
	}


}
