package bgu.spl.mics.application.objects;

import bgu.spl.mics.application.services.GPUService;
import bgu.spl.mics.example.messages.ExampleEvent;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

import static bgu.spl.mics.application.objects.Data.Type.Images;

public class GPUTest extends TestCase {
    GPU gpu;
    DataBatch dataBatch;
    Model model;
    GPUService gpuService;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        gpu = new GPU("RTX3090");
        gpuService = new GPUService("gpuService", gpu, new CountDownLatch(0));
        dataBatch = new DataBatch(new Data(Images,1000),gpuService);
        model = new Model("trainModel" , "Images",1000 , new Student());

    }

    @Test
    public void testUpdateTick() {
        int beforeGPUTime = gpu.getGPUTime();
        gpu.updateTick();
        assertEquals(beforeGPUTime,gpu.getGPUTime());
        gpu.startTrainingProcess(model);
        gpu.updateTick();
        assertEquals(beforeGPUTime+1,gpu.getGPUTime());
        assertEquals(gpu.getStartProcessTick() , 0);
        gpu.getProcessed_batches().add(dataBatch);
        gpu.startTrainBatch();
        assertEquals(0,gpu.getStartProcessTick());
        gpu.updateTick();
        assertNull(gpu.getTrainingBatch());
        assertEquals(0 , gpu.getStartProcessTick());

    }
    @Test
    public void testIsTrainingAModel() {
        assertFalse(gpu.isTrainingAModel());
        gpu.startTrainingProcess(model);
        assertTrue(gpu.isTrainingAModel());
    }
    @Test
    public void testIsTrainingABatch(){
        assertFalse(gpu.isTrainingABatch());
        gpu.startTrainingProcess(model);
        gpu.getProcessed_batches().add(dataBatch);
        gpu.startTrainBatch();
        assertTrue(gpu.isTrainingABatch());
    }

    @Test
    public void testStartTrainingProcess() {
        assertNull(gpu.getModel());
        assertEquals(0, gpu.getNumberOfDataBatches());
        gpu.startTrainingProcess(model);
        assertEquals(model , gpu.getModel());
        assertEquals(1, gpu.getNumberOfDataBatches());
    }


    @Test
    public void testStartTrainBatch() {
        gpu.startTrainingProcess(model);
        assertFalse(gpu.isTrainingABatch());
        int beforeProcessedBatchesNum = gpu.getProcessed_batches().size();//0
        gpu.getProcessed_batches().add(dataBatch);
        assertEquals(beforeProcessedBatchesNum +1, gpu.getProcessed_batches().size());
        assertNull(gpu.getTrainingBatch());
        gpu.startTrainBatch();
        assertTrue(gpu.isTrainingABatch());
        assertEquals(gpu.getTrainingBatch() , dataBatch);
        assertEquals(beforeProcessedBatchesNum  , gpu.getProcessed_batches().size());
        assertEquals(0 , gpu.getStartProcessTick());

    }
    @Test
    public void testIsDoneTrainingBatch() {
        gpu.startTrainingProcess(model);
        gpu.getProcessed_batches().add(dataBatch);
        assertFalse(gpu.isDoneTrainingBatch());
        gpu.startTrainBatch();
        assertFalse(gpu.isDoneTrainingBatch());
        gpu.updateTick();
        assertFalse(gpu.isTrainingABatch());
        assertNull(gpu.getTrainingBatch());
        assertEquals(1 , gpu.getTrainedDataBatches());
    }
    @Test
    public void testDoneTrainingBatch() {
        gpu.startTrainingProcess(model);
        gpu.getProcessed_batches().add(dataBatch);
        int beforeTrainedDataBatches = gpu.getTrainedDataBatches();
        assertFalse(gpu.isDoneTrainingBatch());
        gpu.startTrainBatch();
        assertTrue(gpu.isTrainingABatch());
        assertFalse(gpu.isDoneTrainingBatch());
        gpu.updateTick();
        assertNull(gpu.getTrainingBatch());
        assertFalse(gpu.isTrainingABatch());
        assertEquals(beforeTrainedDataBatches , gpu.getTrainedDataBatches() -1);
    }
    @Test
    public void testDoneProcess() {
        assertFalse(gpu.isTrainingAModel());
        gpu.startTrainingProcess(model);
        assertTrue(gpu.isTrainingAModel());
        gpu.getProcessed_batches().add(dataBatch);
        gpu.startTrainBatch();
        gpu.updateTick();
        assertTrue(gpu.isTrainingAModel());
        assertEquals(gpu.getNumberOfDataBatches(), gpu.getTrainedDataBatches());
        gpu.doneProcess();
        assertFalse(gpu.isTrainingAModel());
        assertNull(gpu.getModel());
    }
}