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

package fr.quatrevieux.araknemu.core.network.session.extension;

import fr.quatrevieux.araknemu.core.network.SessionClosed;
import fr.quatrevieux.araknemu.core.network.SessionCreated;
import fr.quatrevieux.araknemu.core.network.exception.HandlingException;
import fr.quatrevieux.araknemu.core.network.parser.HandlerNotFoundException;
import fr.quatrevieux.araknemu.core.network.session.ConfigurableSession;
import fr.quatrevieux.araknemu.core.network.session.Session;
import fr.quatrevieux.araknemu.core.network.session.SessionConfigurator;
import org.slf4j.Logger;

import java.util.function.Consumer;

/**
 * Add loggers to a session
 */
final public class SessionLogger implements ConfigurableSession.ExceptionHandler<Throwable>, ConfigurableSession.ReceivePacketMiddleware, ConfigurableSession.SendPacketTransformer {
    static public class Configurator<S extends Session> implements SessionConfigurator.Configurator<S> {
        final private Logger logger;

        public Configurator(Logger logger) {
            this.logger = logger;
        }

        @Override
        public void configure(ConfigurableSession inner, Session session) {
            SessionLogger sessionLogger = new SessionLogger(session, logger);

            inner.addExceptionHandler(sessionLogger);
            inner.addSendTransformer(sessionLogger);
            inner.addReceiveMiddleware(sessionLogger);
        }
    }

    final private Session session;
    final private Logger logger;

    public SessionLogger(Session session, Logger logger) {
        this.session = session;
        this.logger = logger;
    }

    @Override
    public Class<Throwable> type() {
        return Throwable.class;
    }

    @Override
    public boolean handleException(Throwable cause) {
        if (cause instanceof HandlingException) {
            return true;
        }

        if (cause instanceof HandlerNotFoundException) {
            logger.warn(cause.getMessage());

            return false;
        }

        logger.error("[{}] Uncaught exception", session.channel().id(), cause);

        if (cause.getCause() != null) {
            logger.error("[{}] Cause : {}", session.channel().id(), cause.getCause());
        }

        return true;
    }

    @Override
    public void handlePacket(Object packet, Consumer<Object> next) {
        if (packet instanceof SessionCreated) {
            logger.info("[{}] Session created", session.channel().id());
        } else if (packet instanceof SessionClosed) {
            logger.info("[{}] Session closed", session.channel().id());
        } else {
            logger.info("[{}] Recv << {}", session.channel().id(), packet);
        }

        next.accept(packet);
    }

    @Override
    public Object transformPacket(Object packet) {
        logger.info("[{}] Send >> {}", session.channel().id(), packet);

        return packet;
    }
}
