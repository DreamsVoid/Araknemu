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

package fr.quatrevieux.araknemu.data.living.repository.implementation.sql;

import fr.quatrevieux.araknemu.DatabaseTestCase;
import fr.quatrevieux.araknemu.core.dbal.repository.EntityNotFoundException;
import fr.quatrevieux.araknemu.core.dbal.executor.ConnectionPoolExecutor;
import fr.quatrevieux.araknemu.data.living.entity.account.Account;
import fr.quatrevieux.araknemu.data.living.transformer.PermissionsTransformer;
import fr.quatrevieux.araknemu.common.account.Permission;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.*;

class SqlAccountRepositoryTest extends DatabaseTestCase {
    private fr.quatrevieux.araknemu.data.living.repository.account.AccountRepository repository;

    @BeforeEach
    @Override
    public void setUp() throws Exception {
        super.setUp();

        repository = new SqlAccountRepository(new ConnectionPoolExecutor(connection), new PermissionsTransformer());
        repository.initialize();
    }

    @AfterEach
    void tearDown() throws SQLException {
        dropTable("ACCOUNT");
    }

    @Test
    void testInitialize() throws SQLException {
        assertTableExists("ACCOUNT");
    }

    @Test
    void testAdd() {
        Account account = repository.add(new Account(0, "test", "password", "testouille"));

        assertEquals(1, account.id());
        assertEquals("test", account.name());
        assertEquals("password", account.password());
        assertEquals("testouille", account.pseudo());

        assertEquals(2, repository.add(new Account(0)).id());
    }

    @Test
    void testGet() {
        Account account = repository.add(new Account(0, "test", "password", "pseudo"));

        assertEquals(account, repository.get(account));
    }

    @Test
    void getNotFound() {
        assertThrows(EntityNotFoundException.class, () -> repository.get(new Account(5)));
    }

    @Test
    void testHas() {
        Account inserted = repository.add(new Account(0, "test", "password", "testouille"));

        assertFalse(repository.has(new Account(-1)));
        assertTrue(repository.has(inserted));
    }

    @Test
    void testDelete() {
        Account inserted = repository.add(new Account(0, "test", "password", "testouille"));

        repository.delete(inserted);

        assertFalse(repository.has(inserted));
    }

    @Test
    void findByUsername() {
        Account inserted = repository.add(new Account(0, "test", "password", "testouille"));
        repository.add(new Account(0, "other", "pass", "aaa"));

        assertEquals(inserted, repository.findByUsername("test"));
    }

    @Test
    void permissions() {
        Account account = repository.add(new Account(0, "other", "pass", "aaa", EnumSet.of(Permission.ACCESS), "", ""));
        account = repository.get(account);

        assertEquals(
            EnumSet.of(Permission.ACCESS),
            account.permissions()
        );
    }

    @Test
    void questionAndAnswer() {
        Account account = repository.add(new Account(0, "other", "pass", "aaa", EnumSet.noneOf(Permission.class), "azerty", "uiop"));
        account = repository.get(account);

        assertEquals("azerty", account.question());
        assertEquals("uiop", account.answer());
    }
}
