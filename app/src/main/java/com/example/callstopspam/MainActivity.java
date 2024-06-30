package com.example.callstopspam;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TimePicker startTimePicker;
    private TimePicker endTimePicker;

    private FlightModeReceiver flightModeReceiver;
    private SharedPreferences sharedPref;

    private static final String PREF_NAME = "FlightModePrefs";
    private static final String FLIGHT_MODE_ALARM_ENABLED_KEY = "flightModeAlarmEnabled";
    private boolean flightModeAlarmEnabled;
    private static final String KEY_START_TIME = "startTime";
    private static final String KEY_END_TIME = "endTime";

    private static final String ACTION_START_FLIGHT_MODE = "ACTION_START_FLIGHT_MODE";
    private static final String ACTION_END_FLIGHT_MODE = "ACTION_END_FLIGHT_MODE";


    private static final String RECEIVER_ENABLED_KEY = "receiverEnabled";
    private boolean receiverEnabled;


//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//    }

    //включение режима полета в заданные промежутки времени
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startTimePicker = findViewById(R.id.startTimePicker);
        startTimePicker.setIs24HourView(true);

        endTimePicker = findViewById(R.id.endTimePicker);
        endTimePicker.setIs24HourView(true);

        sharedPref = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        //загрузка ранее сохраненных значений startTime и endTime
        String savedStartTime = sharedPref.getString(KEY_START_TIME, null);
        String savedEndTime = sharedPref.getString(KEY_END_TIME, null);


        if (savedStartTime != null) {
            String[] timeParts = savedStartTime.split(":");
            if (timeParts.length == 2) {
                try {
                    startTimePicker.setCurrentHour(Integer.parseInt(timeParts[0]));
                    startTimePicker.setCurrentMinute(Integer.parseInt(timeParts[1]));
                } catch (NumberFormatException e) {
                    Log.e("NumberFormatException", "NumberFormatException: " + e);
                }
            }
        }

        if (savedEndTime != null) {
            String[] timeParts = savedEndTime.split(":");
            if (timeParts.length == 2) {
                try {
                    endTimePicker.setCurrentHour(Integer.parseInt(timeParts[0]));
                    endTimePicker.setCurrentMinute(Integer.parseInt(timeParts[1]));
                } catch (NumberFormatException e) {
                    Log.e("NumberFormatException", "NumberFormatException: " + e);
                }
            }
        }

//        Button saveButton = findViewById(R.id.saveButton);
        Button toggleSaveButton = findViewById(R.id.toggleSaveButton);

        flightModeReceiver = new FlightModeReceiver();
        Context context = this.getApplicationContext();


        if (savedStartTime != null && savedEndTime != null) {
            flightModeReceiver.setFlightModeTimes(context, savedStartTime, savedEndTime);
        }

        // Устанавливка Alarm для включения режима "полета" в начале временного интервала
        setAlarm(context, ACTION_START_FLIGHT_MODE);

        //устанавливка Alarm для выключения режима "полета" в конце временного интервала
        setAlarm(context, ACTION_END_FLIGHT_MODE);


        flightModeAlarmEnabled = sharedPref.getBoolean(FLIGHT_MODE_ALARM_ENABLED_KEY, false);

        if (flightModeAlarmEnabled) {
            toggleSaveButton.setText("Включен автосамолет");
        } else {
            toggleSaveButton.setText("Выключен автосамолет");
        }
