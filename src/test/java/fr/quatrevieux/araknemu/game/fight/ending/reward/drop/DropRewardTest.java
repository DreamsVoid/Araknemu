package fr.quatrevieux.araknemu.game.fight.ending.reward.drop;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.ending.reward.RewardType;
import fr.quatrevieux.araknemu.game.fight.ending.reward.drop.action.DropRewardAction;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class DropRewardTest extends FightBaseCase {
    @Test
    void getters() throws Exception {
        Fight fight = createFight();
        fight.nextState();

        Fighter fighter = player.fighter();

        DropReward reward = new DropReward(RewardType.WINNER, fighter, Collections.emptyList());

        reward.setKamas(250);
        reward.setXp(1145);
        reward.setGuildXp(55);
        reward.setMountXp(14);

        assertSame(RewardType.WINNER, reward.type());
        assertSame(fighter, reward.fighter());

        assertEquals(1145, reward.xp());
        assertEquals(250, reward.kamas());
        assertEquals(14, reward.mountXp());
        assertEquals(55, reward.guildXp());
    }

    @Test
    void renderForWinner() throws Exception {
        Fight fight = createFight();
        fight.nextState();

        Fighter winner = player.fighter();

        DropReward reward = new DropReward(RewardType.WINNER, winner, Collections.emptyList());

        reward.setKamas(250);
        reward.setXp(1145);

        assertEquals("2;1;Bob;50;0;5350000;5481459;5860000;1145;;;;250", reward.render());
    }

    @Test
    void renderForWinnerWithMountAndGuildXp() throws Exception {
        Fight fight = createFight();
        fight.nextState();

        Fighter winner = player.fighter();

        DropReward reward = new DropReward(RewardType.WINNER, winner, Collections.emptyList());

        reward.setKamas(250);
        reward.setXp(1145);
        reward.setMountXp(15);
        reward.setGuildXp(22);

        assertEquals("2;1;Bob;50;0;5350000;5481459;5860000;1145;22;15;;250", reward.render());
    }

    @Test
    void renderWithItems() throws Exception {
        Fight fight = createFight();
        fight.nextState();

        Fighter winner = player.fighter();

        Map<Integer, Integer> items = new HashMap<>();

        items.put(12, 3);
        items.put(56, 2);

        DropReward reward = new DropReward(RewardType.WINNER, winner, Collections.emptyList());

        reward.addItem(12, 3);
        reward.addItem(56, 2);

        assertEquals("2;1;Bob;50;0;5350000;5481459;5860000;;;;56~2,12~3;", reward.render());
    }

    @Test
    void renderForLooser() throws Exception {
        Fight fight = createFight();
        fight.nextState();

        Fighter fighter = player.fighter();

        DropReward reward = new DropReward(RewardType.LOOSER, fighter, Collections.emptyList());

        assertEquals("0;1;Bob;50;0;5350000;5481459;5860000;;;;;", reward.render());
    }

    @Test
    void renderForMonster() throws Exception {
        Fight fight = createPvmFight();
        fight.nextState();

        Fighter fighter = fight.team(1).fighters().stream().findFirst().get();

        DropReward reward = new DropReward(RewardType.WINNER, fighter, Collections.emptyList());

        assertEquals("2;-1;31;4;0;0;0;0;;;;;", reward.render());
    }

    @Test
    void apply() throws Exception {
        Fight fight = createFight();
        fight.nextState();

        Fighter fighter = player.fighter();
        DropRewardAction action = Mockito.mock(DropRewardAction.class);

        DropReward reward = new DropReward(RewardType.WINNER, fighter, Collections.singletonList(action));

        reward.apply();

        Mockito.verify(action).apply(reward, fighter);
    }

    @Test
    void addAction() throws Exception {
        Fight fight = createFight();
        fight.nextState();

        Fighter fighter = player.fighter();
        DropRewardAction action = Mockito.mock(DropRewardAction.class);

        DropReward reward = new DropReward(RewardType.WINNER, fighter, Collections.emptyList());

        reward.addAction(action);

        reward.apply();

        Mockito.verify(action).apply(reward, fighter);
    }
}