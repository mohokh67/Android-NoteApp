
package com.irfsf.todo808956;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.irfsf.todo808956.notepad.R;

public class Main extends ListActivity implements View.OnClickListener, DialogInterface.OnCancelListener {
    private static final int ACTIVITY_CREATE = 0;
    private static final int ACTIVITY_EDIT = 1;

    private static final int INSERT_ID = Menu.FIRST;
    private static final int DELETE_ID = Menu.FIRST + 1;
    private static final int ABOUT_ID = 9;

    private String aboutMe = "CSM06 Mobile application\nMohamad Khaleqi 808956";

    private NotesDbAdapter mDbHelper;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mDbHelper = new NotesDbAdapter(this);
        mDbHelper.open();
        fillData();
        registerForContextMenu(getListView());
    }

    private void fillData() {
        Cursor notesCursor = mDbHelper.fetchAllNotes();
        startManagingCursor(notesCursor);

        // Create an array to specify the fields we want to display in the list (only TITLE)
        /*
        String tempTitle = NotesDbAdapter.KEY_TITLE;
        if (tempTitle.trim().isEmpty()){
            tempTitle = "No title...";
        }
        */

        String[] from = new String[]{NotesDbAdapter.KEY_TITLE, NotesDbAdapter.KEY_TYPE};
        //String[] from = new String[]{tempTitle, NotesDbAdapter.KEY_TYPE};

        //Create an array to specify the fields we want to display in the list (only BODY)
       // String[] fromBody = new String[]{NotesDbAdapter.KEY_BODY};

        // and an array of the fields we want to bind those fields to (in this case just text1)
        int[] to = new int[]{R.id.eachItem};

        //SimpleCursorAdapter notes = new SimpleCursorAdapter(this, R.layout.notes_row, notesCursor, from, to);
        ThisSimpleCursorAdapter notes = new ThisSimpleCursorAdapter(this, R.layout.notes_row, notesCursor, from, to);
        setListAdapter(notes);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, INSERT_ID, 0, R.string.menu_insert);
        //menu.add(R.string.menu_about);
        menu.add(0, ABOUT_ID, 1, R.string.menu_about);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
            case INSERT_ID:
                createNote();
                return true;

            case ABOUT_ID:
                this.displayPopup("About me", aboutMe);
                return true;

        }

        return super.onMenuItemSelected(featureId, item);
    }

    private void displayPopup(String title, String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.show();

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, DELETE_ID, 0, R.string.menu_delete);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case DELETE_ID:
                AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
                mDbHelper.deleteNote(info.id);
                fillData();
                return true;
        }
        return super.onContextItemSelected(item);
    }

    private void createNote() {
        Intent i = new Intent(this, NoteEdit.class);
        startActivityForResult(i, ACTIVITY_CREATE);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent i = new Intent(this, NoteEdit.class);
        i.putExtra(NotesDbAdapter.KEY_ROWID, id);
        startActivityForResult(i, ACTIVITY_EDIT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        fillData();
    }


    @Override
    public void onCancel(DialogInterface dialog) {

    }

    @Override
    public void onClick(View v) {

    }

    public class ThisSimpleCursorAdapter extends SimpleCursorAdapter {

        public ThisSimpleCursorAdapter(Context context, int layout, Cursor cursor, String[] from, int[] to) {
            super(context, layout, cursor, from, to);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor)  {
            super.bindView(view, context, cursor);
            String priority = cursor.getString(cursor.getColumnIndexOrThrow(NotesDbAdapter.KEY_TYPE));
            String title = cursor.getString(cursor.getColumnIndexOrThrow(NotesDbAdapter.KEY_TITLE));


            if(title.isEmpty()){
                ((TextView) view.findViewById(R.id.eachItem)).setHint(getResources().getString(R.string.no_title));
            }


            if(priority.equalsIgnoreCase("3"))
                view.setBackgroundColor(getResources().getColor(R.color.note_red));
            else if(priority.equalsIgnoreCase("2"))
                view.setBackgroundColor(getResources().getColor(R.color.note_orange));
            else
                view.setBackgroundColor(getResources().getColor(R.color.note_green));

        }
    }
}
