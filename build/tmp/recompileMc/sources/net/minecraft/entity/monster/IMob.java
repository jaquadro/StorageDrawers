package net.minecraft.entity.monster;

import com.google.common.base.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.IAnimals;

public interface IMob extends IAnimals
{
    /** Entity selector for IMob types. */
    Predicate<Entity> MOB_SELECTOR = new Predicate<Entity>()
    {
        public boolean apply(@Nullable Entity p_apply_1_)
        {
            return p_apply_1_ instanceof IMob;
        }
    };
    /** Entity selector for IMob types that are not invisible */
    Predicate<Entity> VISIBLE_MOB_SELECTOR = new Predicate<Entity>()
    {
        public boolean apply(@Nullable Entity p_apply_1_)
        {
            return p_apply_1_ instanceof IMob && !p_apply_1_.isInvisible();
        }
    };
}