package gamemodel.pawn;

import exception.HiveException;
import gamemodel.BoardPosition;

import java.util.Set;

/**
 * Created by Wout Slabbinck on 31/03/2016.
 */
public class HPGrasshopper extends HivePawn {
    public HPGrasshopper(char color, int number) {
        super(color, 'G', number);
    }

    @Override
    public Set<BoardPosition> destinations() throws HiveException {
        return position.grasshopperSteps();
    }

}
