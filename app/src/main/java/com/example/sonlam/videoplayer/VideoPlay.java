package com.example.sonlam.videoplayer;

/**
 * Created by Son Lam on 12/18/2015.
 */
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.SeekBar;

import java.io.IOException;
import java.util.logging.Handler;

import com.example.sonlam.videoplayer.R;


public class VideoPlay extends ActionBarActivity implements
        MediaPlayer.OnBufferingUpdateListener,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnInfoListener,
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnSeekCompleteListener,
        MediaPlayer.OnVideoSizeChangedListener,
        SurfaceHolder.Callback,
        MediaController.MediaPlayerControl {
    private MediaPlayer mediaPlayer = null;
    private String videoUri = null;
    private SurfaceView videoView = null;
    private SurfaceHolder videoHolder = null;
    private String DEBUG = "video play";
    private MediaController mediaController = null;

    private SeekBar volumeSeekbar = null;
    private AudioManager audioManager = null;
    private Runnable volumeSeebarRunnable = null;
    Handler mHandler = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        //supportRequestWindowFeature(Window.FEATURE_NO_TITLE);        //Loai bo thanh menu
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        setContentView(R.layout.video_play);



        videoView = (SurfaceView)findViewById(R.id.videoSurface);

        volumeSeekbar = (SeekBar)findViewById(R.id.volumeSeekbar);
        volumeSeekbar.setVisibility(View.GONE);                           //Hide volume Seekbar

        videoHolder = videoView.getHolder();
        videoHolder.addCallback(this);
        mediaController = new MediaController(this);
        initialMediaPlayer();
        videoUri = getVideoUri();
        try {
            mediaPlayer.setDataSource(videoUri);
        } catch (IOException e) {
            e.printStackTrace();
            finish();
        }

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        volumeSeebarRunnable = new Runnable() {
            @Override
            public void run() {
                volumeSeekbar.setVisibility(View.INVISIBLE);
                volumeSeekbar.setVisibility(View.GONE);
            }
        };
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_video_play, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private String getVideoUri() {
        Intent intent = getIntent();
        Uri videoUri = intent.getData();
        return videoUri.toString();
    }

    private void initialMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnInfoListener(this);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnSeekCompleteListener(this);
        mediaPlayer.setOnVideoSizeChangedListener(this);
    }
    private void setVideoSize() {                       //set Video size

        // // Get the dimensions of the video
        int videoWidth = mediaPlayer.getVideoWidth();
        int videoHeight = mediaPlayer.getVideoHeight();
        float videoProportion = (float) videoWidth / (float) videoHeight;

        // Get the width of the screen
        int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        int screenHeight = getWindowManager().getDefaultDisplay().getHeight();
        float screenProportion = (float) screenWidth / (float) screenHeight;

        // Get the SurfaceView layout parameters
        android.view.ViewGroup.LayoutParams lp = videoView.getLayoutParams();
        if (videoProportion > screenProportion) {
            lp.width = screenWidth;
            lp.height = (int) ((float) screenWidth / videoProportion);
        } else {
            lp.width = (int) (videoProportion * (float) screenHeight);
            lp.height = screenHeight;
        }
        // Commit the layout parameters
        videoView.setLayoutParams(lp);
        mediaPlayer.start();
    }

    private void initControls()             //Khoi tao volume seekbar control
    {
        try{
            volumeSeekbar = (SeekBar)findViewById(R.id.volumeSeekbar);
            audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
            volumeSeekbar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
            volumeSeekbar.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));

            volumeSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, i, 0);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        //mAnimationHandler.removeCallbacks(mFadeOut);
        super.onConfigurationChanged(newConfig);

        setVideoSize();
    }


    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        setVideoSize();
        mediaPlayer.start();
        mediaController.setMediaPlayer(this);
        mediaController.setAnchorView(this.findViewById(R.id.MainView));
        mediaController.setEnabled(true);
        mediaController.show();
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {

    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mediaPlayer.prepareAsync();
        mediaPlayer.setDisplay(videoHolder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    protected void onStop() {
        super.onStop();
        mediaPlayer.release();
        mediaPlayer = null;
    }

    @Override
    public void start() {
        mediaPlayer.start();
    }

    @Override
    public void pause() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    @Override
    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    @Override
    public int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    @Override
    public void seekTo(int pos) {
        mediaPlayer.seekTo(pos);
    }

    @Override
    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mediaController.show();
        volumeSeekbar.setVisibility(View.VISIBLE);
        volumeSeekbar.removeCallbacks(volumeSeebarRunnable);
        volumeSeekbar.postDelayed(volumeSeebarRunnable, 3000);
        initControls();
        return super.onTouchEvent(event);
    }
}
