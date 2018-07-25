package com.three_eung.saemoi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsMessage;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.three_eung.saemoi.infos.SMSInfo;

import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SMSReceiver extends BroadcastReceiver {
    private static final String TAG = SMSReceiver.class.getSimpleName();

    private String sender;
    private StringBuilder sms;
    private Pattern msgPattern = Pattern.compile("[[a-zA-Z0-9가-힣\\n\\s]&&[^\\[\\]]]");
    private Pattern valuePattern = Pattern.compile("\\d{0,3},\\d{3}원");
    private Pattern datePattern = Pattern.compile("\\d{2}/\\d{2} \\d{2}:\\d{2}");

    DatabaseReference mRef;

    @Override
    public IBinder peekService(Context myContext, Intent service) {
        return super.peekService(myContext, service);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "SMS Received");

        if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();

            if(mUser != null) {
                mRef = FirebaseDatabase.getInstance().getReference("users").child(mUser.getUid()).child("sms");
                sms = new StringBuilder();
                Bundle data = intent.getExtras();
                if (data != null) {
                    Object[] pdusObj = (Object[]) data.get("pdus");
                    SmsMessage[] messages = new SmsMessage[pdusObj.length];

                    for (int i = 0; i < pdusObj.length; i++) {
                        messages[i] = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    }

                    sender = messages[0].getOriginatingAddress();

                    for (SmsMessage smsMessage : messages) {
                        sms.append(smsMessage.getMessageBody());
                    }

                    sms.toString();

                    insertValue();
                }
            }
        }
    }

    private void insertValue() {
        Matcher msgMatcher = null;
        Matcher valueMatcher = valuePattern.matcher(sms);
        Matcher dateMatcher = datePattern.matcher(sms);

        StringTokenizer tokenizer = new StringTokenizer(sms.toString(), "\\n");

        while (tokenizer.hasMoreTokens()) {
            String tmp = tokenizer.nextToken();
            Log.e(TAG, "insertValue: " + tmp);
            msgMatcher = msgPattern.matcher(tmp);
            if(msgMatcher.matches()) {
                break;
            }
        }

        if(msgMatcher.find() && valueMatcher.find() && dateMatcher.find()) {
            SMSInfo smsInfo = new SMSInfo(msgMatcher.group(0), valueMatcher.group(0), dateMatcher.group(0));

            mRef.push().setValue(smsInfo);
        }
    }
}
