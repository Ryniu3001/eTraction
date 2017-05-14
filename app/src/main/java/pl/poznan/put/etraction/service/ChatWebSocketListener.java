package pl.poznan.put.etraction.service;

import android.util.Log;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFrame;

/**
 * Created by Marcin on 14.05.2017.
 */

public class ChatWebSocketListener extends WebSocketAdapter {

    private static final String TAG = ChatWebSocketListener.class.getSimpleName();
    private ChatService mService;

    public ChatWebSocketListener(ChatService service){
        this.mService = service;
    }

    @Override
    public void onTextMessage(WebSocket websocket, String text) throws Exception {
        Log.d(TAG, "Nowa wiadomosc:\n" + text);
        mService.sendPong();
    }

    @Override
    public void handleCallbackError(WebSocket websocket, Throwable cause) throws Exception {
        Log.e(TAG, "WS CALLBACK ERROR", cause);
    }

    @Override
    public void onConnectError(WebSocket websocket, WebSocketException exception) throws Exception {
        Log.e(TAG, "ASYNC CONNECT ERROR", exception);
    }

    @Override
    public void onPingFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
        Log.d(TAG, "PING FRAME RECEIVED:\n" + frame);
        mService.sendPong();
    }
}
