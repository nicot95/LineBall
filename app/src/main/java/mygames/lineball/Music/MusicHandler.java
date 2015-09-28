package mygames.lineball.Music;

import android.content.Context;
import android.media.MediaPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import mygames.lineball.R;

public class MusicHandler {

    private static MusicHandler instance = null;

    private MediaPlayer gameBackgroundSong;
    private MediaPlayer menuBackgroundSong;
    private MediaPlayer gameOver;
    private MediaPlayer fiveSecsLeft;


    private MediaPlayer shape1;
    private MediaPlayer shape2;

    private List<MediaPlayer> allSounds;

    private boolean muted = false;

    private Random gen;

    private MusicHandler(Context context) {
        updateContext(context);

        menuBackgroundSong.setVolume(0.3f, 0.3f);
        gameBackgroundSong.setVolume(0.5f, 0.5f);
        fiveSecsLeft.setVolume(0.7f, 0.7f);
        this.shape1.setVolume(0.5f, 0.5f);
        this.shape2.setVolume(0.5f, 0.5f);

        menuBackgroundSong.setLooping(true);
        gameBackgroundSong.setLooping(true);

        this.gen            = new Random();

    }

    public static MusicHandler getInstance(Context context) {
        if (instance == null) {
            instance = new MusicHandler(context);
        }

        return instance;

    }

    public void updateContext(Context context) {
        this.gameBackgroundSong = MediaPlayer.create(context, R.raw.pamgea);
        this.menuBackgroundSong = MediaPlayer.create(context, R.raw.builder);
        this.gameOver = MediaPlayer.create(context, R.raw.game_over);
        this.fiveSecsLeft = MediaPlayer.create(context, R.raw.timer);
        this.shape1         = MediaPlayer.create(context, R.raw.shape1short);
        this.shape2         = MediaPlayer.create(context, R.raw.shape2short);
        this.allSounds = new ArrayList<MediaPlayer>();
        allSounds.add(gameBackgroundSong);
        allSounds.add(menuBackgroundSong);
        allSounds.add(gameOver);
        allSounds.add(fiveSecsLeft);
        allSounds.add(shape1);
        allSounds.add(shape2);

        if(muted) {
            for(MediaPlayer mp: allSounds) {
                mp.setVolume(0,0);
            }
        }
    }

    public void muteOrUnmute() {
        float newVolume = muted ? 1 : 0;

        for(MediaPlayer mp : allSounds) {
            mp.setVolume(newVolume, newVolume);
        }
        muted = muted ? false : true;
    }

    public void playGameBackgroundMusic() {

        gameBackgroundSong.start();
    }

    public void playMenuBackgroundMusic() {
        menuBackgroundSong.start();}

    public void playGameOverMusic() { gameOver.start();}

    public void playFiveSecsLeftMusic() {
        fiveSecsLeft.start();
    }



    public void playShapeCompleted() {
        //Stops mediaplayers so no overlap occurs.
        if (shape1.isPlaying()) {
            shape2.start();
            return;
        }
        if (shape2.isPlaying()){
            shape1.start();
            return;
        }

        boolean random = gen.nextBoolean();
        if (random) {
            shape1.start();
        } else {
            shape2.start();
        }
    }

    public void stopMusic() {
        if (gameBackgroundSong.isPlaying()) {
            gameBackgroundSong.stop();
            //gameBackgroundSong.release();
        }
    }

    public void stopMenuMusic() {
        if (menuBackgroundSong.isPlaying()) {
            //menuBackgroundSong.pause();
            menuBackgroundSong.stop();
            //menuBackgroundSong.release();
        }
    }

    public void stopTimer() {
        if(fiveSecsLeft.isPlaying()) {
            fiveSecsLeft.stop();
        }
    }

}
