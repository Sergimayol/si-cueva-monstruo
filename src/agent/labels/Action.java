package agent.labels;

import agent.Executable;
import agent.Explorer;
import utils.RichPoint;

public enum Action implements Executable<Explorer> {
    MOVE_NORTH {
        @Override
        public void execute(Explorer explorer) {
            explorer.move(-1, 0);
        }
    },
    MOVE_SOUTH {
        @Override
        public void execute(Explorer explorer) {
            explorer.move(1, 0);
        }
    },
    MOVE_EAST {
        @Override
        public void execute(Explorer explorer) {
            explorer.move(0, 1);
        }
    },
    MOVE_WEST {
        @Override
        public void execute(Explorer explorer) {
            explorer.move(0, -1);
        }
    },
    SHOOT_NORTH {
        @Override
        public void execute(Explorer explorer) {
            explorer.shootMonster(-1, 0);
        }
    },
    SHOOT_SOUTH {
        @Override
        public void execute(Explorer explorer) {
            explorer.shootMonster(1, 0);
        }
    },
    SHOOT_EAST {
        @Override
        public void execute(Explorer explorer) {
            explorer.shootMonster(0, 1);
        }
    },
    SHOOT_WEST {
        @Override
        public void execute(Explorer explorer) {
            explorer.shootMonster(0, -1);
        }
    },
    BRIDGE_NORTH {
        @Override
        public void execute(Explorer explorer) {
            explorer.putBridge(-1, 0);
        }
    },
    BRIDGE_SOUTH {
        @Override
        public void execute(Explorer explorer) {
            explorer.putBridge(1, 0);
        }
    },
    BRIDGE_EAST {
        @Override
        public void execute(Explorer explorer) {
            explorer.putBridge(0, 1);
        }
    },
    BRIDGE_WEST {
        @Override
        public void execute(Explorer explorer) {
            explorer.putBridge(0, -1);
        }
    },
    TAKE_TREASURE {
        @Override
        public void execute(Explorer explorer) {
            explorer.takeTreasure();
        }
    },
    RETURN_HOME {
        @Override
        public void execute(Explorer explorer) {
            Explorer currExplorer = explorer;

            if (currExplorer.getDisplacement().x == 0 && currExplorer.getDisplacement().y == 0) {
                currExplorer.finished();
                return;
            }

            if (currExplorer.getMovemementsToHome() == null) {
                currExplorer.setMovemementsToHome(
                        currExplorer.calculateActionsToHome(
                                new RichPoint(currExplorer.getDisplacement()),
                                new RichPoint(0, 0)));

            }

            if (!currExplorer.getMovemementsToHome().isEmpty()) {
                RichPoint movement = currExplorer.getMovemementsToHome().remove(0);
                currExplorer.move(movement.x, movement.y);
            }

        }
    },
    NOT_MOVE {
        @Override
        public void execute(Explorer explorer) {
            // Do nothing
        }
    };

}
