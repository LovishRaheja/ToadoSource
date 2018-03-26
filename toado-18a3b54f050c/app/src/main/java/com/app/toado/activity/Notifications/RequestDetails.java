package com.app.toado.activity.Notifications;

import com.google.firebase.database.DataSnapshot;

/**
 * Created by Silent Knight on 27-02-2018.
 */

public class RequestDetails {

    String profpicurl,otherusrname,otherusrkey;

    public RequestDetails() {
    }

    public RequestDetails(String profpicurl, String otherusrname,String otherusrkey) {
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

    public static RequestDetails parse(DataSnapshot dataSnapshot) throws NullPointerException {
        RequestDetails usr = new RequestDetails();
        usr.setOtherusrname(dataSnapshot.child("Name").getValue().toString());
        usr.setOtherusrkey(dataSnapshot.getKey().toString());


       // usr.setProfpicurl(dataSnapshot.child("ProfpicUrl").getValue().toString());


        return usr;
    }
}

