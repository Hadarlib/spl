package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

import java.util.LinkedList;
import bgu.spl.mics.application.objects.Model;



public class PublishConferenceBroadcast implements Broadcast {
    private LinkedList<Model> publishedModels;

    public PublishConferenceBroadcast(LinkedList<Model> publishedModels) {
        this.publishedModels = publishedModels;
    }

    public LinkedList<Model> getPublishedModels() {
        return publishedModels;
    }
}
