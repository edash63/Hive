package exception;

/**
 * Created by EH053 on 26/04/2016.
 */
public enum ErrorCode {
    NO_ERROR,
    UNSPECIFIED_ERROR,
    EMPTY_MOVE,
    INVALID_PAWN_COLOR,
    INVALID_PAWN_DESCRIPTION,
    INVALID_DESTINATION_POSITION,
    MISSING_DESTINATION_AND_NOT_FIRST_LINE,
    INVALID_POSITIONING_CHARACTER,
    DESTINATION_REFERENCES_UNPLACED_PAWN,
    DESTINATION_ALREADY_OCCUPIED,
    BOARD_OVERFLOW
}
