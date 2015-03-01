package de.uni_weimar.m18.exkursion;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.uni_weimar.m18.exkursion.data.level.LevelColumns;

/**
 * Created by stu on 22.02.2015.
 */
public class LevelAdapter extends CursorAdapter {

    public LevelAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    private String convertCursorRowToUXFormat(Cursor cursor) {
        int idx_title = cursor.getColumnIndex(LevelColumns.TITLE);
        String title = cursor.getString(idx_title);
        return title;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_level, parent, false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // simple and slow binding
        TextView tv = (TextView)view;
        tv.setText(convertCursorRowToUXFormat(cursor));
    }
}
