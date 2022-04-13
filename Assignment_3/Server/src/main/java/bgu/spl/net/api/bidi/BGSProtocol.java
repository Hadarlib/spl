package bgu.spl.net.api.bidi;
import java.lang.String;
import bgu.spl.net.api.Commands.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;



public class BGSProtocol implements BidiMessagingProtocol<String> {
    private int ID; //each protocol contains an ID with initialize only once to the value accepts by connections object
    private String feedback; //will hold ACK\ERROR msg from server
    private String notificationPM; //will hold notificationPM msg from server
    private String notificationPOST; //will hold notificationPM msg from server
    private DATA data = DATA.getInstance(); //singleton
    private User user; // each protocol will hold the data of the user he handles
    private Connections<String> connections; //singleton
    private boolean needToTerminate; //termination condition of the protocol


    public BGSProtocol() {
        this.user = null;
        notificationPM = null;
        notificationPOST = null;
        needToTerminate = false;
    }

    @Override

    public void start(int connectionId, Connections<String> connections) {
        ID = connectionId;
        this.connections = connections;
    }

    @Override
    public void process(String message) {
        String s_opcode = message.substring(0 , message.indexOf(" "));
        String[] arr = new String[10];
        if(!s_opcode.equals("03") & !s_opcode.equals("07")) {
            message = message.substring(3);
            arr = message.split("\0");
        }
        if(s_opcode.equals("01")) {
            Short opcode = 1;
            String userName = arr[0];
            String password = arr[1];
            String birthday = arr[2];
            CommandImpl commandToExecute = new Register(opcode, user , userName, password, birthday, ID);
            feedback = commandToExecute.execute();
            if(feedback.equals("1001")) { //for successful register initialize the user
                user = data.getUsers().get(userName);
            }

        }
        else if(s_opcode.equals("02")){
            Short opcode = 2;
            String  userName = arr[0];
            String password = arr[1];
            Byte captcha = Byte.parseByte(arr[2]);
            CommandImpl commandToExecute = new Login(opcode , user ,userName , password , captcha );
            feedback = commandToExecute.execute();
            if(feedback.equals("1002")) { // for successful login initialize the user and the user id( might be different if the login wasn't right after REGISTER
                user = data.getUsers().get(userName);
                user.setID(ID);
            }
        }
        else if(s_opcode.equals("03")){
            Short opcode = 3;
            CommandImpl commandToExecute = new Logout(opcode , user);
            feedback = commandToExecute.execute();

        }
        else if(s_opcode.equals("04")){
            Short opcode = 4;
            char followUnfollow = arr[0].charAt(0);
            String userName = arr[0].substring(1);
            CommandImpl commandToExecute = new FollowUnfollow(opcode , followUnfollow , userName, user);
            feedback = commandToExecute.execute();
        }
        else if(s_opcode.equals("05")){
            Short opcode = 5;
            String content = arr[0];
            CommandImpl commandToExecute = new Post(opcode , content , user);
            feedback = commandToExecute.execute();
            if(feedback.equals("1005")) // for successful POST creat the content for NOTIFICATION msg
                notificationPOST = "0901"+ user.getName() + '\0' + content + '\0';
        }

        else if(s_opcode.equals("06")){
            Short opcode = 6;
            String userNameToSend = arr[0];
            String content = arr[1];
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy "/*HH:mm:ss"*/);
            String strDate = sdf.format(cal.getTime());
            CommandImpl commandToExecute = new PM(opcode, userNameToSend ,content, strDate, user);
            feedback = commandToExecute.execute();
            if(feedback.equals("1006"))// for successful PM creat the content for NOTIFICATION msg
                notificationPM = "0900" + userNameToSend + " " + user.getName() + '\0' + content + " " + strDate + '\0';
        }

        else if(s_opcode.equals("07")){
            Short opcode = 7;
            CommandImpl commandToExecute = new LogSTAT(opcode,user);
            feedback = commandToExecute.execute();
        }


        else if(s_opcode.equals("08")){
            Short opcode = 8;
            //split take the string until '|' , and after the '|' if exists
            String[] ListUsers = arr[0].split("\\|");
            CommandImpl commandToExecute = new STAT(opcode ,ListUsers, user);
            feedback = commandToExecute.execute();
            if(!feedback.equals("1108")){
               feedback = feedback.replaceAll("1008", "ACK 8");
               feedback = feedback.replaceFirst("ACK 8", "1008");
            }

        }

        else if(s_opcode.equals("12")){
            Short opcode = 12;
            String userName = arr[0];
            CommandImpl commandToExecute = new Block(opcode ,userName, user);
            feedback = commandToExecute.execute();
        }

        if(feedback != null){ //ACK / ERROR
            connections.send(ID ,feedback); //sent to client with ID
            if(feedback.equals("1002") && !user.getNotifications().isEmpty()){//after successful login send the notifications for client if exists
                for (String notification : user.getNotifications())
                    connections.send(ID, notification);
                user.getNotifications().clear();
            }

            if(feedback.equals("1003")) {//ACK for logout
                needToTerminate = true;
                connections.disconnect(ID);
            }
            feedback = null;
        }

        if(notificationPM != null) {//in case of PM
            String part1 = notificationPM.substring(0,4); //no space at the end
            String userNameToSend = notificationPM.substring(4, notificationPM.indexOf(" ")); //no space at the end
            notificationPM = part1 + notificationPM.substring(notificationPM.indexOf(" ")+1); //space between the strings
            int idToSend = data.getUsers().get(userNameToSend).getID();
            if(data.getUsers().get(userNameToSend).isLoggedIn())
                connections.send(idToSend, notificationPM); //send if logged
            else
                data.getUsers().get(userNameToSend).addNotification(notificationPM); //keep until the user will log-in

            notificationPM = null;
        }
        else if (notificationPOST != null){//in case of post
                for(User follower : user.getFollowers()) {//send the post to all the followers users
                    if(follower.isLoggedIn())
                        connections.send(follower.getID(), notificationPOST);//send if logged
                    else
                        follower.addNotification(notificationPOST); //keep until the user will log-in
                }
                if( notificationPOST.contains("@")) { // someone was tagged
                    String content_copy = notificationPOST.substring(notificationPOST.indexOf('@'));
                    String[] arrayOfTags = content_copy.split("@");
                    for (String tag : arrayOfTags) {
                        String userNameToSend = "";
                        if(tag.contains(" "))
                            userNameToSend = tag.substring(0, tag.indexOf(" "));
                        else if(!tag.isEmpty())
                            userNameToSend = tag.substring(0 , tag.length()-1);
                        if (data.getUsers().containsKey(userNameToSend) && !user.getBlockedByUsers().contains(userNameToSend) && !user.getFollowers().contains(data.getUsers().get(userNameToSend))) {
                            int idToSend = data.getUsers().get(userNameToSend).getID();
                            if (data.getUsers().get(userNameToSend).isLoggedIn())//send if logged
                                connections.send(idToSend, notificationPOST);
                            else
                                data.getUsers().get(userNameToSend).addNotification(notificationPOST); //keep until the user will log-in
                        }
                    }
                }
                notificationPOST = null;
            }

        }






    @Override
    public boolean shouldTerminate() {
        return needToTerminate;
    }


    public int getID() {
        return ID;
    }



    public void setID(int ID) {
        this.ID = ID;
    }


}
