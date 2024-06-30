package com.example.callstopspam;


import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class FlightModeReceiver extends BroadcastReceiver {

    private static final String ACTION_START_FLIGHT_MODE = "ACTION_START_FLIGHT_MODE";
    private static final String ACTION_END_FLIGHT_MODE = "ACTION_END_FLIGHT_MODE";
    private String startTime;
    private String endTime;

    private static final String PREF_NAME = "FlightModePrefs";
    private static final String FLIGHT_MODE_ALARM_ENABLED_KEY = "flightModeAlarmEnabled";

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {
        startTime = intent.getStringExtra("startTime");
        endTime = intent.getStringExtra("endTime");

        //проверка, если текущее время входит в заданный промежуток
//        Log.d("intent_2", "intent: 2 " + intent.getAction());

        SharedPreferences sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        boolean flightModeAlarmEnabled = sharedPref.getBoolean(FLIGHT_MODE_ALARM_ENABLED_KEY, false);

//        Log.d("flightModeAlarmEnabled", "flightModeAlarmEnabled: 2 " + flightModeAlarmEnabled);
        if (flightModeAlarmEnabled && intent.getAction() != null) {

//            Log.d("boolean", "boolean: " + isInFlightModeTime(hour, minute) + " " + ACTION_START_FLIGHT_MODE.equals(intent.getAction()));

            if (isInFlightModeTime() && ACTION_START_FLIGHT_MODE.equals(intent.getAction())) {
                //включение режима полета
                setFlightMode(context, true);
            } else if (ACTION_END_FLIGHT_MODE.equals(intent.getAction())) {
                //отключение режима полета
                setFlightMode(context, false);
            }
        }
    }

    //проверка текущего времени - находится ли оно в заданном промежутке времени
    private boolean isInFlightModeTime() {
        if (startTime == null || endTime == null) {
            return false;
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());

            // преобразование текущего время в формат, с которым можно сравнивать
            Calendar calTime = Calendar.getInstance();
            calTime.setTime(sdf.parse(startTime));

            int startHour = calTime.get(Calendar.HOUR_OF_DAY);
            int startMinute = calTime.get(Calendar.MINUTE);

            calTime = Calendar.getInstance();
            calTime.setTime(sdf.parse(endTime));

            int endHour = calTime.get(Calendar.HOUR_OF_DAY);
            int endMinute = calTime.get(Calendar.MINUTE);

            // получение текущего времени
            Calendar currentTime = Calendar.getInstance();
            int currentHour = currentTime.get(Calendar.HOUR_OF_DAY);
            int currentMinute = currentTime.get(Calendar.MINUTE);

            int currentTotalMinutes = currentHour * 60 + currentMinute;

            int startTotalMinutes = startHour * 60 + startMinute;
            int endTotalMinutes = endHour * 60 + endMinute;

            if (endTotalMinutes < startTotalMinutes) {
                // диапазон времени переходит через полночь
                endTotalMinutes += 24 * 60;
                if (currentTotalMinutes < startTotalMinutes) {
                    currentTotalMinutes += 24 * 60;
                }
            }

            return currentTotalMinutes >= startTotalMinutes && currentTotalMinutes <= endTotalMinutes;

        } catch (ParseException e) {
            Log.e("ParseException", "ParseException: " + e);
        }

        return false;
    }

    public void setFlightMode(Context context, boolean enable) {
        // включение/выключение режима "полета"
        Settings.System.putInt(context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, enable ? 1 : 0);
        Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        intent.putExtra("state", enable);
        context.sendBroadcast(intent);
    }

    public void setFlightModeTimes(Context context, String startTime, String endTime) {
        this.startTime = startTime;
        this.endTime = endTime;

        // вызов метода для проверки времени
        if (isInFlightModeTime()) {
            setFlightMode(context, true);
        } else {
            setFlightMode(context, false);
        }
    }

}
