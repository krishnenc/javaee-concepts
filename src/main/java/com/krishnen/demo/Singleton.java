package com.krishnen.demo;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.*;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Created by User on 25/01/2016.
 * This class creates a bunch of workers to do async processing
 * The timer is triggered every 10 seconds and workers that have stopped or crashed are restarted
 * meaning that the system can survive unintended failures
 */
@Startup
@javax.ejb.Singleton
public class Singleton {

    @Resource
    TimerService timerService;

    @Inject
    Instance<JobTask> jobTask;

    private List<Future<String>> futuresJobList;

    @Resource(name = "DefaultManagedExecutorService")
    ManagedExecutorService executor;

    @Inject
    private Logger log;

    @PostConstruct
    public void init() {
        ScheduleExpression sexpr = new ScheduleExpression();
        // set schedule to every 10 seconds for demonstration
        sexpr.hour("*").minute("*").second("*/10");
        // persistent must be false because the timer is started by the HASingleton service
        timerService.createCalendarTimer(sexpr, new TimerConfig("JobsTimer", false));
        futuresJobList = new ArrayList<Future<String>>();
    }

    @Timeout
    @AccessTimeout(value = 5, unit = TimeUnit.MINUTES)
    public void timeout(Timer timer) {
        //Cleanup any futures that have completed/failed
        for (Iterator<Future<String>> it = futuresJobList.iterator(); it.hasNext();) {
            Future<String> f = it.next();
            if (f.isDone() || f.isCancelled()) {
                it.remove();
                log.info("Removed Dead Campaign Worker");
            }
        }
        int listSize = 10 - futuresJobList.size();
        //Add back new future instances to replace completed/failed ones
        if (listSize > 0) {
            do {
                JobTask OTAJobWorker = jobTask.get();
                Future<String> f = executor.submit(OTAJobWorker);
                futuresJobList.add(f);
                log.info("Started Job Worker");
                listSize--;
            } while (listSize > 0);
        }
    }

}
