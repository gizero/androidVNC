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
import android.androidVNC.R;
import android.androidVNC.VncCanvasActivity;
import android.app.Dialog;
import android.content.Context;
import android.os.IBinder;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;

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
    private InputMethodManager inputMethodManager;

    private FrameLayout mView;

    private EditText mEditTextHidden;

    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            Log.d("TEST", "onTextChanged: " + s.toString());
            sc.send(s.toString());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };


    public SipClient(Context context, Handler handler) {
        _context = context;
        mhandler = handler;

        VncCanvasActivity act = (VncCanvasActivity) _context;

        mView = (FrameLayout) act.findViewById(R.id.fl_main);
        mhandler.post(new Runnable() {
            @Override
            public void run() {
                attachHiddenEditText();
            }
        });
    }

    private void attachHiddenEditText() {
        mEditTextHidden = new EditText(_context);
        mEditTextHidden.setLines(1);
        mEditTextHidden.setSingleLine(true);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(0,0);
        mView.addView(mEditTextHidden, mView.getChildCount(), params);

        inputMethodManager =
                (InputMethodManager) _context.getSystemService(Context.INPUT_METHOD_SERVICE);

    }

    private void openKeyboard(String prevText) {
        mEditTextHidden.setFocusable(true);
        mEditTextHidden.setFocusableInTouchMode(true);
        mEditTextHidden.requestFocus();
        mEditTextHidden.setText(prevText);
        mEditTextHidden.addTextChangedListener(mTextWatcher);
        mEditTextHidden.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                Log.d("Test", "onKey -> Event Action: " + event.getAction() + " keyCode: " + keyCode );
                if(event.getAction() == KeyEvent.ACTION_UP) {
                    if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_ENTER) {
                        closeKeyboard();
                        return true;
                    }
                }
                return false;
            }
        });
        inputMethodManager.showSoftInput(mView, InputMethodManager.SHOW_FORCED);
        IBinder token = mView.getApplicationWindowToken();
        inputMethodManager.showSoftInputFromInputMethod(token,
                InputMethodManager.SHOW_FORCED);
        inputMethodManager.toggleSoftInputFromWindow(token,
                InputMethodManager.SHOW_FORCED, 0);
        setEditing(true);

    }

    private void closeKeyboard() {
        setEditing(false);
        mEditTextHidden.setFocusable(false);
        mEditTextHidden.setFocusableInTouchMode(false);
        mEditTextHidden.clearFocus();
        mEditTextHidden.removeTextChangedListener(mTextWatcher);
        mEditTextHidden.setOnKeyListener(null);
        mEditTextHidden.setText("");
        inputMethodManager.hideSoftInputFromWindow(mView.getWindowToken(), 0);
    }

    public void init(String serverURI) throws IOException {
        if (SOCKET_CLIENT_TYPE == WEB_SOCKET_CLIENT) {
            sc = new SipWebsocketClient(serverURI);
            sc.setSocketListener(this);
            sc.open();
        }
    }

    public void close() {
        if (sc != null) sc.close();
    }

    public void setEditing(boolean value) {
        editing = value;
    }

    @Override
    public void onOpenDialog(String s) {
            mhandler.post(new SipClient.editRunnable(_context, s));
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
            openKeyboard(_it);
        }

            /*
            Dialog d = new EnterTextSimpleDialog(_context, _it, new EnterTextSimpleDialog.EnterTextSimpleDialogListener() {
                @Override
                public void onSendText(String text) {
                    Log.d(TAG, "sending...." + text);
                    sc.send(text);
                }

                @Override
                public void onDismiss() {
                    setEditing(false);
                }
            });
            d.setCancelable(false);
            d.show();
        }
        */
    }
}