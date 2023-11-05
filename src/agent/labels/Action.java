package agent.labels;

import agent.Executable;
import agent.Explorer;

public enum Action implements Executable {
    MOVE_NORTH {
        @Override
        public void execute(Object explorer) {
            // Do nothing
        }
    },
    MOVE_SOUTH {
        @Override
        public void execute(Object explorer) {
            // Do nothing
        }
    },
    MOVE_EAST {
        @Override
        public void execute(Object explorer) {
            // Do nothing
        }
    },
    MOVE_WEST {
        @Override
        public void execute(Object explorer) {
            // Do nothing
        }
    },
    SHOOT_NORTH {
        @Override
        public void execute(Object explorer) {
            // Do nothing
        }
    },
    SHOOT_SOUTH {
        @Override
        public void execute(Object explorer) {
            // Do nothing
        }
    },
    SHOOT_EAST {
        @Override
        public void execute(Object explorer) {
            // Do nothing
        }
    },
    SHOOT_WEST {
        @Override
        public void execute(Object explorer) {
            // Do nothing
        }
    },
    BRIDGE_NORTH {
        @Override
        public void execute(Object explorer) {
            // Do nothing
        }
    },
    BRIDGE_SOUTH {
        @Override
        public void execute(Object explorer) {
            // Do nothing
        }
    },
    BRIDGE_EAST {
        @Override
        public void execute(Object explorer) {
            // Do nothing
        }
    },
    BRIDGE_WEST {
        @Override
        public void execute(Object explorer) {
            // Do nothing
        }
    },
    TAKE_TREASURE {
        @Override
        public void execute(Object explorer) {
            // Do nothing
        }
    },
    RETURN_HOME {
        @Override
        public void execute(Object explorer) {
            // Do nothing
        }
    },
    NOT_MOVE {
        @Override
        public void execute(Object explorer) {
            // Do nothing
        }
    };

    private static boolean assertIsRobot(Object obj) {
        return obj instanceof Explorer;
    }
}
