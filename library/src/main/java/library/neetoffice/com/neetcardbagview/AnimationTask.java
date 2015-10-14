package library.neetoffice.com.neetcardbagview;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;

/**
 * Created by Deo-chainmeans on 2015/10/14.
 */
public class AnimationTask {
    private AnimatorSet set;
    private boolean AnimationPlaying;
    private final Animator.AnimatorListener listener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {
            AnimationPlaying = true;
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            AnimationPlaying = false;
            set = null;
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    };

    public AnimatorSet.Builder play(ObjectAnimator animator) {
        set = new AnimatorSet();
        return set.play(animator);
    }

    public void start() {
        if (!AnimationPlaying && set != null) {
            AnimationPlaying = true;
            set.addListener(listener);
            set.start();
        }
    }

    public boolean isAnimationPlaying() {
        return AnimationPlaying;
    }
}
