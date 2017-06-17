package bredesh.medico.Menu;

import android.content.Intent;

/**
 * Created by Omri on 01/06/2017.
 */

public class Item_Menu {

    int image_src;
    String text;
    Intent nextScreen;
    MainMenu.MODE mode;
    int backgroundId;

    public Item_Menu(int image_src, String text, Intent nextScreen, MainMenu.MODE mode, int backgroundId) {
        this.image_src = image_src;
        this.text = text;
        this.nextScreen = nextScreen;
        this.mode = mode;
        this.backgroundId = backgroundId;
    }


}
