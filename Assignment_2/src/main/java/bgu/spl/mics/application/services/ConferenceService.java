package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.PublishConferenceBroadcast;
import bgu.spl.mics.application.messages.PublishResultsEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.ConfrenceInformation;

import java.util.concurrent.CountDownLatch;

/**
 * Conference service is in charge of
 * aggregating good results and publishing them via the {@link //PublishConfrenceBroadcast},
 * after publishing results the conference will unregister from the system.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class ConferenceService extends MicroService {
    private ConfrenceInformation conference;
    private final CountDownLatch doneSignal;


    public ConferenceService(String name , ConfrenceInformation conference , CountDownLatch doneSignal) {
        super(name);
        this.conference = conference;
        this.doneSignal = doneSignal;
    }

    @Override
    protected void initialize() {
        bus.register(this);

        subscribeEvent(PublishResultsEvent.class , (PublishResultsEvent publish)->{
            conference.getModelsToPublish().add(publish.getModel());//adding this model to this conference list of models to publish
        });

        subscribeBroadcast(TickBroadcast.class , (TickBroadcast Tick)->{
            conference.updateTick();
            if(conference.needToPublish()){//if this tick time equals to the date of publish
                sendBroadcast(new PublishConferenceBroadcast(conference.getModelsToPublish()));
                terminate();
            }

        });
        subscribeBroadcast(TerminateBroadcast.class , (TerminateBroadcast terminate)->{
            terminate();
        });

        doneSignal.countDown();
    }
}
