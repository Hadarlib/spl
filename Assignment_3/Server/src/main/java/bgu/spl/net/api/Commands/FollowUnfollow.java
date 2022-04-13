package bgu.spl.net.api.Commands;

import bgu.spl.net.api.bidi.User;

public class FollowUnfollow extends CommandImpl{
    private char FollowOrUnfollow;
    private String UserNameToFollowUnfollow;
    private User user;

    public FollowUnfollow(short opcode, char followOrUnfollow, String userName , User user) {
        super(opcode);
        FollowOrUnfollow = followOrUnfollow;
        UserNameToFollowUnfollow = userName;
        this.user = user;
    }

    public String execute(){
        if(user == null || !user.isLoggedIn() || !data.getUsers().containsKey(UserNameToFollowUnfollow) ) {
            return "1104";
        }
        User otherUser = data.getUsers().get(UserNameToFollowUnfollow);
        if(otherUser.getBlockedByUsers().contains(user.getName()) | user.getBlockedByUsers().contains(UserNameToFollowUnfollow))
            return "1104";
        if(FollowOrUnfollow == '0') {//follow case
            if(user.getFollowing().contains(otherUser)) {//if this user already following the other user
                return "1104";
            }
            else{
                user.getFollowing().add(otherUser);
                otherUser.getFollowers().add(user);
            }
        }
        else if (FollowOrUnfollow == '1'){//unfollow case
            if(!user.getFollowing().contains(otherUser)) {//if this user already unfollowing the other user
                return "1104";
            }
            else{
                user.getFollowing().remove(otherUser);
                otherUser.getFollowers().remove(user);
            }
        }
        return "1004" + FollowOrUnfollow + UserNameToFollowUnfollow;//??? check what needs to be back
    }
}
