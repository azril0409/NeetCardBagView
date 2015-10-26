package library.neetoffice.com.neetcardbagview;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import java.util.Calendar;

/**
 * Created by Deo-chainmeans on 2015/10/12.
 */
public class CardBagView extends FrameLayout {
    private static final String TAG = CardBagView.class.getSimpleName();
    private final static int NORMAL = 0;
    private final static int COLSE_DOWN = 4;
    private final AnimationTask animationTask = new AnimationTask();
    float sumY = 0;
    private float bottomY = 0;
    private float startY = 0;
    private long startDownTime = 0;
    private AdapterDataSetObserver mDataSetObserver;
    private CardBagAdapter mAdapter;
    private int showingCard = -1;
    private final OnClickListener titleListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d(TAG, "open : " + v.getId());
            open(v.getId());
        }
    };
    private VelocityTracker mVelocityTracker;
    private int TouchStatus = NORMAL;

    public CardBagView(Context context) {
        super(context);
        init(context, null, 0, 0);
    }

    public CardBagView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0, 0);
    }

    public CardBagView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CardBagView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        if (attrs != null) {

        } else {

        }
        mVelocityTracker = VelocityTracker.obtain();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int width = MeasureSpec.getSize(widthMeasureSpec);
        final int height = MeasureSpec.getSize(heightMeasureSpec);
        Log.d(TAG, String.format("onMeasure : width = %s , height = %s", width, height));
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        Log.d(TAG, "onLayout");
        final int mCount = getChildCount();
        final float height = getMeasuredHeight();
        final float titleHeight = getResources().getDimensionPixelSize(R.dimen.default_card_layout_title);
        for (int index = 0; index < mCount; index++) {
            CardBagController child = (CardBagController) getChildAt(index);
            child.setY(titleHeight * index);
            child.index = index;
        }
        sumY = 0;
        bottomY = height - (titleHeight * mCount);
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    public void computeScroll() {
        Log.d(TAG + "_computeScroll", "computeScroll");
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        if (showingCard > -1) {
            if (MotionEvent.ACTION_UP == action) {
                close();
            }
            return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        mVelocityTracker.addMovement(event);
        mVelocityTracker.computeCurrentVelocity(1);
        if (showingCard > -1) {
            if (MotionEvent.ACTION_DOWN == action) {
                final View child = getChildAt(showingCard);
                final RectF rect = new RectF(child.getX(), child.getY(), child.getX() + child.getMeasuredWidth(), child.getMeasuredHeight());
                if (!rect.contains(event.getX(), event.getY())) {
                    TouchStatus = COLSE_DOWN;
                }
            } else if (MotionEvent.ACTION_UP == action) {
                if (TouchStatus == COLSE_DOWN) {
                    TouchStatus = NORMAL;
                    final View child = getChildAt(showingCard);
                    final RectF rect = new RectF(child.getX(), child.getY(), child.getX() + child.getMeasuredWidth(), child.getMeasuredHeight());
                    if (!rect.contains(event.getX(), event.getY())) {
                        Log.d(TAG + "_onInterceptTouchEvent", "onInterceptTouchEvent : close()");
                        close();
                        return true;
                    }
                }
                TouchStatus = NORMAL;
            }
        } else {
            if (MotionEvent.ACTION_DOWN == action) {
                startY = event.getY();
                startDownTime = Calendar.getInstance().getTimeInMillis();
            } else if (MotionEvent.ACTION_MOVE == action) {
                float y = event.getY();
                move(y);
                startY = y;
            } else if (MotionEvent.ACTION_UP == action) {
                boolean isClick = isClick(event);
                if (!isClick) {
                    moveEnd(mVelocityTracker.getYVelocity());
                }
                startY = 0;
                startDownTime = 0;
                return !isClick;
            }
        }
        return false;
    }

    private boolean isClick(MotionEvent event) {
        final long time = Calendar.getInstance().getTimeInMillis();
        boolean isClick = false;
        Log.d(TAG + "_YVelocity", String.format("isClick.getYVelocity : %s", Math.abs(mVelocityTracker.getYVelocity())));
        if (time - startDownTime < 100 && Math.abs(mVelocityTracker.getYVelocity()) < 1) {
            isClick = true;
        }
        return isClick;
    }

    public void open(int position) {
        if (animationTask.isAnimationPlaying()) {
            return;
        }
        final float pty = getPaddingTop();
        if (showingCard < 0) {
            final float titleHeight = getResources().getDimensionPixelSize(R.dimen.default_card_layout_title);
            final float height = getMeasuredHeight();
            AnimatorSet.Builder builder = null;
            final int mCount = getChildCount();
            for (int i = 0; i < mCount; i++) {
                View view = getChildAt(i);
                ObjectAnimator animator = null;
                if (i == position) {
                    showingCard = position;
                    animator = ObjectAnimator.ofFloat(view, "unOriginalY", view.getY(), pty);
                } else if (i < position) {
                    animator = ObjectAnimator.ofFloat(view, "unOriginalY", view.getY(), pty);
                } else if (i > position) {
                    animator = ObjectAnimator.ofFloat(view, "unOriginalY", view.getY(), height + (titleHeight / 4F * (i - position - 3)));
                }
                if (animator != null) {
                    if (builder == null) {
                        builder = animationTask.play(animator);
                    } else {
                        builder.with(animator);
                    }
                }
            }
            animationTask.start();

        }
    }

    public void close() {
        if (animationTask.isAnimationPlaying()) {
            return;
        }
        if (showingCard > -1) {
            AnimatorSet.Builder builder = null;
            final int mCount = getChildCount();
            for (int i = 0; i < mCount; i++) {
                CardBagController view = (CardBagController) getChildAt(i);
                ObjectAnimator animatorY = ObjectAnimator.ofFloat(view, "unOriginalY", view.getY(), view.getOriginalY());
                if (builder == null) {
                    builder = animationTask.play(animatorY);
                } else {
                    builder.with(animatorY);
                }
            }
            animationTask.start();
            showingCard = -1;
        }
    }

    private void move(float y) {
        if (animationTask.isAnimationPlaying()) {
            return;
        }
        final float pby = getPaddingBottom();
        final float height = getMeasuredHeight();
        final float titleHeight = getResources().getDimensionPixelSize(R.dimen.default_card_layout_title);
        final int mCount = getChildCount();
        final float dy = y - startY;
        if ((getChildAt(mCount - 1).getY() + dy) < (height - titleHeight * 2 - pby)) {
            return;
        }
        sumY = sumY + dy;
        Log.d(TAG + "_move", String.format("sumY : %s", sumY));
        Log.d(TAG + "_move", String.format("dy : %s", dy));
        for (int index = 0; index < mCount; index++) {
            CardBagController child = (CardBagController) getChildAt(mCount - index - 1);
            float ty = child.getOriginalY() + dy;
            child.setY(ty);
        }

    }

    private void moveEnd(float velocityY) {
        if (animationTask.isAnimationPlaying()) {
            return;
        }
        Log.d(TAG + "_moveEnd", String.format("sumY : %s", sumY));
        final float titleHeight = getResources().getDimensionPixelSize(R.dimen.default_card_layout_title);
        final float pby = getPaddingBottom();
        final float height = getMeasuredHeight();
        final int mCount = getChildCount();
        AnimatorSet.Builder builder = null;
        final AnimatorSet set = new AnimatorSet();
        for (int index = 0; index < mCount; index++) {
            CardBagController child = (CardBagController) getChildAt(mCount - index - 1);
            ObjectAnimator animatorY;
            if (sumY < bottomY) {
                animatorY = ObjectAnimator.ofFloat(child, "y", child.getY(), height - pby - titleHeight * (index + 1));
            } else {
                animatorY = ObjectAnimator.ofFloat(child, "y", child.getY(), child.getOriginalY());
            }
            animatorY.setDuration(300);
            if (builder == null) {
                builder = set.play(animatorY);
            } else {
                builder = builder.with(animatorY);
            }
        }
        set.start();
        sumY = sumY < bottomY ? bottomY : sumY;
    }

    public void setAdapter(CardBagAdapter cardBagAdapter) {
        if (mAdapter != null && mDataSetObserver == null) {
            mDataSetObserver = new AdapterDataSetObserver();
            mAdapter.registerDataSetObserver(mDataSetObserver);
        }
        removeAllViewsInLayout();
        mAdapter = cardBagAdapter;

        final int mCount = mAdapter.getCount();
        int position = 0;
        View titleView = null;
        View convertView = null;
        do {
            Log.d(TAG, String.format("position = %s", position));
            CardBagController cardBagController = new CardBagController(getContext());
            final FrameLayout titleViewGroup = new FrameLayout(getContext());
            final FrameLayout contentViewGroup = new FrameLayout(getContext());
            final View cardTitleView = mAdapter.getCardTitleView(position, titleView, null);
            final View cardConvertView = mAdapter.getView(position, convertView, null);
            cardBagController.setOrientation(LinearLayout.VERTICAL);
            cardBagController.addView(titleViewGroup, new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen.default_card_layout_title)));
            cardBagController.addView(contentViewGroup, new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            titleViewGroup.addView(cardTitleView, new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            contentViewGroup.addView(cardConvertView, new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                cardBagController.setBackground(mAdapter.getCardBackground(position));
            } else {
                cardBagController.setBackgroundDrawable(mAdapter.getCardBackground(position));
            }
            titleViewGroup.setId(position);
            titleViewGroup.setOnClickListener(titleListener);
            addViewInLayout(cardBagController, position, new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            position++;
        } while (position < mCount);

        if (mAdapter != null) {
            mDataSetObserver = new AdapterDataSetObserver();
            mAdapter.registerDataSetObserver(mDataSetObserver);
        }
        requestLayout();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mAdapter != null && mDataSetObserver == null) {
            mDataSetObserver = new AdapterDataSetObserver();
            mAdapter.registerDataSetObserver(mDataSetObserver);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mAdapter != null && mDataSetObserver != null) {
            mAdapter.unregisterDataSetObserver(mDataSetObserver);
            mDataSetObserver = null;
        }
    }

    final class AdapterDataSetObserver extends DataSetObserver {
        @Override
        public void onChanged() {
            Log.d(TAG, "onChanged");
            requestLayout();
        }

        @Override
        public void onInvalidated() {
            Log.d(TAG, "onInvalidated");
            requestLayout();
        }
    }
}
