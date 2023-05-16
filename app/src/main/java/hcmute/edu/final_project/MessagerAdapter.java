package hcmute.edu.final_project;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MessagerAdapter extends RecyclerView.Adapter<MessagerAdapter.MyViewHolder>{

    List<Messager> messagerList;
    public MessagerAdapter(List<Messager> messagerList) {
        this.messagerList = messagerList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View chatView = LayoutInflater.from(parent.getContext()).inflate(R.layout.messeger_layout,null);
        MyViewHolder myViewHolder = new MyViewHolder(chatView);
        return  myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Messager messager = messagerList.get(position);
        if(messager.getSendBy().equals(Messager.SEND_BY_ME)){
            holder.leftLayoutView.setVisibility(View.GONE);
            holder.rightLayoutView.setVisibility(View.VISIBLE);
            holder.rightChatView.setText(messager.getMessager());
        }else{
            holder.rightLayoutView.setVisibility(View.GONE);
            holder.leftLayoutView.setVisibility(View.VISIBLE);
            holder.leftChatView.setText(messager.getMessager());
        }
    }

    @Override
    public int getItemCount() {
        return messagerList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        LinearLayout leftLayoutView , rightLayoutView;

        TextView leftChatView , rightChatView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            leftLayoutView = itemView.findViewById(R.id.left_chat_view);
            leftChatView = itemView.findViewById(R.id.left_chat_text_view);
            rightLayoutView = itemView.findViewById(R.id.right_chat_view);
            rightChatView = itemView.findViewById(R.id.right_chat_text_view);
        }
    }
}
