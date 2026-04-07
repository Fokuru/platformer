package gamelogic.clientHandling;

import java.util.ArrayList;

public class Information {
    Integer myX;
    Integer myY;
    Integer myHitX;
    Integer myHitY;

    public Information(int visX, int visY, int hitX, int hitY) {
        myX = visX;
        myY = visY;
        myHitX = hitX;
        myHitY = hitY;
    }

    public void changeInfo(int visX, int visY, int hitX, int hitY) {
        myX = visX;
        myY = visY;
        myHitX = hitX;
        myHitY = hitY;
    }

    public ArrayList<Integer> getData() {
        ArrayList<Integer> ret = new ArrayList<>();
        ret.add(myX);
        ret.add(myY);
        ret.add(myHitX);
        ret.add(myHitY);

        return ret;
    }
}
