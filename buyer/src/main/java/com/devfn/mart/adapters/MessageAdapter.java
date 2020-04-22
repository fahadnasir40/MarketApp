package com.devfn.mart.adapters;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.devfn.common.model.ChatMessage;
import com.devfn.common.model.ChatModel;
import com.devfn.mart.R;
import com.devfn.mart.activities.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.ocpsoft.prettytime.PrettyTime;

import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder>{

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    private List<ChatMessage> messageList;
    private Context        mContext;
    public FirebaseUser firebaseUser;

    public MessageAdapter(List<ChatMessage> messageList, Context mContext) {
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

        long previousTs = 0;
        if(position < messageList.size()-1){
            ChatMessage pm = messageList.get(position+1);
            previousTs = Long.parseLong(pm.getTimeStamp());
        }

        setTimeTextVisibility(Long.parseLong(message.getTimeStamp()), previousTs, holder.dateMessage,position);
    }

    String getFormattedNumber(int number){
        NumberFormat myFormat = NumberFormat.getInstance();
        myFormat.setGroupingUsed(true); // this will also round numbers, 3
        return myFormat.format(number);
    }

    @Override
    public int getItemViewType(int position) {

            if(messageList.get(position).getChatId().equals(firebaseUser.getUid())){
                return  MSG_TYPE_RIGHT;
            }
            else{
                return  MSG_TYPE_LEFT;
            }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

        private TextView showMessage,messageTime;
        private TextView dateMessage;


        ViewHolder(@NonNull final View itemView) {
            super(itemView);

           dateMessage = itemView.findViewById(R.id.chat_date);

            showMessage = itemView.findViewById(R.id.chat_show_message);
            messageTime = itemView.findViewById(R.id.chat_time);
            showMessage.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {

            contextMenu.setHeaderTitle("Select the Action");

            contextMenu.add(this.getAdapterPosition(), 101, 0, "Copy");
            contextMenu.add(this.getAdapterPosition(), 102, 1, "Delete");
        }
    }

    private void setTimeTextVisibility(long ts1, long ts2, TextView timeText,int position){

        if(ts2==0){
            timeText.setVisibility(View.VISIBLE);
            SimpleDateFormat formatter = new SimpleDateFormat("d MMM YYYY");
            String date = formatter.format(new Date(ts1));
            timeText.setText(date);
        }else {

            Date a = new Timestamp(ts1);
            Date b = new Timestamp(ts2);

            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

            if(formatter.format(b).compareTo(formatter.format(a)) == 0){
                timeText.setVisibility(View.GONE);
            }

//
//            Calendar cal1 = Calendar.getInstance();
//            Calendar cal2 = Calendar.getInstance();
//            cal1.setTimeInMillis(ts1);
//            cal2.setTimeInMillis(ts2);
//
//            boolean sameMonth = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
//                    cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)  && cal1.get(Calendar.DATE)==cal2.get(Calendar.DATE);
//
//            if(sameMonth){
//                timeText.setVisibility(View.GONE);
//                timeText.setText("");
//            }else {
//                timeText.setVisibility(View.VISIBLE);
//                SimpleDateFormat formatter = new SimpleDateFormat("d MMM YYYY");
//                 String date = formatter.format(new Date(ts2));
//                timeText.setText(date);
//            }
        }
    }

}
