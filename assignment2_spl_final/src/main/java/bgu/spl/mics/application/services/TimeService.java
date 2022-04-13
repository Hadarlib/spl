package bgu.spl.mics.application.services;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;


import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;

/**
 * TimeService is the global system timer There is only one instance of this micro-service.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other micro-services about the current time tick using {@link TickBroadcast}.
 * This class may not hold references for objects which it is not responsible for.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends MicroService{
	private int duration;//total ticks in the running of the program
	private int tick;//milliseconds
	private AtomicInteger currentTick;
	private Timer timer;
	private TimerTask task;



	public TimeService(String name , int tick , int duration) {
		super(name);
		this.tick = tick;
		this.duration = duration;
		timer = new Timer(true);
		currentTick = new AtomicInteger(0);
		this.task = new TimerTask() {
			public void run() {
				sendBroadcast(new TickBroadcast());
				if(currentTick.incrementAndGet() >= duration) {//need to close the program
					sendBroadcast(new TerminateBroadcast());
					timer.cancel();
				}
			}
		};
	}

	@Override
	protected void initialize()  {
		bus.register(this);
		timer.scheduleAtFixedRate(task , 0 , tick);//starting the timer for sending the ticks
		subscribeBroadcast(TerminateBroadcast.class, (TerminateBroadcast terminate) -> {
			terminate();
		});
		}

	}


