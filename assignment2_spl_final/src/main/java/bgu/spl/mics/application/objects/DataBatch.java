package bgu.spl.mics.application.objects;

import bgu.spl.mics.application.services.GPUService;

/**
 * Passive object representing a data used by a model.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */

public class DataBatch {
    private Data data;
    private int start_index;
    private GPUService GPUServiceSender;

    public DataBatch(Data data, GPUService gpu) {
        this.data = data;
        this.GPUServiceSender = gpu;
        start_index = 0 ;
    }

    public GPUService getGPUServiceSender() {
        return GPUServiceSender;
    }

    public Data.Type getType(){
        return data.getType();
    }
    
}
