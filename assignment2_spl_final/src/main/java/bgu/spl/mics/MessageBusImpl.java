package bgu.spl.mics;
import java.util.concurrent.*;


/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {
	//contains the messages queue of each microService
	private ConcurrentHashMap<MicroService , BlockingQueue<Message>> microsQueues;
	//contains the subscribes of each message type
	private ConcurrentHashMap<Class<? extends Message>, BlockingQueue<MicroService>> subscribes;
	//contains the future object of each event
	private ConcurrentHashMap<Event , Future> futureEvents;
	//contains the events and broadcasts which the micro-service subscribed to

	private static class messageBusHolder {
		private static MessageBusImpl instance = new MessageBusImpl();
	}

	private MessageBusImpl(){
		microsQueues = new ConcurrentHashMap<>();
		subscribes = new ConcurrentHashMap<>();
		futureEvents = new ConcurrentHashMap<>();
	}

	public static MessageBusImpl getInstance(){
		return messageBusHolder.instance;
	}

	@Override
	/**
	 * @param type != null, m != null
	 * @pre isSubscribedEvent(type , m) == false
	 * @post isSubscribedEvent(type , m) == true
	 *
	 */
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		subscribeMessages(type,m);
	}

	public <T> boolean isSubscribedEvent(Class<? extends Event<T>> type, MicroService m){
		if(subscribes.get(type) == null)
			return false;
		else
			return subscribes.get(type).contains(m);
	}

	@Override
	/**
	 * @param type != null, m != null
	 * @pre isSubscribedBroadcast() == false
	 * @post isSubscribedBroadcast() == true
	 *
	 */
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		subscribeMessages(type , m);
	}
	public boolean isSubscribedBroadcast(Class<? extends Broadcast> type, MicroService m) {
		if (subscribes.get(type) == null)
			return false;
		else
			return subscribes.get(type).contains(m);
	}

	public void subscribeMessages(Class<? extends Message> type, MicroService m){
		if(!microsQueues.containsKey(m))
			throw new NullPointerException("m isn't register");
		subscribes.putIfAbsent(type, new LinkedBlockingQueue<>());
		if(!subscribes.get(type).contains(m))
			subscribes.get(type).add(m);
	}

	@Override
	/**
	 * @param e != null, result != null , futureEvents.getKey(e) != null
	 * @pre getFuture(e).isDone() == false
	 * @post getFuture(e).get() == result, getFuture(e).isDone() == true
	 */
	public <T> void complete(Event<T> e, T result) {
		if(futureEvents.containsKey(e))
			futureEvents.get(e).resolve(result);
	}

	public <T> Future<T> getFuture(Event<T> e){
		return futureEvents.get(e);
	}

	@Override
	/**
	 * @param b != null
	 * @pre none
	 * @post the DS that will hold the microServices messages
	 * Queues which subscribed to this broadcast contains this broadcast
	 */
	public void sendBroadcast(Broadcast b) {
		BlockingQueue<MicroService> q = subscribes.get(b.getClass());
		synchronized (q) {
			if (q == null)
				return;
			for (MicroService m : q) {
					microsQueues.get(m).add(b);
			}
		}
	}

	
	@Override
	/**
	 * @param e != null
	 * @pre none
	 * @post the DS that will hold the microServices messages
	 * Queues which subscribed to this event contains this event
	 */
	public <T> Future<T> sendEvent(Event<T> e) {
		Future<T> newFuture = new Future<T>();
		BlockingQueue<MicroService> q = subscribes.get(e.getClass());
		synchronized (q) {
			if (q == null)
				return null;
			try {//if Q is empty wait until it doesn't
				MicroService m = q.take();
				microsQueues.get(m).put(e);
				q.add(m);
				futureEvents.put(e, newFuture);
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
			return newFuture;
		}
	}



	@Override
	/**
	 * @param m != null
	 * @pre isRegister(m) == false
	 * @post isRegister(m) == true
	 */
	public void register(MicroService m) {
		microsQueues.putIfAbsent(m, new LinkedBlockingQueue<>());
	}

	public boolean isRegister(MicroService m){
		return microsQueues.containsKey(m);
	}

	@Override
	/**
	 * @param m != null
	 * @pre isRegister(m) == true
	 * @post isRegister(m) == false
	 */
	public void unregister(MicroService m) {
		//remove m from every message queue m was subscribed to
		subscribes.values().stream().filter(q -> q.contains(m)).forEach(q -> q.remove(m));
		microsQueues.remove(m);//remove m message queue of the bus
	}

	@Override
	/**
	 * @param m != null
	 * @pre isRegister(m) == true , getMicroMessageQueue(m).contains(Message)
	 * @post Message  != null
	 */
	public Message awaitMessage(MicroService m) throws InterruptedException {
		Message message = null;
		if(!microsQueues.containsKey(m))
			throw new InterruptedException("Micro service isn't registered");
		try {
			message = microsQueues.get(m).take();
		}
		catch (InterruptedException ex) {
			ex.printStackTrace();
		}
		return message;
	}


	public <T> BlockingQueue<Message> getMicroMessageQueue(MicroService m ){
		return microsQueues.get(m);
	}

	

}
