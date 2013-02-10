package unlekker.util;

import java.util.ArrayList;

public class UThreadPool implements Runnable {
	private ArrayList<UThread> active,inactive;
	public int threadNum,activeNum,inactiveNum; 
	public ArrayList<Runnable> taskQueue;
	private boolean doDispose;
	
	
	public UThreadPool(int threadNum) {
		this.threadNum=threadNum;
		taskQueue=new ArrayList<Runnable>();
		inactive=new ArrayList<UThreadPool.UThread>();
		active=new ArrayList<UThreadPool.UThread>();
		for(int i=0; i<threadNum; i++) inactive.add(new UThread());
	}

	public void run() {
		while(!doDispose) {
			synchronized(active) {activeNum=active.size();}
			synchronized(inactive) {inactiveNum=inactive.size();}
			if(inactiveNum>0 && taskQueue.size()>0) {
				
			}
		}		
	}

	private void threadCallback(UThread theThread) {
		synchronized(active) {active.remove(theThread);		}
		synchronized(inactive) {inactive.add(theThread);		}
	}
	
	
	class UThread extends Thread {
		long start,end,duration;		
		Runnable task;
		boolean running;
		
		public void runTask(Runnable task) {
			this.task=task;
			run();
		}
		
		public void run() {
			start=System.currentTimeMillis();
			task.run();
			end=System.currentTimeMillis();
			duration=end-start;
			threadCallback(this);
		}
	}
}
