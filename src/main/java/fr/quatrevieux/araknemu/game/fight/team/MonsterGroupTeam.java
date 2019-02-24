package fr.quatrevieux.araknemu.game.fight.team;

import fr.quatrevieux.araknemu.data.constant.Alignment;
import fr.quatrevieux.araknemu.game.fight.JoinFightError;
import fr.quatrevieux.araknemu.game.fight.exception.JoinFightException;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.monster.MonsterFighter;
import fr.quatrevieux.araknemu.game.monster.Monster;
import fr.quatrevieux.araknemu.game.monster.group.MonsterGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Fight team for {@link MonsterGroup}
 *
 * This team is read only, and contains exactly all group monsters as {@link MonsterFighter}
 * This team has no leader
 */
final public class MonsterGroupTeam implements FightTeam {
    final private MonsterGroup monsterGroup;
    final private int number;
    final private List<Integer> startPlaces;

    final private List<MonsterFighter> fighters;

    public MonsterGroupTeam(MonsterGroup monsterGroup, List<Integer> startPlaces, int number) {
        this.monsterGroup = monsterGroup;
        this.number = number;
        this.startPlaces = startPlaces;

        this.fighters = makeFighters();
    }

    @Override
    public Fighter leader() {
        return null;
    }

    @Override
    public int id() {
        return monsterGroup.id();
    }

    @Override
    public int cell() {
        return monsterGroup.cell();
    }

    @Override
    public int type() {
        return 1;
    }

    @Override
    public Alignment alignment() {
        return Alignment.NONE;
    }

    @Override
    public int number() {
        return number;
    }

    @Override
    public List<Integer> startPlaces() {
        return startPlaces;
    }

    @Override
    public Collection<Fighter> fighters() {
        return Collections.unmodifiableCollection(fighters);
    }

    @Override
    public void send(Object packet) {
        // No op : monster do not receive packets
    }

    @Override
    public boolean alive() {
        return fighters.stream().anyMatch(fighter -> !fighter.dead());
    }

    @Override
    public void join(Fighter fighter) throws JoinFightException {
        throw new JoinFightException(JoinFightError.TEAM_CLOSED);
    }

    @Override
    public void kick(Fighter fighter) {
        throw new UnsupportedOperationException("Read-only team");
    }

    /**
     * Creates fighters from monsters of the group
     * Ids of monsters are negative integer sequence (starting at -1 for the first monster)
     */
    private List<MonsterFighter> makeFighters() {
        List<MonsterFighter> fighters = new ArrayList<>(monsterGroup.monsters().size());

        int id = 0;

        for (Monster monster : monsterGroup.monsters()) {
            fighters.add(new MonsterFighter(--id, monster, this));
        }

        return fighters;
    }
}
