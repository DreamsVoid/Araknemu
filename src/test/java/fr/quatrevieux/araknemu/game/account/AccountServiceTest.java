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

package fr.quatrevieux.araknemu.game.account;

import fr.quatrevieux.araknemu.core.dbal.repository.EntityNotFoundException;
import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.living.entity.account.Account;
import fr.quatrevieux.araknemu.data.living.repository.account.AccountRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.GameConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AccountServiceTest extends GameBaseCase {
    private AccountService service;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        service = new AccountService(
            container.get(AccountRepository.class),
            container.get(GameConfiguration.class)
        );

        dataSet.use(Account.class);
    }

    @Test
    void loadAccountNotFound() {
        assertThrows(EntityNotFoundException.class, () -> service.load(new Account(-1)));
    }

    @Test
    void loadSuccess() throws ContainerException {
        Account account = new Account(1, "name", "pass", "pseudo");
        dataSet.push(account);

        GameAccount ga = service.load(new Account(1));

        assertEquals(1, ga.id());
        assertEquals(2, ga.serverId());
    }

    @Test
    void isLogged() {
        assertFalse(service.isLogged(1));

        GameAccount account = new GameAccount(
            new Account(1),
            service,
            1
        );

        account.attach(session);
        assertTrue(service.isLogged(1));
    }
}