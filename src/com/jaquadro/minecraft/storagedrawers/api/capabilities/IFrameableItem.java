package com.jaquadro.minecraft.storagedrawers.api.capabilities;

import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

/**
 * An interface for treating an item as frameable for the framing table.
 * <p>
 * Any item that implements this will be considered valid for framing by the framing table.
 */
public interface IFrameableItem
{

    /**
     * Get the {@link ConsumeType} for the side material.
     * If, during attempted framing, either this, {@link #consumeTrim()} or {@link #consumeFront()}
     * rejects its respective material, the framing will not take place.
     *
     * @return Whether the side material is {@link ConsumeType#REQUIRED}, {@link ConsumeType#OPTIONAL},
     * or {@link ConsumeType#UNACCEPTABLE} for framing.
     */
    @Nonnull
    ConsumeType consumeSide ();

    /**
     * Get the {@link ConsumeType} for the side material.
     * If, during attempted framing, either this, {@link #consumeSide()} or {@link #consumeFront()}
     * rejects its respective material, the framing will not take place.
     *
     * @return Whether the trim material is {@link ConsumeType#REQUIRED}, {@link ConsumeType#OPTIONAL},
     * or {@link ConsumeType#UNACCEPTABLE} for framing.
     */
    @Nonnull
    ConsumeType consumeTrim ();

    /**
     * Get the {@link ConsumeType} for the side material.
     * If, during attempted framing, either this, {@link #consumeTrim()} or {@link #consumeTrim()} ()}
     * rejects its respective material, the framing will not take place.
     *
     * @return Whether the front material is {@link ConsumeType#REQUIRED}, {@link ConsumeType#OPTIONAL},
     * or {@link ConsumeType#UNACCEPTABLE} for framing.
     */
    @Nonnull
    ConsumeType consumeFront ();

    /**
     * Make the item stack to render, or the output of the recipe.
     *
     * @param source   A copy of the ingredient stack. Feel free to mutate this.
     * @param matSide  The side material. May be empty even if required.
     * @param matTrim  The trim material. May be empty even if required.
     * @param matFront The front material. May be empty even if required.
     * @return The ItemStack that would be the result of this craft.
     * May return {@link ItemStack#EMPTY} if the craft could not take place.
     */
    @Nonnull
    ItemStack makeItemStack (@Nonnull ItemStack source, @Nonnull ItemStack matSide, @Nonnull ItemStack matTrim, @Nonnull ItemStack matFront);

    enum ConsumeType
    {
        /**
         * Framing can only be performed if the ItemStack is present.
         */
        REQUIRED,
        /**
         * Framing cannot be performed unless the ItemStack is empty.
         */
        UNACCEPTABLE,
        /**
         * Framing can be performed whether or not the ItemStack is present.
         */
        OPTIONAL;

        public boolean accepts (@Nonnull ItemStack stack) {
            switch (this) {
                case REQUIRED:
                    return !stack.isEmpty();
                case UNACCEPTABLE:
                    return stack.isEmpty();
                default:
                    return true;
            }
        }
    }

}
