package se.viktorc.a7117_app;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by viktor on 2017-03-31.
 */

public class Menu {

    private boolean showing = false;
    private ArrayList<TextView> buttons = new ArrayList<TextView>();
    private ArrayList<Drawable> images = new ArrayList<Drawable>();
    private TextView text;
    private String name;
    private int counter = -1;

    public Menu(TextView text_) {
        text = text_;
    }

    public void put(TextView button) {
        buttons.add(button);
    }

    public void put(Drawable image) {
        images.add(image);
    }

    public void update() {
        if(showing)
            hide();
        else
            show();

        showing = !showing;
    }

    private void hide() {
        text.setText(text.getText().subSequence(0, text.length() - 2) + " " + ((char) 0x21F2));
        for(TextView b : buttons) {
            b.setVisibility(View.GONE);
        }
    }

    private void show() {
        text.setText(text.getText().subSequence(0, text.length() - 2) + " " + ((char) 0x21F1));
        for(TextView b : buttons) {
            b.setVisibility(View.VISIBLE);
        }
    }

    public Drawable nextImage() {
        counter = (counter + 1) % images.size();
        return images.get(counter);
    }

}