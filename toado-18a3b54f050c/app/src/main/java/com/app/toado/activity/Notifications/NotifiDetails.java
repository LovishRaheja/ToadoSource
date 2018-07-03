package com.app.toado.activity.Notifications;


import com.google.firebase.database.DataSnapshot;

/**
 * Created by Silent Knight on 05-03-2018.
 */

public class NotifiDetails {

    String profpicurl,otherusrname,otherusrkey;

    public NotifiDetails() {
    }

    public NotifiDetails(String profpicurl, String otherusrname,String otherusrkey) {
        this.profpicurl = profpicurl;
        this.otherusrname = otherusrname;
        this.otherusrkey=otherusrkey;
    }

    public String getProfpicurl() {
        return profpicurl;
    }

    public void setProfpicurl(String profpicurl) {
        this.profpicurl = profpicurl;
    }

    public String getOtherusrname() {
        return otherusrname;
    }

    public void setOtherusrname(String otherusrname) {
        this.otherusrname = otherusrname;
    }


    public String getOtherusrkey() {
        return otherusrkey;
    }

    public void setOtherusrkey(String otherusrkey) {
        this.otherusrkey = otherusrkey;
    }

    public static NotifiDetails parse(DataSnapshot dataSnapshot) throws NullPointerException {
        NotifiDetails usr = new NotifiDetails();
        usr.setOtherusrname(dataSnapshot.child("Name").getValue().toString());
        usr.setProfpicurl(dataSnapshot.child("ProfilePicUrl").getValue().toString());



        // usr.setProfpicurl(dataSnapshot.child("ProfpicUrl").getValue().toString());


        return usr;
    }
}
