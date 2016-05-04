package gamemodel.pawn;

import exception.HiveException;
import gamemodel.BoardPosition;

import java.util.Set;

/**
 * Created by Wout Slabbinck on 31/03/2016.
 */
public class HPBeetle extends HivePawn {
    public HPBeetle(char color, int number) {
        super(color, 'B', number);
    }

    @Override
    public Set<BoardPosition> destinations() throws HiveException {
        return position.step();
    }
}
