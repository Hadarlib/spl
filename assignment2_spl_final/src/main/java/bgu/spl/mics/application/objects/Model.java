package bgu.spl.mics.application.objects;

import static bgu.spl.mics.application.objects.Data.Type.*;

/**
 * Passive object representing a Deep Learning model.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Model {
    public enum Status{PreTrained , Training , Trained , Tested };
    public enum Result{None, Good , Bad};

    private String name;
    private int size;
    private Data data;
    private Student student;
    private Status status;
    private Result result;

    public Model(String name ,String type , int size, Student student ){
        this.name = name;
        this.size = size;
        this.student = student;
        status = Status.PreTrained;
        if(type.equals(Images.toString()))
            this.data = new Data(Images , size);
        else if(type.equals(Text.toString()))
            this.data = new Data(Text , size);
        else if(type.equals(Tabular.toString()))
            this.data = new Data(Tabular , size);
        this.result = Result.None;
    }
    public String getName(){ return name;}

    public Student getStudent() {
        return student;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public Status getStatus() {
        return status;
    }

    public Data getData() {
        return data;
    }

    public Result getResult() {
        return result;
    }
}
