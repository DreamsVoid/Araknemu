package fr.quatrevieux.araknemu.game.player.inventory;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.data.constant.Effect;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.event.inventory.EquipmentChanged;
import fr.quatrevieux.araknemu.game.event.inventory.ObjectQuantityChanged;
import fr.quatrevieux.araknemu.game.event.listener.player.SendStats;
import fr.quatrevieux.araknemu.game.item.ItemService;
import fr.quatrevieux.araknemu.game.player.characteristic.SpecialEffects;
import fr.quatrevieux.araknemu.game.player.inventory.itemset.PlayerItemSet;
import fr.quatrevieux.araknemu.game.player.inventory.slot.AmuletSlot;
import fr.quatrevieux.araknemu.game.player.inventory.slot.BootsSlot;
import fr.quatrevieux.araknemu.game.player.inventory.slot.HelmetSlot;
import fr.quatrevieux.araknemu.game.world.item.Item;
import fr.quatrevieux.araknemu.game.world.item.inventory.exception.BadLevelException;
import fr.quatrevieux.araknemu.game.world.item.inventory.exception.InventoryException;
import fr.quatrevieux.araknemu.game.world.item.inventory.exception.ItemNotFoundException;
import fr.quatrevieux.araknemu.game.world.item.inventory.exception.MoveException;
import fr.quatrevieux.araknemu.network.game.out.object.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

public class FunctionalTest extends GameBaseCase {
    private PlayerInventory inventory;
    private ItemService itemService;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet
            .pushItemTemplates()
            .pushItemSets()
        ;

        itemService = container.get(ItemService.class);
        inventory = gamePlayer(true).inventory();
        requestStack.clear();

