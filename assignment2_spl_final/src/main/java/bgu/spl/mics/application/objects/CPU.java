package bgu.spl.mics.application.objects;
import java.util.HashMap;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * Passive object representing a single CPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class CPU {
    private Queue<DataBatch> dataBatchToProcess; //will always hold only one data Batch
    private int cores;
    private int totalDataBatches; //used for statistics
    private int CPUTime; //used for statistics
    private int durationOfProcess; //used to count the number of ticks since the process began
    private HashMap<Data.Type,Integer> processingTimeType; //contains the time needed to process a batch according to the number of cores


    public CPU(int cores){
        this.cores = cores;
        dataBatchToProcess = new LinkedBlockingQueue<>();
        totalDataBatches = 0;
        CPUTime = 0;
        durationOfProcess = 0;
        processingTimeType = new HashMap<>();
        processingTimeType.put(Data.Type.Images , 32/cores*4); //images time processing
        processingTimeType.put(Data.Type.Text , 32/cores*2); //text time processing
        processingTimeType.put(Data.Type.Tabular , 32/cores*1); //tabular time processing

    }
    /**
     * @pre isProcessing() == true
     * @post @pre(CPUTime) +1 = CPUTime
     * @post @pre(durationOfProcess) + 1 = durationOfProcess
     *
     */
    public void updateTick() {
        if(isProcessing()) {
            CPUTime++;
            durationOfProcess++;
        }

    }
    //indicates whether the CPU is currently processing data
     public boolean isProcessing(){
        return !dataBatchToProcess.isEmpty();
     }

    /**
     * @param dataB != null
     * @pre DataBatchToProcess.isEmpty() == true
     * @post durationOfProcess == 0
     * @post DataBatchToProcess.contains(dataB) == true;
     *
     */
     public void startProcess(DataBatch dataB){ //dataB is sent from CPU service-> massageType hold data batch
        dataBatchToProcess.add(dataB);
    }

    public boolean isDoneProcess(){
         if(dataBatchToProcess.isEmpty())
             return false;
        else
            return durationOfProcess == processingTimeType.get(dataBatchToProcess.peek().getType());
    }
    /**
     * @pre DataBatchToProcess.isEmpty() == false
     * @post DataBatchToProcess.isEmpty() == true
     * @post @pre(totalDataBatches) + 1 = totalDataBatches
     *
     */
    public void doneProcess(){
        totalDataBatches++;
        durationOfProcess = 0;
    }

    public int getCPUTime() {
        return CPUTime;
    }

    public int getDurationOfProcess() {
        return durationOfProcess;
    }

    public int getTotalDataBatches() {
        return totalDataBatches;
    }

    public Queue<DataBatch> getDataBatchToProcess() {
        return dataBatchToProcess;
    }
}
