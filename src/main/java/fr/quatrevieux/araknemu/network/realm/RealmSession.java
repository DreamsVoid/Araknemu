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

package fr.quatrevieux.araknemu.network.realm;

import fr.quatrevieux.araknemu.core.network.session.AbstractDelegatedSession;
import fr.quatrevieux.araknemu.core.network.session.Session;
import fr.quatrevieux.araknemu.realm.ConnectionKey;
import fr.quatrevieux.araknemu.realm.authentication.AuthenticationAccount;

/**
 * Wrap IoSession for Realm
 */
final public class RealmSession extends AbstractDelegatedSession implements Session {
    final private ConnectionKey key;

    private AuthenticationAccount account;

    public RealmSession(Session session) {
        super(session);

        key = new ConnectionKey();
    }

    /**
     * Get or generate connection key
     */
    public ConnectionKey key() {
        return key;
    }

    /**
     * Attach account to the session
     */
    public void attach(AuthenticationAccount account) {
        this.account = account;
    }

    /**
     * Detach account from session
     */
    public void detach() {
        account = null;
    }

    /**
     * Get attached account
     */
    public AuthenticationAccount account() {
        return account;
    }

    /**
     * Check is the client is logged (has an attached account)
     */
    public boolean isLogged() {
        return account != null;
    }
}
