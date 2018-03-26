package com.app.toado.FirebaseChat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.toado.R;
import com.app.toado.activity.chat.ChatActivity;
import com.app.toado.settings.UserSession;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class FirebaseChat extends AppCompatActivity {

    public static final int VIEW_TYPE_USER_MESSAGE = 0;
    public static final int VIEW_TYPE_FRIEND_MESSAGE = 1;
    UserSession usrsess;
    String mykey = "nokey";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_chat);

        usrsess = new UserSession(this);
        mykey = usrsess.getUserKey();
    }

}

    class ListMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private Context context;
        private Consersation consersation;
        private HashMap<String, Bitmap> bitmapAvata;
        private HashMap<String, DatabaseReference> bitmapAvataDB;
        private Bitmap bitmapAvataUser;

        public ListMessageAdapter(Context context, Consersation consersation, HashMap<String, Bitmap> bitmapAvata, Bitmap bitmapAvataUser) {
            this.context = context;
            this.consersation = consersation;
            this.bitmapAvata = bitmapAvata;
            this.bitmapAvataUser = bitmapAvataUser;
            bitmapAvataDB = new HashMap<>();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == FirebaseChat.VIEW_TYPE_FRIEND_MESSAGE) {
                View view = LayoutInflater.from(context).inflate(R.layout.rc_item_message_friend, parent, false);
                return new ItemMessageFriendHolder(view);
            } else if (viewType == FirebaseChat.VIEW_TYPE_USER_MESSAGE) {
                View view = LayoutInflater.from(context).inflate(R.layout.rc_item_message_user, parent, false);
                return new ItemMessageUserHolder(view);
            }

            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            if (holder instanceof ItemMessageFriendHolder) {
                ((ItemMessageFriendHolder) holder).txtContent.setText(consersation.getListMessageData().get(position).text);

            } else if (holder instanceof ItemMessageUserHolder) {
                ((ItemMessageUserHolder) holder).txtContent.setText(consersation.getListMessageData().get(position).text);

            }

        }

        @Override
        public int getItemViewType(int position) {
            return consersation.getListMessageData().get(position).idSender.equals(StaticConfig.UID) ? FirebaseChat.VIEW_TYPE_USER_MESSAGE : FirebaseChat.VIEW_TYPE_FRIEND_MESSAGE;

        }

        @Override
        public int getItemCount() {
            return consersation.getListMessageData().size();

        }
    }



class ItemMessageUserHolder extends RecyclerView.ViewHolder {
    public TextView txtContent;


    public ItemMessageUserHolder(View itemView) {
        super(itemView);
        txtContent = (TextView) itemView.findViewById(R.id.textContentUser);

    }
}

class ItemMessageFriendHolder extends RecyclerView.ViewHolder {
    public TextView txtContent;
    public CircleImageView avata;

    public ItemMessageFriendHolder(View itemView) {
        super(itemView);
        txtContent = (TextView) itemView.findViewById(R.id.textContentFriend);

    }
}