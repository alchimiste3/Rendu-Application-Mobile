package com.android.testservice.testservice.write_read;

/**
 * Created by user on 03/12/16.
 */

public enum NameFiles {

    DEBUG_FILE("dataMyService.txt"),
    AMPLITUDE_FILE("amplitudeSound.txt"),
    CLUSTER_FILE("clusters.txt");

    public String value;

    NameFiles(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

}
