/*
 * Copyright (c) 2025 AMAK Inc. All rights reserved.
 */

package tech.amak.portbuddy.cli.ui;

public interface HttpLogSink {
    void onHttpLog(final String method, final String url, final int status);
}
