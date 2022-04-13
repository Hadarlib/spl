package bgu.spl.net.api.Commands;

import bgu.spl.net.api.bidi.User;

public class LogSTAT extends CommandImpl{
    private User user;

    public LogSTAT(short opcode , User user){
        super(opcode);
        this.user = user;
    }


    public String execute(){
        if(user == null || !user.isLoggedIn())
            return "1107";
        StringBuilder status = new StringBuilder();
        status.append("1007"+ user.getStatus()+'\n');
        for (User otherUser : data.getUsers().values()){
            if(!otherUser.equals(user) && otherUser.isLoggedIn() & !otherUser.getBlockedByUsers().contains(user.getName()) & !user.getBlockedByUsers().contains(otherUser.getName()))
                status.append("ACK 7"+ otherUser.getStatus()+'\n');
        }
        //remove the last empty line
        status.deleteCharAt(status.length()-1);
        return status.toString();
    }
}
