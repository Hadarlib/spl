package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.Model;

public class DoneTrainingModelBroadcast implements Broadcast {
    private Model model;

    public DoneTrainingModelBroadcast(Model model) {
        this.model = model;
    }


    public Model getModel() {
        return model;
    }
}


