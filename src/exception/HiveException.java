package exception;

/**
 * Created by Wout Slabbinck on 31/03/2016.
 */
public class HiveException extends Exception {
    public final static int ERROR_EMPTY_MOVE = 1;
    public final static int ERROR_INVALID_PAWN = 2;
    public final static int ERROR_INVALID_POSITION_CHARACTER = 3;
    public final static int ERROR_INVALID_DESTINATION_POSITION = 4;

    public HiveException(String message) {
        super(message);
    }
    public HiveException(int errorNr) { super("Error " + errorNr); }
}