package bgu.spl.net.api.bidi;
import bgu.spl.net.srv.ConnectionHandler;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ConnectionsImpl<T> implements Connections<T>{
    private ConcurrentHashMap<Integer , ConnectionHandler> idToHandler;
    private AtomicInteger ID;

    private ConnectionsImpl() {
        this.idToHandler = new ConcurrentHashMap<>();
        this.ID = new AtomicInteger(0);
    }

    private static class ConnectionsHolder {
        private static ConnectionsImpl instance = new ConnectionsImpl();
    }
    public static ConnectionsImpl getInstance(){
        return ConnectionsHolder.instance;
    }

    public int add(ConnectionHandler ch){
        idToHandler.putIfAbsent(ID.incrementAndGet(), ch);
        return ID.intValue();
    }


    @Override
    public boolean send(int connectionId, T msg) {
        idToHandler.get(connectionId).send(msg);//we call this function from the protocol after checking the existance of the user
        return true;
    }

    @Override
    public void broadcast(T msg) {

    }

    @Override
    public void disconnect(int connectionId) {
        idToHandler.remove(connectionId);
    }
}
