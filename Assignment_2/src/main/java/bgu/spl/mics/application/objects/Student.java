package bgu.spl.mics.application.objects;

import java.util.ArrayList;

/**
 * Passive object representing single student.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Student {


    /**
     * Enum representing the Degree the student is studying for.
     */
    public enum Degree {
        MSc, PhD
    }
    private String name;
    private String department;
    private Degree status;
    private ArrayList<Model> models;
    private int publications;
    private int papersRead;

    public Student() {
    }

    public Student(String name , String department , String status ) {
        this.name = name;
        this.department = department;
        this.models = new ArrayList<>();
        if(status.equals("MSc"))
            this.status = Degree.MSc;
        else
            this.status = Degree.PhD;
    }
    public ArrayList<Model> getModels() {
        return models;
    }
    public String getName() {
        return name;
    }

    public Degree getDegree() {
        return status;
    }

    public void setModels(ArrayList<Model> models){
        this.models = models;
    }

    public String getDepartment() {
        return department;
    }

    public int getPublications() {
        return publications;
    }

    public int getPapersRead() {
        return papersRead;
    }
    
    public void setPublications(int publications) {
        this.publications = publications;
    }

    public void setPapersRead(int papersRead) {
        this.papersRead = papersRead;
    }
}
