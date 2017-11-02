//
//  Copyright (C) 2017 Andrea Galbusera <gizero@gmail.com>  All Rights Reserved.
//
//  This is free software; you can redistribute it and/or modify
//  it under the terms of the GNU General Public License as published by
//  the Free Software Foundation; either version 2 of the License, or
//  (at your option) any later version.
//
//  This software is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU General Public License for more details.
//
//  You should have received a copy of the GNU General Public License
//  along with this software; if not, write to the Free Software
//  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307,
//  USA.
//

//
// SipProto.java
//

package android.androidVNC.sip;

//import java.io.*;
//- import java.awt.*;
//- import java.awt.event.*;
//import java.net.Socket;
//- import java.util.zip.*;
//import android.util.Log;
//import android.androidVNC.DH;

import android.androidVNC.EnterTextSimpleDialog;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.os.Handler;

import java.io.IOException;
import java.net.URI;

/**
 * Access the RFB protocol through a socket.
 * <p>
 * This class ...
 */
public class SipClient implements SocketListener {
    private final static String TAG = "SipClient";
    private final static int WEB_SOCKET_CLIENT = 0; // websocket
    private final static int RAW_SOCKET_CLIENT = 1; // rawsocket

    private final static int SOCKET_CLIENT_TYPE = WEB_SOCKET_CLIENT;

    private boolean editing = false;
    private Context _context;

    private Handler mhandler;

    private SocketClient sc;

    public SipClient(Context context, Handler handler) {
        _context = context;
        mhandler = handler;
    }

    public void init(String serverURI) throws IOException {
        if (SOCKET_CLIENT_TYPE == WEB_SOCKET_CLIENT) {
            sc = new SipWebsocketClient(serverURI);
            sc.setSocketListener(this);
            sc.open();
        }
    }

    public void setEditing(boolean value) {
        editing = value;
    }

    @Override
    public void onOpenDialog(String s) {
        if (!editing) {
            setEditing(true);

            mhandler.post(new SipClient.editRunnable(_context, s));
        }
    }

    class editRunnable implements Runnable
    {
        private Context _context;
        private String _it;

        public editRunnable(Context context, String it) {
            _context = context;
            _it = it;
        }

        public void run() {
            Dialog d = new EnterTextSimpleDialog(_context, _it, new EnterTextSimpleDialog.EnterTextSimpleDialogListener() {
                @Override
                public void onSendText(String text) {
                    Log.d(TAG, "sending...." + text);
                }

                @Override
                public void onDismiss() {
                    setEditing(false);
                }
            });
            d.setCancelable(false);
            d.show();
        }
    }
}