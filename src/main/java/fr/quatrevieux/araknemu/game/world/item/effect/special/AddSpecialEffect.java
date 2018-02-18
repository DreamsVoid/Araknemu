package fr.quatrevieux.araknemu.game.world.item.effect.special;

import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.player.characteristic.SpecialEffects;
import fr.quatrevieux.araknemu.game.world.item.effect.SpecialEffect;
import fr.quatrevieux.araknemu.util.RandomUtil;

/**
 * Add special effect to player {@link SpecialEffects}
 */
final public class AddSpecialEffect implements SpecialEffectHandler {
    final static private RandomUtil RANDOM = new RandomUtil();

    final private SpecialEffects.Type type;

    public AddSpecialEffect(SpecialEffects.Type type) {
        this.type = type;
    }

    @Override
    public void apply(SpecialEffect effect, GamePlayer player) {
        player.characteristics().specials().add(type, effect.arguments()[0]);
    }

    @Override
    public void relieve(SpecialEffect effect, GamePlayer player) {
        player.characteristics().specials().sub(type, effect.arguments()[0]);
    }

    @Override
    public SpecialEffect create(ItemTemplateEffectEntry entry, boolean maximize) {
        int value = maximize
            ? Math.max(entry.min(), entry.max())
            : RANDOM.rand(entry.min(), entry.max())
        ;

        return new SpecialEffect(this, entry.effect(), new int[] {value, 0, entry.special()}, "0d0+" + value);
    }
}