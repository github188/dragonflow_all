package com.siteview.nnm.concurrent;


public class InterfaceJob  extends Job{


	public static final String module = InterfaceJob.class.getName();

	public InterfaceJob(String jobId) {
		super(jobId);
	}

	/**
	 * Invokes the service.
	 */
	@Override
	public void exec() throws Exception {
		if (currentState != State.QUEUED) {
			throw new Exception("Illegal state change");
		}
		currentState = State.RUNNING;
		// init();
		Throwable thrown = null;
		
		// no transaction is necessary since runSync handles this
		try {
			// get the dispatcher and invoke the service via runSync -- will run
			// all ECAs
			
		} catch (Throwable t) {

			thrown = t;
		}
		if (thrown == null) {

			finish(null);
		} else {
			failed(thrown);
		}
	}

	/**
	 * Method is called prior to running the service.
	 */
	protected void init() throws Exception {

	}

	/**
	 * Method is called after the service has finished successfully.
	 */
	protected void finish(String result) throws Exception {
		if (currentState != State.RUNNING) {
			throw new Exception("Illegal state change");
		}
		currentState = State.FINISHED;
		// finish
	}

	/**
	 * Method is called when the service fails.
	 * 
	 * @param t
	 *            Throwable
	 */
	protected void failed(Throwable t) throws Exception {
		if (currentState != State.RUNNING) {
			throw new Exception("Illegal state change");
		}
		currentState = State.FAILED;
		// failed
	}

	@Override
	public boolean isValid() {
		return currentState == State.CREATED;
	}

	@Override
	public void deQueue() throws Exception {
		super.deQueue();
		throw new Exception("Unable to queue job [" + getJobId() + "]");
	}


}
