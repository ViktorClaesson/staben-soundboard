package se.viktorc.a7117_app;

import android.support.v7.app.AppCompatActivity;

import android.content.res.Resources;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.TreeMap;

public class MainActivity extends AppCompatActivity {

    private boolean paused = false;
    private String prevName = null;
    private int[] bgColor = {0xfff0f0f0, 0xffe0e0e0};
    private int bgIndex = 0;

    private MediaPlayer mediaPlayer;

    private Map<String, Menu> menus;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        menus = new TreeMap<String, Menu>();
        build();
    }

    private void build() {
        LinearLayout mLayout = (LinearLayout) findViewById(R.id.layout);
        Field[] fields = R.raw.class.getFields();

        findViewById(R.id.media_control).setOnClickListener(mediaClick());

        for(Field field : fields) {
            try {
                String[] name = field.getName().split("_");
                if(name[0].equals("ljud")) {
                    buildMenu(mLayout, name[1]);
                    buildButton(mLayout, name[1], name[2], field.getInt(field));
                }
            } catch(IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private void buildMenu(LinearLayout mLayout, String name) {
        if(!name.equals(prevName)) {
            menus.get(prevName).buildImage();

            bgIndex = (bgIndex + 1) % 2;
            prevName = name;

            LinearLayout newLayout = new LinearLayout(this);
            newLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            newLayout.setOrientation(LinearLayout.HORIZONTAL);
            newLayout.setBackgroundColor(bgColor[bgIndex]);
            newLayout.setOnClickListener(menuClick(name));

            final TextView text = new TextView(this);
            text.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            text.setAllCaps(true);
            text.setTextSize(40);
            text.setGravity(Gravity.CENTER);
            text.setTypeface(Typeface.SERIF, Typeface.BOLD);
            text.setText(String.format(" %s ", name));

            final ImageView image = new ImageView(this);
            image.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
            image.setImageResource(R.drawable.collapsed);

            newLayout.addView(text);
            newLayout.addView(image);

            mLayout.addView(newLayout);

            menus.put(name, new Menu(image));
        }
    }

    private void buildButton(LinearLayout mLayout, String owner, String name, int RID) {
        final Button button = new Button(this);
        button.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        button.setText(parseString(name));
        button.setAllCaps(false);
        button.setOnClickListener(songClick(button.getText(), RID));
        button.setBackgroundColor(bgColor[bgIndex]);
        mLayout.addView(button);
        button.setVisibility(View.GONE);

        menus.get(owner).put(button);
    }

    private String parseString(String raw) {
        String s = raw.toLowerCase().replace("oo", "ö").replace("aaa", "å").replace("aa", "ä").replace("zzz", ",").replace("zz", "?").replace("z", " ");
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    private OnClickListener mediaClick() {
        return new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer != null) {
                    paused = !paused;
                    if (!paused) { // since we have to reverse it before it needs to be negated here.
                        mediaPlayer.start();
                        ((ImageButton) findViewById(R.id.media_control)).setImageResource(Resources.getSystem().getIdentifier("ic_media_pause", "drawable", "android"));
                    } else {
                        mediaPlayer.pause();
                        ((ImageButton) findViewById(R.id.media_control)).setImageResource(Resources.getSystem().getIdentifier("ic_media_play", "drawable", "android"));
                    }
                }
            }
        };
    }

    private OnClickListener menuClick(final String name) {
        return new OnClickListener() {
            @Override
            public void onClick(View v) {
                menus.get(name).update();
            }
        };
    }

    private OnClickListener songClick(final CharSequence name, final int RID) {
        return new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((TextView) findViewById(R.id.currently_playing)).setText(String.format("%s %s", getString(R.string.currently_playing), name));
                ((ImageButton) findViewById(R.id.media_control)).setImageResource(Resources.getSystem().getIdentifier("ic_media_pause", "drawable", "android"));

                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer = null;
                }

                new Thread() {
                    public void run() {
                        paused = false;
                        mediaPlayer = MediaPlayer.create(getApplicationContext(), RID);
                        mediaPlayer.start();
                        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                ((ImageButton) findViewById(R.id.media_control)).setImageResource(Resources.getSystem().getIdentifier("ic_media_play", "drawable", "android"));
                                paused = true;
                            }
                        });
                    }
                }.start();
            }
        };
    }
}