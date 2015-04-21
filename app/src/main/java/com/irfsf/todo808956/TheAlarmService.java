package com.irfsf.todo808956;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.irfsf.todo808956.notepad.R;

public class TheAlarmService extends Service
{



    @Override
    public IBinder onBind(Intent arg0)
    {
        return null;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
    }

    @SuppressWarnings("static-access")
    @Override
    public void onStart(Intent intent, int startId)
    {
        super.onStart(intent, startId);

        String notificationTitle = getResources().getString(R.string.Reminder);
        String notificationMsg = getResources().getString(R.string.ReminderMessage);

        Long rowID = intent.getExtras().getLong(NotesDbAdapter.KEY_ROWID);
        int reqCode = rowID.intValue();

        NotificationManager notificationManager = (NotificationManager)
                this.getApplicationContext().getSystemService(this.getApplicationContext().NOTIFICATION_SERVICE);
        Intent mIntent = new Intent(this.getApplicationContext(), NoteEdit.class);
        mIntent.putExtra(NotesDbAdapter.KEY_ROWID, rowID);


        Notification notification = new Notification(R.drawable.ic_launcher, notificationTitle, System.currentTimeMillis());
        mIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP| Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent mPendingNotificationIntent = PendingIntent.getActivity(
                this.getApplicationContext(), reqCode, mIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.setLatestEventInfo(this.getApplicationContext(), notificationTitle,
				notificationMsg, mPendingNotificationIntent);

        notificationManager.notify(reqCode, notification);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

}