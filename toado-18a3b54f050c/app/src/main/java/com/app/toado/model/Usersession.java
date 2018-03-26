package com.app.toado.model;

import com.google.firebase.database.DataSnapshot;

/**
 * Created by RajK on 13-06-2017.
 */

public class Usersession {
    private String lastseen;
    private Boolean online;

    public Usersession() {
    }

    public Usersession(String lastseen, Boolean online) {
        this.lastseen = lastseen;
        this.online = online;
    }

    public String getLastseen() {
        return lastseen;
    }

    public void setLastseen(String lastseen) {
        this.lastseen = lastseen;
    }

    public Boolean getOnline() {
        return online;
    }

    public void setOnline(Boolean online) {
        this.online = online;
    }

    public static Usersession parse(DataSnapshot dataSnapshot) throws NullPointerException {
        Usersession usr = new Usersession();

      usr.setLastseen(dataSnapshot.child("online").getValue().toString());
//        System.out.println("from user parse function"+usr.getName()+usr.getDob()+usr.getUserLikes());
        return usr;
    }
}
