package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.application.objects.Model;

public class DoneTestModelBroadcast implements Broadcast {
    private Model model;

    public DoneTestModelBroadcast(Model model) {
        this.model = model;
    }


    public Model getModel() {
        return model;
    }
}

