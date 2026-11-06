package com.sokoban.model.box;

public class WeakBox extends Box {
    private int remainingPushes;

    /** @param maxPushes pushes the box withstands; the next one breaks it */
    public WeakBox(int maxPushes) {
        this.remainingPushes = maxPushes;
    }

    @Override
    public int getRemainingPushes() {
        return remainingPushes;
    }

    @Override
    public void setRemainingPushes(int remaining) {
        this.remainingPushes = remaining;
    }

    @Override
    public BoxType getType() {
        return BoxType.WEAK;
    }

    @Override
    protected PushResult onPushed() {
        if (remainingPushes == 0) {
            return PushResult.BROKEN;
        }
        remainingPushes--;
        return PushResult.PUSHED;
    }
}
