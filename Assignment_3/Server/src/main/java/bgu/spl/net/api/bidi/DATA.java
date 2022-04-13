package bgu.spl.net.api.bidi;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DATA {
    private ConcurrentLinkedQueue<String> PMs;
    private ConcurrentLinkedQueue<String> Posts;
    private ArrayList<String> Filter;// array of the words to filter
    private ConcurrentHashMap<String , User> Users;

    private static class DATAHolder {
        private static DATA instance = new DATA();
    }
    public static DATA getInstance(){
        return DATAHolder.instance;
    }

    public DATA( ) {
        PMs = new ConcurrentLinkedQueue();
        Posts = new ConcurrentLinkedQueue();
        Filter = new ArrayList<>();
        Users = new ConcurrentHashMap<>();
    }

    public ArrayList<String> getFilter() {
        return Filter;
    }

    public ConcurrentHashMap<String, User> getUsers() {
        return Users;
    }

    public void addUser(User user) {
        Users.putIfAbsent(user.getName(), user);
    }

    public void addPost(String post){
        Posts.add(post);
    }

    public void addPM(String PM){
        PMs.add(PM);
    }
}
