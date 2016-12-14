package com.siteview.nnm.concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.RejectedExecutionException;

public class QueueManager {


    public static final String module = QueueManager.class.getName();
    private static boolean isShutDown = false;
    private static List<Job> allJobs;
    private String queueManagerId;

   

	public String getQueueManagerId() {
		return queueManagerId;
	}

	private static void assertIsRunning() {
        if (isShutDown) {
            throw new IllegalStateException("shutting down");
        }
    }

    /**
     * Returns a <code>JobManager</code> instance.
     * @param delegator
     * @param enablePoller Enables polling of the  entity.
     * @throws IllegalStateException if the Job Manager is shut down.
     */
    public static QueueManager getInstance(String id, List<Job> allJob,boolean enablePoller) {
        assertIsRunning();
        allJobs=allJob;
        QueueManager jm=new QueueManager(id);
        if(enablePoller){
        	JobQueuer.registerQueueManager(jm);
        }
        return jm;
    }

    /**
     * Shuts down all job managers. This method is called when ITOSS shuts down.
     */
    public static void shutDown() {
        isShutDown = true;
        JobQueuer.getInstance().stop();
    }
  
    private boolean crashedJobsReloaded = false;

    private QueueManager(String queueManagerId ) {
      this.queueManagerId=queueManagerId;
    }
    

    /**
     * returns a list of jobs that are due to run.
     * Returns an empty list if there are no jobs due to run.
     * This method is called by the {@link JobPoller} polling thread.
     */
    protected List<Job> poll(int limit) {
        assertIsRunning();
        List<Job> poll = new ArrayList<Job>(limit);
        for(Job job:allJobs){
        	if(job.isValid()){
        		poll.add(job);
        	}
        }
        return poll;
    }
    /**
     * Reload job
     */
    private boolean JobsReloaded = false;
    
    /**
     * when schedule is fired ,reload scan job 
     */
    public  synchronized void ReloadJob(){
    	
    }
    
    public void schedule( long startTime,
            int frequency, int interval, int count, long endTime, int maxRetry){
    	
    }
    
    
    /**
     * Get a List of each threads current state.
     * 
     * @return List containing a Map of each thread's state.
     */
    public Map<String, Object> getPoolState() {
        return JobQueuer.getInstance().getPoolState();
    }

    public static QueueManager getInstance(String id)
    {
    	return JobQueuer.getInstance().getQueueManager(id);
    }

    

    /** Queues a Job to run now.
     * @throws IllegalStateException if the Job Manager is shut down.
     * @throws RejectedExecutionException if the poller is stopped.
     */
    public void runJob(Job job) throws Exception{
        assertIsRunning();
        if (job.isValid()) {
        	JobQueuer.getInstance().queueNow(job);
        }
    }

    

}
