package pl.poznan.put.etraction.service;

/**
 * Created by Marcin on 14.05.2017.
 */

public class ChatWebSocketListener {

    private static final String TAG = ChatWebSocketListener.class.getSimpleName();
    private EtractionService mService;

    public ChatWebSocketListener(EtractionService service){
        this.mService = service;
    }

}
