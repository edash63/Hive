package gamemodel.pawn;

import exception.HiveException;
import gamemodel.BoardPosition;

import java.util.Set;

/**
 * Created by Wout Slabbinck on 31/03/2016.
 */
public class HPAnt extends HivePawn {
    public HPAnt(char color, int number) {
        super(color, 'A', number);
    }

    @Override
    public Set<BoardPosition> destinations() throws HiveException {
        return position.allSteps();
    }
}
