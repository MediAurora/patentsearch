package patentsearch;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.triggers.SimpleTriggerImpl;

public class GoogleSearchScheduler{

	public static void main(String[] args) throws Exception{
    	//schedule it
    	Scheduler scheduler = new StdSchedulerFactory().getScheduler();
    	scheduler.start();
		JobDetail job = JobBuilder.newJob(GoogleScraperMaster.class)
						.withIdentity("googlesearch", "group1")
						.build();
    	
		// compute a time that is on the next round minute		
		Calendar calendar = new GregorianCalendar();
		System.out.println(calendar.getTime());
		calendar.set(Calendar.MINUTE, 42);
		System.out.println("new Date is ==> " + calendar.getTime());
		Date runTime = calendar.getTime();
    	//configure the scheduler time
		/*CronTrigger trigger = TriggerBuilder.newTrigger()
    		      .withIdentity("myTrigger", "group1")
    		      .startNow()
    		      .withSchedule(CronScheduleBuilder.cronSchedule("0/20 * * * * ?"))
    		      .build();
		*/

    	Trigger trigger = TriggerBuilder.newTrigger().withIdentity("trigger1", "group1").startAt(runTime).build();
    	scheduler.scheduleJob(job, trigger);
    	scheduler.start();
    	Thread.sleep(90L * 1000L);
    	scheduler.shutdown(true);
	}//Main method ends

}

