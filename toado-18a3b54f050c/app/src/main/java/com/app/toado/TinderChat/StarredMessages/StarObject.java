package com.app.toado.TinderChat.StarredMessages;

/**
 * Created by Silent Knight on 11-06-2018.
 */

public class StarObject {
    private String message;
    private Boolean currentUser;
    String timestamp;
    String sender,reciever,position;
    public StarObject(String message, String timestamp,String position,String sender,String reciever){
        this.message = message;
        this.currentUser = currentUser;
        this.timestamp=timestamp;
        this.sender=sender;
        this.reciever=reciever;
        this.position=position;
    }

    public String getMessage(){
        return message;
    }
    public void setMessage(String userID){
        this.message = message;
    }


    public String getSender() {
        return sender;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReciever() {
        return reciever;
    }

    public void setReciever(String reciever) {
        this.reciever = reciever;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
