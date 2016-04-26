package exception;

/**
 * Created by Wout Slabbinck on 31/03/2016.
 */
public class HiveException extends Exception {
    public final static int ERROR_EMPTY_MOVE = 1;
    public final static int ERROR_INVALID_PAWN = 2;
    public final static int ERROR_INVALID_POSITION_CHARACTER = 3;
    public final static int ERROR_INVALID_DESTINATION_POSITION = 4;

    private ErrorCode reason;

    public HiveException (String msg){
        super(msg);
        this.reason = ErrorCode.WRONG_MOVE;
    }
    public HiveException(ErrorCode code) {
        super(code.name());
        this.reason =code;
    }

    @Deprecated
    public HiveException(int errorNr) { super("Error " + errorNr);

    }

    public ErrorCode getReason() {
        return reason;
    }
}