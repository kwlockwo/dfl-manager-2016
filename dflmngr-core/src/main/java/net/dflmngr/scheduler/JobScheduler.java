package net.dflmngr.scheduler;

import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.CronScheduleBuilder.*;  

import java.util.Map;

import javax.servlet.ServletContext;

import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.ee.servlet.QuartzInitializerListener;
import org.quartz.impl.StdSchedulerFactory;

import net.dflmngr.logging.LoggingUtils;
import net.dflmngr.utils.DflmngrUtils;

public class JobScheduler {
	private LoggingUtils loggerUtils;
	
	private StdSchedulerFactory factory = null;
		
	public void schedule(String jobName, String jobGroup, String jobClassStr, Map<String, Object> jobParams, String cronStr, boolean isImmediate, ServletContext context) throws Exception {
		
		loggerUtils = new LoggingUtils("online-logger", "online.name", "Scheduler");
		
		try {			
			String now = DflmngrUtils.getNowStr();
			String jobNameKey;
			String jobTriggerKey;
			
			loggerUtils.log("info", "Schedule job: {}", jobName);
			
			factory = (StdSchedulerFactory) context.getAttribute(QuartzInitializerListener.QUARTZ_FACTORY_KEY);
			
			if(isImmediate) {
				jobNameKey = jobName + "_immediate_" + now;
				jobTriggerKey = jobName + "_trigger_immediate_" + now;
				createAndSchedule(jobNameKey, jobGroup, jobClassStr, jobTriggerKey, jobParams, cronStr, true);
			}
			
			if(cronStr != null && !cronStr.equals("")) {
				jobNameKey = jobName + "_" + now;
				jobTriggerKey = jobName + "_trigger_" + now;
				createAndSchedule(jobNameKey, jobGroup, jobClassStr, jobTriggerKey, jobParams, cronStr, false);
			}
			
			loggerUtils.log("info", "Scheduled job: {}", jobName);
		} catch (Exception ex) {
			loggerUtils.log("error", "Error in ... ", ex);
		}
	}
	
	private void createAndSchedule(String jobNameKey, String group, String jobClassStr, String jobTriggerKey, Map<String, Object> jobParams, String cronStr, boolean isImmediate) throws Exception {
		
		loggerUtils.log("info", "Final job details: jobNameKey={}; group={}; jobClassStr={}; jobTriggerKey={}; jobParams={}; cronStr={}; isImmediate={};", jobNameKey, group, jobClassStr, jobTriggerKey, jobParams, cronStr, isImmediate);
		
		Class<? extends Job> jobClass = (Class<? extends Job>) Class.forName(jobClassStr);
		
		JobDetail job = null;
		Trigger trigger = null;
		
		job = newJob(jobClass).withIdentity(jobNameKey, group).build();
		if(jobParams != null && !jobParams.isEmpty()) {
			job.getJobDataMap().putAll(jobParams);
		}
		
		if(isImmediate) {
			trigger = newTrigger().withIdentity(jobTriggerKey, group).startNow().forJob(job).build();
		} else {
			trigger = newTrigger().withIdentity(jobTriggerKey, group).withSchedule(cronSchedule(cronStr)).forJob(job).build();
		}
		
		Scheduler scheduler = factory.getScheduler("DflmngrScheduler");
		scheduler.scheduleJob(job, trigger);
	}

}
