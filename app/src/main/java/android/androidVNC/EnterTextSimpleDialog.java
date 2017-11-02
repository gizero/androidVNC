package android.androidVNC;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.io.IOException;

class EnterTextSimpleDialog extends Dialog {
    private EditText _textEnterText;
    private String text = null;
    private EnterTextSimpleDialogListener mEnterTextSimpleDialogListener;

    private VncCanvasActivity _canvasActivity;

    EnterTextSimpleDialog(Context context, String initial_text, EnterTextSimpleDialogListener enterTextSimpleDialogListener){
        super(context);
        setOwnerActivity((Activity)context);
        _canvasActivity = (VncCanvasActivity)context;
        text = initial_text;
        mEnterTextSimpleDialogListener = enterTextSimpleDialogListener;
    }

    EnterTextSimpleDialog(Context context, String initial_text) {
        this(context, initial_text, null);
    }

    private void sendText(String s)
    {
        if (mEnterTextSimpleDialogListener != null) {
            mEnterTextSimpleDialogListener.onSendText(s);
        }

        RfbProto rfb = _canvasActivity.vncCanvas.rfb;
        int l = s.length();
        for (int i = 0; i<l; i++)
        {
            char c = s.charAt(i);
            int meta = 0;
            int keysym = c;
            if (Character.isISOControl(c))
            {
                if (c=='\n')
                    keysym = MetaKeyBean.keysByKeyCode.get(KeyEvent.KEYCODE_ENTER).keySym;
                else
                    continue;
            }
            try
            {
                rfb.writeKeyEvent(keysym, meta, true);
                rfb.writeKeyEvent(keysym, meta, false);
            }
            catch (IOException ioe)
            {
                // TODO: log this
            }
        }
    }

    /* (non-Javadoc)
 * @see android.app.Dialog#onCreate(android.os.Bundle)
 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entertextsimple);
        setTitle(R.string.enter_text_title);
        _textEnterText = (EditText)findViewById(R.id.textEnterText);
        _textEnterText.setText(text);

        (findViewById(R.id.buttonSendText)).setOnClickListener(new View.OnClickListener() {

            /* (non-Javadoc)
             * @see android.view.View.OnClickListener#onClick(android.view.View)
             */
            @Override
            public void onClick(View v) {
                sendText(_textEnterText.getText().toString());
                _textEnterText.setText("");
                dismiss();
                if (mEnterTextSimpleDialogListener != null) {
                    mEnterTextSimpleDialogListener.onDismiss();
                }
            }
        });
    }

    /* (non-Javadoc)
     * @see android.app.Dialog#onStart()
     */
    @Override
    protected void onStart() {
        super.onStart();
        _textEnterText.requestFocus();
        InputMethodManager imm = (InputMethodManager) _canvasActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    public interface EnterTextSimpleDialogListener {
        void onSendText(String text);
        void onDismiss();
    }

}
