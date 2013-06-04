package sate2012.avatar.android.augmentedrealityview;
//package sate2012.avatar.android;
//
//
//import java.io.File;
//
//import mil.nga.giat.dulles.moterrain.R;
//import mil.nga.giat.dulles.moterrain.MOTerrainConstants;
//import android.app.Activity;
//import android.content.Intent;
//import android.media.CamcorderProfile;
//import android.media.MediaRecorder;
//import android.os.Bundle;
//import android.os.Environment;
//import android.os.Handler;
//import android.util.Log;
//import android.view.SurfaceHolder;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.Window;
//import android.widget.Button;
//import android.widget.ImageButton;
//import android.widget.MediaController;
//import android.widget.TextView;
//import android.widget.VideoView;
//
//public class VideoCamActivity extends Activity implements
//        SurfaceHolder.Callback {
//
//    private MediaRecorder recorder = null;
//
//    private VideoView videoView = null;
//    private ImageButton startBtn = null;
//    private ImageButton playRecordingBtn = null;
//    private ImageButton returnToSubmission;
//
//    public static final String OUTPUT_FILE = "video_"
//            + System.currentTimeMillis() + ".mp4";
//    public static final String VIDEO = "VIDEO";
//    private File videoRecording;
//
//    private Boolean playing = false;
//    private Boolean recording = false;
//    
//    ///////////////////////////////
//    private TextView mVideoClockUI;
//    private Handler mHandler;
//    private int mVideoClockTime;
//    private Runnable mClockTask;
//    ////////////////////////////
//
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        // Remove title bar
//        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//
//        setContentView(R.layout.video);
//        
//        ///////////////////
//        mVideoClockTime = 0;
//        mHandler = new Handler();
//        mClockTask = new Runnable() {
//            
//            public void run() {
//                mVideoClockTime++;
//                
//                int minutes = mVideoClockTime / 60;
//                mVideoClockTime = mVideoClockTime % 60;
//                mVideoClockUI.setText(String.format("%02d:%02d", minutes, mVideoClockTime));
//                mHandler.postDelayed(this, 1000); // Every second.
//            }
//        };
//        mVideoClockUI = (TextView)findViewById(R.id.video_clock_ui);
//        ////////////////////
//
//        startBtn = (ImageButton) findViewById(R.id.bgnBtn);
//
//        playRecordingBtn = (ImageButton) findViewById(R.id.playRecordingBtn);
//        playRecordingBtn.setEnabled(false);
//
//        videoView = (VideoView) this.findViewById(R.id.videoView);
//
//        returnToSubmission = (ImageButton) findViewById(R.id.returnToForm);
//
//        final SurfaceHolder holder = videoView.getHolder();
//        holder.addCallback(this);
//        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
//
//        startBtn.setOnClickListener(new OnClickListener() {
//
//            public void onClick(View view) {
//
//                if (!VideoCamActivity.this.recording
//                        & !VideoCamActivity.this.playing) {
//                    try {
//                        beginRecording(holder);
//                        
//                        playing = false;
//                        recording = true;
////                        startBtn.setText(R.string.stop_recording);
//                        startBtn.setImageResource(R.drawable.stop_recording_video);
//                        mVideoClockUI.setVisibility(View.VISIBLE);
//                        mVideoClockUI.setText("00:00");
//                        mHandler.postDelayed(mClockTask, 1000);
//                    } catch (Exception e) {
//                        Log.e("ERROR", "Exception caught recording video.", e);
//                    }
//                } else if (VideoCamActivity.this.recording) {
//                    try {
//                        stopRecording();
//                        playing = false;
//                        recording = false;
////                        startBtn.setText(R.string.start_recording);
//                        startBtn.setImageResource(R.drawable.record_video);
//                        playRecordingBtn.setEnabled(true);
//                        playRecordingBtn.setImageResource(R.drawable.play_video);
//                        startBtn.setEnabled(false); // Don't allow any more recording in this session.
//                        startBtn.setImageResource(R.drawable.record_video_greyscale);
//                        mHandler.removeCallbacks(mClockTask);
//                        mVideoClockUI.setVisibility(View.INVISIBLE);
//                    } catch (Exception e) {
//                        Log.e("ERROR", "Exception caught stopping recording.",
//                                e);
//                    }
//                }
//            }
//        });
//
//        playRecordingBtn.setOnClickListener(new OnClickListener() {
//
//            public void onClick(View view) {
//                if (!VideoCamActivity.this.playing
//                        & !VideoCamActivity.this.recording) {
//                    try {
//                        playRecording();
//                        VideoCamActivity.this.playing = true;
//                        VideoCamActivity.this.recording = false;
//                        playRecordingBtn.setEnabled(false);
//                        playRecordingBtn.setImageResource(R.drawable.play_video_greyscale);
//                    } catch (Exception e) {
//                        Log.e("ERROR", "Exception caught playing video.", e);
//                    }
//                } else if (VideoCamActivity.this.playing) {
//                    try {
//                        stopPlayingRecording();
//                        VideoCamActivity.this.playing = false;
//                        VideoCamActivity.this.recording = false;
////                        playRecordingBtn.setText(R.string.play_recording);
//                    } catch (Exception e) {
//                        Log.e("ERROR", "Exception caught stopping play.", e);
//                    }
//                }
//
//            }
//        });
//
//        returnToSubmission.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                Intent data = new Intent();
//                data.putExtra(VIDEO, getVideoRecording());
//                if (getVideoRecording() != null) {
//                    setResult(Activity.RESULT_OK, data);
//                } else {
//                    setResult(Activity.RESULT_CANCELED);
//
//                }
//                finish();
//            }
//        });
//
//    }
//
//    public void surfaceCreated(SurfaceHolder holder) {
//        startBtn.setEnabled(true);
//    }
//
//    public void surfaceDestroyed(SurfaceHolder holder) {
//    }
//
//    public void surfaceChanged(SurfaceHolder holder, int format, int width,
//            int height) {
//        Log.i("INFO", "Width x Height = " + width + "x" + height);
//    }
//
//    private void playRecording() {
//        MediaController mc = new MediaController(this);
//        videoView.setMediaController(mc);
//        videoView.setVideoPath(videoRecording.getAbsolutePath());
//        videoView.start();
//    }
//
//    private void stopPlayingRecording() {
//        videoView.stopPlayback();
//    }
//
//    private void stopRecording() throws Exception {
//        if (recorder != null) {
//            recorder.stop();
//            setVideoRecording(videoRecording);
//        }
//    }
//
//    protected void onDestroy() {
//        super.onDestroy();
//        if (recorder != null) {
//            recorder.release();
//        }
//        mHandler.removeCallbacks(mClockTask);
//    }
//
//    private void beginRecording(SurfaceHolder holder) throws Exception {
//        if (recorder != null) {
//            recorder.stop();
//            recorder.release();
//        }
//
//        videoRecording = new File(Environment.getExternalStorageDirectory(),
//                MOTerrainConstants.MOTERRAIN_STORAGE_DIRECTORY
//                        + MOTerrainConstants.MOTERRAIN_MEDIA_DIRECTORY + OUTPUT_FILE);
//        if (videoRecording.exists()) {
//            videoRecording.delete();
//        }
//
//        try {
//            recorder = new MediaRecorder();
//
//            recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
//            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//            recorder.setProfile(CamcorderProfile
//                    .get(CamcorderProfile.QUALITY_LOW));
//            recorder.setMaxDuration(20000);
//            recorder.setOutputFile(videoRecording.getAbsolutePath());
//            recorder.setPreviewDisplay(holder.getSurface());
//            recorder.prepare();
//            recorder.start();
//        } catch (Exception e) {
//            Log.e("ERROR", "Exception caught creating media recorder.", e);
//        }
//    }
//
//    /**
//     * @return the videoRecording
//     */
//    public File getVideoRecording() {
//        return this.videoRecording;
//    }
//
//    /**
//     * @param videoRecording
//     *            the videoRecording to set
//     */
//    public void setVideoRecording(File videoRecording) {
//        this.videoRecording = videoRecording;
//    }
//
//}
