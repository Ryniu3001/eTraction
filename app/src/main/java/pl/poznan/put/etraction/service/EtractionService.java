package pl.poznan.put.etraction.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.gson.JsonElement;
import com.hosopy.actioncable.ActionCable;
import com.hosopy.actioncable.ActionCableException;
import com.hosopy.actioncable.Channel;
import com.hosopy.actioncable.Consumer;
import com.hosopy.actioncable.Subscription;

import java.net.URI;
import java.net.URISyntaxException;

import pl.poznan.put.etraction.MainActivity;
import pl.poznan.put.etraction.R;

public class EtractionService extends Service {

    private static final String TAG = EtractionService.class.getSimpleName();

    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();
    private EtractionServiceListener mChatListener;
    private EtractionServiceListener mStatementsListener;
    private Consumer mConsumer;
    private Subscription mChatSubscription;
    private Subscription mStatementsSubscription;

    private enum NotificationType {
        chatNotification(69),
        statementsNotification(666);

        int id;
        private NotificationType(int id){
            this.id = id;
        }

        public int getId(){
            return id;
        }
    }
    private static final int CHAT_NOTIFICATION_ID = 69;
    private static final int STATEMENTS_NOTIFICATION_ID = 666;

    public EtractionService() {
        try {
            initializeWSConnection();
            subscribeStatementsRoom();
            subscribeChatRoom();
        } catch (URISyntaxException e) {
            Log.e(TAG, "ERROR", e);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "BIND TO SERVICE");
        return mBinder;
    }


    private void initializeWSConnection() throws URISyntaxException {
        // 1. Setup
        URI uri = new URI("wss://etraction.herokuapp.com/cable");
        mConsumer = ActionCable.createConsumer(uri);
        mConsumer.connect();
        Log.d(TAG, "WS connected to " + uri.toString());
    }

    private void subscribeChatRoom() {
        // 2. Create subscription
        Channel chatRoomChannel = new Channel("ChatRoomChannel");
        mChatSubscription = mConsumer.getSubscriptions().create(chatRoomChannel);
        mChatSubscription
                .onConnected(new Subscription.ConnectedCallback() {
                    @Override
                    public void call() {
                        Log.d(TAG, "CHAT SUBSCRIBED");
                    }
                }).onRejected(new Subscription.RejectedCallback() {
            @Override
            public void call() {
                Log.d(TAG, "CHAT SUBSCRIBE REJECTED");
            }
        }).onReceived(new Subscription.ReceivedCallback() {
            @Override
            public void call(JsonElement data) {
                Log.d(TAG, "CHAT SUBSCRIBE MESSAGE RECEIVED");
                if (mChatListener != null) {
                    mChatListener.newMessage(data);
                } else {
                    sendNotificationIfNotExists(NotificationType.chatNotification);
                }

            }
        }).onDisconnected(new Subscription.DisconnectedCallback() {
            @Override
            public void call() {
                Log.d(TAG, "CHAT SUBSCRIPTION CLOSED");
            }
        }).onFailed(new Subscription.FailedCallback() {
            @Override
            public void call(ActionCableException e) {
                Log.e(TAG, "CHAT SUBSCRIPTION ERROR", e);
            }
        });
    }

    private void subscribeStatementsRoom() {
        // 2. Create subscription
        Channel appearanceChannel = new Channel("StatementNotificationChannel");
        mStatementsSubscription = mConsumer.getSubscriptions().create(appearanceChannel);
        mStatementsSubscription
                .onConnected(new Subscription.ConnectedCallback() {
                    @Override
                    public void call() {
                        Log.d(TAG, "STATEMENTS SUBSCRIBE CONNECTED");
                    }
                }).onRejected(new Subscription.RejectedCallback() {
            @Override
            public void call() {
                Log.d(TAG, "STATEMENTS SUBSCRIPTION REJECTED");
            }
        }).onReceived(new Subscription.ReceivedCallback() {
            @Override
            public void call(JsonElement data) {
                Log.d(TAG, "STATEMENTS SUBSCRIBE MESSAGE RECEIVED");
                if (mStatementsListener != null) {
                    mStatementsListener.newMessage(data);
                } else {
                    sendNotificationIfNotExists(NotificationType.statementsNotification);
                }
            }
        }).onDisconnected(new Subscription.DisconnectedCallback() {
            @Override
            public void call() {
                Log.d(TAG, "STATEMENTS SUBSCRIPTION CLOSED");
            }
        }).onFailed(new Subscription.FailedCallback() {
            @Override
            public void call(ActionCableException e) {
                Log.e(TAG, "STATEMENTS SUBSCRIPTION ERROR", e);
            }
        });
    }

    private void unsubscribeStatementsRoom() {
        if (mStatementsSubscription != null && mConsumer != null) {
            mStatementsListener = null;
            mConsumer.getSubscriptions().remove(mStatementsSubscription);
            mStatementsSubscription = null;
        }
    }

    private void unsubscribeChatRoom() {
        if (mChatSubscription != null && mConsumer != null) {
            mChatListener = null;
            mConsumer.getSubscriptions().remove(mChatSubscription);
            mChatSubscription = null;
        }
    }

    public void setChatListener(EtractionServiceListener listener) {
        mChatListener = listener;
    }

    public void setStatementsListener(EtractionServiceListener listener) {
        mStatementsListener = listener;
    }

    public void removeChatListener() {
        mChatListener = null;
    }

    public void removeStatementsListener() {
        mStatementsListener = null;
    }


    /**
     * Send notification
     * @param type
     */
    private void sendNotificationIfNotExists(NotificationType type) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = buildNotification(type);
        notification.flags |= Notification.FLAG_ONLY_ALERT_ONCE;
        notificationManager.notify(type.getId(), notification);
    }

    /**
     * Build a notification object ready to send to the Android Notification Service
     * @param type
     * @return
     */
    private Notification buildNotification(NotificationType type) {
        String title;
        String text;
        if (type.equals(NotificationType.chatNotification)){
            title = getResources().getString(R.string.chat_notification_title);
            text = getResources().getString(R.string.chat_notification_text);
        } else {
            title = getResources().getString(R.string.statements_notification_title);
            text = getResources().getString(R.string.statements_notification_text);
        }
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_notif)
                        .setContentTitle(title)
                        .setContentText(text)
                        .setAutoCancel(true);


        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, MainActivity.class);
        if (type.equals(NotificationType.chatNotification)){
            resultIntent.putExtra(getResources().getString(R.string.notification_bundle_frag_key), R.id.nav_chat);
        } else {
            resultIntent.putExtra(getResources().getString(R.string.notification_bundle_frag_key), R.id.nav_statements);
        }

        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent,  PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent);


        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setSound(alarmSound);
        return builder.build();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unsubscribeChatRoom();
        unsubscribeStatementsRoom();
        mConsumer.disconnect();
        Log.d(TAG, "SERVICE CLOSED");
    }

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        public EtractionService getService() {
            // Return this instance of LocalService so clients can call public methods
            return EtractionService.this;
        }
    }

    public interface EtractionServiceListener {
        void newMessage(JsonElement msg);
    }

}
