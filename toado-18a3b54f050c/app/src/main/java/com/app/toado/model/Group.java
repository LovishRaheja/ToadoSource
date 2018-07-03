package com.app.toado.model;

import com.google.firebase.database.DataSnapshot;

import java.util.List;

import io.realm.annotations.PrimaryKey;

/**
 * Created by Silent Knight on 29-03-2018.
 */

public class Group {
    private String groupName,memberName,key;


    public Group() {
    }


    public Group( String groupName) {

        this.groupName = groupName;
    }



    public Group(String key,String groupName, String memberName) {
        groupName = groupName;
        memberName = memberName;
        key=key;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }


    public static Group parse(DataSnapshot dataSnapshot) throws NullPointerException {
        Group grp = new Group();

        grp.setGroupName(dataSnapshot.child("groupInfo").child("name").getValue().toString());


//        System.out.println("from user parse function"+usr.getName()+usr.getDob()+usr.getUserLikes());
        return grp;
    }
}
