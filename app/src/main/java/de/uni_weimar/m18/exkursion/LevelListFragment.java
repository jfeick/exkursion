package de.uni_weimar.m18.exkursion;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import de.uni_weimar.m18.exkursion.data.LevelContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class LevelListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = LevelListFragment.class.getSimpleName();

    private static final int LEVEL_LOADER = 0;

    private static final String[] LEVEL_COLUMNS = {
            LevelContract.LevelEntry.TABLE_NAME + "." + LevelContract.LevelEntry._ID,
            LevelContract.LevelEntry.COLUMN_PATH,
            LevelContract.LevelEntry.COLUMN_TITLE,
            LevelContract.LevelEntry.COLUMN_MD5SUM
    };
    static final int COL_LEVEL_ID       = 0;
    static final int COL_LEVEL_PATH     = 1;
    static final int COL_LEVEL_TITLE    = 2;
    static final int COL_LEVEL_MD5SUM   = 3;


    private LevelAdapter mLevelAdapter;

    public LevelListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_levels, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_refresh) {
            updateLevels();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        String sortOrder = LevelContract.LevelEntry.COLUMN_PATH + " ASC";
        Uri levelUri = LevelContract.LevelEntry.CONTENT_URI;
        Cursor cur = getActivity().getContentResolver().query(levelUri, null, null, null, sortOrder);
        mLevelAdapter = new LevelAdapter(getActivity(), cur, 0);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        ListView listView = (ListView) rootView.findViewById(R.id.listview_levels);
        listView.setAdapter(mLevelAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // CursorAdapter returns a cursor at the correct position for getItem()
                // if it cannot seek to that position
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                long idx = cursor.getLong(COL_LEVEL_ID);
                Log.v(LOG_TAG, "OnItemClickListener id: " + Long.toString(idx));
                Log.v(LOG_TAG, "Uri to intent: " + LevelContract.LevelEntry.buildLevelsUri(idx));
                if( cursor != null ) {
                    Intent intent = new Intent(getActivity(), LevelPrepareActivity.class)
                            .setData(LevelContract.LevelEntry.buildLevelsUri(idx));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                }
            }
        });

        return rootView;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(LEVEL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    private void updateLevels() {
        FetchLevelsTask levelsTask = new FetchLevelsTask(
                getActivity(), getString(R.string.GAME_BASE_URL));
        levelsTask.execute();
    }

    @Override
    public void onStart() {
        super.onStart();
        updateLevels();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder = LevelContract.LevelEntry.COLUMN_PATH + " ASC";
        Uri levelUri = LevelContract.LevelEntry.CONTENT_URI;
        return new CursorLoader(getActivity(),
                levelUri,
                null,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mLevelAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mLevelAdapter.swapCursor(null);
    }
}
