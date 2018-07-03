package com.app.toado.fragments.groupchat;

/**
 * Created by Silent Knight on 18-06-2018.
 */

public class GroupObject {

    String groupId,name,image;

    public GroupObject(String groupId,String name,String image) {
        this.groupId=groupId;
        this.name = name;
        this.image=image;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
