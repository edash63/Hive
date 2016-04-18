package gameview;

import java.util.Observable;

/**
 * Created by Wout Slabbinck on 17/04/16.
 */
public class HivePawnObserver implements java.util.Observer {
    private HivePawnSprite sprite;

    public HivePawnObserver(HivePawnSprite sprite) {
        this.sprite = sprite;
    }

    @Override
    public void update(Observable observable, Object object) {
        sprite.move();
    }

}
