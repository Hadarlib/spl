package bgu.spl.mics.application.services;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.Student;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.Future;


import bgu.spl.mics.MicroService;
import static bgu.spl.mics.application.objects.Model.Status.*;
import static bgu.spl.mics.application.objects.Model.Result.*;
import static bgu.spl.mics.application.objects.Student.Degree.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Student is responsible for sending the {@link TrainModelEvent},
 * {@link //TestModelEvent} and {@link //PublishResultsEvent}.
 * In addition, it must sign up for the conference publication broadcasts.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class StudentService extends MicroService {
    private Student student;
    private Future<Model> currentFuture;
    private int indexNextModelToSend;


    public StudentService(String name, Student student) {
        super(name);
        this.student = student;
        currentFuture = new Future<>();
        indexNextModelToSend = 0 ;
    }

    @Override
    protected void initialize() {
        bus.register(this);
        subscribeBroadcast(DoneTrainingModelBroadcast.class, (DoneTrainingModelBroadcast done)->{
            if(done.getModel().getStudent().getName().equals(this.getName())){
                currentFuture = sendEvent(new TestModelEvent(done.getModel()));
            }
        });
        subscribeBroadcast(DoneTestModelBroadcast.class, (DoneTestModelBroadcast done)->{
            if(done.getModel().getStudent().getName().equals(this.getName())){
                Model updateModel = currentFuture.get();

                if(updateModel.getResult().toString().equals(Good.toString())){
                    currentFuture = sendEvent(new PublishResultsEvent(updateModel));
                }
                if(indexNextModelToSend < student.getModels().size())
                    sendTrainModelEvent();
                //else no more models to train
            }
        });

        subscribeBroadcast(PublishConferenceBroadcast.class, (PublishConferenceBroadcast publish) -> {
            LinkedList<Model> publishedModels = publish.getPublishedModels();
            for(Model model : publishedModels ){
                if(model.getStudent().getName().equals(this.getName())){//this model belongs to this student, need to add it to the publications
                    int studentPublications = student.getPublications();
                    student.setPublications(studentPublications+1);
                }
                else{//this model doesn't belong to this student, need to add it to number of papers read
                    int studentPapersRead = student.getPapersRead();
                    student.setPapersRead(studentPapersRead+1);
                }
            }
        });
        subscribeBroadcast(TerminateBroadcast.class, (TerminateBroadcast terminate)->{
            terminate();
        });
        sendTrainModelEvent();//after the student subscribed, send the first model to training
    }
    
    public void sendTrainModelEvent(){
        Model sendModel = student.getModels().get(indexNextModelToSend);
        currentFuture = sendEvent(new TrainModelEvent(sendModel));
        indexNextModelToSend++;
    }
}
