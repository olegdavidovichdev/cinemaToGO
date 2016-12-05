package com.olegdavidovichdev.cinematogo;

import android.view.View;

import it.sephiroth.android.library.tooltip.Tooltip;

/**
 * Created by Oleg on 05.12.2016.
 */

public class Tooltips {

    public Tooltips(View v, int id, Tooltip.Gravity gravity, int duration, String text) {

        Tooltip.make(v.getContext(),
                new Tooltip.Builder(id)
                        .anchor(v, gravity)
                        .closePolicy(new Tooltip.ClosePolicy()
                                .insidePolicy(true, false)
                                .outsidePolicy(true, false), duration)
                        .activateDelay(500)
                        .showDelay(150)
                        .text(text)
                        .maxWidth(500)
                        .withArrow(true)
                        .withOverlay(true)
                        .floatingAnimation(Tooltip.AnimationBuilder.DEFAULT)
                        .build()
        ).show();
    }
}
