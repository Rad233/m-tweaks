package me.melontini.tweaks;

import me.melontini.tweaks.util.LogUtil;

public class TweaksEarlyRiser implements Runnable {
    @Override
    public void run() {
        LogUtil.importantInfo("Definitely up to a lot of good");
    }
}
