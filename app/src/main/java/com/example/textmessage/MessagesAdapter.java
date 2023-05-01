package com.example.textmessage;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {
    private ArrayList<Message> items;
    private Context context;

    public MessagesAdapter(Context context, ArrayList<Message> items) {
        super();

        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message, parent, false);

        ViewHolder holder = new ViewHolder(v, items.get(viewType));
        return holder;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private Message message;
        private TextView body;
        private CardView textWrapper;
        private TextView status;

        public ViewHolder(@NonNull View itemView, Message message) {
            super(itemView);

            this.message = message;

            textWrapper = itemView.findViewById(R.id.textWrapper);
            body = itemView.findViewById(R.id.body);
            status = itemView.findViewById(R.id.status);

            if (message.sent) {
                status.setVisibility(View.VISIBLE);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.gravity = Gravity.RIGHT;

                textWrapper.setLayoutParams(params);
                body.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                   status.setText("Delivered");
                }, 1000);
            }
            body.setText(message.body);
        }
    }
}
