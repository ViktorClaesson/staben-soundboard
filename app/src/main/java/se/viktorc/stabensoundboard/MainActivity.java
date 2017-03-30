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

public class MainActivity extends AppCompatActivity {

    private boolean playing = false;

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
                button.setText(name[1] + " " + name[2]);
                button.setOnClickListener(onClick(field.getInt(field)));
                return button;
            } else {
                return null;
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
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
