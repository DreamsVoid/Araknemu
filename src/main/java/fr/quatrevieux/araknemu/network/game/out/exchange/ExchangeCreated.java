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

package fr.quatrevieux.araknemu.network.game.out.exchange;

import fr.quatrevieux.araknemu.game.exploration.exchange.ExchangeType;
import fr.quatrevieux.araknemu.game.world.creature.Creature;

/**
 * The exchange is created
 *
 * Note: Do not supports shops or mount park
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Exchange.as#L321
 */
final public class ExchangeCreated {
    final private ExchangeType type;
    final private Creature target;

    public ExchangeCreated(ExchangeType type) {
        this(type, null);
    }

    /**
     * @param type The exchange type
     * @param target The exchange target. May be null
     */
    public ExchangeCreated(ExchangeType type, Creature target) {
        this.type = type;
        this.target = target;
    }

    @Override
    public String toString() {
        return "ECK" + type.ordinal() + (target != null ? "|" + target.id() : "");
    }
}
