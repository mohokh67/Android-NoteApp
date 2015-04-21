package com.irfsf.todo808956;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class TheAlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent)
    {
        Intent serviceIntent = new Intent(context, TheAlarmReceiver.class);
        Long rowID = intent.getExtras().getLong(NotesDbAdapter.KEY_ROWID);
        serviceIntent.putExtra(NotesDbAdapter.KEY_ROWID, rowID);
        context.startService(serviceIntent);

    }
}
