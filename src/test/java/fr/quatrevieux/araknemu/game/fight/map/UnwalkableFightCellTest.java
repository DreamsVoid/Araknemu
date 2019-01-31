package fr.quatrevieux.araknemu.game.fight.map;

import fr.quatrevieux.araknemu.data.world.entity.environment.MapTemplate;
import fr.quatrevieux.araknemu.data.world.repository.environment.MapTemplateRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.fight.exception.FightMapException;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UnwalkableFightCellTest extends GameBaseCase {
    private MapTemplate mapTemplate;
    private FightMap map;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps();

        mapTemplate = container.get(MapTemplateRepository.class).get(10340);
        map = new FightMap(mapTemplate);
    }

    @Test
    void sightBlocking() {
        UnwalkableFightCell cell = new UnwalkableFightCell(map, mapTemplate.cells().get(0), 0);
        assertFalse(cell.sightBlocking());

        cell = new UnwalkableFightCell(map, mapTemplate.cells().get(11), 11);
        assertTrue(cell.sightBlocking());
    }

    @Test
    void getters() {
        UnwalkableFightCell cell = new UnwalkableFightCell(map, mapTemplate.cells().get(0), 0);

        assertEquals(0, cell.id());
        assertFalse(cell.walkable());
        assertFalse(cell.walkableIgnoreFighter());
        assertSame(Optional.empty(), cell.fighter());
        assertSame(map, cell.map());
    }

    @Test
    void set() {
        UnwalkableFightCell cell = new UnwalkableFightCell(map, mapTemplate.cells().get(0), 0);

        assertThrows(FightMapException.class, () -> cell.set(Mockito.mock(Fighter.class)));
    }

    @Test
    void removeFighter() {
        UnwalkableFightCell cell = new UnwalkableFightCell(map, mapTemplate.cells().get(0), 0);

        assertThrows(FightMapException.class, () -> cell.removeFighter());
    }

    @Test
    void equals() {
        UnwalkableFightCell cell0 = new UnwalkableFightCell(map, mapTemplate.cells().get(0), 0);
        UnwalkableFightCell cell1 = new UnwalkableFightCell(map, mapTemplate.cells().get(1), 1);

        assertEquals(cell0, cell0);
        assertNotEquals(cell0, cell1);
    }
}
