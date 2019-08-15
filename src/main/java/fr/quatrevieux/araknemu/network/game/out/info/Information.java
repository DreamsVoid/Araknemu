package fr.quatrevieux.araknemu.network.game.out.info;

import fr.quatrevieux.araknemu.data.constant.Characteristic;

/**
 * Information messages
 */
final public class Information extends InformationMessage {
    public Information(Entry... entries) {
        super(Type.INFO, entries);
    }

    public Information(int id) {
        this(new Entry(id));
    }

    public Information(int id, Object... arguments) {
        this(new Entry(id, arguments));
    }

    /**
     * Message for global channel flood
     *
     * @param remainingSeconds Remaining time in seconds before send another message
     */
    static public Information chatFlood(int remainingSeconds) {
        return new Information(115, remainingSeconds);
    }

    /**
     * An item cannot be posted to the channel
     */
    static public Information cannotPostItemOnChannel() {
        return new Information(114);
    }

    /**
     * Add life points message
     *
     * @param value The recovered life points
     */
    static public Information heal(int value) {
        return new Information(1, value);
    }

    /**
     * Message for spell learned
     *
     * @param spellId The learned spell id
     */
    static public Information spellLearn(int spellId) {
        return new Information(3, spellId);
    }

    /**
     * Send message for characteristic boost
     *
     * @param characteristic The boosted characteristic
     * @param value The boost value
     */
    static public Information characteristicBoosted(Characteristic characteristic, int value) {
        switch (characteristic) {
            case WISDOM:
                return new Information(9, value);
            case STRENGTH:
                return new Information(10, value);
            case LUCK:
                return new Information(11, value);
            case AGILITY:
                return new Information(12, value);
            case VITALITY:
                return new Information(13, value);
            case INTELLIGENCE:
                return new Information(14, value);
        }

        return null;
    }

    /**
     * The position of the player is saved
     */
    static public Information positionSaved() {
        return new Information(6);
    }

    /**
     * The bank tax has been payed
     *
     * @param cost The kamas given by the player
     */
    static public Information bankTaxPayed(long cost) {
        return new Information(20, cost);
    }
}
