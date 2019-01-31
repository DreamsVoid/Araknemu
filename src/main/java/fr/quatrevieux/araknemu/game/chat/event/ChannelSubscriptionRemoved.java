package fr.quatrevieux.araknemu.game.chat.event;

import fr.quatrevieux.araknemu.game.chat.ChannelType;

import java.util.Collection;

/**
 * Trigger when channels subscription changed
 */
final public class ChannelSubscriptionRemoved {
    final private Collection<ChannelType> channels;

    public ChannelSubscriptionRemoved(Collection<ChannelType> channels) {
        this.channels = channels;
    }

    public Collection<ChannelType> channels() {
        return channels;
    }
}
