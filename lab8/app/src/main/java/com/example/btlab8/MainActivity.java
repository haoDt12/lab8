package com.example.btlab8;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MainActivity extends AppCompatActivity {
    private EditText inputMessage;
    private TextView chatTextView;
    private LinearLayout messageLayout;
    private TextView welcomeMessage;

    private ImageButton sendButton;
    private String username;
    private Socket mSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inputMessage = (EditText) findViewById(R.id.inputMessage);
        chatTextView = (TextView) findViewById(R.id.chatTextView);
        messageLayout = (LinearLayout) findViewById(R.id.messageLayout);
        sendButton = (ImageButton) findViewById(R.id.sendButton);
        welcomeMessage = (TextView) findViewById(R.id.welcomeMessage);


        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        try {
            mSocket = IO.socket("http://192.168.1.4:3000");
            mSocket.emit("join",username);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        mSocket.emit("new message");
        mSocket.on("chat message", onNewMessage);
        Welcome("Chào mừng đến với phòng chat !", "");
        mSocket.connect();
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptSend();
            }
        });
    }
    private void attemptSend() {
        String message = inputMessage.getText().toString().trim();
        if (TextUtils.isEmpty(message)) {
            return;
        }
        inputMessage.setText("");

        // Tạo một đối tượng JSONObject chứa thông tin người dùng và nội dung tin nhắn
        JSONObject messageObject = new JSONObject();
        try {
            messageObject.put("username", username);
            messageObject.put("message", message);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Gửi tin nhắn kèm thông tin người dùng đến server
        mSocket.emit("chat message", messageObject);
    }
    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Object data = args[0];
                    if (data instanceof String) {
                        // Nếu dữ liệu là chuỗi thông báo "Welcome"
                        String welcomeMessage = (String) data;
                        Welcome("", welcomeMessage);
                    } else if (data instanceof JSONObject) {
                        // Nếu dữ liệu là đối tượng JSON
                        JSONObject jsonData = (JSONObject) data;
                        String username;
                        String message;
                        try {
                            username = jsonData.getString("username");
                            message = jsonData.getString("message");
                            addMessage(username, message);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    };
    @Override
    public void onDestroy() {
        super.onDestroy();

        mSocket.disconnect();
        mSocket.off("new message", onNewMessage);
    }
    private void Welcome(String username, String message) {
        welcomeMessage.append(username + " " + message + "\n");
    }
    private void addMessage(String username, String message) {
        //chatTextView.append(username + " : " + message + "\n");
        int userColor = getColorForUser(username);
        String messageWithColor = "<font color='" + ContextCompat.getColor(this, userColor) + "'>" + username + "</font>: " + message + "<br>";
        chatTextView.append(Html.fromHtml(messageWithColor));
    }
    private int getColorForUser(String username) {
        // Định nghĩa danh sách các màu sắc có thể chọn
        int[] colors = {R.color.colorUser1, R.color.colorUser2, R.color.colorUser3};

        // Xác định màu sắc dựa vào tên người dùng
        int colorIndex = Math.abs(username.hashCode()) % colors.length;
        return colors[colorIndex];
    }
}