package se.viktorc.a7117_app;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by viktor on 2017-03-31.
 */

public class Menu {

    private boolean showing = false;
    private ArrayList<Button> buttons = new ArrayList<Button>();
    private ImageView image;

    public Menu(ImageView image_) {
        image = image_;
    }

    public void put(Button button) {
        buttons.add(button);
    }

    public void update() {
        if(showing)
            hide();
        else
            show();

        showing = !showing;
    }

    private void hide() {
        image.setImageResource(R.drawable.collapsed);
        for(Button b : buttons) {
            b.setVisibility(View.GONE);
        }
    }

    private void show() {
        image.setImageResource(R.drawable.expanded);
        for(Button b : buttons) {
            b.setVisibility(View.VISIBLE);
        }
    }

    public void buildImage() {

    }

}