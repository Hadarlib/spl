package bgu.spl.mics.application.objects;

import java.util.LinkedList;

/**
 * Passive object representing information on a conference.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class ConfrenceInformation {

    private String name;
    private int date;
    private LinkedList<Model> modelsToPublish;
    private int currentTickTime;

    public ConfrenceInformation(String name , int date) {
        this.name = name;
        this.date = date;
        modelsToPublish = new LinkedList<>();
        currentTickTime = 0;
    }
    public void updateTick(){
        currentTickTime++;
    }

    public boolean needToPublish(){
        return currentTickTime == date;
    }
    public String getName() {
        return name;
    }

    public int getDate() {
        return date;
    }

    public LinkedList<Model> getModelsToPublish() {
        return modelsToPublish;
    }

    public int getCurrentTickTime() {
        return currentTickTime;
    }
}
