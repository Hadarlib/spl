package bgu.spl.net.api.Commands;

import bgu.spl.net.api.bidi.User;

public class Logout extends CommandImpl{
    private User user;

    public Logout(short opcode, User user) {
        super(opcode);
        this.user = user;
    }

    public String execute(){
        if(user == null || !user.isLoggedIn())
            return "1103";
        data.getUsers().get(user.getName()).setLoggedIn(false);
        return "1003";
    }
}
