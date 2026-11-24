package in.gullak.app;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.Telephony;
import android.telephony.SmsMessage;

import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.getcapacitor.PermissionState;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.annotation.Permission;
import com.getcapacitor.annotation.PermissionCallback;

/** Reads bank / UPI SMS from the inbox so the app can offer to log them. Messages never leave the phone. */
@CapacitorPlugin(
    name = "SmsReader",
    permissions = {
        @Permission(alias = "sms", strings = { Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS })
    }
)
public class SmsReaderPlugin extends Plugin {
    private BroadcastReceiver receiver;

    @Override
    public void load() {
        if (getPermissionState("sms") == PermissionState.GRANTED) startListening();
    }

    private boolean granted() {
        return getPermissionState("sms") == PermissionState.GRANTED;
    }

    @PluginMethod
    public void checkPermission(PluginCall call) {
        JSObject r = new JSObject();
        r.put("granted", granted());
        call.resolve(r);
    }

    @PluginMethod
    public void requestPermission(PluginCall call) {
        if (granted()) {
            JSObject r = new JSObject();
            r.put("granted", true);
            call.resolve(r);
            return;
        }
        requestPermissionForAlias("sms", call, "smsPermCallback");
    }

    @PermissionCallback
    private void smsPermCallback(PluginCall call) {
        boolean ok = granted();
        if (ok) startListening();
        JSObject r = new JSObject();
        r.put("granted", ok);
        call.resolve(r);
    }

    @PluginMethod
    public void getMessages(PluginCall call) {
        if (!granted()) {
            call.reject("SMS permission not granted");
            return;
        }
        long since = call.getLong("since", 0L);
        int limit = call.getInt("limit", 300);
        JSArray arr = new JSArray();
        Cursor c = null;
        try {
            Uri uri = Telephony.Sms.Inbox.CONTENT_URI;
            String[] proj = { Telephony.Sms._ID, Telephony.Sms.ADDRESS, Telephony.Sms.BODY, Telephony.Sms.DATE };
            c = getContext().getContentResolver().query(uri, proj, Telephony.Sms.DATE + " > ?",
                    new String[] { String.valueOf(since) }, Telephony.Sms.DATE + " DESC");
            int n = 0;
            if (c != null) {
                while (c.moveToNext() && n < limit) {
                    JSObject m = new JSObject();
                    m.put("id", c.getString(0));
                    m.put("address", c.getString(1));
                    m.put("body", c.getString(2));
                    m.put("date", c.getLong(3));
                    arr.put(m);
                    n++;
                }
            }
            JSObject r = new JSObject();
            r.put("messages", arr);
            call.resolve(r);
        } catch (Exception e) {
            call.reject("Could not read SMS: " + e.getMessage());
        } finally {
            if (c != null) c.close();
        }
    }

    private void startListening() {
        if (receiver != null) return;
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context ctx, Intent intent) {
                try {
                    SmsMessage[] msgs = Telephony.Sms.Intents.getMessagesFromIntent(intent);
                    if (msgs == null || msgs.length == 0) return;
                    StringBuilder body = new StringBuilder();
                    for (SmsMessage m : msgs) body.append(m.getMessageBody());
                    JSObject d = new JSObject();
                    d.put("address", msgs[0].getOriginatingAddress());
                    d.put("body", body.toString());
                    d.put("date", msgs[0].getTimestampMillis());
                    notifyListeners("smsReceived", d);
                } catch (Exception ignored) { }
            }
        };
        IntentFilter f = new IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION);
        if (Build.VERSION.SDK_INT >= 33) {
            getContext().registerReceiver(receiver, f, Context.RECEIVER_EXPORTED);
        } else {
            getContext().registerReceiver(receiver, f);
        }
    }

    @Override
    protected void handleOnDestroy() {
        if (receiver != null) {
            try { getContext().unregisterReceiver(receiver); } catch (Exception ignored) { }
            receiver = null;
        }
    }
}
