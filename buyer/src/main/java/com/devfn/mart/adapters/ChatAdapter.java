package com.devfn.mart.adapters;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.devfn.common.model.ChatMessage;
import com.devfn.mart.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder>{

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    private List<ChatMessage> messageList;
    private Context   mContext;
    public FirebaseUser firebaseUser;

    public ChatAdapter(List<ChatMessage> messageList, Context mContext) {
        this.messageList = messageList;
        this.mContext     = mContext;
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == MSG_TYPE_LEFT){
            View v = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left,parent,false);
            return new ViewHolder(v);
        }
        else {
            View v = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right,parent,false);
            return new ViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        ChatMessage message = messageList.get(position);
        holder.showMessage.setText(message.getMessage());
        holder.messageTime.setText(message.getDateFromTimeStamp("h:mm a"));

        if(holder.getItemViewType() == 1){
            if(message.getIsSeen()){
                holder.deliveryStatus.setText("R");
            }
            else if(message.getStatus().equals("3")){
                holder.deliveryStatus.setText("D");
            }
            else{
                holder.deliveryStatus.setText("N");
            }
        }

        long previousTs = 0;
        boolean side,change = false;

        side = holder.getItemViewType() == 1;

        if(position < messageList.size() - 1){
            ChatMessage pm = messageList.get(position+1);
            previousTs = Long.parseLong(pm.getTimeStamp());
            if(side != checkType(position+1))
                change = true;
        }

        setTimeTextVisibility(Long.parseLong(message.getTimeStamp()), previousTs, holder.dateMessage,change);

    }

    private void setTimeTextVisibility(long ts1, long ts2, TextView timeText,boolean change){


        Date a = new Timestamp(ts1);
        Date b = new Timestamp(ts2);
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

        timeText.setVisibility(View.GONE);
        if(change){
            timeText.setPadding(0,0,0,0);
            timeText.setTextSize(0);
            timeText.setVisibility(View.INVISIBLE);

        }

            if(formatter.format(b).compareTo(formatter.format(a)) < 0){
                timeText.setVisibility(View.VISIBLE);
                SimpleDateFormat formatter2 = new SimpleDateFormat("d MMM YYYY", Locale.US);
                timeText.setText(formatter2.format(new Date(ts1)));
            }

    }


    String getFormattedNumber(int number){
        NumberFormat myFormat = NumberFormat.getInstance();
        myFormat.setGroupingUsed(true); // this will also round numbers, 3
        return myFormat.format(number);
    }

    @Override
    public int getItemViewType(int position) {

        if(checkType(position))
            return  MSG_TYPE_RIGHT;
        else
            return  MSG_TYPE_LEFT;
    }

    boolean checkType(int position){
        if(messageList.get(position).getChatId().equals(firebaseUser.getUid()))
            return true;
        return false;
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

        private TextView showMessage,messageTime;
        private TextView dateMessage;
        private TextView deliveryStatus;
        private ConstraintLayout container;


        ViewHolder(@NonNull final View itemView) {
            super(itemView);


            deliveryStatus = itemView.findViewById(R.id.chat_delivery_status);
            dateMessage = itemView.findViewById(R.id.chat_date);
            showMessage = itemView.findViewById(R.id.chat_show_message);
            messageTime = itemView.findViewById(R.id.chat_time);
            container = itemView.findViewById(R.id.chat_container);

            container.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            contextMenu.setHeaderTitle("Select the Action");
            contextMenu.add(this.getAdapterPosition(), 101, 0, "Copy");
            contextMenu.add(this.getAdapterPosition(), 102, 1, "Delete");
        }
    }




    private void setTextWithStyling(TextView timeText,String text){


//        android:fontFamily="@font/roboto"
//        android:background="@drawable/bg_chat_date"
//        android:layout_centerHorizontal="true"
//        android:paddingStart="16dp"
//        android:paddingEnd="16dp"
//        android:paddingTop="8dp"
//        android:paddingBottom="8dp"
//        android:alpha="0.7"
//        android:layout_margin="8dp"
//
        timeText.setVisibility(View.VISIBLE);
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) timeText.getLayoutParams();
        lp.setMargins(8,0,8,8);
        timeText.setPadding(16,8,16,8);
        timeText.setBackground(mContext.getDrawable(R.drawable.bg_chat_date));
        timeText.setText("2123");
    }
}
