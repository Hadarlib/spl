package bgu.spl.mics.application;

import bgu.spl.mics.application.objects.ConfrenceInformation;
import bgu.spl.mics.application.objects.Data;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.Student;

import java.util.ArrayList;

public class Output {

    private ArrayList<StudentOutput> students;
    private ArrayList<ConferencesOutput> conferences;
    private int cpuTimeUsed;
    private int gpuTimeUsed;
    private int batchesProcessed;

    public Output(ArrayList<Student> students, ArrayList<ConfrenceInformation> conferences, int cpuTimeUsed, int gpuTimeUsed, int batchesProcessed) {
        this.students = new ArrayList<>();
        for (Student student : students)
            this.students.add(new StudentOutput(student));
        this.conferences = new ArrayList<>();
        for (ConfrenceInformation conference : conferences)
            this.conferences.add(new ConferencesOutput(conference));
        this.cpuTimeUsed = cpuTimeUsed;
        this.gpuTimeUsed = gpuTimeUsed;
        this.batchesProcessed = batchesProcessed;
    }
}

    class StudentOutput {
        private String name;
        private String department;
        private String status;
        private int publications;
        private int papersRead;
        private ArrayList<ModelOutput> trainedModels;

        public StudentOutput(Student student) {
            this.name = student.getName();
            this.department = student.getDepartment();
            this.status = student.getDegree().toString();
            this.publications = student.getPublications();
            this.papersRead = student.getPapersRead();
            this.trainedModels = new ArrayList<>();
            for (Model model : student.getModels()) {
                if (model.getStatus().toString().equals("Trained") || model.getStatus().toString().equals("Tested"))
                    this.trainedModels.add(new ModelOutput(model));
            }
        }
    }

    class ModelOutput {
        private String name;
        private Data data;
        private String status;
        private String results;

        public ModelOutput(Model model) {
            this.name = model.getName();
            this.data = model.getData();
            this.status = model.getStatus().toString();
            this.results = model.getResult().toString();
        }
    }

    class ConferencesOutput {
        private String name;
        private int date;
        private ArrayList<ModelOutput> publications;

        public ConferencesOutput(ConfrenceInformation conference) {
            this.name = conference.getName();
            this.date = conference.getDate();
            this.publications = new ArrayList<>();
            for (Model model : conference.getModelsToPublish())
                publications.add(new ModelOutput(model));

        }
    }


