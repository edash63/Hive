package pawn;

/**
 * Created by Wout Slabbinck on 31/03/2016.
 */
public class HPQueen extends HivePawn{
    public HPQueen(char color) {
        super(color, 'Q', 1);
        description = description.substring(0, 2);
    }
}
