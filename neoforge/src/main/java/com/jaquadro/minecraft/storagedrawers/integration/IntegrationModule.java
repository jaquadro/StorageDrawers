package com.jaquadro.minecraft.storagedrawers.integration;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;
import org.apache.maven.artifact.versioning.VersionRange;

public abstract class IntegrationModule
{
    public abstract String getModID ();

    public boolean versionCheck () {
        String pattern = versionPattern();
        if (pattern == null)
            return true;

        ModContainer mod = ModList.get().getModContainerById(pattern).orElse(null);
        if (mod != null) {
            try {
                VersionRange validVersions = VersionRange.createFromVersionSpec(pattern);
                ArtifactVersion version = mod.getModInfo().getVersion();
                return validVersions.containsVersion(version);
            }
            catch (InvalidVersionSpecificationException e) {
                return false;
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
