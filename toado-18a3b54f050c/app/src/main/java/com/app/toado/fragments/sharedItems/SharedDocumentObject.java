package com.app.toado.fragments.sharedItems;

/**
 * Created by Silent Knight on 06-06-2018.
 */

public class SharedDocumentObject {

    private String message;
    private Boolean currentUser;
    String timestamp;
    public SharedDocumentObject(String message, String timestamp,Boolean currentUser){
        this.message = message;
        this.currentUser = currentUser;
        this.timestamp=timestamp;
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
