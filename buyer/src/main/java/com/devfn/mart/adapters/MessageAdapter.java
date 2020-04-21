package com.devfn.mart.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.devfn.common.model.ChatMessage;
import com.devfn.common.model.ChatModel;
import com.devfn.mart.R;
import com.devfn.mart.activities.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.NumberFormat;
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
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {


        holder.showMessage.setText(messageList.get(position).getMessage());
        //        holder.showDetails.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Bundle bundle = new Bundle();
//                bundle.putSerializable("order_object_details", messageList.get(position));
//
//                Intent intent = new Intent(mContext, OrderDetails.class);
//                intent.putExtras(bundle);
//                mContext.startActivity(intent);
//            }
//        });

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
        else
            return  MSG_TYPE_LEFT;

    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView showMessage;


        ViewHolder(@NonNull final View itemView) {
            super(itemView);

            showMessage = itemView.findViewById(R.id.chat_show_message);

        }
    }


}
