package exception;

/**
 * Created by Wout Slabbinck on 31/03/2016.
 */
public class HiveException extends Exception {

    private ErrorCode reason;

    public HiveException (String msg){
        super(msg);
        this.reason = ErrorCode.UNSPECIFIED_ERROR;
    }

    public HiveException(ErrorCode code) {
        super(code.name());
        this.reason =code;
    }

    public ErrorCode getReason() {
        return reason;
    }
}