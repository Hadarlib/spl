package bgu.spl.net.api.Commands;

import bgu.spl.net.api.bidi.User;

public class PM extends CommandImpl{
    private String UserNameToSend;
    private String Content;
    private String sendingDateAndTime;
    private User user;

    public PM(short opcode, String UserNameToSend, String content, String sendingDateAndTime, User user) {
        super(opcode);
        this.UserNameToSend = UserNameToSend;
        Content = content;
        this.sendingDateAndTime = sendingDateAndTime;
        this.user = user;
    }

    public String execute(){
        if( user == null || !user.isLoggedIn() || user.getBlockedByUsers().contains(UserNameToSend) )
            return "1106";
        if( !data.getUsers().containsKey(UserNameToSend) )
            return "1106@"+UserNameToSend+" isn't applicable for private messages";
        if(!user.getFollowing().contains(data.getUsers().get(UserNameToSend)))
            return "1106";
        String copy_content = Content;
        for(String toFilter : data.getFilter())
           copy_content = copy_content.replaceAll(toFilter , "<filtered>");
        data.addPM(copy_content);
        return "1006";


    }
}
