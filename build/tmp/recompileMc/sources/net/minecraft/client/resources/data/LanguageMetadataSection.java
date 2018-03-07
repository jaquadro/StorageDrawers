package net.minecraft.client.resources.data;

import java.util.Collection;
import net.minecraft.client.resources.Language;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class LanguageMetadataSection implements IMetadataSection
{
    private final Collection<Language> languages;

    public LanguageMetadataSection(Collection<Language> languagesIn)
    {
        this.languages = languagesIn;
    }

    public Collection<Language> getLanguages()
    {
        return this.languages;
    }
}