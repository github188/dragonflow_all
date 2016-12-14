/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *******************************************************************************/
package com.siteview.nnm.concurrent;

import java.util.Date;


/**
 *  Job.
 */
public class Job implements Runnable  {


    public static final String module = Job.class.getName();
    public static enum State {CREATED, QUEUED, RUNNING, FINISHED, FAILED};
    private  String jobId;
    public State currentState = State.CREATED;
    private long elapsedTime = 0;
    private final Date startTime = new Date();
    public Job(){
    	
    }
    public Job(String jobId) {
    	if(jobId!=null)
        this.jobId = jobId;
    	
    }

    public State currentState() {
        return currentState;
    }

    
    public String getJobId() {
        return this.jobId;
    }

   
    public void queue()  throws Exception {
        if (currentState != State.CREATED) {
        	throw  new Exception("Illegal state change");
        }
        this.currentState = State.QUEUED;
    }

   
    public void deQueue()  throws Exception {
        if (currentState != State.QUEUED) {
        	throw  new Exception("Illegal state change");
        }
        this.currentState = State.CREATED;
    }

    /**
     *  Executes this Job. The {@link #run()} method calls this method.
     */
    public  void exec() throws Exception
    {
    	
    }

   
    public void run() {
        long startMillis = System.currentTimeMillis();
        try {
            exec();
        } catch (Exception e) {
        }
        elapsedTime = System.currentTimeMillis() - startMillis;
        
       // System.out.println(this.getJobId()+"耗时(ms):"+elapsedTime);
    }

    
    public long getRuntime() {
        return elapsedTime;
    }

    
    public Date getStartTime() {
        return startTime;
    }

	
	public boolean isValid() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public String toString() {
		return "ID [jobId=" + this.getJobId()+ "]";
	}
	
		
}
