package com.angular.gerardosuarez.carpoolingapp.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import com.angular.gerardosuarez.carpoolingapp.R;

public class CustomTimePickerDialog extends AlertDialog implements DialogInterface.OnClickListener, TimePicker.OnTimeChangedListener, RadioGroup.OnCheckedChangeListener {

    public static final int TIME_PICKER_INTERVAL = 30;
    private static final String HOUR = "hour";
    private static final String MINUTE = "minute";
    private static final String IS_24_HOUR = "is24hour";
    private final TimePicker mTimePicker;
    private final OnTimeSetListener mCallback;
    private boolean mIgnoreEvent = false;
    private int mInitialHourOfDay;
    private int mInitialMinute;
    private boolean mIs24HourView;

    private RadioButton check30m;
    private RadioButton check1h;
    private RadioButton check1_3h;
    private RadioButton check2h;
    private TextView txtIntervalos;

    private RadioGroup radioGroup;

    private View view;

    private String valorIntervalo;


    public CustomTimePickerDialog(Context context, OnTimeSetListener callBack, int hourOfDay, int minute,
                                  boolean is24HourView, boolean isPasajero) {

        this(context, 0, callBack, hourOfDay, minute, is24HourView, isPasajero);


    }

    public CustomTimePickerDialog(Context context,
                                  int theme,
                                  OnTimeSetListener callBack,
                                  int hourOfDay, int minute, boolean is24HourView, boolean isPasajero) {
        super(context, theme);
        mCallback = callBack;
        mInitialHourOfDay = hourOfDay;
        mInitialMinute = minute;
        mIs24HourView = is24HourView;
        setIcon(0);
        //setTitle(R.string.time_picker_dialog_title);
        Context themeContext = getContext();
        setButton(BUTTON_POSITIVE, themeContext.getText(R.string.date_time_set), this);
        setButton(BUTTON_NEGATIVE, themeContext.getText(R.string.cancel),
                (OnClickListener) null);

        //Carga de la vista al dialog
        LayoutInflater inflater =
                (LayoutInflater) themeContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.vista_intervalos_tiempo, null);
        setView(view);

        mTimePicker = view.findViewById(R.id.reloj);

        check30m = view.findViewById(R.id.check30m);
        check1h = view.findViewById(R.id.check1h);
        check1_3h = view.findViewById(R.id.check1_3h);
        check2h = view.findViewById(R.id.check2h);
        txtIntervalos = view.findViewById(R.id.txtDescripcion);

        radioGroup = view.findViewById(R.id.grupoCheck);
        radioGroup.setOnCheckedChangeListener(this);

        if(isPasajero){
            radioGroup.setVisibility(View.VISIBLE);
            txtIntervalos.setVisibility(View.VISIBLE);
        }else{
            txtIntervalos.setVisibility(View.INVISIBLE);
            radioGroup.setVisibility(View.INVISIBLE);
        }

        // initialize state
        mTimePicker.setIs24HourView(mIs24HourView);
        mTimePicker.setCurrentHour(mInitialHourOfDay);
        mTimePicker.setCurrentMinute(mInitialMinute);
        mTimePicker.setOnTimeChangedListener(this);

        valorIntervalo ="0";

    }

    public static int getRoundedMinute(int minute) {
        if (minute % TIME_PICKER_INTERVAL != 0) {
            int minuteFloor = minute - (minute % TIME_PICKER_INTERVAL);
            minute = minuteFloor + (minute == minuteFloor + 1 ? TIME_PICKER_INTERVAL : 0);
            if (minute == 60) minute = 0;
        }

        return minute;
    }

    public static int getRoundedMinute(int minute, int timePickerInterval) {
        if (minute % timePickerInterval != 0) {
            int minuteFloor = minute - (minute % timePickerInterval);
            minute = minuteFloor + (minute == minuteFloor + 1 ? timePickerInterval : 0);
            if (minute == 60) minute = 0;
        }

        return minute;
    }

    public void onClick(DialogInterface dialog, int which) {
        if (mCallback != null) {
            mTimePicker.clearFocus();
            mCallback.onTimeSet(mTimePicker, mTimePicker.getCurrentHour(),
                    mTimePicker.getCurrentMinute(),darValorIntervalo());
        }
    }

    public void updateTime(int hourOfDay, int minutOfHour) {
        mTimePicker.setCurrentHour(hourOfDay);
        mTimePicker.setCurrentMinute(minutOfHour);
    }



    /*
     * (non-Javadoc)
     * @see android.app.TimePickerDialog#onTimeChanged(android.widget.TimePicker, int, int)
     * Implements Time Change Interval
     */

    @Override
    public Bundle onSaveInstanceState() {
        Bundle state = super.onSaveInstanceState();
        state.putInt(HOUR, mTimePicker.getCurrentHour());
        state.putInt(MINUTE, mTimePicker.getCurrentMinute());
        state.putBoolean(IS_24_HOUR, mTimePicker.is24HourView());
        return state;
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        int hour = savedInstanceState.getInt(HOUR);
        int minute = savedInstanceState.getInt(MINUTE);
        mTimePicker.setIs24HourView(savedInstanceState.getBoolean(IS_24_HOUR));
        mTimePicker.setCurrentHour(hour);
        mTimePicker.setCurrentMinute(minute);
    }

    @Override
    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {

        this.setTitle("2. Select Time");
        if (!mIgnoreEvent) {
            minute = getRoundedMinute(minute);
            mIgnoreEvent = true;
            mTimePicker.setCurrentMinute(minute);
            mIgnoreEvent = false;
        }


    }

    public String darValorIntervalo(){

        return valorIntervalo;

    }
    public void setValorIntervalo(String valor){

        valorIntervalo=valor;
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {


        if (group.getCheckedRadioButtonId() == R.id.check30m) {

            check2h.setChecked(false);
            check1h.setChecked(false);
            check1_3h.setChecked(false);
            setValorIntervalo("30m");


        } else if (group.getCheckedRadioButtonId() == R.id.check1h) {

            check2h.setChecked(false);
            check30m.setChecked(false);
            check1_3h.setChecked(false);
            setValorIntervalo("1h");



        } else if (group.getCheckedRadioButtonId() == R.id.check2h) {

            check1h.setChecked(false);
            check30m.setChecked(false);
            check1_3h.setChecked(false);
            setValorIntervalo("2h");


        } else if (group.getCheckedRadioButtonId() == R.id.check1_3h) {

            check1h.setChecked(false);
            check30m.setChecked(false);
            check2h.setChecked(false);
            setValorIntervalo("1:30h");


        }

    }


}

