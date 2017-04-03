package se.viktorc.a7117_app;

import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.TreeMap;

public class MainActivity extends AppCompatActivity {

    private boolean paused = false;
    private String prevName = null;

    private int colorIndex = 1;
    private int[] colors = { R.color.rosa, R.color.lila };

    private MediaPlayer mediaPlayer;

    private Map<String, Menu> menus;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        ConstraintLayout root = (ConstraintLayout) findViewById(R.id.layout_root);
        root.setBackground(getDrawable(R.drawable.p_bibbi1));

        menus = new TreeMap<String, Menu>();
        build();
        try {
            for (Field f : R.drawable.class.getFields()) {
                String[] op = f.getName().split("_");
                if (op[0].equals("p")) {
                    System.out.println("Adding " + op[1]);
                    menus.get(op[1].substring(0, op[1].length() - 1)).put(getDrawable(f.getInt(f)));
                }
                System.gc();
            }
        } catch(IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void build() {
        LinearLayout mLayout = (LinearLayout) findViewById(R.id.layout_list);
        Field[] fields = R.raw.class.getFields();

        findViewById(R.id.media_control).setOnClickListener(mediaClick());

        for(Field field : fields) {
            try {
                String[] name = field.getName().split("_");
                if(name[0].equals("ljud")) {
                    buildMenu(mLayout, name[1]);
                    buildButton(mLayout, name[1], parseString(name[2]), field.getInt(field));
                }
            } catch(IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private void buildMenu(LinearLayout mLayout, String name) {
        if(!name.equals(prevName)) {
            colorIndex = (colorIndex + 1) % 2;
            prevName = name;

            LinearLayout newLayout = new LinearLayout(this);
            newLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            newLayout.setOrientation(LinearLayout.HORIZONTAL);
            newLayout.setOnClickListener(menuClick(name));

            final TextView text = new TextView(this);
            text.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            text.setAllCaps(true);
            text.setTextSize(40);
            text.setGravity(Gravity.CENTER_VERTICAL);
            text.setTypeface(Typeface.SERIF, Typeface.BOLD);
            text.setText(String.format(" %s " + ((char) 0x21F2), parseString(name)));
            text.setTextColor(ContextCompat.getColor(this, colors[colorIndex]));
            text.setShadowLayer(1.5f, 3, 3, Color.BLACK);

            newLayout.addView(text);

            mLayout.addView(newLayout);

            menus.put(name, new Menu(text));
        }
    }

    private void buildButton(LinearLayout mLayout, String owner, String name, int RID) {
        final TextView button = new TextView(this);
        button.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        button.setOnClickListener(songClick(owner, name, RID));
        //button.setBackgroundColor(bgColor[bgIndex]);
        button.setText("   > " + name);
        button.setTextColor(ContextCompat.getColor(this, colors[colorIndex]));
        button.setTextSize(32);
        button.setShadowLayer(4f, 3, 3, Color.BLACK);
        button.setGravity(Gravity.CENTER_VERTICAL);
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
                        ((ImageButton) findViewById(R.id.media_control)).setImageResource(R.drawable.media_pause);
                    } else {
                        mediaPlayer.pause();
                        ((ImageButton) findViewById(R.id.media_control)).setImageResource(R.drawable.media_play);
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

    private OnClickListener songClick(final String owner, final CharSequence name, final int RID) {
        return new OnClickListener() {

            @Override
            public void onClick(View v) {
                ((TextView) findViewById(R.id.currently_playing)).setText(String.format("%s %s", getString(R.string.currently_playing), name));
                ((ImageButton) findViewById(R.id.media_control)).setImageResource(R.drawable.media_pause);
                ((ConstraintLayout) findViewById(R.id.layout_root)).setBackground(menus.get(owner).nextImage());

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
                                ((ImageButton) findViewById(R.id.media_control)).setImageResource(R.drawable.media_play);
                                paused = true;
                            }
                        });
                    }
                }.start();
            }
        };
    }
}