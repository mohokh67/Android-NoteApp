/*
 * Copyright (C) 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.demo.todo808956;

import android.annotation.TargetApi;
import android.app.Activity;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TimePicker;

import com.android.demo.notepad3.R;

import java.util.Date;

public class NoteEdit extends Activity {

    private EditText mTitleText;
    private EditText mBodyText;
    private EditText mTypeValue;

    private RadioButton radioBasic;
    private RadioButton radioNormal;
    private RadioButton radioEmergency;

    private DatePicker datePicker;
    private TimePicker timePicker;

    private Long mRowId;
    private NotesDbAdapter mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDbHelper = new NotesDbAdapter(this);
        mDbHelper.open();


        setContentView(R.layout.note_edit);
        setTitle(R.string.edit_note);

        mTitleText = (EditText) findViewById(R.id.title);
        mBodyText = (EditText) findViewById(R.id.body);
        mTypeValue = (EditText) findViewById(R.id.type);

        radioBasic = (RadioButton) findViewById(R.id.radio_basic);
        radioNormal = (RadioButton) findViewById(R.id.radio_normal);
        radioEmergency = (RadioButton) findViewById(R.id.radio_emergency);

        Button confirmButton = (Button) findViewById(R.id.confirm);

        datePicker = (DatePicker) findViewById(R.id.datePicker);
        timePicker = (TimePicker) findViewById(R.id.timePicker);


        mRowId = (savedInstanceState == null) ? null :
                (Long) savedInstanceState.getSerializable(NotesDbAdapter.KEY_ROWID);
        if (mRowId == null) {
            Bundle extras = getIntent().getExtras();
            mRowId = extras != null ? extras.getLong(NotesDbAdapter.KEY_ROWID) : null;
        }


        populateFields();

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
            mTitleText.setText(note.getString(note.getColumnIndexOrThrow(NotesDbAdapter.KEY_TITLE)));
            mBodyText.setText(note.getString(note.getColumnIndexOrThrow(NotesDbAdapter.KEY_BODY)));
            mTypeValue.setText(note.getString(note.getColumnIndexOrThrow(NotesDbAdapter.KEY_TYPE)));

            String tmp = note.getString(note.getColumnIndexOrThrow(NotesDbAdapter.KEY_TYPE));

            if (tmp.equalsIgnoreCase("3")) {
                int year = Integer.valueOf(note.getString(note.getColumnIndexOrThrow(NotesDbAdapter.KEY_YEAR)));
                int month = Integer.valueOf(note.getString(note.getColumnIndexOrThrow(NotesDbAdapter.KEY_MONTH)));
                int day = Integer.valueOf(note.getString(note.getColumnIndexOrThrow(NotesDbAdapter.KEY_DAY)));

                datePicker.init(year, (month-1), day, null);

                timePicker.setCurrentHour(Integer.valueOf(note.getString(note.getColumnIndexOrThrow(NotesDbAdapter.KEY_HOUR))));
                timePicker.setCurrentMinute(Integer.valueOf(note.getString(note.getColumnIndexOrThrow(NotesDbAdapter.KEY_MINUTE))));

            }


            if (tmp.equalsIgnoreCase("3")) {
                radioEmergency.setChecked(true);
                radioNormal.setChecked(false);
                radioBasic.setChecked(false);
                datePicker.setVisibility(View.VISIBLE);
                timePicker.setIs24HourView(true);
                timePicker.setVisibility(View.VISIBLE);

            } else if (tmp.equalsIgnoreCase("2")) {
                radioEmergency.setChecked(false);
                radioNormal.setChecked(true);
                radioBasic.setChecked(false);
                datePicker.setVisibility(View.GONE);
                timePicker.setVisibility(View.GONE);

            } else {
                radioEmergency.setChecked(false);
                radioNormal.setChecked(false);
                radioBasic.setChecked(true);
                datePicker.setVisibility(View.GONE);
                timePicker.setVisibility(View.GONE);
            }


        }
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
        String title = mTitleText.getText().toString();
        String body = mBodyText.getText().toString();
        String type = mTypeValue.getText().toString();
        Date date = null;

        if (title.trim().isEmpty()){
            title = "No title";
        }

        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth() + 1;
        int year = datePicker.getYear();

        int hour = timePicker.getCurrentHour();
        int minute = timePicker.getCurrentMinute();


        if (mRowId == null) {
            long id = mDbHelper.createNote(title, body, type, year, month, day, hour, minute);
            if (id > 0) {
                mRowId = id;
            }
        } else {
            mDbHelper.updateNote(mRowId, title, body, type, year, month, day, hour, minute);
        }
    }


    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.radio_basic:
                if (checked)
                    mTypeValue.setText("1");
                break;
            case R.id.radio_normal:
                if (checked)
                    mTypeValue.setText("2");
                break;
            case R.id.radio_emergency:
                if (checked)
                    mTypeValue.setText("3");
                    datePicker.setVisibility(view.VISIBLE);
                    timePicker.setIs24HourView(true);
                    timePicker.setVisibility(view.VISIBLE);
                break;
            default:
                mTypeValue.setText("1");
                break;
        }
    }

}
