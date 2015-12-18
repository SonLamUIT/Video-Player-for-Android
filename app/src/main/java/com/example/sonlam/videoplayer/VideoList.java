package com.example.sonlam.videoplayer;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.view.MenuInflater;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;


public class VideoList extends AppCompatActivity {


    private static final String TAG = VideoList.class.getSimpleName();
    SwipeRefreshLayout swipeRefreshLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list);
        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        videoResolver= this.getContentResolver();
        Cursor videoCursor;
        videoResolver=getContentResolver();
        videoInfos = new ArrayList<Video_Info>();
        videoCursor = videoResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, videoColums, null, null, null);
        if (videoCursor.moveToFirst()) {
            do {
                Video_Info videoInfo = new Video_Info();
                videoInfo.setTitle(videoCursor.getString(videoCursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE)));

                videoInfo.setUrl(videoCursor.getString(videoCursor.getColumnIndex(MediaStore.Video.Media.DATA)));
                videoInfo.setMimeType(videoCursor.getString(videoCursor.getColumnIndex(MediaStore.Video.Media.MIME_TYPE)));
                videoInfos.add(videoInfo);
            }while (videoCursor.moveToNext());
        }

        videoList=(ListView)findViewById(R.id.videoList);
        mAdapter = new Video_Adapter(this,videoInfos);
        videoList.setAdapter(mAdapter);
        videoList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                jumpToVideoPlay(videoInfos.get(i).getUrl());
            }
        });



        txtsearch = (EditText)findViewById(R.id.edit_text_search_listview);
        txtsearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mAdapter.getFilter().filter(charSequence);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Exit")
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        System.exit(0);
                    }
                }).setNegativeButton("No", null).show();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


        // Inflate the menu; this adds items to the action bar if it is present.
   /*     getMenuInflater().inflate(R.menu.menu_video_list, menu);
        MenuItem item = menu.findItem(R.id.edit_text_search_listview);*/

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_video_list, menu);

        //SearchView searchView =(SearchView)item.getActionView();
        //searchView.setOnQueryTextListener(this);
        return true;

    }

    public void jumpToVideoPlay(String url) {
        Intent intent = new Intent(VideoList.this, VideoPlay.class);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                return true;
            case R.id.action_info:
                showInfo();
                return true;
            case R.id.open_search:
                showSearchBox();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showSearchBox()
    {
        EditText edTx1=(EditText)findViewById(R.id.edit_text_search_listview);
        if(edTx1.getVisibility ()== View.INVISIBLE)
            edTx1.setVisibility(View.VISIBLE);
        else
        if(edTx1.getVisibility()==View.VISIBLE)
            edTx1.setVisibility(View.INVISIBLE);
    }

    public void showInfo() {
        DialogFragment newFragment = new InfoDialogFragment();
        newFragment.show(getSupportFragmentManager(), "Info");
    }


    static public class InfoDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(R.string.info)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }


    Video_Adapter mAdapter;
    private ListView videoList = null;
    private ListView videoListSearch = null;
    private BaseAdapter videoAdapter = null;

    private static ContentResolver videoResolver = null;
    EditText txtsearch;
    ArrayList<Video_Info> videoInfos;
    //
    private static String[] videoColums = {
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DATA,
            MediaStore.Video.Media.TITLE,
            MediaStore.Video.Media.MIME_TYPE
    };
    public  void LoadVideoInfo() {
        Cursor videoCursor;
        videoResolver=getContentResolver();
        videoInfos = new ArrayList<Video_Info>();
        videoCursor = videoResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, videoColums, null, null, null);
        if (videoCursor.moveToFirst()) {
            do {
                Video_Info videoInfo = new Video_Info();
                videoInfo.setTitle(videoCursor.getString(videoCursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE)));

                videoInfo.setUrl(videoCursor.getString(videoCursor.getColumnIndex(MediaStore.Video.Media.DATA)));
                videoInfo.setMimeType(videoCursor.getString(videoCursor.getColumnIndex(MediaStore.Video.Media.MIME_TYPE)));
                videoInfos.add(videoInfo);
            }while (videoCursor.moveToNext());
        }
    }


}
