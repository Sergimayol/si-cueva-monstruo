package agent.labels;

import java.awt.Point;

import agent.Executable;
import agent.Robot;

public enum Action implements Executable {
    MOVE_NORTH {
        @Override
        public void execute(Object robot) {
            if (!assertIsRobot(robot)) {
                return;
            }
            ((Robot) robot).setLooking(LookingAt.NORTH);
            Point currPosition = ((Robot) robot).getPosition();
            ((Robot) robot).setPosition(currPosition.x - 1, currPosition.y);
        }
    },
    MOVE_SOUTH {
        @Override
        public void execute(Object robot) {
            if (!assertIsRobot(robot)) {
                return;
            }
            ((Robot) robot).setLooking(LookingAt.SOUTH);
            Point currPosition = ((Robot) robot).getPosition();
            ((Robot) robot).setPosition(currPosition.x + 1, currPosition.y);
        }
    },
    MOVE_EAST {
        @Override
        public void execute(Object robot) {
            if (!assertIsRobot(robot)) {
                return;
            }
            ((Robot) robot).setLooking(LookingAt.EAST);
            Point currPosition = ((Robot) robot).getPosition();
            ((Robot) robot).setPosition(currPosition.x, currPosition.y + 1);
        }
    },
    MOVE_WEST {
        @Override
        public void execute(Object robot) {
            if (!assertIsRobot(robot)) {
                return;
            }
            ((Robot) robot).setLooking(LookingAt.WEST);
            Point currPosition = ((Robot) robot).getPosition();
            ((Robot) robot).setPosition(currPosition.x, currPosition.y - 1);
        }
    },
    DO_NOTHING {
        @Override
        public void execute(Object robot) {
            // Do nothing
        }
    };

    private static boolean assertIsRobot(Object obj) {
        return obj instanceof Robot;
    }
}
