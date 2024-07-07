package com.example.callstopspam;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.lang.reflect.Method;

public class IncomingCallReceiver extends BroadcastReceiver {

    private static final String PREF_NAME = "FlightModePrefs";
    private static final String RECEIVER_ENABLED_KEY = "receiverEnabled";


    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {

//        Log.d("intent_0", "intent: 0" + intent.getAction());

        SharedPreferences sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        boolean receiverEnabled = sharedPref.getBoolean(RECEIVER_ENABLED_KEY, false);

//        Log.d("receiverEnabled", "receiverEnabled: " + receiverEnabled);

        if (receiverEnabled) {
            try {
                String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
                String number = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);

                if (state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_RINGING)) {

//                    Log.d("number", "number: " + number);
//                // Удаляем все символы, кроме цифр
                    String cleanedPhoneNumber = number.replaceAll("\\D", "");
//
//                    Log.d("cleanedPhoneNumber", "cleanedPhoneNumber: " + cleanedPhoneNumber);

//                // Форматируем номер телефона
                    number = String.format("+%s %s %s-%s-%s",
                            cleanedPhoneNumber.substring(0, 1),
                            cleanedPhoneNumber.substring(1, 4),
                            cleanedPhoneNumber.substring(4, 7),
                            cleanedPhoneNumber.substring(7, 9),
                            cleanedPhoneNumber.substring(9));

//                    Log.d("cleanedPhoneNumber", "cleanedPhoneNumber: " + number);

//                    if (!contactExists(context, number)) {
                    if (!isNumberInContacts(context, number)) {
                        disconnectCall();
                    }

                }

            } catch (Exception e) {
                Log.e("Exception", "Exception: " + e);
            }
        }
    }

    //    slow
    public boolean contactExists(Context context, String number) {
        number = number.replaceAll("[\\s()\\-]+", "");

        ContentResolver cr = context.getContentResolver();
        Cursor curContacts = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);

        while (curContacts.moveToNext()) {
            @SuppressLint("Range") String contactNumber = curContacts.getString(curContacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

//            Log.d("contactNumber", "contactNumber: " + contactNumber);

            if (contactNumber.charAt(0) == '8') {
                contactNumber = "+7" + contactNumber.substring(1);
            }

            if (number.equals(contactNumber.replaceAll("[\\s()\\-]+", ""))) {
                return true;
            }
        }
        return false;
    }

    //    fast
    private boolean isNumberInContacts(Context context, String phoneNumber) {
        if (phoneNumber != null) {
            ContentResolver contentResolver = context.getContentResolver();
            Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
            String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER};
            String selection = ContactsContract.CommonDataKinds.Phone.NUMBER + " = ?";
            String[] selectionArgs = new String[]{phoneNumber};

            Cursor cursor = contentResolver.query(uri, projection, selection, selectionArgs, null);

            if (cursor != null && cursor.moveToFirst()) {
                cursor.close();
                return true;
            }

            if (cursor != null) {
                cursor.close();
            }
        }
        return false;
    }

    public void disconnectCall() {
        try {
            String serviceManagerName = "android.os.ServiceManager";
            String serviceManagerNativeName = "android.os.ServiceManagerNative";
            String telephonyName = "com.android.internal.telephony.ITelephony";
            Class<?> telephonyClass;
            Class<?> telephonyStubClass;
            Class<?> serviceManagerClass;
            Class<?> serviceManagerNativeClass;
            Method telephonyEndCall;
            Object telephonyObject;
            Object serviceManagerObject;
            telephonyClass = Class.forName(telephonyName);
            telephonyStubClass = telephonyClass.getClasses()[0];
            serviceManagerClass = Class.forName(serviceManagerName);
            serviceManagerNativeClass = Class.forName(serviceManagerNativeName);
            Method getService = // getDefaults[29];
                    serviceManagerClass.getMethod("getService", String.class);
            Method tempInterfaceMethod = serviceManagerNativeClass.getMethod("asInterface", IBinder.class);
            Binder tmpBinder = new Binder();
            tmpBinder.attachInterface(null, "fake");
            serviceManagerObject = tempInterfaceMethod.invoke(null, tmpBinder);
            IBinder retbinder = (IBinder) getService.invoke(serviceManagerObject, "phone");
            Method serviceMethod = telephonyStubClass.getMethod("asInterface", IBinder.class);
            telephonyObject = serviceMethod.invoke(null, retbinder);
            telephonyEndCall = telephonyClass.getMethod("endCall");
            telephonyEndCall.invoke(telephonyObject);

        } catch (Exception e) {
            Log.e("error", "FATAL ERROR: could not connect to telephony subsystem. Exception object: " + e);
        }
    }
}
