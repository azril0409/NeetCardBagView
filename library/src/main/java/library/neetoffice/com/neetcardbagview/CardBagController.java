package library.neetoffice.com.neetcardbagview;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.widget.LinearLayout;

/**
 * Created by Deo on 2015/10/15.
 */
class CardBagController extends LinearLayout {
    private static final String TAG = CardBagController.class.getSimpleName();
    private final float titleHeight;
    private final int index;
    private final float default_card_layout_elevatio;

    public CardBagController(Context context, int index) {
        super(context);
        this.index = index;
        titleHeight = getResources().getDimensionPixelSize(R.dimen.default_card_layout_title);
        default_card_layout_elevatio = getResources().getDimensionPixelSize(R.dimen.default_card_layout_elevatio);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setElevation(default_card_layout_elevatio);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final CardBagView cardBagView = (CardBagView) getParent();
        final int maxheight = (int) (cardBagView.getMeasuredHeight() - getPaddingTop() - getPaddingBottom() - titleHeight);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(maxheight, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private float computeTargetY() {
        final CardBagView cardBagView = (CardBagView) getParent();
        final float scrollY = cardBagView.getScrollY();
        final float pyt = cardBagView.getPaddingTop();
        final float py = pyt + index * titleHeight;
        final float topY = scrollY + pyt + cardBagView.paddinTop;
        float ty;
        if (topY > py) {
            ty = topY;
        } else {
            ty = py;
        }
        return ty;
    }

    private float computeTopY() {
        final CardBagView cardBagView = (CardBagView) getParent();
        final float scrollY = cardBagView.getScrollY();
        final float pyt = cardBagView.getPaddingTop();
        return scrollY + pyt + cardBagView.paddinTop;
    }

    private float computeY() {
        final CardBagView cardBagView = (CardBagView) getParent();
        final float pyt = cardBagView.getPaddingTop();
        return pyt + index * titleHeight;
    }

    public void move() {
        final float topY = computeTopY();
        final float py = computeY();
        if (topY > py) {
            setY(topY);
            setScaleX(py / topY);
            setScaleY(py / topY);
        } else {
            setY(py);
            setScaleX(1);
            setScaleY(1);
        }
        setY(computeTargetY());
    }

    public void open(int index) {
        final CardBagView cardBagView = (CardBagView) getParent();
        final float scrollY = cardBagView.getScrollY();
        final float pyt = cardBagView.getPaddingTop();
        final float height = cardBagView.getMeasuredHeight();
        float ty;
        if (index - this.index > 0) {
            ty = scrollY + pyt - getMeasuredHeight() - default_card_layout_elevatio;
        } else if (index - this.index < 0) {
            ty = scrollY + height - titleHeight - (index - this.index) * titleHeight / 2;
        } else {
            ty = scrollY + pyt;
        }
        final AnimatorSet set = new AnimatorSet();
        final ObjectAnimator animation = ObjectAnimator.ofFloat(this, "Y", getY(), ty);
        final ObjectAnimator animationx = ObjectAnimator.ofFloat(this, "ScaleX", getScaleX(), 1);
        final ObjectAnimator animationy = ObjectAnimator.ofFloat(this, "ScaleY", getScaleY(), 1);
        set.play(animation).with(animationx).with(animationy);
        set.start();
    }

    public void close() {
        final float topY = computeTopY();
        final float py = computeY();
        final AnimatorSet set = new AnimatorSet();
        final ObjectAnimator animation = ObjectAnimator.ofFloat(this, "Y", getY(), computeTargetY());
        float scale = 1f;
        if (topY > py) {
            scale = py / topY;
        }
        final ObjectAnimator animationx = ObjectAnimator.ofFloat(this, "ScaleX", getScaleX(), scale);
        final ObjectAnimator animationy = ObjectAnimator.ofFloat(this, "ScaleY", getScaleY(), scale);
        set.play(animation).with(animationx).with(animationy);
        set.start();
    }
}
