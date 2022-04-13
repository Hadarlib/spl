package bgu.spl.net.api.Commands;
import bgu.spl.net.api.bidi.User;

public class Block extends CommandImpl{
    private String userNameToBlock;
    private User user;

    public Block(Short opcode, String userNameToBlock , User user) {
        super(opcode);
        this.userNameToBlock = userNameToBlock;
        this.user = user;
    }

    @Override
    public String execute() {
        if(user == null || !user.isLoggedIn() || !data.getUsers().containsKey(userNameToBlock) )
            return "1112";
        User otherUser = data.getUsers().get(userNameToBlock);
        if(otherUser.getBlockedByUsers().contains(user.getName()))//if this user already blocked the other user
            return "1112";
        user.getFollowing().remove(otherUser);
        user.getFollowers().remove(otherUser);
        otherUser.getBlockedByUsers().add(user.getName());
        otherUser.getFollowing().remove(user);
        otherUser.getFollowers().remove(user);
        return "1012";
    }
}
