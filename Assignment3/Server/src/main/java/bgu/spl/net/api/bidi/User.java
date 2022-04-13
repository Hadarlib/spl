package bgu.spl.net.api.bidi;

import java.util.concurrent.ConcurrentLinkedQueue;

public class User {
    private int ID; //each user has an ID which changes after every log-in
    private boolean loggedIn;
    private String name;
    private String password;
    private int age;
    private int numberOfPosts;
    private ConcurrentLinkedQueue<User> followers;
    private ConcurrentLinkedQueue<User> following;
    private ConcurrentLinkedQueue<String> blockedByUsers; //list of the user's names who blocked this user
    private ConcurrentLinkedQueue<String> notifications; //contains notification until the user will log-in



    public User( int ID , String name, String password, int age) {
        this.ID = ID;
        this.name = name;
        this.password = password;
        this.age = age;
        loggedIn = false;
        numberOfPosts = 0 ;
        followers = new ConcurrentLinkedQueue<>();
        following = new ConcurrentLinkedQueue<>();
        blockedByUsers = new ConcurrentLinkedQueue<>();
        notifications = new ConcurrentLinkedQueue<>();
    }

    public void addNotification(String notification){
        notifications.add(notification);
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public int getNumberOfPosts() {
        return numberOfPosts;
    }

    public void setNumberOfPosts(int numberOfPosts) {
        this.numberOfPosts = numberOfPosts;
    }

    public ConcurrentLinkedQueue<User> getFollowers() {
        return followers;
    }

    public ConcurrentLinkedQueue<User> getFollowing() {
        return following;
    }

    public String getStatus() {
        return " " + age+" "+ numberOfPosts+" "+ followers.size()+" "+ following.size();
    }

    public ConcurrentLinkedQueue<String> getNotifications() {
        return notifications;
    }

    public ConcurrentLinkedQueue<String> getBlockedByUsers() {
        return blockedByUsers;
    }
}
