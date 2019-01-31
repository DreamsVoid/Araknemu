package fr.quatrevieux.araknemu.game.player.inventory.slot.constraint;

import fr.quatrevieux.araknemu.game.item.Item;
import fr.quatrevieux.araknemu.game.item.inventory.exception.BadLevelException;
import fr.quatrevieux.araknemu.game.item.inventory.exception.InventoryException;
import fr.quatrevieux.araknemu.game.player.GamePlayer;

/**
 * Check the player level for equip the item
 */
final public class EquipmentLevelConstraint implements SlotConstraint {
    final private GamePlayer player;

    public EquipmentLevelConstraint(GamePlayer player) {
        this.player = player;
    }

    @Override
    public void check(Item item, int quantity) throws InventoryException {
        if (item.template().level() > player.properties().experience().level()) {
            throw new BadLevelException(item.template().level());
        }
    }
}
