package fr.quatrevieux.araknemu.data.world.entity.monster;

import fr.quatrevieux.araknemu.data.value.Interval;
import fr.quatrevieux.araknemu.data.value.Position;

import java.time.Duration;
import java.util.List;

/**
 * Store information for generate monster groups
 */
final public class MonsterGroupData {
    final static public class Monster {
        final private int id;
        final private Interval level;
        final private int rate;

        public Monster(int id, Interval level, int rate) {
            this.id = id;
            this.level = level;
            this.rate = rate;
        }

        /**
         * Get the monster id
         *
         * @see MonsterTemplate#id()
         */
        public int id() {
            return id;
        }

        /**
         * Get the level interval
         */
        public Interval level() {
            return level;
        }

        /**
         * Get the spawn chance of the monster
         *
         * This rate is relative to all monsters of the group
         * Higher the rate is, higher the spawn chance is
         * A monster with a rate of 10 has a probability of two times more than a rate of 5
         *
         * The rate of all monsters of the group are added for compute the probability
         * By default the rate is 1
         *
         * @see MonsterGroupData#totalRate()
         */
        public int rate() {
            return rate;
        }
    }

    final private int id;
    final private Duration respawnTime;
    final private int maxSize;
    final private int maxCount;
    final private List<Monster> monsters;
    final private String comment;
    final private Position winFightTeleport;
    final private boolean fixedTeamNumber;

    final private int totalRate;

    public MonsterGroupData(int id, Duration respawnTime, int maxSize, int maxCount, List<Monster> monsters, String comment, Position winFightTeleport, boolean fixedTeamNumber) {
        this.id = id;
        this.respawnTime = respawnTime;
        this.maxSize = maxSize;
        this.maxCount = maxCount;
        this.monsters = monsters;
        this.comment = comment;
        this.winFightTeleport = winFightTeleport;
        this.fixedTeamNumber = fixedTeamNumber;

        this.totalRate = monsters != null ? monsters.stream().mapToInt(Monster::rate).sum() : 0;
    }

    /**
     * The group id
     * This is the primary key of the group data
     */
    public int id() {
        return id;
    }

    /**
     * Get the group respawn time
     * The respawn timer will starts when group disappear (a fight is started)
     *
     * Note: The data is stored as long integer for duration in milliseconds
     */
    public Duration respawnTime() {
        return respawnTime;
    }

    /**
     * Get the monsters data
     */
    public List<Monster> monsters() {
        return monsters;
    }

    /**
     * Human readable comment for the group data
     * Not used by the server
     */
    public String comment() {
        return comment;
    }

    /**
     * The maximal size of the group
     *
     * If the value is 0, all monsters on the data are present : a fixed group is generated
     * Else, a random group is generated, with size between [1, maxSize] include
     *
     * For dungeon groups, this value should be 0
     */
    public int maxSize() {
        return maxSize;
    }

    /**
     * Maximum number of occurrence of the group
     *
     * If the value is 1, only one group is spawn, and can respawn only when the previous group has start a fight
     *
     * For dungeon groups, this value should be 1
     */
    public int maxCount() {
        return maxCount;
    }

    /**
     * Get the teleport position when win a fight with the group
     * The position may be null, to not teleport after the fight
     *
     * @see Position#isNull()
     */
    public Position winFightTeleport() {
        return winFightTeleport;
    }

    /**
     * Get the sum of all rates of monsters
     *
     * @see Monster#rate()
     */
    public int totalRate() {
        return totalRate;
    }

    /**
     * Does the fight team number is fixed or not ?
     *
     * If this value is false, a random team will be choose for the monster group and the player
     * If this value is true, the monster group will always join fight on team 1 (second team)
     *
     * This value should be true for dungeon groups
     */
    public boolean fixedTeamNumber() {
        return fixedTeamNumber;
    }
}