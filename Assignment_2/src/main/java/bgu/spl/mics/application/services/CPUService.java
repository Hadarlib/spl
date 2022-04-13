package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.CPU;
import bgu.spl.mics.application.objects.Cluster;
import bgu.spl.mics.application.objects.DataBatch;

import java.util.concurrent.CountDownLatch;


/**
 * CPU service is responsible for handling the {@link //DataPreProcessEvent}.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class CPUService extends MicroService {
    private static Cluster cluster = Cluster.getInstance();
    CPU cpu;
    private final CountDownLatch doneSignal;


    public CPUService(String name, CPU cpu, CountDownLatch doneSignal) {
        super(name);
        this.cpu = cpu;
        this.doneSignal = doneSignal;
    }
    public CPU getCpu() {
        return cpu;
    }

    @Override
    protected void initialize() {
        bus.register(this);
        subscribeBroadcast(TickBroadcast.class, (TickBroadcast tick)-> {
            cpu.updateTick(); //if currently processing a batch then update
            if(cpu.isDoneProcess()) { //for the case that done process a batch because of the current tick
                cpu.doneProcess();
                DataBatch processedBatch = cpu.getDataBatchToProcess().remove();
                cluster.getReadyToTrain().get(processedBatch.getGPUServiceSender()).add(processedBatch);
            }
            if(!cpu.isProcessing()){  //for the case that didn't process a batch before the tick or now ready to process a new batch
                DataBatch batch = null;
                batch = cluster.awaitToProcessDataBatch(this);
                if(batch != null)//else there aren't batches to process
                    cpu.startProcess(batch);
            }
        });
        subscribeBroadcast(TerminateBroadcast.class , (TerminateBroadcast terminate)->{
            terminate();
        });
        doneSignal.countDown();
    }



}
