/*
 * This file is part of Araknemu.
 *
 * Araknemu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Araknemu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Araknemu.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Copyright (c) 2017-2019 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.spell.effect.target;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.fight.state.PlacementState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SpellEffectTargetTest extends FightBaseCase {
    Fight fight;

    PlayerFighter caster;
    PlayerFighter enemy;
    PlayerFighter teammate;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        fight = createFight();

        caster = player.fighter();
        enemy = other.fighter();

        teammate = makePlayerFighter(makeSimpleGamePlayer(10));
        fight.state(PlacementState.class).joinTeam(teammate, caster.team());
    }

    @Test
    void testDefault() {
        assertTrue(SpellEffectTarget.DEFAULT.test(caster, caster));
        assertTrue(SpellEffectTarget.DEFAULT.test(caster, enemy));
        assertTrue(SpellEffectTarget.DEFAULT.test(caster, teammate));
    }

    @Test
    void testNotTeam() {
        SpellEffectTarget et = new SpellEffectTarget(SpellEffectTarget.NOT_TEAM);

        assertFalse(et.test(caster, caster));
        assertTrue(et.test(caster, enemy));
        assertFalse(et.test(caster, teammate));
    }

    @Test
    void testNotSelf() {
        SpellEffectTarget et = new SpellEffectTarget(SpellEffectTarget.NOT_SELF);

        assertFalse(et.test(caster, caster));
        assertTrue(et.test(caster, enemy));
        assertTrue(et.test(caster, teammate));
    }

    @Test
    void testNotEnemy() {
        SpellEffectTarget et = new SpellEffectTarget(SpellEffectTarget.NOT_ENEMY);

        assertTrue(et.test(caster, caster));
        assertFalse(et.test(caster, enemy));
        assertTrue(et.test(caster, teammate));
    }

    @Test
    void testOnlyInvoc() {
        SpellEffectTarget et = new SpellEffectTarget(SpellEffectTarget.ONLY_INVOC);

        assertFalse(et.test(caster, caster));
        assertFalse(et.test(caster, enemy));
        assertFalse(et.test(caster, teammate));
    }

    @Test
    void testNotEnemyAndNotSelf() {
        SpellEffectTarget et = new SpellEffectTarget(SpellEffectTarget.NOT_ENEMY | SpellEffectTarget.NOT_SELF);

        assertFalse(et.test(caster, caster));
        assertFalse(et.test(caster, enemy));
        assertTrue(et.test(caster, teammate));
    }
}
