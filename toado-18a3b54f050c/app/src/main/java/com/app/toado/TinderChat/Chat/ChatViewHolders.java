package com.app.toado.TinderChat.Chat;

import android.media.Image;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.toado.R;

import org.w3c.dom.Text;


/**
 * Created by Silent Knight on 06-01-2018.
 */

public class ChatViewHolders extends RecyclerView.ViewHolder {
    public TextView mMessage,textTime;
    public LinearLayout mContainer;
    ImageView tick,docTick,imgTick,videoTick,locTick,songTick,conTick;

    ImageView docImage;
    TextView docText;
    LinearLayout docLinear,docLayout;
    TextView docTime;

    ImageView imgchat;
    TextView caption,imgTime;
    LinearLayout imgLinear,imgLayout;

    RelativeLayout videoLayout;
    RelativeLayout videoLinear;
    ImageView videoChat;
    TextView videoCaption,videoTime;
    ImageView vThumb,vDownload;
    ProgressBar vProgress;

    LinearLayout locLayout;
    ImageView chatMap;
    TextView locTime;
    LinearLayout locLinear;

    LinearLayout songLayout,songLinear;
    ImageView songImage,songStatus;
    TextView songName;
    RelativeLayout songProgress;
    TextView songPer;


    LinearLayout conLayout,conLinear;
    ImageView conImage;
    TextView conName,conNumber,conTime;

    ImageView showonline;

    TextView date;

    public ChatViewHolders(View itemView) {
        super(itemView);

        mMessage = itemView.findViewById(R.id.message);
        mContainer = itemView.findViewById(R.id.container);
        textTime=(TextView)itemView.findViewById(R.id.textTime);


        docImage=(ImageView)itemView.findViewById(R.id.docImage);
        docText=(TextView)itemView.findViewById(R.id.docName);
        docLinear=(LinearLayout)itemView.findViewById(R.id.docLinear);
        docLayout=(LinearLayout)itemView.findViewById(R.id.docLayout);
        docTime=(TextView)itemView.findViewById(R.id.docTime);

        imgchat=(ImageView)itemView.findViewById(R.id.imgchat);
        caption=(TextView)itemView.findViewById(R.id.caption);
        imgLinear=(LinearLayout)itemView.findViewById(R.id.imgLinear);
        imgLayout=(LinearLayout)itemView.findViewById(R.id.imgLayout);
        imgTime=(TextView)itemView.findViewById(R.id.imgTime);


        videoLayout=(RelativeLayout)itemView.findViewById(R.id.videoLayout);
        videoLinear=(RelativeLayout)itemView.findViewById(R.id.videoLinear);
        videoChat=(ImageView)itemView.findViewById(R.id.videochat);
        videoCaption=(TextView)itemView.findViewById(R.id.videocaption);
        vThumb=(ImageView)itemView.findViewById(R.id.vThumb);
        vDownload=(ImageView)itemView.findViewById(R.id.vDownload);
        vProgress=(ProgressBar)itemView.findViewById(R.id.videoProgress);
        videoTime=(TextView)itemView.findViewById(R.id.videoTime);

        locLayout=(LinearLayout)itemView.findViewById(R.id.locLayout);
        chatMap=(ImageView)itemView.findViewById(R.id.chatMap);
        locTime=(TextView)itemView.findViewById(R.id.locTime);
        locLinear=(LinearLayout)itemView.findViewById(R.id.locLinear);


        songLayout=(LinearLayout)itemView.findViewById(R.id.songLayout);
        songLinear=(LinearLayout)itemView.findViewById(R.id.songLinear);
        songImage=(ImageView)itemView.findViewById(R.id.songImage);
        songName=(TextView)itemView.findViewById(R.id.songName);
        songStatus=(ImageView)itemView.findViewById(R.id.songStatus);
        songProgress=(RelativeLayout)itemView.findViewById(R.id.songProgress);


        conLayout=(LinearLayout)itemView.findViewById(R.id.conLayout);
        conLinear=(LinearLayout)itemView.findViewById(R.id.conLinear);
        conImage=(ImageView)itemView.findViewById(R.id.conImage);
        conName=(TextView)itemView.findViewById(R.id.conName);
        conNumber=(TextView)itemView.findViewById(R.id.conNumber);
        conTime=(TextView)itemView.findViewById(R.id.conTime);


        tick=(ImageView)itemView.findViewById(R.id.tick);
        docTick=(ImageView)itemView.findViewById(R.id.docTick);
        imgTick=(ImageView)itemView.findViewById(R.id.imgTick);
        videoTick=(ImageView)itemView.findViewById(R.id.videoTick);
        locTick=(ImageView)itemView.findViewById(R.id.locTick);
        songTick=(ImageView)itemView.findViewById(R.id.songTick);
        songPer=(TextView)itemView.findViewById(R.id.songper);
        conTick=(ImageView)itemView.findViewById(R.id.conTick);



        showonline=(ImageView)itemView.findViewById(R.id.showOnline);

        date=(TextView)itemView.findViewById(R.id.date);
    }

}