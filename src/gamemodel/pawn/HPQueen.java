package gamemodel.pawn;

import exception.HiveException;
import gamemodel.BoardPosition;

import java.util.Set;

/**
 * Created by Wout Slabbinck on 31/03/2016.
 */
public class HPQueen extends HivePawn {
    public HPQueen(char color) {
        super(color, 'Q', 1);
    }

    @Override
    public Set<BoardPosition> destinations() throws HiveException {
        return position.step();
    }
}
