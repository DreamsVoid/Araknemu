package fr.quatrevieux.araknemu.game.fight.map;

import fr.quatrevieux.araknemu.data.world.entity.environment.MapTemplate;
import fr.quatrevieux.araknemu.game.fight.exception.FightMapException;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;

import java.util.Optional;

/**
 * Base fight cell
 */
final public class WalkableFightCell implements FightCell {
    final private FightMap map;
    final private MapTemplate.Cell template;
    final private int id;

    private Fighter fighter;

    public WalkableFightCell(FightMap map, MapTemplate.Cell template, int id) {
        this.map = map;
        this.template = template;
        this.id = id;
    }

    @Override
    public int id() {
        return id;
    }

    @Override
    public FightMap map() {
        return map;
    }

    @Override
    public boolean walkable() {
        return fighter == null;
    }

    @Override
    public boolean walkableIgnoreFighter() {
        return true;
    }

    @Override
    public boolean sightBlocking() {
        return !template.lineOfSight() || fighter != null;
    }

    @Override
    public Optional<Fighter> fighter() {
        return Optional.ofNullable(fighter);
    }

    @Override
    public void set(Fighter fighter) {
        if (this.fighter != null) {
            throw new FightMapException("A fighter is already set on this cell (" + id + ")");
        }

        this.fighter = fighter;
    }

    @Override
    public void removeFighter() {
        if (this.fighter == null) {
            throw new FightMapException("No fighter found on cell " + id);
        }

        this.fighter = null;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof WalkableFightCell && equals((FightCell) obj);
    }
}
