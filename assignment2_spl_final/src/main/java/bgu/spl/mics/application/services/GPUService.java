package bgu.spl.mics.application.services;

import bgu.spl.mics.Event;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.*;

import java.util.Queue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

import static bgu.spl.mics.application.objects.Model.Status.*;
import static bgu.spl.mics.application.objects.Model.Result.*;
import static bgu.spl.mics.application.objects.Student.Degree.*;

/**
 * GPU service is responsible for handling the
 * {@link TrainModelEvent} and {@link TestModelEvent},
 * in addition to sending the {@link //DataPreProcessEvent}.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class GPUService extends MicroService {
    private TrainModelEvent currentTrainModelEvent;
    private TestModelEvent currentTestModelEvent;
    private GPU gpu;
    private static Cluster cluster = Cluster.getInstance();
    private Queue<Event> gpuEvents;
    private final CountDownLatch doneSignal;


    public GPUService(String name, GPU gpu, CountDownLatch doneSignal) {
        super(name);
        this.gpu = gpu;
        currentTrainModelEvent = null;
        currentTestModelEvent = null;
        this.doneSignal = doneSignal;
        this.gpu.setGpuService(this);
        gpuEvents = new LinkedBlockingQueue<>();
    }

    @Override
    protected void initialize() {
        bus.register(this);
        subscribeBroadcast(TickBroadcast.class, (TickBroadcast tick) -> {
            gpu.updateTick(); //matters only if currently training a model: if so continue training the processed batches
            //after the current tick there are no more batches to train: done the training event
            if (gpu.isTrainingAModel() & gpu.getTrainedDataBatches() == gpu.getNumberOfDataBatches()) {
                gpu.getModel().setStatus(Trained);
                complete(currentTrainModelEvent, gpu.getModel());
                // send the bus broadcast that will arrive to all the students and the one that is relevant for will creat a test model event as a response
                sendBroadcast(new DoneTrainingModelBroadcast(gpu.getModel()));
                cluster.getReadyToTrain().remove(this);
                cluster.getDataBatchFromGpu().remove(gpu.getSendBatch());
                gpu.doneProcess();
            }
            // the first batch to train or after the current tick gpu can start training a new batch
            if (gpu.isTrainingAModel() & !gpu.isTrainingABatch()) {
                DataBatch batch = cluster.awaitToTrainDataBatch(this);
                if (batch != null) {//else there are no processed batches for this GPU
                    gpu.getProcessed_batches().add(batch);
                    gpu.startTrainBatch();
                }
            }
            //after the current tick gpu has done training the model, so he can take new train model event from his queue.
            if (!gpu.isTrainingAModel() & !gpuEvents.isEmpty()) {
                Event e = gpuEvents.remove();
                if (e.getClass().equals(TrainModelEvent.class)) {
                    currentTrainModelEvent = (TrainModelEvent) e;
                    Queue<DataBatch> gpuBatches = gpu.startTrainingProcess(currentTrainModelEvent.getModel());
                    cluster.getNeedToProcess().add(gpuBatches);
                    cluster.getDataBatchFromGpu().putIfAbsent(gpu.getSendBatch(), this);
                    cluster.getReadyToTrain().putIfAbsent(this, new LinkedBlockingQueue<>());
                    currentTrainModelEvent.getModel().setStatus(Training);
                } else if (e.getClass().equals(TestModelEvent.class)) {
                    currentTestModelEvent = (TestModelEvent) e;
                    testModel(currentTestModelEvent);
                }
                //if the event queue is empty, do nothing
            }
        });

        subscribeEvent(TrainModelEvent.class, (TrainModelEvent trainModel) -> {
            if (!gpu.isTrainingAModel()) { //train only if this currently not training a model
                currentTrainModelEvent = trainModel;
                Queue<DataBatch> gpuBatches = gpu.startTrainingProcess(currentTrainModelEvent.getModel());
                cluster.getNeedToProcess().add(gpuBatches);
                cluster.getDataBatchFromGpu().putIfAbsent(gpu.getSendBatch(), this);
                cluster.getReadyToTrain().putIfAbsent(this, new LinkedBlockingQueue<>());
                currentTrainModelEvent.getModel().setStatus(Training);

            } else { //already training a model- put at the end of this gpu events queue
                //other training model events will wait in this gpu events queue until this model will be done training
                gpuEvents.add(trainModel);
            }
        });
        subscribeEvent(TestModelEvent.class, (TestModelEvent testModel) -> {
            if (!gpu.isTrainingAModel())
                testModel(testModel);
            else {
                gpuEvents.add(testModel);
            }
        });
        subscribeBroadcast(TerminateBroadcast.class, (TerminateBroadcast terminate) -> {
            terminate();
        });
        doneSignal.countDown();
    }

    public void testModel(TestModelEvent testModel) {
        double randomNumber = Math.random();
        testModel.getModel().setStatus(Tested);
        if (testModel.getModel().getStudent().getDegree().equals(PhD)) {
            if (randomNumber <= 0.8) {
                testModel.getModel().setResult(Good);
                complete(testModel, testModel.getModel());
            } else {
                testModel.getModel().setResult(Bad);
                complete(testModel, testModel.getModel());
            }
        } else if (testModel.getModel().getStudent().getDegree().equals(MSc)) {
            if (randomNumber <= 0.6) {
                testModel.getModel().setResult(Good);
                complete(testModel, testModel.getModel());
            } else {
                testModel.getModel().setResult(Bad);
                complete(testModel, testModel.getModel());
            }
        }
        //sending broadcast to announce that this gpu finished test model event
        sendBroadcast(new DoneTestModelBroadcast(testModel.getModel()));

    }
}

