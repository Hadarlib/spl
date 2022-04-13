package bgu.spl.mics.application.objects;

import bgu.spl.mics.application.services.GPUService;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import bgu.spl.mics.application.objects.CPU;

import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;


public class CPUTest extends TestCase {
    CPU cpu;
    DataBatch dataBatch;
    GPU gpu;
    GPUService gpuService;

    @Before
    public void setUp() throws Exception {
        cpu = new CPU(32);
        gpu = new GPU("RTX3090");
        gpuService = new GPUService("gpuService", gpu, new CountDownLatch(0));
        dataBatch = new DataBatch(new Data(Data.Type.Images,1000),gpuService);
    }


    @Test
    public void testUpdateTick() {
        int cpuTimeBefore = cpu.getCPUTime();
        int durationBefore = cpu.getDurationOfProcess();
        cpu.updateTick();
        assertEquals(cpuTimeBefore, cpu.getCPUTime());
        assertEquals(durationBefore, cpu.getDurationOfProcess());
        cpu.startProcess(dataBatch);
        assertTrue(cpu.isProcessing());
        cpu.updateTick();
        assertEquals(cpuTimeBefore+1, cpu.getCPUTime());
        assertEquals(durationBefore+1, cpu.getDurationOfProcess());
        int i = 0;
        while(i<3){
            cpu.updateTick();
            i++;
        }
        assertTrue(cpu.isDoneProcess());
        assertEquals(durationBefore+4, cpu.getDurationOfProcess());
        cpu.doneProcess();
        cpu.getDataBatchToProcess().remove();
        assertEquals(cpuTimeBefore+4, cpu.getCPUTime());
        assertEquals(0 , cpu.getDurationOfProcess());
        assertFalse(cpu.isProcessing());
        cpu.startProcess(dataBatch);
        assertEquals(0, cpu.getDurationOfProcess());
        assertEquals(1 , cpu.getTotalDataBatches());
    }
    @Test
    public void testIsProcessing() {
        assertFalse(cpu.isProcessing());
        cpu.startProcess(dataBatch);
        assertTrue(cpu.isProcessing());
    }
    @Test
    public void testStartProcess() {
        assertTrue(cpu.getDataBatchToProcess().isEmpty());
        cpu.startProcess(dataBatch);
        assertFalse(cpu.getDataBatchToProcess().isEmpty());
        assertEquals(0,cpu.getDurationOfProcess());
        assertTrue(cpu.getDataBatchToProcess().contains(dataBatch));
    }
    @Test
    public void testIsDoneProcess() {
        assertFalse(cpu.isDoneProcess());
        cpu.startProcess(dataBatch);
        assertEquals(0, cpu.getDurationOfProcess());
        assertFalse(cpu.isDoneProcess());
        assertTrue(cpu.isProcessing());
        int i = 0;
        while(i<4){
            cpu.updateTick();
            i++;
        }
        assertTrue(cpu.isDoneProcess());
        cpu.doneProcess();
        cpu.getDataBatchToProcess().remove();
        assertFalse(cpu.isProcessing());

    }
    @Test
    public void testDoneProcess() {
        int beforeTotalDataBatches = cpu.getTotalDataBatches();
        assertFalse(cpu.isProcessing());
        cpu.startProcess(dataBatch);
        assertTrue(cpu.isProcessing());
        assertEquals(0,cpu.getDurationOfProcess());
        assertTrue(cpu.getDataBatchToProcess().contains(dataBatch));
        int i = 0;
        while(i<4){
            cpu.updateTick();
            i++;
        }
        assertTrue(cpu.isProcessing());
        assertTrue(cpu.isDoneProcess());
        cpu.doneProcess();
        cpu.getDataBatchToProcess().remove();
        assertEquals(cpu.getTotalDataBatches() , beforeTotalDataBatches+1);
        assertFalse(cpu.isProcessing());
        assertEquals(0 , cpu.getDurationOfProcess());
    }
    @Test
    public void testGetCPUTime() {
        assertEquals(0, cpu.getCPUTime());
        cpu.startProcess(dataBatch);
        cpu.updateTick();
        assertEquals(1,cpu.getCPUTime());
        cpu.doneProcess();
        cpu.getDataBatchToProcess().remove();
        cpu.updateTick();
        assertEquals(1,cpu.getCPUTime());



    }
    @Test
    public void testGetDurationOfProcess() {
        assertEquals(0, cpu.getDurationOfProcess());
        cpu.startProcess(dataBatch);
        cpu.updateTick();
        assertEquals(1,cpu.getDurationOfProcess());
        cpu.doneProcess();
        assertEquals(0,cpu.getDurationOfProcess());
    }
    @Test
    public void testGetTotalDataBatches() {
        assertEquals(0, cpu.getTotalDataBatches());
        cpu.startProcess(dataBatch);
        int i = 0;
        while(i<4){
            cpu.updateTick();
            i++;
        }
        assertTrue(cpu.isDoneProcess());
        cpu.doneProcess();
        cpu.getDataBatchToProcess().remove();
        assertEquals(1,cpu.getTotalDataBatches());
        cpu.startProcess(dataBatch);
        i = 0;
        while(i<4){
            cpu.updateTick();
            i++;
        }
        assertTrue(cpu.isDoneProcess());
        cpu.doneProcess();
        cpu.getDataBatchToProcess().remove();
        assertEquals(2,cpu.getTotalDataBatches());
    }

}