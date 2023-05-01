package com.example.textmessage;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

// Emulate message receiving using the terminal
// "adb emu sms send <PORT> <MESSAGE>"
// ex. "adb emu sms send 4567 hello"

public class MainActivity extends AppCompatActivity {
    private final TextMessageReceiver textMessageReceiver = new TextMessageReceiver();
    private SmsManager smsManager;

    public RecyclerView messagesRV;
    private Button send;
    private EditText messageInputET;
    private Switch aiSwitch;
    private EditText recipientNumber;

    private MessagesAdapter messagesAdapter;
    public final ArrayList<Message> messages = new ArrayList<>();
    private final ChatGPTAPI chatGPTAPI = new ChatGPTAPI();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityResultLauncher<String[]> smsPermissionRequest =
                    registerForActivityResult(new ActivityResultContracts
                                    .RequestMultiplePermissions(), result -> {
                                Boolean smsSendGranted = result.getOrDefault(
                                android.Manifest.permission.RECEIVE_SMS,false);
                                Boolean smsReceiveGranted = result.getOrDefault(
                                        android.Manifest.permission.RECEIVE_SMS,false);
                                if (smsSendGranted != null && smsReceiveGranted != null && smsSendGranted && smsReceiveGranted) {

                                } else {

                                    finishAndRemoveTask();
                                }
                            }
                    );

            if (ActivityCompat.checkSelfPermission(
                    this, Manifest.permission.RECEIVE_SMS) ==
                    PackageManager.PERMISSION_GRANTED) {
                // You can use the API that requires the permission.

            } else if (shouldShowRequestPermissionRationale(Manifest.permission.RECEIVE_SMS)) {
                // In an educational UI, explain to the user why your app requires this
                // permission for a specific feature to behave as expected, and what
                // features are disabled if it's declined. In this UI, include a
                // "cancel" or "no thanks" button that lets the user continue
                // using your app without granting the permission.
            } else {
                smsPermissionRequest.launch(new String[] {
                        Manifest.permission.SEND_SMS,
                        Manifest.permission.RECEIVE_SMS
                });
            }

        }

        smsManager = SmsManager.getDefault();

        aiSwitch = findViewById(R.id.aiSwitch);
        send = findViewById(R.id.send);
        messagesRV = findViewById(R.id.messages);
        messageInputET = findViewById(R.id.messageInput);
        recipientNumber = findViewById(R.id.recipientNumber);

        messagesRV.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager( this, LinearLayoutManager.VERTICAL, false);
        messagesRV.setLayoutManager(layoutManager);
        messagesAdapter = new MessagesAdapter(this, messages);
        messagesRV.setAdapter(messagesAdapter);

        send.setOnClickListener((View v) -> {
            String message = messageInputET.getText().toString();
            if (message.equals("")) return;

            messages.add(new Message(true, message));
            messagesAdapter.notifyItemInserted(messages.size() - 1);
            messagesRV.scrollToPosition(messages.size() - 1);

            if (aiSwitch.isChecked()) {
                chatGPTAPI.getGPT35Response(message, new AsyncHttpClient.HTTPJSONResponse() {
                    @Override
                    public void onSuccess(JSONObject result) throws JSONException {
                        messages.add(new Message(false, "(ChatGPT) " + result.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content")));
                        messagesAdapter.notifyItemInserted(messages.size() - 1);
                        messagesRV.scrollToPosition(messages.size() - 1);
                    }

                    @Override
                    public void onException(Exception e) {

                    }
                });
            } else {
                String number = recipientNumber.getText().toString();
                sendMessage(number.equals("") ? number : "5556", message);
            }

            messageInputET.setText("");
        });
    }

    private void sendMessage(String phoneNumber, String message) {
        smsManager.sendTextMessage(phoneNumber, null, message, null, null);
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(textMessageReceiver, intentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(textMessageReceiver);
    }
}