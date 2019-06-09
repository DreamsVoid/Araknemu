package fr.quatrevieux.araknemu.data.world.repository.implementation.sql;

import fr.quatrevieux.araknemu.core.dbal.repository.EntityNotFoundException;
import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.data.transformer.ImmutableCharacteristicsTransformer;
import fr.quatrevieux.araknemu.data.world.entity.monster.MonsterTemplate;
import fr.quatrevieux.araknemu.data.world.transformer.ColorsTransformer;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SqlMonsterTemplateRepositoryTest extends GameBaseCase {
    private SqlMonsterTemplateRepository repository;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMonsterTemplates();

        repository = new SqlMonsterTemplateRepository(
            app.database().get("game"),
            container.get(ColorsTransformer.class),
            container.get(ImmutableCharacteristicsTransformer.class)
        );
    }

    @Test
    void getNotFound() {
        assertThrows(EntityNotFoundException.class, () -> repository.get(-5));
    }

    @Test
    void getById() {
        MonsterTemplate template = repository.get(31);

        assertEquals(31, template.id());
        assertEquals("Larve Bleue", template.name());
        assertEquals(1563, template.gfxId());
        assertEquals("AGGRESSIVE", template.ai());
        assertCount(5, template.grades());
        assertEquals(-1, template.colors().color1());
        assertEquals(-1, template.colors().color2());
        assertEquals(-1, template.colors().color3());

        assertEquals(2, template.grades()[0].level());
        assertEquals(10, template.grades()[0].life());
        assertEquals(20, template.grades()[0].initiative());

        assertEquals(4, template.grades()[0].characteristics().get(Characteristic.ACTION_POINT));
        assertEquals(2, template.grades()[0].characteristics().get(Characteristic.MOVEMENT_POINT));

        assertEquals(80, template.grades()[0].characteristics().get(Characteristic.STRENGTH));
        assertEquals(0, template.grades()[0].characteristics().get(Characteristic.WISDOM));
        assertEquals(80, template.grades()[0].characteristics().get(Characteristic.INTELLIGENCE));
        assertEquals(80, template.grades()[0].characteristics().get(Characteristic.LUCK));
        assertEquals(0, template.grades()[0].characteristics().get(Characteristic.AGILITY));

        assertEquals(1, template.grades()[0].characteristics().get(Characteristic.RESISTANCE_PERCENT_NEUTRAL));
        assertEquals(5, template.grades()[0].characteristics().get(Characteristic.RESISTANCE_PERCENT_EARTH));
        assertEquals(5, template.grades()[0].characteristics().get(Characteristic.RESISTANCE_PERCENT_FIRE));
        assertEquals(-9, template.grades()[0].characteristics().get(Characteristic.RESISTANCE_PERCENT_WATER));
        assertEquals(-9, template.grades()[0].characteristics().get(Characteristic.RESISTANCE_PERCENT_AIR));
        assertEquals(5, template.grades()[0].characteristics().get(Characteristic.RESISTANCE_ACTION_POINT));
        assertEquals(3, template.grades()[0].characteristics().get(Characteristic.RESISTANCE_MOVEMENT_POINT));

        assertEquals(2, template.grades()[0].spells().size());
        assertEquals(1, (int) template.grades()[0].spells().get(212));
        assertEquals(1, (int) template.grades()[0].spells().get(213));

        assertEquals(4, template.grades()[4].characteristics().get(Characteristic.ACTION_POINT));
        assertEquals(2, template.grades()[4].characteristics().get(Characteristic.MOVEMENT_POINT));

        assertEquals(100, template.grades()[4].characteristics().get(Characteristic.STRENGTH));
        assertEquals(0, template.grades()[4].characteristics().get(Characteristic.WISDOM));
        assertEquals(100, template.grades()[4].characteristics().get(Characteristic.INTELLIGENCE));
        assertEquals(100, template.grades()[4].characteristics().get(Characteristic.LUCK));
        assertEquals(0, template.grades()[4].characteristics().get(Characteristic.AGILITY));

        assertEquals(5, template.grades()[4].characteristics().get(Characteristic.RESISTANCE_PERCENT_NEUTRAL));
        assertEquals(9, template.grades()[4].characteristics().get(Characteristic.RESISTANCE_PERCENT_EARTH));
        assertEquals(9, template.grades()[4].characteristics().get(Characteristic.RESISTANCE_PERCENT_FIRE));
        assertEquals(-5, template.grades()[4].characteristics().get(Characteristic.RESISTANCE_PERCENT_WATER));
        assertEquals(-5, template.grades()[4].characteristics().get(Characteristic.RESISTANCE_PERCENT_AIR));
        assertEquals(9, template.grades()[4].characteristics().get(Characteristic.RESISTANCE_ACTION_POINT));
        assertEquals(7, template.grades()[4].characteristics().get(Characteristic.RESISTANCE_MOVEMENT_POINT));

        assertEquals(2, template.grades()[4].spells().size());
        assertEquals(5, (int) template.grades()[4].spells().get(212));
        assertEquals(5, (int) template.grades()[4].spells().get(213));
    }

    @Test
    void getByEntity() {
        MonsterTemplate template = repository.get(new MonsterTemplate(31, null, 0, null, null, null));

        assertEquals(31, template.id());
        assertEquals("Larve Bleue", template.name());
        assertEquals(1563, template.gfxId());
        assertEquals("AGGRESSIVE", template.ai());
        assertCount(5, template.grades());
    }

    @Test
    void has() {
        assertTrue(repository.has(new MonsterTemplate(31, null, 0, null, null, null)));
        assertTrue(repository.has(new MonsterTemplate(36, null, 0, null, null, null)));
        assertFalse(repository.has(new MonsterTemplate(-5, null, 0, null, null, null)));
    }

    @Test
    void all() {
        List<MonsterTemplate> templates = repository.all();

        assertCount(3, templates);

        assertEquals(31, templates.get(0).id());
        assertEquals(34, templates.get(1).id());
        assertEquals(36, templates.get(2).id());
    }
}