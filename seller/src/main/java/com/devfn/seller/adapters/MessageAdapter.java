package com.devfn.seller.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.devfn.common.model.ChatMessage;
import com.devfn.common.model.ChatModel;
import com.devfn.seller.R;
import com.devfn.seller.activities.ChatSeller;
import com.devfn.seller.activities.Messages;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.ocpsoft.prettytime.PrettyTime;

import java.sql.Timestamp;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder>{

    private List<HashMap<String,Object>> messageList;
    private Context   mContext;

    public MessageAdapter(List<HashMap<String,Object>> messageList, Context mContext) {
        this.messageList = messageList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.message_item,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        final HashMap<String,Object> objectHashMap = messageList.get(position);
        final ChatMessage chatMessage = (ChatMessage)objectHashMap.get("messageObject");

        if(holder.date.getVisibility() == View.VISIBLE && chatMessage == null){
            holder.date.setVisibility(View.GONE);
            holder.message.setText("Chat Now");
            holder.message.setTextColor(mContext.getResources().getColor(R.color.PaleVioletRed));
        }
        else{

            if(chatMessage!=null){
                holder.message.setTextColor(mContext.getResources().getColor(R.color.colorPrimaryDark));
                holder.message.setText(chatMessage.getMessage());
                if(holder.date.getVisibility()==View.GONE)
                    holder.date.setVisibility(View.VISIBLE);
                PrettyTime time = new PrettyTime();
                holder.date.setText(time.format(new Date(Long.parseLong(chatMessage.getTimeStamp()))));

                String seenCounter = String.valueOf(objectHashMap.get("seenCounter"));

                if(!seenCounter.equals("0")){
                    if(holder.seenBadge.getVisibility()==View.GONE)
                        holder.seenBadge.setVisibility(View.VISIBLE);
                    holder.seenBadge.setText(seenCounter);
                }


            }
        }

        final String senderName = (String)objectHashMap.get("senderName");

        holder.senderName.setText(senderName);
        if(senderName!=null && !senderName.equals(""))
         holder.iconText.setText( senderName.substring(0,1).toUpperCase());

        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, ChatSeller.class);

                String userId = (String)objectHashMap.get("senderId");
                intent.putExtra("senderName",senderName);
                intent.putExtra("senderId",userId);
                mContext.startActivity(intent);
            }
        });

    }



    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView senderName,message,iconText;
        private TextView date;
        private TextView seenBadge;
        private RelativeLayout container;

        ViewHolder(@NonNull final View itemView) {
            super(itemView);

            container = itemView.findViewById(R.id.messages_item);
            message = itemView.findViewById(R.id.messages_first_message);
            date = itemView.findViewById(R.id.messages_time);
            seenBadge = itemView.findViewById(R.id.messages_new_badge);
            senderName = itemView.findViewById(R.id.messages_sender_name);
            iconText = itemView.findViewById(R.id.messages_icon_text);
        }
    }
}
