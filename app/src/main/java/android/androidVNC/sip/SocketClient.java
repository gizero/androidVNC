package android.androidVNC.sip;

import java.io.IOException;

/**
 * Created by gizero on 13/10/2017.
 */

public abstract class SocketClient {
    protected String uri;
    protected SocketListener listener;

    public SocketClient(String uri) {
        this.uri = uri;
    }

    public void setSocketListener(SocketListener listener) {
        this.listener = listener;
    }

    public abstract void open() throws IOException;
    public abstract void close();
    public abstract void send(String s);
}
