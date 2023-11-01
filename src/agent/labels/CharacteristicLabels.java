package agent.labels;

/**
 * RB = Robot
 * | s1 | s4 | s6|
 * | s2 | RB | s7|
 * | s3 | s5 | s8|
 */
public enum CharacteristicLabels {
    WALL_NW, // S1
    NOT_WALL_NW, // NOT S1
    WALL_W, // S2
    NOT_WALL_W, // NOT S2
    WALL_SW, // S3
    NOT_WALL_SW, // NOT S3
    WALL_N, // S4
    NOT_WALL_N, // NOT S4
    WALL_S, // S5
    NOT_WALL_S, // NOT S5
    WALL_NE, // S6
    NOT_WALL_NE, // NOT S6
    WALL_E, // S7
    NOT_WALL_E, // NOT S7
    WALL_SE, // S8
    NOT_WALL_SE, // NOT S8
    LOOKING_NORTH, // looking = NORTE
    LOOKING_EAST, // looking = ESTE
    LOOKING_SOUTH, // looking = SUR
    LOOKING_WEST // looking = OESTE
}
