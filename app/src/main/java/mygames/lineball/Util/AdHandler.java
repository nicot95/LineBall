package mygames.lineball.Util;

import android.content.Context;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import mygames.lineball.R;

/*
    Manages the initialization of the intersitial ad in the main activity.
 */
public class AdHandler {

    private static final int MOD = 4;
    private static int adCounter; // EveryMultiple of MOD will render an intersitial ad

    private InterstitialAd mInterstitialAd;
    private boolean isAdOpen;

    public AdHandler(Context context) {
        this.isAdOpen = true;
        adCounter = MOD - 1;

        mInterstitialAd = new InterstitialAd(context);
        mInterstitialAd.setAdUnitId(context.getString(R.string.intesitial_ad_unit_id)); // Test

        /*mInterstitialAd.setAdUnitId(context.getResources().
                        getString(R.string.intesitial_ad_unit_id));
*/

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                isAdOpen = false;
            }
        });
        requestNewInterstitial();
    }

    public void openPossibleIntersitialAd() {
        if (!mInterstitialAd.isLoaded()) return;

        if (adCounter == 0) {
            isAdOpen = true;
            mInterstitialAd.show();
            adCounter++;
        } else {
            adCounter = (adCounter + 1) % MOD;
        }
    }

    // Loads a new ad, but it waits before showing it to the user.
    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder().build();

        mInterstitialAd.loadAd(adRequest);
    }

    public boolean isAdOpen() {
        return isAdOpen;
    }

    public boolean isLoaded() {
        return mInterstitialAd.isLoaded();
    }
}
