package com.app.toado.activity.BlockedUsers;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.toado.R;

/**
 * Created by Silent Knight on 23-05-2018.
 */

public class BlockedViewHolders extends RecyclerView.ViewHolder  {
    public TextView mMatchId, mMatchName;
    public ImageView mMatchImage;
    public BlockedViewHolders(View itemView) {
        super(itemView);

        mMatchId = (TextView) itemView.findViewById(R.id.Matchid);
        mMatchName = (TextView) itemView.findViewById(R.id.MatchName);

        mMatchImage = (ImageView) itemView.findViewById(R.id.MatchImage);


    }
}
