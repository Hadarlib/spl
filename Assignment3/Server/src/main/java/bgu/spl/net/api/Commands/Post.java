package bgu.spl.net.api.Commands;
import bgu.spl.net.api.bidi.User;


public class Post extends CommandImpl{
    private String Content;
    private User user;

    public Post(short opcode, String content , User user) {
        super(opcode);
        Content = content;
        this.user = user;
    }

    public String execute(){
        if(user == null || !user.isLoggedIn())
            return "1105";
        user.setNumberOfPosts(user.getNumberOfPosts()+1);
        data.addPost(Content);
        return "1005";
    }

}
