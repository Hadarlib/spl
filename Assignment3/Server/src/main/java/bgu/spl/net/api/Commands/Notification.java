package bgu.spl.net.api.Commands;



public class Notification extends CommandImpl{
    private Byte notificationType;
    private String PostingUser;
    private String Content;

    public Notification(short opcode, Byte notificationType, String postingUser, String content) {
        super(opcode);
        this.notificationType = notificationType;
        PostingUser = postingUser;
        Content = content;
    }

    public String execute(){
        return null;
    }
}
