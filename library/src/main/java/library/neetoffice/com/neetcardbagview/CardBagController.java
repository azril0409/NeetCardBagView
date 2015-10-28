package library.neetoffice.com.neetcardbagview;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.Log;
import android.view.animation.Animation;
import android.widget.LinearLayout;

import java.util.Objects;

/**
 * Created by Deo-chainmeans on 2015/10/15.
 */
class CardBagController extends LinearLayout {
    private static final String TAG = CardBagController.class.getSimpleName();
    private final float titleHeight;
    private final int index;

    public CardBagController(Context context, int index) {
        super(context);
        this.index = index;
        titleHeight = getResources().getDimensionPixelSize(R.dimen.default_card_layout_title);
    }


    private float computeTargetY() {
        CardBagView cardBagView = (CardBagView) getParent();
        final float scrollY = cardBagView.getScrollY();
        Log.d(TAG + "_" + index, String.format("scrollY : %s", scrollY));
        final float pyt = cardBagView.getPaddingTop();
        Log.d(TAG + "_" + index, String.format("pyt = %s ", pyt));
        final float py = pyt + index * titleHeight;
        Log.d(TAG + "_" + index, String.format("py = %s ", py));
        final float topY = scrollY + pyt;
        Log.d(TAG + "_" + index, String.format("topY = %s ", topY));
        float ty;
        if (topY > py) {
            ty = topY;
        } else {
            ty = py;
        }
        return ty;
    }

    public void move() {
        setY(computeTargetY());
    }

    public void open(int index) {
        Log.d(TAG + "_" + index, String.format("difference index = %s ", index-this.index));
        CardBagView cardBagView = (CardBagView) getParent();
        final float scrollY = cardBagView.getScrollY();
        final float pyt = cardBagView.getPaddingTop();
        final float height = cardBagView.getMeasuredHeight();
        float ty;
        if(index-this.index>0){
            ty = scrollY + pyt;
        }else if(index-this.index<0){
            ty = scrollY + height-titleHeight-(index-this.index)*titleHeight/2;
        }else {
            ty = scrollY + pyt;
        }
        Log.d(TAG + "_" + this.index, String.format("ty = %s ", ty));
        AnimatorSet set = new AnimatorSet();
        ObjectAnimator animation = ObjectAnimator.ofFloat(this, "Y", getY(), ty);
        set.play(animation);
        set.start();
    }

    public void close() {
        AnimatorSet set = new AnimatorSet();
        ObjectAnimator animation = ObjectAnimator.ofFloat(this, "Y", getY(), computeTargetY());
        set.play(animation);
        set.start();
    }
}
