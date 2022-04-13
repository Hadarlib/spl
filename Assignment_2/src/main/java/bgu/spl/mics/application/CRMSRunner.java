package bgu.spl.mics.application;
import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.*;
import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;


/** This is the Main class of Compute Resources Management System application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output a text file.
 */
public class CRMSRunner {
    public static void main(String[] args) {
        ArrayList<Student> students = new ArrayList<>();
        ArrayList<GPU> GPUS = new ArrayList<>();
        ArrayList<CPU> CPUS = new ArrayList<>();
        ArrayList<ConfrenceInformation> conferences = new ArrayList<>();
        ArrayList<Thread> studentsThreads = new ArrayList<>();
        ArrayList<Thread> threads = new ArrayList<>();
        File input = new File(args[0]);

        try {
            JsonElement fileElement = JsonParser.parseReader(new FileReader(input));
            JsonObject fileObject = fileElement.getAsJsonObject();
            JsonArray jsonArrayOfStudents = fileObject.get("Students").getAsJsonArray();
            for (JsonElement studentElement : jsonArrayOfStudents) {
                JsonObject studentJasonObject = studentElement.getAsJsonObject();
                String name = studentJasonObject.get("name").getAsString();
                String department = studentJasonObject.get("department").getAsString();
                String status = studentJasonObject.get("status").getAsString();
                Student student = new Student(name, department, status);
                students.add(student);
                Thread studentService = new Thread(new StudentService(name, student));
                studentsThreads.add(studentService);
                threads.add(studentService);
                JsonArray jsonArrayOfModels = studentJasonObject.get("models").getAsJsonArray();
                ArrayList<Model> models = new ArrayList<>();
                for (JsonElement modelElement : jsonArrayOfModels) {
                    JsonObject modelJasonObject = modelElement.getAsJsonObject();
                    String nameModel = modelJasonObject.get("name").getAsString();
                    String type = modelJasonObject.get("type").getAsString();
                    int size = modelJasonObject.get("size").getAsInt();
                    Model model = new Model(nameModel, type, size, student);
                    models.add(model);
                }
                student.setModels(models);
            }
            JsonArray jsonArrayOfGPUS = fileObject.get("GPUS").getAsJsonArray();
            for (JsonElement gpuElement : jsonArrayOfGPUS) {
                String type = gpuElement.getAsString();
                GPU gpu = new GPU(type);
                GPUS.add(gpu);
            }
            JsonArray jsonArrayOfCPUS = fileObject.get("CPUS").getAsJsonArray();
            for (JsonElement cpuElement : jsonArrayOfCPUS) {
                int cores = cpuElement.getAsInt();
                CPU cpu = new CPU(cores);
                CPUS.add(cpu);
            }
            JsonArray jsonArrayOfConferences = fileObject.get("Conferences").getAsJsonArray();
            for (JsonElement conferencesElement : jsonArrayOfConferences) {
                JsonObject conferenceJasonObject = conferencesElement.getAsJsonObject();
                String name = conferenceJasonObject.get("name").getAsString();
                int date = conferenceJasonObject.get("date").getAsInt();
                ConfrenceInformation conference = new ConfrenceInformation(name, date);
                conferences.add(conference);
            }
            int tick = fileObject.get("TickTime").getAsInt();
            int duration = fileObject.get("Duration").getAsInt();
            Thread timeService = new Thread(new TimeService("timeService", tick, duration));
            studentsThreads.add(timeService);
            threads.add(timeService);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        int tasks = GPUS.size() + CPUS.size() + conferences.size();
        // in order to let gpus, cpus , conferences services to register and subscribe before students will send an event and time service will start
        CountDownLatch doneSignal = new CountDownLatch(tasks);

        for (GPU gpu : GPUS) { //initialize gpu services
            Thread gpuService = new Thread(new GPUService("gpuService", gpu, doneSignal));
            threads.add(gpuService);
            gpuService.start();
        }
        for (CPU cpu : CPUS) {//initialize cpu services
            Thread cpuService = new Thread(new CPUService("cpuService", cpu, doneSignal));
            threads.add(cpuService);
            cpuService.start();
        }
        for (ConfrenceInformation conference : conferences) { //initialize conferences services
            Thread conferenceService = new Thread(new ConferenceService(conference.getName(), conference, doneSignal));
            threads.add(conferenceService);
            conferenceService.start();
        }
        try {
            doneSignal.await(); // wait for all to register and subscribe before the students and time service
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (Thread t : studentsThreads)  //let the students start (including the time service)
            t.start();

        for (Thread t : threads) {
            try {
                t.join();//waiting for all the services to terminate
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        int cpuTimeUsed = 0;
        int batchesProcessed = 0;
        for (CPU cpu : CPUS) {//calculating for the output
            cpuTimeUsed = cpuTimeUsed + cpu.getCPUTime();
            batchesProcessed = batchesProcessed + cpu.getTotalDataBatches();
        }
        int gpuTimeUsed = 0;
        for (GPU gpu : GPUS) {//calculating for the output
            gpuTimeUsed += gpu.getGPUTime();

        }


        Output output = new Output(students, conferences, cpuTimeUsed, gpuTimeUsed, batchesProcessed);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            FileWriter writer = new FileWriter(args[1]);
            gson.toJson(output,writer);
            writer.flush();
        }catch (IOException ex){
            ex.printStackTrace();
        }
    }


}




