package pl.poznan.put.etraction.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketFactory;

import java.io.IOException;

import pl.poznan.put.etraction.model.ChatMessageMsg;

public class ChatService extends Service {

    private static final String TAG = ChatService.class.getSimpleName();

    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();
    private ChatServiceListener mListener;
    private WebSocket mWebSocket;


    public ChatService() {
        initializeWSConnection();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


    private void initializeWSConnection(){
        WebSocket ws = null;
        try {
            WebSocketFactory factory = new WebSocketFactory();
            mWebSocket = factory.createSocket("wss://etraction.herokuapp.com/cable", 5000);
            mWebSocket.addListener(new ChatWebSocketListener(this));
        } catch (IOException e) {
            Log.e(TAG, "WS ERROR", e);
        }

        mWebSocket.connectAsynchronously();
    }

    public void sendPong(){
        mWebSocket.sendPong();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mWebSocket.disconnect();
    }

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        public ChatService getService() {
            // Return this instance of LocalService so clients can call public methods
            return ChatService.this;
        }

        public void setListener(ChatServiceListener listener) {
            mListener = listener;
        }
    }

    public interface ChatServiceListener {
        void newMessage(ChatMessageMsg msg);
    }

}
