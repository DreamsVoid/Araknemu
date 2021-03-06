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

package fr.quatrevieux.araknemu.game.exploration.exchange;

import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.creature.ExplorationCreature;
import fr.quatrevieux.araknemu.game.exploration.interaction.exchange.ExchangeInteraction;

/**
 * Simply define the factory with constructor's parameters
 *
 * @param <C> The creature type target
 */
final public class SimpleExchangeTypeFactory<C extends ExplorationCreature> implements ExchangeTypeFactory<C> {
    @FunctionalInterface
    public interface Factory<C extends ExplorationCreature> {
        public ExchangeInteraction create(ExplorationPlayer initiator, C target);
    }

    final private ExchangeType type;
    final private Factory<C> factory;

    public SimpleExchangeTypeFactory(ExchangeType type, Factory<C> factory) {
        this.type = type;
        this.factory = factory;
    }

    @Override
    public ExchangeType type() {
        return type;
    }

    @Override
    public ExchangeInteraction create(ExplorationPlayer initiator, C target) {
        return factory.create(initiator, target);
    }
}
