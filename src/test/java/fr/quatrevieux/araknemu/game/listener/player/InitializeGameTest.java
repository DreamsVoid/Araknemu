package fr.quatrevieux.araknemu.game.listener.player;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.event.StartExploration;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.network.game.out.account.Stats;
import fr.quatrevieux.araknemu.network.game.out.info.Error;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class InitializeGameTest extends GameBaseCase {
    private InitializeGame listener;
    private ExplorationPlayer player;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        listener = new InitializeGame(
            player = new ExplorationPlayer(
                gamePlayer()
            ),
            container.get(ExplorationMapService.class)
        );

        dataSet.pushMaps();
    }

    @Test
    void onStartExploration() throws SQLException, ContainerException {
        listener.on(new StartExploration(player));

        requestStack.assertAll(
            new Stats(player.properties()),
            Error.welcome()
        );

        assertNotNull(player.map());
        assertEquals(10540, player.map().id());
    }
}