        // remove indirect inventory listeners
        gamePlayer().dispatcher().remove(SendStats.class);
    }

    @Test
    void addNewItem() throws InventoryException {
        Item item = itemService.create(284);

        InventoryEntry entry = inventory.add(item);

        assertEquals(-1, entry.position());
        assertEquals(1, entry.quantity());
        assertSame(item, entry.item());
        assertSame(entry, inventory.get(entry.id()));

        requestStack.assertLast(
            new AddItem(entry)
        );
    }

    @Test
    void addManyItems() throws InventoryException {
        Item item = itemService.create(284);

        InventoryEntry entry = inventory.add(item, 10);

        assertEquals(-1, entry.position());
        assertEquals(10, entry.quantity());
        assertSame(item, entry.item());
        assertSame(entry, inventory.get(entry.id()));

        requestStack.assertLast(
            new AddItem(entry)
        );
    }

    @Test
    void addMultipleItems() throws InventoryException {
        List<InventoryEntry> entries = Arrays.asList(
            inventory.add(itemService.create(2411)),
            inventory.add(itemService.create(2414)),
            inventory.add(itemService.create(2416))
        );

        assertIterableEquals(entries, inventory);

        requestStack.assertAll(
            new AddItem(entries.get(0)),
            new AddItem(entries.get(1)),
            new AddItem(entries.get(2))
        );
    }

    @Test
    void moveBadQuantity() throws InventoryException {
        InventoryEntry entry = inventory.add(itemService.create(2411));
        requestStack.clear();

        assertThrows(InventoryException.class, () -> entry.move(6, 3));

        requestStack.assertEmpty();
        assertEquals(-1, entry.position());
        assertEquals(1, entry.quantity());
    }

    @Test
    void moveBadSlot() throws InventoryException {
        InventoryEntry entry = inventory.add(itemService.create(2411));
        requestStack.clear();

        assertThrows(InventoryException.class, () -> entry.move(1, 1));

        requestStack.assertEmpty();
        assertEquals(-1, entry.position());
        assertEquals(1, entry.quantity());
    }

    @Test
    void moveSlotFull() throws InventoryException {
        inventory.add(itemService.create(39), 1, 0);
        InventoryEntry entry = inventory.add(itemService.create(2425));
        requestStack.clear();

        assertThrows(InventoryException.class, () -> entry.move(0, 1));

        requestStack.assertEmpty();
        assertEquals(-1, entry.position());
        assertEquals(1, entry.quantity());
    }

    @Test
    void moveSuccess() throws InventoryException {
        InventoryEntry entry = inventory.add(itemService.create(2425));
        requestStack.clear();

        entry.move(0, 1);

        requestStack.assertLast(new ItemPosition(entry));
        assertEquals(0, entry.position());
        assertEquals(1, entry.quantity());
    }

    @Test
    void moveSuccessFromStackedItem() throws InventoryException {
        InventoryEntry entry = inventory.add(itemService.create(39), 10);
        requestStack.clear();

        entry.move(0, 1);

        InventoryEntry newEntry = inventory.get(2);

        requestStack.assertAll(
            new AddItem(newEntry),
            new ItemQuantity(entry)
        );

        assertEquals(-1, entry.position());
        assertEquals(9, entry.quantity());

        assertEquals(0, newEntry.position());
        assertEquals(1, newEntry.quantity());
    }

    @Test
    void equipItem() throws InventoryException, SQLException, ContainerException {
        InventoryEntry entry = inventory.add(itemService.create(2425, true));
        requestStack.clear();

        AtomicReference<EquipmentChanged> ref = new AtomicReference<>();
        gamePlayer().dispatcher().add(EquipmentChanged.class, ref::set);

        entry.move(0, 1);

        assertNotNull(ref.get(), "EquipmentChanged should be dispatched");
        assertSame(entry, ref.get().entry());

        assertEquals(10, gamePlayer().characteristics().stuff().get(Characteristic.INTELLIGENCE));
        assertEquals(10, gamePlayer().characteristics().stuff().get(Characteristic.STRENGTH));
    }

    @Test
    void equipItemBadLevel() throws InventoryException, SQLException, ContainerException {
        dataSet.pushHighLevelItems();

        InventoryEntry entry = inventory.add(itemService.create(112425, true));
        requestStack.clear();

        assertThrows(BadLevelException.class, () -> entry.move(0, 1));

        assertEquals(-1, entry.position());
    }

    @Test
    void unequipItem() throws InventoryException, SQLException, ContainerException {
        InventoryEntry entry = inventory.add(itemService.create(2425, true), 1, 0);
        requestStack.clear();

        AtomicReference<EquipmentChanged> ref = new AtomicReference<>();
        gamePlayer().dispatcher().add(EquipmentChanged.class, ref::set);

        entry.move(-1, 1);

        assertNotNull(ref.get(), "EquipmentChanged should be dispatched");
        assertSame(entry, ref.get().entry());

        assertEquals(0, gamePlayer().characteristics().stuff().get(Characteristic.INTELLIGENCE));
        assertEquals(0, gamePlayer().characteristics().stuff().get(Characteristic.STRENGTH));
    }

    @Test
    void equipAccessoryItemOnExploration() throws InventoryException, SQLException, ContainerException {
        explorationPlayer();

        InventoryEntry entry = inventory.add(itemService.create(2411));
        requestStack.clear();

        entry.move(HelmetSlot.SLOT_ID, 1);

        requestStack.assertAll(
            new UpdateItemSet(inventory.itemSets().get(entry.item().set().get())),
            new SpriteAccessories(gamePlayer().id(), inventory.accessories()),
            new ItemPosition(entry)
        );
    }

    @Test
    void equipNotAccessoryItemOnExploration() throws InventoryException, SQLException, ContainerException {
        explorationPlayer();

        InventoryEntry entry = inventory.add(itemService.create(39));
        requestStack.clear();

        entry.move(AmuletSlot.SLOT_ID, 1);

        requestStack.assertAll(
            new ItemPosition(entry)
        );
    }

    @Test
    void addBadPosition() {
        assertThrows(InventoryException.class, () -> inventory.add(itemService.create(2425), 1, 6), "Cannot add this item to this slot");
    }

    @Test
    void deleteSimpleItem() throws InventoryException {
        InventoryEntry entry = inventory.add(itemService.create(2425));
        requestStack.clear();

        inventory.delete(entry);

        assertThrows(ItemNotFoundException.class, () -> inventory.get(entry.id()));
        requestStack.assertAll(
            new DestroyItem(entry)
        );
    }

    @Test
    void deleteEquipedItem() throws InventoryException, SQLException, ContainerException {
        InventoryEntry entry = inventory.add(itemService.create(2425), 1, 0);
        requestStack.clear();


        AtomicReference<EquipmentChanged> ref = new AtomicReference<>();
        gamePlayer().dispatcher().add(EquipmentChanged.class, ref::set);

        inventory.delete(entry);

        requestStack.assertAll(
            new UpdateItemSet(inventory.itemSets().get(entry.item().set().get())),
            new DestroyItem(entry)
        );

        assertSame(entry, ref.get().entry());
        assertFalse(ref.get().equiped());
        assertEquals(0, ref.get().slot());
    }

    @Test
    void deleteAccessory() throws InventoryException, SQLException, ContainerException {
        explorationPlayer();

        InventoryEntry entry = inventory.add(itemService.create(2411), 1, 6);
        requestStack.clear();

        inventory.delete(entry);

        requestStack.assertAll(
            new UpdateItemSet(inventory.itemSets().get(entry.item().set().get())),
            new SpriteAccessories(gamePlayer().id(), inventory.accessories()),
            new DestroyItem(entry)
        );
    }

    @Test
    void removeItem() throws InventoryException {
        InventoryEntry entry = inventory.add(itemService.create(2411), 10, -1);
        requestStack.clear();

        entry.remove(3);

        assertEquals(7, entry.quantity());

        requestStack.assertLast(
            new ItemQuantity(entry)
        );
    }

    @Test
    void removeEntry() throws InventoryException {
        InventoryEntry entry = inventory.add(itemService.create(2411), 10, -1);
        requestStack.clear();

        entry.remove(10);

        assertEquals(0, entry.quantity());

        requestStack.assertLast(
            new DestroyItem(entry)
        );
    }

    @Test
    void addWillStack() throws InventoryException {
        InventoryEntry entry = inventory.add(itemService.create(2411, true));
        requestStack.clear();

        assertSame(entry, inventory.add(itemService.create(2411, true)));
        assertEquals(2, entry.quantity());

        requestStack.assertLast(
            new ItemQuantity(entry)
        );
    }

    @Test
    void moveAndStack() throws InventoryException {
        InventoryEntry entry = inventory.add(itemService.create(2425, true));
        InventoryEntry entry2 = inventory.add(itemService.create(2425, true), 1, 0);
        assertNotSame(entry, entry2);
        requestStack.clear();

        entry2.move(-1, 1);

        assertEquals(2, entry.quantity());
        assertEquals(-1, entry.position());

        requestStack.assertAll(
            new UpdateItemSet(inventory.itemSets().get(entry2.item().set().get())),
            new DestroyItem(entry2),
            new ItemQuantity(entry)
        );
    }

    @Test
    void moveItemWillIndexingForStacking() throws InventoryException {
        InventoryEntry entry = inventory.add(itemService.create(2425, true), 1, 0);
        entry.move(-1, 1);
        requestStack.clear();

        assertSame(entry, inventory.add(itemService.create(2425, true)));

        assertEquals(2, entry.quantity());
        assertEquals(-1, entry.position());
    }

    @Test
    void moveToUseBar() throws SQLException, ContainerException, InventoryException {
        dataSet.pushUsableItems();

        InventoryEntry entry = inventory.add(itemService.create(468, true), 100, -1);
        entry.move(41, 50);

        assertEquals(50, entry.quantity());
        assertEquals(-1, entry.position());

        InventoryEntry newEntry = inventory.get(2);

        assertEquals(50, newEntry.quantity());
        assertEquals(41, newEntry.position());
    }

    @Test
    void moveToUseBarNotUsable() throws SQLException, ContainerException, InventoryException {
        dataSet.pushUsableItems();

        InventoryEntry entry = inventory.add(itemService.create(2425, true), 10, -1);

        assertThrows(InventoryException.class, () -> entry.move(41, 1));

        assertEquals(10, entry.quantity());
        assertEquals(-1, entry.position());
    }

    @Test
    void equipWithItemSetAlreadySet() throws InventoryException, SQLException, ContainerException {
        inventory.add(itemService.create(2411, true), 1, HelmetSlot.SLOT_ID);
        InventoryEntry entry = inventory.add(itemService.create(2425, true));
        requestStack.clear();

        entry.move(0, 1);

        PlayerItemSet itemSet = inventory.itemSets().get(entry.item().set().get());

        assertCount(2, itemSet.items());
        assertCount(2, itemSet.bonus().characteristics());
        assertEquals(Effect.ADD_STRENGTH, itemSet.bonus().characteristics().get(0).effect());
        assertEquals(Effect.ADD_INTELLIGENCE, itemSet.bonus().characteristics().get(1).effect());

        assertEquals(55, gamePlayer().characteristics().stuff().get(Characteristic.STRENGTH));
        assertEquals(55, gamePlayer().characteristics().stuff().get(Characteristic.INTELLIGENCE));

        requestStack.assertOne(
            new UpdateItemSet(itemSet)
        );
    }

    @Test
    void unequipWillRemoveItemSet() throws InventoryException {
        InventoryEntry entry = inventory.add(itemService.create(2425, true), 1, AmuletSlot.SLOT_ID);
        requestStack.clear();

        entry.move(-1, 1);

        PlayerItemSet itemSet = inventory.itemSets().get(entry.item().set().get());

        assertCount(0, itemSet.items());
        assertCount(0, itemSet.bonus().characteristics());
        assertTrue(itemSet.isEmpty());

        requestStack.assertOne(
            new UpdateItemSet(itemSet)
        );
    }

    @Test
    void equipItemSetWillUpdateSpecialEffects() throws ContainerException, InventoryException, SQLException {
        inventory.add(container.get(ItemService.class).create(8213), 1, 0);
        inventory.add(container.get(ItemService.class).create(8219), 1, 2);
        inventory.add(container.get(ItemService.class).create(8225), 1, BootsSlot.SLOT_ID);
        inventory.add(container.get(ItemService.class).create(8231), 1, 7);

        InventoryEntry entry = inventory.add(container.get(ItemService.class).create(8243), 1, -1);
        entry.move(6, 1);

        assertEquals(30, gamePlayer().characteristics().specials().get(SpecialEffects.Type.INITIATIVE));
    }

    @Test
    void unequipItemSetWillUpdateSpecialEffects() throws ContainerException, InventoryException, SQLException {
        inventory.add(container.get(ItemService.class).create(8213), 1, 0);
        inventory.add(container.get(ItemService.class).create(8219), 1, 2);
        inventory.add(container.get(ItemService.class).create(8225), 1, BootsSlot.SLOT_ID);
        inventory.add(container.get(ItemService.class).create(8231), 1, 7);
        InventoryEntry entry = inventory.add(container.get(ItemService.class).create(8243), 1, 6);
        entry.move(-1, 1);

        assertEquals(0, gamePlayer().characteristics().specials().get(SpecialEffects.Type.INITIATIVE));
    }
}
