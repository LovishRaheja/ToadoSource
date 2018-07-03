package com.app.toado.TinderChat.ChatFragment;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.toado.R;
import com.app.toado.TinderChat.Chat.ChatActivity;

import org.w3c.dom.Text;

/**
 * Created by Silent Knight on 08-05-2018.
 */

public class ChatFragmentViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener{
    public TextView mMatchId, mMatchName,recentMsg,timestamp;
    public ImageView mMatchImage,showOnline;
    public ChatFragmentViewHolders(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);

        mMatchId = (TextView) itemView.findViewById(R.id.Matchid);
        mMatchName = (TextView) itemView.findViewById(R.id.MatchName);

        mMatchImage = (ImageView) itemView.findViewById(R.id.MatchImage);
        recentMsg=(TextView)itemView.findViewById(R.id.recentMsg);
        showOnline=(ImageView)itemView.findViewById(R.id.showOnline);
        timestamp=(TextView)itemView.findViewById(R.id.timestamp);

    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(view.getContext(), ChatActivity.class);
        Bundle b = new Bundle();
        b.putString("matchId", mMatchId.getText().toString());
        b.putString("name",mMatchName.getText().toString());

        intent.putExtras(b);
        view.getContext().startActivity(intent);
    }
}