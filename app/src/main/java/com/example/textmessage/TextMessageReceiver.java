package com.example.textmessage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.telephony.SmsMessage;
import android.util.Log;

public class TextMessageReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context ctx, Intent intent) {
        SmsMessage[] msgs;
        Bundle params = intent.getExtras();

        Object[] pdus = (Object[]) params.get("pdus");
        String format = params.getString("format");

        msgs = new SmsMessage[pdus.length];

        for (int i = 0; i < msgs.length; i++) {
            msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i], format);
        }

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            for (SmsMessage msg : msgs) {
                String body = "(" + msg.getOriginatingAddress() + ") " + msg.getMessageBody();
                ((MainActivity) ctx).messages.add(new Message(false, body));
                ((MainActivity) ctx).messagesRV.getAdapter().notifyItemInserted( ((MainActivity) ctx).messages.size() - 1);
                ((MainActivity) ctx).messagesRV.scrollToPosition(((MainActivity) ctx).messages.size() - 1);
            }
        }, 1000);
    }
}
