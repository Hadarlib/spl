package bgu.spl.net.api.Commands;

import bgu.spl.net.api.bidi.User;

public class Register extends CommandImpl {
    private String UserName;
    private String Password;
    private String Birthday;
    private User user;
    private User protocolUser;
    private int ID ;

    public Register(short opcode,User user , String UserName, String Password, String Birthday, int ID) {
        super(opcode);
        this.UserName = UserName;
        this.Password = Password;
        this.Birthday = Birthday;
        this.user = null;
        protocolUser = user;
        this.ID = ID;

    }

    public String execute(){
        if(data.getUsers().containsKey(UserName) || protocolUser != null && protocolUser.isLoggedIn())
            return "1101";
        int age = 2022 - Integer.parseInt(Birthday.substring(6));
        this.user = new User(ID, UserName , Password , age);
        data.addUser(user);
        return "1001";
    }

    public User getUser() {
        return user;
    }
}
