package com.app.toado.activity.GroupChat;

/**
 * Created by Silent Knight on 18-06-2018.
 */

public class GroupChatObject {
    String createdByUser;
    private String message;
    private Boolean currentUser;
    String timestamp;
    public GroupChatObject(String createdByUser,String message, String timestamp,Boolean currentUser){
        this.message = message;
        this.currentUser = currentUser;
        this.timestamp=timestamp;
        this.createdByUser=createdByUser;
    }

    public String getCreatedByUser() {
        return createdByUser;
    }

    public void setCreatedByUser(String createdByUser) {
        this.createdByUser = createdByUser;
    }

    public String getMessage(){
        return message;
    }
    public void setMessage(String userID){
        this.message = message;
    }

    public Boolean getCurrentUser(){
        return currentUser;
    }
    public void setCurrentUser(Boolean currentUser){
        this.currentUser = currentUser;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

}
