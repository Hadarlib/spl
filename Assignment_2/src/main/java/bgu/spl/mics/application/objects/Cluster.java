package bgu.spl.mics.application.objects;

import bgu.spl.mics.Message;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.services.CPUService;
import bgu.spl.mics.application.services.GPUService;

import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Passive object representing the cluster.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Cluster {
	private ConcurrentHashMap<DataBatch , GPUService> dataBatchFromGpu;
	private ConcurrentHashMap<GPUService, Queue<DataBatch>> readyToTrain;
	private BlockingQueue<Queue<DataBatch>> needToProcess;


	private Cluster(){
		dataBatchFromGpu = new ConcurrentHashMap<>();
		readyToTrain = new ConcurrentHashMap<>();
		needToProcess = new LinkedBlockingQueue<>();
	}

	private static class clusterHolder {
		private static Cluster instance = new Cluster();
	}
	/**
	 * Retrieves the single instance of this class.
	 */
	public static Cluster getInstance() {
		return Cluster.clusterHolder.instance;
	}

	//method that called by CPU to get data batch to process
	public DataBatch awaitToProcessDataBatch(CPUService CPUService)throws InterruptedException {
		DataBatch dataBatch = null;
		synchronized (needToProcess) {
			if (!needToProcess.isEmpty()) {
				Queue<DataBatch> q = needToProcess.remove();
				if (!q.isEmpty()) {
					dataBatch = q.remove();
					if (!q.isEmpty())//else the q is empty, don't add the q to end of needToProcess queue (there are no more data batches to process for this GPU
						needToProcess.add(q);//sending the data batches to the CPUs in a round-robin
				}
			}
		}
		return dataBatch;
	}

	//method that called by GPU to get a data batch sent by it to train
	public DataBatch awaitToTrainDataBatch(GPUService GPUService) throws InterruptedException {
		DataBatch dataBatch = null;
		Queue<DataBatch> q = readyToTrain.get(GPUService);
		if(!q.isEmpty())
			dataBatch =  q.remove();
		return dataBatch;
	}

	public synchronized ConcurrentHashMap<GPUService, Queue<DataBatch>>  getReadyToTrain() {
		return readyToTrain;
	}

	public synchronized ConcurrentHashMap<DataBatch, GPUService> getDataBatchFromGpu() {
		return dataBatchFromGpu;
	}

	public synchronized BlockingQueue<Queue<DataBatch>> getNeedToProcess() {
		return needToProcess;
	}
}

