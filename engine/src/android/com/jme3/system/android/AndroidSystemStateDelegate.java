package com.jme3.system.android;

import com.jme3.system.AbstractSystemStateDelegate;
import com.jme3.system.Platform;

public class AndroidSystemStateDelegate extends AbstractSystemStateDelegate {

	@Override
    public Platform getPlatform() {
        String arch = System.getProperty("os.arch").toLowerCase();
        if (arch.contains("arm")) {
            if (arch.contains("v5")) {
                return Platform.Android_ARM5;
            } else if (arch.contains("v6")) {
                return Platform.Android_ARM6;
            } else if (arch.contains("v7")) {
                return Platform.Android_ARM7;
            } else {
                return Platform.Android_ARM5; // unknown ARM
            }
        } else {
            throw new UnsupportedOperationException("Unsupported Android Platform");
        }
    }

}