/*
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Context context = getApplicationContext();

                int startHour = startTimePicker.getCurrentHour();
                int startMinute = startTimePicker.getCurrentMinute();
                String startTime = String.format(Locale.getDefault(), "%02d:%02d", startHour, startMinute);

                int endHour = endTimePicker.getCurrentHour();
                int endMinute = endTimePicker.getCurrentMinute();
                String endTime = String.format(Locale.getDefault(), "%02d:%02d", endHour, endMinute);

                //сохраняем startTime и endTime в SharedPreferences
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(KEY_START_TIME, startTime);
                editor.putString(KEY_END_TIME, endTime);
                editor.apply();


                flightModeReceiver.setFlightModeTimes(context, startTime, endTime);

                setAlarm(context, ACTION_START_FLIGHT_MODE);
                setAlarm(context, ACTION_END_FLIGHT_MODE);

//                int startHour1 = 5;
//                int startMinute1 = 0;
//                int endHour1 = 7;
//                int endMinute1 = 30;
//
//                if (flightModeReceiver.isCurrentTimeInTimeRange(startHour1, startMinute1, endHour1, endMinute1)) {
//                    System.out.println("Текущее время находится в указанном диапазоне.");
//                } else {
//                    System.out.println("Текущее время не находится в указанном диапазоне.");
//                }


            }
        });
*/

        toggleSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                flightModeAlarmEnabled = !flightModeAlarmEnabled;
                setEnabled(context, flightModeAlarmEnabled, FLIGHT_MODE_ALARM_ENABLED_KEY);
                if (flightModeAlarmEnabled) {
                    toggleSaveButton.setText("Включен автосамолет");

                    int startHour = startTimePicker.getCurrentHour();
                    int startMinute = startTimePicker.getCurrentMinute();
                    String startTime = String.format(Locale.getDefault(), "%02d:%02d", startHour, startMinute);

                    int endHour = endTimePicker.getCurrentHour();
                    int endMinute = endTimePicker.getCurrentMinute();
                    String endTime = String.format(Locale.getDefault(), "%02d:%02d", endHour, endMinute);

                    //сохраняем startTime и endTime в SharedPreferences
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString(KEY_START_TIME, startTime);
                    editor.putString(KEY_END_TIME, endTime);
                    editor.apply();


                    flightModeReceiver.setFlightModeTimes(context, startTime, endTime);

                    setAlarm(context, ACTION_START_FLIGHT_MODE);
                    setAlarm(context, ACTION_END_FLIGHT_MODE);

                } else {
                    toggleSaveButton.setText("Выключен автосамолет");

                    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

                    Intent intent = new Intent(context, FlightModeReceiver.class);
                    intent.setAction(ACTION_START_FLIGHT_MODE);

                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmManager.cancel(pendingIntent);

                    intent.setAction(ACTION_END_FLIGHT_MODE);

                    pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmManager.cancel(pendingIntent);
                }

            }
        });


        Button toggleReceiverBtn = findViewById(R.id.toggleReceiverButton);

        receiverEnabled = sharedPref.getBoolean(RECEIVER_ENABLED_KEY, false);

        if (receiverEnabled) {
            toggleReceiverBtn.setText("Включен антиспам");
        } else {
            toggleReceiverBtn.setText("Выключен антиспам");
        }

        toggleReceiverBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                receiverEnabled = !receiverEnabled;
                setEnabled(context, receiverEnabled, RECEIVER_ENABLED_KEY);
//                IncomingCallReceiver.setEnabled(context, flightModeAlarmEnabled);
                if (receiverEnabled) {
                    toggleReceiverBtn.setText("Включен антиспам");
                } else {
                    toggleReceiverBtn.setText("Выключен антиспам");
                }
            }
        });

    }

    private void setEnabled(Context context, boolean isEnabled, String key) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(key, isEnabled);
        editor.apply();
    }


    private void setAlarm(Context context, String action) {

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        String savedStartTime = sharedPref.getString(KEY_START_TIME, null);
        String savedEndTime = sharedPref.getString(KEY_END_TIME, null);

        Intent intent = new Intent(context, FlightModeReceiver.class);
        intent.putExtra("startTime", savedStartTime);
        intent.putExtra("endTime", savedEndTime);
        intent.setAction(action);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        int startTimeHour = startTimePicker.getCurrentHour();
        int startTimeMinute = startTimePicker.getCurrentMinute();

        int endTimeHour = endTimePicker.getCurrentHour();
        int endTimeMinute = endTimePicker.getCurrentMinute();

        Calendar calendar = Calendar.getInstance();

        if (action.equals(ACTION_START_FLIGHT_MODE)) {
            //установка времени начала интервала, когда нужно включить режим "полета"
            calendar.set(Calendar.HOUR_OF_DAY, startTimeHour);
            calendar.set(Calendar.MINUTE, startTimeMinute);
        } else {
            //установка времени окончания интервала, когда нужно выключить режим "полета"
            calendar.set(Calendar.HOUR_OF_DAY, endTimeHour);
            calendar.set(Calendar.MINUTE, endTimeMinute);
        }


        flightModeAlarmEnabled = sharedPref.getBoolean(FLIGHT_MODE_ALARM_ENABLED_KEY, false);
        if (flightModeAlarmEnabled) {
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
//            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 3000, pendingIntent);
        } else {
            alarmManager.cancel(pendingIntent);

        }

    }


//    показать в виде списка все контакты
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) ListView contactsListView = findViewById(R.id.contacts_list_view);
//        ArrayList<String> contacts = new ArrayList<>();
//
//        Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
//                null, null, null, null);
//
//        if (cursor != null && cursor.moveToFirst()) {
//            do {
//                @SuppressLint("Range") String contactDisplayName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
//                @SuppressLint("Range") String phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//
//                contacts.add(contactDisplayName + ": " + phoneNumber);
//            } while (cursor.moveToNext());
//            cursor.close();
//        }
//
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, contacts);
//        contactsListView.setAdapter(adapter);
//    }
}