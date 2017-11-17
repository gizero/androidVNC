package android.androidVNC.sip;

import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by gizero on 13/10/2017.
 */

public class SipWebsocketClient extends SocketClient {
    private final static String TAG = "SipWebsocketClient";
    private WebSocketClient cc = null;
    private URI serverURI;

    public SipWebsocketClient(String uri) {
        super(uri);
        try {
            serverURI = new URI(uri);
        } catch (URISyntaxException e) {
            //log
            e.printStackTrace();
        }
    }

    @Override
    public void open() throws IOException {
        if (serverURI == null) throw new IOException("Invalid server URI: " + uri);

        cc = new WebSocketClient(serverURI) {

            @Override
            public void onMessage(String message) {
                Log.v(TAG, "onMessage received " + message);

                JSONObject mc = null;
                try {
                    mc = new JSONObject(message);
                } catch (JSONException e) {
                    Log.e(TAG, "JSONException" + e.getMessage());
                    return;
                }

                try {
                    String type = mc.getString("type");

                    if (type.equals("EDITMODE")) {
                        String it = null;

                        // useless try
                        try {
                            it = mc.getString("text");
                        } catch (JSONException e) {
                            Log.e(TAG, "JSONException " + e.getMessage());
                        }

                        if (listener != null) {
                            listener.onOpenDialog(it);
                        }

                    }
                } catch (JSONException e) {
                    Log.e(TAG, "JSONException" + e.getMessage());
                }
            }

            @Override
            public void onOpen(ServerHandshake handshake) {
                Log.v(TAG, "onOpen ws://");
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                Log.v(TAG, "onClose ws://");
                throw new RuntimeException("SIP connection terminated");
            }

            @Override
            public void onError(Exception ex) {
                Log.v(TAG, "onError ws://");
                throw new RuntimeException("SIP connection terminated");
            }
        };

        cc.connect();

    }

    @Override
    public void close() {
        if (cc != null) cc.close();
    }

    @Override
    public void send(String s) {
        cc.send(s);
    }
}
