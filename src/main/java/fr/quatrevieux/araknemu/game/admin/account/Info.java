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

package fr.quatrevieux.araknemu.game.admin.account;

import fr.quatrevieux.araknemu.common.account.Permission;
import fr.quatrevieux.araknemu.data.living.entity.account.Account;
import fr.quatrevieux.araknemu.data.living.repository.account.AccountRepository;
import fr.quatrevieux.araknemu.game.account.GameAccount;
import fr.quatrevieux.araknemu.game.admin.AbstractCommand;
import fr.quatrevieux.araknemu.game.admin.AdminPerformer;

import java.util.List;

/**
 * Info command for account
 */
final public class Info extends AbstractCommand {
    final private GameAccount account;
    final private AccountRepository repository;

    public Info(GameAccount account, AccountRepository repository) {
        this.account = account;
        this.repository = repository;
    }

    @Override
    protected void build(Builder builder) {
        builder
            .description("Display info about the account")
            .help("info")
            .requires(Permission.MANAGE_ACCOUNT)
        ;
    }

    @Override
    public String name() {
        return "info";
    }

    @Override
    public void execute(AdminPerformer performer, List<String> arguments) {
        Account entity = repository.get(new Account(account.id()));

        performer.success("Account info : {}", entity.name());
        performer.success("=================================");

        performer.info("Name:   {}", entity.name());
        performer.info("Pseudo: {}", account.pseudo());
        performer.info("ID:     {}", account.id());

        if (account.isLogged()) {
            performer.success("Logged: Yes");
        } else {
            performer.error("Logged: No");
        }

        if (!account.isMaster()) {
            performer.error("Standard account");
        } else {
            performer.success("Admin account");
            performer.success("Permissions: {}", entity.permissions());
        }
    }
}
