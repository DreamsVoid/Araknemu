package fr.quatrevieux.araknemu.game.player.inventory.slot;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.living.entity.player.PlayerItem;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.event.DefaultListenerAggregate;
import fr.quatrevieux.araknemu.game.item.ItemService;
import fr.quatrevieux.araknemu.game.player.inventory.InventoryEntry;
import fr.quatrevieux.araknemu.game.world.item.inventory.exception.InventoryException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class InventorySlotsTest extends GameBaseCase {
    private InventorySlots slots;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        slots = new InventorySlots(new DefaultListenerAggregate());
    }

    @Test
    void get() throws InventoryException {
        assertInstanceOf(DefaultSlot.class, slots.get(-1));
        assertInstanceOf(AmuletSlot.class, slots.get(0));
        assertInstanceOf(WeaponSlot.class, slots.get(1));
        assertInstanceOf(RingSlot.class, slots.get(2));
        assertInstanceOf(BeltSlot.class, slots.get(3));
        assertInstanceOf(RingSlot.class, slots.get(4));
        assertInstanceOf(BootsSlot.class, slots.get(5));
        assertInstanceOf(HelmetSlot.class, slots.get(6));
        assertInstanceOf(MantleSlot.class, slots.get(7));
        assertInstanceOf(NullSlot.class, slots.get(8));
        assertInstanceOf(DofusSlot.class, slots.get(9));
        assertInstanceOf(DofusSlot.class, slots.get(10));
        assertInstanceOf(DofusSlot.class, slots.get(11));
        assertInstanceOf(DofusSlot.class, slots.get(12));
        assertInstanceOf(DofusSlot.class, slots.get(13));
        assertInstanceOf(DofusSlot.class, slots.get(14));
    }

    @Test
    void getInvalid() {
        assertThrows(InventoryException.class, () -> slots.get(-15));
        assertThrows(InventoryException.class, () -> slots.get(123));
    }

    @Test
    void equipments() throws SQLException, ContainerException, InventoryException {
        dataSet.pushItemTemplates();

        slots.get(0).uncheckedSet(new InventoryEntry(null, new PlayerItem(0, 0, 2425, null, 1, 0), container.get(ItemService.class).create(2425)));
        slots.get(1).uncheckedSet(new InventoryEntry(null, new PlayerItem(0, 0, 2416, null, 1, 1), container.get(ItemService.class).create(2416)));

        assertCount(2, slots.equipments());
    }
}