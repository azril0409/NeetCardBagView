package library.neetoffice.com.neetcardbagview;

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
    private float startY = 0;
    private float startX = 0;
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
            View child = getChildAt(mCount - index - 1);
            CardBagController cardBagController = (CardBagController) child.getTag();
            child.setY(height - titleHeight * (index + 1));
            cardBagController.originalY = child.getY();
            cardBagController.originalX = child.getX();
        }
        Log.d(TAG, String.format("get Padding Top : %s", getPaddingTop()));
        Log.d(TAG, String.format("get Padding Bottom : %s", getPaddingBottom()));
        Log.d(TAG, String.format("get Padding Left : %s", getPaddingLeft()));
        Log.d(TAG, String.format("get Padding Right : %s", getPaddingRight()));
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    public void computeScroll() {
        Log.d(TAG + "_computeScroll", "computeScroll");
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
                startX = event.getX();
                startDownTime = Calendar.getInstance().getTimeInMillis();
            } else if (MotionEvent.ACTION_MOVE == action) {
                float y = event.getY();
                float x = event.getX();
                move(x - startX, y - startY);
                startY = y;
                startX = x;
            } else if (MotionEvent.ACTION_UP == action) {
                boolean isClick = isClick(event);
                if (!isClick) {
                    moveEnd();
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
                    animator = ObjectAnimator.ofFloat(view, "y", view.getY(), 0);
                } else if (i < position) {
                    animator = ObjectAnimator.ofFloat(view, "y", view.getY(), 0);
                } else if (i > position) {
                    animator = ObjectAnimator.ofFloat(view, "y", view.getY(), height + (titleHeight / 4F * (i - position - 3)));
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
                View view = getChildAt(i);
                CardBagController cardBagController = (CardBagController) view.getTag();
                ObjectAnimator animatorY = ObjectAnimator.ofFloat(view, "y", view.getY(), cardBagController.originalY);
                ObjectAnimator animatorX = ObjectAnimator.ofFloat(view, "x", view.getX(), cardBagController.originalX);
                if (builder == null) {
                    builder = animationTask.play(animatorY).with(animatorX);
                } else {
                    builder.with(animatorY).with(animatorX);
                }
            }
            animationTask.start();
            showingCard = -1;
        }
    }

    private void move(float x, float y) {
        synchronized (this) {
            if (animationTask.isAnimationPlaying()) {
                return;
            }
            final float height = getMeasuredHeight();
            final float titleHeight = getResources().getDimensionPixelSize(R.dimen.default_card_layout_title);
            Log.d(TAG + "_move", String.format(" height = %s", height));
            Log.d(TAG + "_move", String.format("titleHeight = %s", titleHeight));
            final int mCount = getChildCount();
            if (getChildAt(mCount - 1).getY() < height - titleHeight * 2) {
                return;
            }
            boolean isSave = false;
            if (height > getChildAt(0).getY() + titleHeight * mCount) {
                isSave = true;
            } else if (height < getChildAt(0).getY() + titleHeight) {
                isSave = true;
            }
            for (int index = 0; index < mCount; index++) {
                View child = getChildAt(mCount - index - 1);
                float ty = child.getY() + y;
                child.setY(ty);
                if (!isSave) {
                    CardBagController cardBagController = (CardBagController) child.getTag();
                    cardBagController.originalX = child.getX();
                    final float bottomY = height + titleHeight * (mCount - index - 2);
                    cardBagController.originalY = child.getY() > bottomY ? bottomY : child.getY();
                    Log.d(TAG + "_move", String.format("index = %s , originalY = %s", mCount - index - 1, cardBagController.originalY));
                }
            }
        }
    }

    private void moveEnd() {
        synchronized (this) {
            if (animationTask.isAnimationPlaying()) {
                return;
            }
            final int mCount = getChildCount();
            AnimatorSet.Builder builder = null;
            final AnimatorSet set = new AnimatorSet();
            for (int index = 0; index < mCount; index++) {
                View child = getChildAt(mCount - index - 1);
                CardBagController cardBagController = (CardBagController) child.getTag();
                ObjectAnimator animatorY = ObjectAnimator.ofFloat(child, "y", child.getY(), cardBagController.originalY);
                ObjectAnimator animatorX = ObjectAnimator.ofFloat(child, "x", child.getX(), cardBagController.originalX);
                if (builder == null) {
                    builder = set.play(animatorY).with(animatorX);
                } else {
                    builder = builder.with(animatorY).with(animatorX);
                }
            }
            set.start();
        }
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
            LinearLayout child = new LinearLayout(getContext());
            CardBagController cardBagController = new CardBagController();
            child.setTag(cardBagController);
            final FrameLayout titleViewGroup = new FrameLayout(getContext());
            final FrameLayout contentViewGroup = new FrameLayout(getContext());
            final View cardTitleView = mAdapter.getCardTitleView(position, titleView, null);
            final View cardConvertView = mAdapter.getView(position, convertView, null);
            if (cardTitleView.getTag() != null) {
                titleView = cardTitleView.findFocus();
            }
            if (cardConvertView.getTag() != null) {
                convertView = cardConvertView.findFocus();
            }
            child.setOrientation(LinearLayout.VERTICAL);
            child.addView(titleViewGroup, new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen.default_card_layout_title)));
            child.addView(contentViewGroup, new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            titleViewGroup.addView(cardTitleView, new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            contentViewGroup.addView(cardConvertView, new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                child.setBackground(mAdapter.getCardBackground(position));
            } else {
                child.setBackgroundDrawable(mAdapter.getCardBackground(position));
            }
            titleViewGroup.setId(position);
            titleViewGroup.setOnClickListener(titleListener);
            addViewInLayout(child, position, new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
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
