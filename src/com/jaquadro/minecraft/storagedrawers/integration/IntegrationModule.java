package com.jaquadro.minecraft.storagedrawers.integration;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.versioning.ArtifactVersion;
import cpw.mods.fml.common.versioning.DefaultArtifactVersion;
import cpw.mods.fml.common.versioning.InvalidVersionSpecificationException;
import cpw.mods.fml.common.versioning.VersionRange;

import java.util.List;

public abstract class IntegrationModule
{
    public String getModID () {
        return null;
    }

    public boolean versionCheck () {
        String pattern = versionPattern();
        if (pattern == null)
            return true;

        List<ModContainer> modList = Loader.instance().getModList();
        for (int i = 0, n = modList.size(); i < n; i++) {
            ModContainer mod = modList.get(i);
            if (mod.getModId().equals(getModID())) {
                try {
                    VersionRange validVersions = VersionRange.createFromVersionSpec(pattern);
                    ArtifactVersion version = new DefaultArtifactVersion(mod.getVersion());
                    return validVersions.containsVersion(version);
                }
                catch (InvalidVersionSpecificationException e) {
                    return false;
                }
            }
        }

        return false;
    }

    protected String versionPattern () {
        return null;
    }

    public abstract void init () throws Throwable;

    public abstract void postInit ();
}
