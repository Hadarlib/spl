package bgu.spl.mics.application.objects;
import bgu.spl.mics.application.services.GPUService;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * Passive object representing a single GPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class GPU {
    /**
     * Enum representing the type of the GPU.
     */
    enum Type {RTX3090, RTX2080, GTX1080}
    private Type type;
    private Model model; //the model the GPU is currently training on or null if it doesn't train a model
    private int numberOfDataBatches; //to count how many data batches are calculated by the data type of the model
    private int trainedDataBatches; //to count how many data batches were trained per process
    private int GPUTime; //used for statistics
    private int durationOfTrain; //the training time needed to train a data batch according the GPU type
    private int maxProcessedBatches; //the limited number of processed batches at a time
    private int startProcessTick; // used to count the number of ticks since the train began.
    private Queue<DataBatch> processed_batches; //cluster will put here processed batches from cpu
    private DataBatch sendBatch;
    private DataBatch trainingBatch;
    private GPUService gpuService;

    public GPU(String type) {
        model = null;
        numberOfDataBatches = 0;
        trainedDataBatches = 0;
        GPUTime = 0;
        if (type.equals(Type.RTX3090.toString())) {
            this.type = Type.RTX3090;
            durationOfTrain = 1;
            maxProcessedBatches = 32;
        } else if (type.equals(Type.RTX2080.toString())) {
            this.type = Type.RTX2080;
            durationOfTrain = 2;
            maxProcessedBatches = 16;
        } else {
            this.type = Type.GTX1080;
            durationOfTrain = 4;
            maxProcessedBatches = 8;
        }
        startProcessTick = 0;
        processed_batches = new LinkedBlockingQueue<>() ;
        trainingBatch = null;
        sendBatch = null;
        gpuService = null;
    }

    /**
     * @pre isProcessing() == true
     * @post @pre GPUTime + 1 = GPUTime
     * @pre isProcessing() == true and isTrainingABatch()
     * @post @pre startProcessTick + 1 = startProcessTick
     */
    public void updateTick() {
        if(isTrainingAModel()) {
            GPUTime++;
            if (isTrainingABatch()) {
                startProcessTick++;
                if (isDoneTrainingBatch())
                    doneTrainingBatch();
            }
        }
    }

    //indicates whether the GPU is currently training a model
    public boolean isTrainingAModel() {
        return model != null;
    }
    //indicates whether the GPU is currently training a batch
    public boolean isTrainingABatch(){
        return trainingBatch != null;
    }
    /**
     *
     * @param otherModel != null
     * @pre isTrainingAModel == false
     * @post isTrainingAModel == true , model == otherModel
     * @post @pre numberOfDataBatches + (int)Math.ceil(model.getData().getSize() / 1000) = numberOfDataBatches
     */
    public Queue<DataBatch> startTrainingProcess(Model otherModel) {//start the train model event
        model = otherModel;
        numberOfDataBatches = (int)Math.ceil(model.getData().getSize() / 1000);
        trainedDataBatches = 0;
        sendBatch = new DataBatch(model.getData(), gpuService);
        Queue<DataBatch> needToProcess = new LinkedList<>();
        //create the data batches according to number of data batches needed
        for(int i = 0 ; i < numberOfDataBatches ; i++){
            needToProcess.add(sendBatch);
        }
        return needToProcess;
    }


    /**
     * @pre Processed_batches.isEmpty() == false
     * @pre isTrainingABatch() == false
     * @post @pre Processed_batches().size() ==  Processed_batches().size() - 1
     * @post trainingBatch == batch
     * @post StartProcessTick() = 0;
     */
    //after GPU gets processed data batch from cluster it needs to start train it
    public void startTrainBatch() {
        if(trainingBatch == null & !processed_batches.isEmpty()) {
            trainingBatch = processed_batches.remove();
        }
    }

    public boolean isDoneTrainingBatch() {
        return startProcessTick == durationOfTrain;
    }

    /**
     * @pre isTrainingABatch() == true
     * @post isTrainingABatch() == false
     * @post @pre trainedDataBatches + 1 = trainedDataBatches
     */
    public void doneTrainingBatch(){
        trainedDataBatches++;
        trainingBatch = null;
        startProcessTick = 0;
    }
    /**
     * @pre isTrainingAModel() == true
     * @post isTrainingAModel() == false
     */
    public void doneProcess(){
        model = null;
        sendBatch = null;
    }

    public Type getType() {
        return type;
    }

    public Model getModel() {
        return model;
    }

    public int getNumberOfDataBatches() {
        return numberOfDataBatches;
    }

    public int getTrainedDataBatches() {
        return trainedDataBatches;
    }

    public int getGPUTime() {
        return GPUTime;
    }

    public int getStartProcessTick() {
        return startProcessTick;
    }

    public Queue<DataBatch> getProcessed_batches() {
        return processed_batches;
    }

    public DataBatch getSendBatch() { return sendBatch;}

    public void setGpuService(GPUService gpuService) {
        this.gpuService = gpuService;
    }

    public DataBatch getTrainingBatch() {
        return trainingBatch;
    }
}

