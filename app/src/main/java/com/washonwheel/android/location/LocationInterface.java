package com.washonwheel.android.location;

import com.google.android.gms.common.api.Status;

/*
 * Created by Gautam on 13-02-2018.
 */

public interface LocationInterface {
    void onSuccess(double latitude, double longitude);

    void onFailure();

    void onResolve(Status status);
}
