
package com.irfsf.todo808956;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.irfsf.todo808956.notepad.R;

import java.util.Calendar;
import java.util.Date;

public class NoteEdit extends Activity {

    private EditText titleText;
    private EditText bodyText;
    private EditText typeValue;

    private RadioGroup noteTypeRadioGroup;
    private RadioButton radioBasic;
    private RadioButton radioNormal;
    private RadioButton radioEmergency;


    private DatePickerDialog datePicker;
    private TimePickerDialog timePicker;

    private Switch switchAlarm;
    private Intent intent;
    private PendingIntent pendingIntent;



    private static final int CURRENT_ID = Menu.FIRST;



    //private String alarmText = "Alarm is not set";
    private TextView alarmEditableText;

    //private ToggleButton toggleButton;

    private Long mRowId;
    private NotesDbAdapter mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDbHelper = new NotesDbAdapter(this);
        mDbHelper.open();


        setContentView(R.layout.note_edit);
        setTitle(R.string.edit_note);

        titleText = (EditText) findViewById(R.id.title);
        bodyText = (EditText) findViewById(R.id.body);
        typeValue = (EditText) findViewById(R.id.type);
        alarmEditableText = (TextView) findViewById(R.id.alarmText);

        noteTypeRadioGroup = (RadioGroup) findViewById(R.id.priorityRG);
        radioBasic = (RadioButton) findViewById(R.id.radio_basic);
        radioNormal = (RadioButton) findViewById(R.id.radio_normal);
        radioEmergency = (RadioButton) findViewById(R.id.radio_emergency);

       switchAlarm = (Switch) findViewById(R.id.AlarmSwitch);

        Button confirmButton = (Button) findViewById(R.id.confirm);

        mRowId = (savedInstanceState == null) ? null :
                (Long) savedInstanceState.getSerializable(NotesDbAdapter.KEY_ROWID);
        if (mRowId == null) {
            Bundle extras = getIntent().getExtras();
            mRowId = extras != null ? extras.getLong(NotesDbAdapter.KEY_ROWID) : null;
        }


        populateFields();
        setListeners();
        activityTitle();
        saveState();

        if(isUrgent()){
            showAlarmSetter(true);

        }

        confirmButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                setResult(RESULT_OK);
                finish();
            }

        });
    }

    private void populateFields() {
        if (mRowId != null) {
            Cursor note = mDbHelper.fetchNote(mRowId);
            startManagingCursor(note);
            titleText.setText(note.getString(note.getColumnIndexOrThrow(NotesDbAdapter.KEY_TITLE)));
            bodyText.setText(note.getString(note.getColumnIndexOrThrow(NotesDbAdapter.KEY_BODY)));
            typeValue.setText(note.getString(note.getColumnIndexOrThrow(NotesDbAdapter.KEY_TYPE)));

            String noteType = note.getString(note.getColumnIndexOrThrow(NotesDbAdapter.KEY_TYPE));
            int alarm = note.getInt(note.getColumnIndexOrThrow(NotesDbAdapter.KEY_ALARM));

            String currentAlarmText = note.getString(note.getColumnIndexOrThrow(NotesDbAdapter.KEY_ALARM_DATE));

            if (noteType.equalsIgnoreCase("3")) {
                radioEmergency.setChecked(true);
                radioNormal.setChecked(false);
                radioBasic.setChecked(false);
                switchAlarm.setVisibility(View.VISIBLE);
                alarmEditableText.setVisibility(View.VISIBLE);

                if(alarm == 1) {
                    switchAlarm.setChecked(true);
					setAlarmText(currentAlarmText);
                }



            } else if (noteType.equalsIgnoreCase("2")) {
                radioEmergency.setChecked(false);
                radioNormal.setChecked(true);
                radioBasic.setChecked(false);
				switchAlarm.setVisibility(View.GONE);
				setAlarmText(null);
                alarmEditableText.setVisibility(View.GONE);

            } else {
                radioEmergency.setChecked(false);
                radioNormal.setChecked(false);
                radioBasic.setChecked(true);
				switchAlarm.setVisibility(View.GONE);
				setAlarmText(null);
                alarmEditableText.setVisibility(View.GONE);
            }


        }
    }

	private void setAlarmText(String text){
		alarmEditableText.setText(text);
	}


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveState();
        outState.putSerializable(NotesDbAdapter.KEY_ROWID, mRowId);
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        populateFields();
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    private void saveState() {
        String title = titleText.getText().toString().trim();
        String body = bodyText.getText().toString().trim();
        String type = typeValue.getText().toString();
        //Date date = null;

		int setAlarm = 0; // alarm is off by default

		String alarm = null;
        if(switchAlarm.isChecked())
            alarm = alarmEditableText.getText().toString();

		if(this.switchAlarm.isChecked())
			setAlarm = 1;

        if (mRowId == null) {
            long id = mDbHelper.createNote(title, body, type, setAlarm, alarm);
            Toast.makeText(NoteEdit.this, R.string.note_created, Toast.LENGTH_SHORT).show();
            if (id > 0) {
                mRowId = id;
            }
        } else {
            mDbHelper.updateNote(mRowId, title, body, type, setAlarm, alarm);
            //Toast.makeText(NoteEdit.this, R.string.note_updated, Toast.LENGTH_SHORT).show();
        }
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.radio_basic:
                if (checked)
                    typeValue.setText("1");
                    switchAlarm.setVisibility(View.GONE);

                break;
            case R.id.radio_normal:
                if (checked)
                    typeValue.setText("2");
                    switchAlarm.setVisibility(View.GONE);

                break;
            case R.id.radio_emergency:
                if (checked)
                    typeValue.setText("3");
                    switchAlarm.setVisibility(View.VISIBLE);

                break;
            default:
                typeValue.setText("1");
                break;
        }
    }

    /*****************************************************************/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, CURRENT_ID, 0, R.string.menu_delete);
        return true;
    }


    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch(item.getItemId()) {
            case CURRENT_ID:
                mDbHelper.deleteNote(mRowId);
                if(userHasSetTime())
                    cancelAlarm();
                saveState();
                setResult(RESULT_OK);
                finish();
                return true;
        }

        return super.onMenuItemSelected(featureId, item);
    }


    @Override
    public void onBackPressed() {
        saveState();
        setResult(RESULT_OK);
        finish();
    }





    /**
     * Show a pop-up which informs that the alarm time is expired.
     */
    void showDismissPopup()
    {

        AlertDialog.Builder mBuilder = null;
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            mBuilder = new AlertDialog.Builder(NoteEdit.this);
        } else {
            mBuilder = new AlertDialog.Builder(NoteEdit.this, AlertDialog.THEME_HOLO_LIGHT);
        }

        AlertDialog mAlertDialog = mBuilder.create();

        mAlertDialog.setMessage(getResources().getString(R.string.miss_alarm));
        mAlertDialog.setButton(getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                cancelAlarm();
                setOffAlarmInDisplay();
            }
        });
        mAlertDialog.show();

    }



    public void activityTitle()
    {
        if(mRowId == null)
            setTitle(R.string.new_note);
        else
            setTitle(R.string.edit_note);
    }



    /**
     * Set all activity listeners
     */
    public void setListeners() {

        noteTypeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                showAlarmSetter(isUrgent());
                if (userHasSetTime()) {
                    setOffAlarmInDisplay();
                    cancelAlarm();
                }
            }
        });

        switchAlarm.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (switchAlarm.isChecked()) {
                    showAlarmTimeDialog();
                } else {
                    cancelAlarm();
                    alarmEditableText.setText(null);
                }
            }
        });
    }


    /**
     * Show TimePickerDialog and a DatePickerDialog to
     * allow the user to choose a time for the alarm.
     */
    public void showAlarmTimeDialog()
    {

        final Calendar mCalendar = Calendar.getInstance();
        mCalendar.setTime(new Date(System.currentTimeMillis()));


        timePicker = new TimePickerDialog(NoteEdit.this, new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker view, int newHour, int newMinute) {

                mCalendar.set(Calendar.HOUR_OF_DAY, newHour);
                mCalendar.set(Calendar.MINUTE, newMinute);

                setAlarmOnGUI(mCalendar.getTime());

            }
        }, mCalendar.get(Calendar.HOUR_OF_DAY), mCalendar.get(Calendar.MINUTE), true);

        timePicker.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

                if (userHasSetTime())
                    datePicker.show();
                else
                    setOffAlarmInDisplay();

            }
        });
        timePicker.setTitle(getResources().getString(R.string.alarm));


        datePicker = new DatePickerDialog(NoteEdit.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int newYear, int newMonth, int newDay) {

                mCalendar.setTime(TheDateUtils.getDateFromString(alarmEditableText.getText()));
                mCalendar.set(newYear, newMonth, newDay);

                if(mCalendar.getTimeInMillis() <= System.currentTimeMillis()){
                    setOffAlarmInDisplay();
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.miss_alarm),
                            Toast.LENGTH_SHORT).show();
                }
                else {
                    setAlarmOnGUI(mCalendar.getTime());
                    setAlarm(mCalendar.getTime());
                }

            }
        }, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH));

        datePicker.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                setOffAlarmInDisplay();
            }
        });
        datePicker.setTitle(getResources().getString(R.string.alarm));

        timePicker.show();

    }


    public void setAlarmOnGUI(Date date)
    {
		setAlarmText(TheDateUtils.getStringFromDate(date));
    }


    /**
     * Update the TextView when the alarm set off.
     */
    public void setOffAlarmInDisplay()
    {
        switchAlarm.setChecked(false);
		setAlarmText(null);
    }


    /**
     * Check ig the user has set the alarm or not     *
     * @return true if the user set a valid alarm, false otherwise
     */
    private boolean userHasSetTime()
    {
        if( alarmEditableText.getText() != null && alarmEditableText.getText() != "" )
            return true;
        else
            return false;
    }

    /**
     * Set a system alarm for the current note creating a service     *
     * @param date the date the alarm will be set at
     */
    public void setAlarm(Date date)
    {
        if(mRowId != null) {
            int reqCode = Integer.valueOf(mRowId.intValue());

            intent = new Intent(NoteEdit.this, TheAlarmReceiver.class);
            intent.putExtra(NotesDbAdapter.KEY_ROWID, mRowId);
            pendingIntent = PendingIntent.getBroadcast(this, reqCode, intent, PendingIntent.FLAG_ONE_SHOT);

            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC, date.getTime(), pendingIntent);
            Toast.makeText(getApplicationContext(), R.string.alarm_time + TheDateUtils.getStringFromDate(date), Toast.LENGTH_SHORT).show();

        }
    }


    /**
     * Cancel the alarm for current note
     */
    public void cancelAlarm()
    {
        if(mRowId != null) {
            int reqCode = Integer.valueOf(mRowId.intValue());
            intent = new Intent(NoteEdit.this, TheAlarmReceiver.class);
            intent.putExtra(NotesDbAdapter.KEY_ROWID, mRowId);
            pendingIntent = PendingIntent.getBroadcast(NoteEdit.this, reqCode, intent, PendingIntent.FLAG_ONE_SHOT);
            pendingIntent.cancel();
            Toast.makeText(getApplicationContext(), R.string.set_off_alarm, Toast.LENGTH_SHORT).show();
        }
    }



    public void showAlarmSetter(boolean isUrgent){
        if(isUrgent)
            findViewById(R.id.AlarmSwitch).setVisibility(View.VISIBLE);
        else
            findViewById(R.id.AlarmSwitch).setVisibility(View.GONE);
    }


    /**
     * Check if the note is urgent or type = 3     *
     * @return true if it is urgent
     */
    public boolean isUrgent() {
        boolean result = false;
        if (mRowId != null) {
            Cursor note = mDbHelper.fetchNote(mRowId);
            String tmp = note.getString(note.getColumnIndexOrThrow(NotesDbAdapter.KEY_TYPE));
            if (!tmp.equalsIgnoreCase("3"))
                result = true;
            else
                result = false;
        }
        return result;
    }



    /*************************************************************/
}
