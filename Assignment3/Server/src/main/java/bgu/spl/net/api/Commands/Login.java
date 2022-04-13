package bgu.spl.net.api.Commands;

import bgu.spl.net.api.bidi.User;

public class Login extends CommandImpl {
    private User user;
    private String UserName;
    private String Password;
    private Byte Captcha;

    public Login(short opcode, User user, String userName, String password, Byte captcha) {
        super(opcode);
        this.user = user;
        UserName = userName;
        Password = password;
        Captcha = captcha;
    }

    public String execute() {
        if(user != null && user.isLoggedIn()) //a user is already logged in this protocol
            return "1102";
        if (!data.getUsers().containsKey(UserName) || data.getUsers().get(UserName).isLoggedIn() || !data.getUsers().get(UserName).getPassword().equals(Password) | Captcha == 0)
            return "1102";
        data.getUsers().get(UserName).setLoggedIn(true);
        return "1002";
    }

}
