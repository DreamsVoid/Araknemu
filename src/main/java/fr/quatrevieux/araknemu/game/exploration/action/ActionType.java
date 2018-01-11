package fr.quatrevieux.araknemu.game.exploration.action;

import java.util.HashMap;
import java.util.Map;

/**
 * Enum for all game actions
 */
public enum ActionType {
    NONE(0),
    MOVE(1);

    final private int id;

    final static private Map<Integer, ActionType> actionsById = new HashMap<>();

    static {
        for (ActionType actionType : values()) {
            actionsById.put(actionType.id, actionType);
        }
    }

    ActionType(int id) {
        this.id = id;
    }

    public int id() {
        return id;
    }

    /**
     * Get an action by id
     */
    static public ActionType byId(int id) {
        return actionsById.get(id);
    }
}