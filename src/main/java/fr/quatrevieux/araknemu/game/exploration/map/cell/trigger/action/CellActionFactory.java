package fr.quatrevieux.araknemu.game.exploration.map.cell.trigger.action;

import fr.quatrevieux.araknemu.data.world.entity.environment.MapTrigger;

/**
 * Factory for cell action
 */
public interface CellActionFactory {
    /**
     * Create the action from the map trigger
     */
    public CellAction create(MapTrigger trigger);
}
