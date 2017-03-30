package se.viktorc.stabensoundboard;

import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.TreeMap;

public class MainActivity extends AppCompatActivity {

    private boolean playing = false;
    private String prevName = "";
    private int[] bgColor = {0xfff0f0f0, 0xffe0e0e0};
    private int bgIndex = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LinearLayout mLayout = (LinearLayout) findViewById(R.id.layout);

        Field[] fields = R.raw.class.getFields();
        for(Field field : fields) {
            Button b = createButton(field);
            if(b != null)
                mLayout.addView(b);
        }
    }

    private Button createButton(Field field) {
        try {
            String[] name = field.getName().split("_");
            if(name[0].equals("ljud")) {
                final LayoutParams lparams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                final Button button = new Button(this);
                button.setLayoutParams(lparams);
                button.setText(name[1] + " " + parseString(name[2]));
                button.setOnClickListener(onClick(field.getInt(field)));
                if(!prevName.equals(name[1])) {
                    bgIndex = (bgIndex + 1) % 2;
                    prevName = name[1];
                }
                button.setBackgroundColor(bgColor[bgIndex]);
                return button;
            } else {
                return null;
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }

    private String parseString(String raw) {
        return raw.replace("oo", "ö").replace("aaa", "å").replace("aa", "ä").replace("z", " ");
    }

    private OnClickListener onClick(final int RID) {
        return new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!playing) {
                    new Thread() {
                        public void run() {
                            playing = true;
                            MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), RID);
                            mediaPlayer.start();
                            while (mediaPlayer.isPlaying()) ;
                            mediaPlayer.release();
                            mediaPlayer = null;
                            playing = false;
                        }
                    }.start();
                }
            }
        };
    }
}
