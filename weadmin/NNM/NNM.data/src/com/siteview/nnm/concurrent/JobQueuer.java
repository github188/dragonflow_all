package com.siteview.nnm.concurrent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


public class JobQueuer {


	public static final int FAILED_RETRY_MIN = 30;
	public static final int MIN_THREADS = 50; // Must be no less than one or the executor will shut down.
	public static final int MAX_THREADS = 200; // Values higher than 5 might slow things down.
	public static final int POLL_WAIT = 30000; // Database polling interval - 30 seconds.
	public static final int PURGE_JOBS_DAYS = 30;
	public static final int QUEUE_SIZE = 1000;
	public static final int THREAD_TTL = 120000; // Idle thread lifespan - 2 minutes.
    public static final String module = JobQueuer.class.getName();
    private static final AtomicInteger created = new AtomicInteger();
    private static final ConcurrentHashMap<String, Job> jobs = new ConcurrentHashMap<String, Job>();
    private static final ConcurrentHashMap<String, QueueManager> jobManagers = new ConcurrentHashMap<String, QueueManager>();
    private static final ThreadPoolExecutor executor = createThreadPoolExecutor();
    private static final JobQueuer instance = new JobQueuer();
 
    /**
     * Returns the <code>JobPoller</code> instance.
     */
    public static JobQueuer getInstance() {
        return instance;
    }

    private static ThreadPoolExecutor createThreadPoolExecutor() {
        try {
            
            return new ThreadPoolExecutor(MIN_THREADS, MAX_THREADS, THREAD_TTL,
                    TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(QUEUE_SIZE), new JobInvokerThreadFactory(), new ThreadPoolExecutor.AbortPolicy());
        } catch (Exception e) {
            return new ThreadPoolExecutor(MIN_THREADS, MAX_THREADS, THREAD_TTL,
                    TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(QUEUE_SIZE), new JobInvokerThreadFactory(), new ThreadPoolExecutor.AbortPolicy());
        }
    }


    private Thread jobManagerPollerThread;

    private JobQueuer() { 
    	    
            jobManagerPollerThread = new Thread(new JobManagerPoller(), "JobPoller");
            jobManagerPollerThread.setDaemon(false);
            jobManagerPollerThread.start();
        
    }

    /**
     * Returns a <code>Map</code> containing <code>JobPoller</code> statistics.
     */
    public Map<String, Object> getPoolState() {
        Map<String, Object> poolState = new HashMap<String, Object>();
        poolState.put("keepAliveTimeInSeconds", executor.getKeepAliveTime(TimeUnit.SECONDS));
        poolState.put("numberOfCoreInvokerThreads", executor.getCorePoolSize());
        poolState.put("currentNumberOfInvokerThreads", executor.getPoolSize());
        poolState.put("numberOfActiveInvokerThreads", executor.getActiveCount());
        poolState.put("maxNumberOfInvokerThreads", executor.getMaximumPoolSize());
        poolState.put("greatestNumberOfInvokerThreads", executor.getLargestPoolSize());
        poolState.put("numberOfCompletedTasks", executor.getCompletedTaskCount());
        BlockingQueue<Runnable> queue = executor.getQueue();
        List<Map<String, Object>> taskList = new ArrayList<Map<String, Object>>();
        Map<String, Object> taskInfo = null;
        for (Runnable task : queue) {
            Job job = (Job) task;
            taskInfo = new HashMap<String, Object>();
            taskInfo.put("id", job.getJobId());
            taskInfo.put("time", job.getStartTime());
            taskInfo.put("runtime", job.getRuntime());
            taskList.add(taskInfo);
        }
        poolState.put("taskList", taskList);
        return poolState;
    }

  


    /**
     * Adds a job to the job queue.
     * @throws InvalidJobException if the job is in an invalid state.
     * @throws RejectedExecutionException if the poller is stopped.
     */
    public void queueNow(Job job) throws Exception{
        job.queue();
        try {
            executor.execute(job);
        } catch (Exception e) {
            job.deQueue();
        }
    }
    
    public void cleanJobs(){
    	
    }

    /**
     * Stops the <code>JobPoller</code>. 
     * The <code>JobPoller</code> cannot be restarted.
     */
    public void stop() {
        if (jobManagerPollerThread != null) {
            jobManagerPollerThread.interrupt();
        }
        List<Runnable> queuedJobs = executor.shutdownNow();
        for (Runnable task : queuedJobs) {
            try {
                Job queuedJob = (Job) task;
                queuedJob.deQueue();
            } catch (Exception e) {
               
            }
        }
    }
 
    public static void registerJob(Job job){
    	if(job!=null)
    		jobs.put(job.getJobId(), job);
    }
    /**
     *registerJob 
     * @param job
     */
    public static void registerQueueManager(QueueManager jm) {
       if(jm!=null)
    	   jobManagers.putIfAbsent(jm.getQueueManagerId(), jm);
    }
    public  QueueManager getQueueManager(String id){
    	return jobManagers.get(id);
    }
    private static class JobInvokerThreadFactory implements ThreadFactory {

        public Thread newThread(Runnable runnable) {
            return new Thread(runnable, "JobQueuer-" + created.getAndIncrement());
        }
    }

    // Polls all registered JobManagers for jobs to queue.
    private class JobManagerPoller implements Runnable {

        // Do not check for interrupts in this method. The design requires the
        // thread to complete the job manager poll uninterrupted.
        public void run() {
            try {
                
                while (!executor.isShutdown()) {
                    int remainingCapacity = executor.getQueue().remainingCapacity();
                    if (remainingCapacity > 0) {
                        // Build "list of lists"
                    	Collection<QueueManager> jmCollection = jobManagers.values();
                        List<Iterator<Job>> pollResults = new ArrayList<Iterator<Job>>();
                        for (QueueManager jm : jmCollection) {
                            pollResults.add(jm.poll(remainingCapacity).iterator());
                        }
                        // Create queue candidate list from "list of lists"
                        List<Job> queueCandidates = new ArrayList<Job>();
                        boolean addingJobs = true;
                        while (addingJobs) {
                            addingJobs = false;
                            for (Iterator<Job> jobIterator : pollResults) {
                                if (jobIterator.hasNext()) {
                                    queueCandidates.add(jobIterator.next());
                                    addingJobs = true;
                                }
                            }
                        }
                        // The candidate list might be larger than the queue remaining capacity,
                        // but that is okay - the excess jobs will be dequeued and rescheduled.
                        for (Job job : queueCandidates) {
                            try {
                                queueNow(job);
                            } catch (Exception e) {
                            }
                        }
                        
                        
//                        for (Job job : jobs.values()) {
//                            try {
//                                queueNow(job);
//                            } catch (Exception e) {
//                            }
//                        }
                    }
                    Thread.sleep(1000);
                }
            } catch (Exception e) {
                // Happens when JobPoller shuts down - nothing to do.
                Thread.currentThread().interrupt();
            }
        }
    }


}
