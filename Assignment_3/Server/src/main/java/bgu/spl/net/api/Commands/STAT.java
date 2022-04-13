package bgu.spl.net.api.Commands;

import bgu.spl.net.api.bidi.User;


public class STAT extends CommandImpl{
    private String[] ListOfUsernames;
    private User user;

    public STAT(short opcode, String[] list, User user){
        super(opcode);
        ListOfUsernames = list;
        this.user = user;
    }

    public String execute(){
            if(user == null || !user.isLoggedIn())
                return "1108";
            StringBuilder status = new StringBuilder();
            for (String otherUser : ListOfUsernames){
                if(otherUser.isEmpty()  || !data.getUsers().containsKey(otherUser))
                    return "1108";
                else if(user.getBlockedByUsers().contains(otherUser) | data.getUsers().get(otherUser).getBlockedByUsers().contains(user.getName()))
                    return "1108";
                else
                    status.append("1008"+ data.getUsers().get(otherUser).getStatus()+'\n');
            }
            //remove the last empty line
            status.deleteCharAt(status.length()-1);
            return status.toString();
        }



}
