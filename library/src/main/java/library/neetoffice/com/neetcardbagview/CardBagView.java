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
    private float startY = 0;
    private long startDownTime = 0;
    private AdapterDataSetObserver mDataSetObserver;
    private CardBagAdapter mAdapter;
    private ShowingCard showingCard = null;
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
            child.setY(height - titleHeight * (index + 1));
        }
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    public void computeScroll() {
        Log.d(TAG, "computeScroll");
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        if (showingCard != null) {
            if (MotionEvent.ACTION_DOWN == action) {
                final View child = getChildAt(showingCard.id);
                final RectF rect = new RectF(child.getX(), child.getY(), child.getX() + child.getMeasuredWidth(), child.getMeasuredHeight());
                if (!rect.contains(event.getX(), event.getY())) {
                    TouchStatus = COLSE_DOWN;
                }
            } else if (MotionEvent.ACTION_UP == action) {
                if (TouchStatus == COLSE_DOWN) {
                    TouchStatus = NORMAL;
                    final View child = getChildAt(showingCard.id);
                    final RectF rect = new RectF(child.getX(), child.getY(), child.getX() + child.getMeasuredWidth(), child.getMeasuredHeight());
                    if (!rect.contains(event.getX(), event.getY())) {
                        Log.d(TAG, "onInterceptTouchEvent : close()");
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
                move(y - startY);
                startY = y;
            } else if (MotionEvent.ACTION_UP == action) {
                boolean isClick = isClick(event);
                if (!isClick) {
                    Log.d(TAG, "onInterceptTouchEvent : moveEnd()");
                    moveEnd();
                }
                startY = 0;
                startDownTime = 0;
                return !isClick;
            }
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        if (showingCard != null) {
            if (MotionEvent.ACTION_DOWN == action) {
                TouchStatus = COLSE_DOWN;
                return true;
            } else if (MotionEvent.ACTION_UP == action) {
                TouchStatus = NORMAL;
                final View child = getChildAt(showingCard.id);
                final RectF rect = new RectF(child.getX(), child.getY(), child.getX() + child.getMeasuredWidth(), child.getMeasuredHeight());
                if (!rect.contains(event.getX(), event.getY())) {
                    Log.d(TAG, "onTouchEvents : close()");
                    close();
                }
            }
        } else {
            if (mVelocityTracker == null) {
                mVelocityTracker = VelocityTracker.obtain();
            }
            mVelocityTracker.addMovement(event);
            if (MotionEvent.ACTION_DOWN == action) {
                startY = event.getY();
                startDownTime = Calendar.getInstance().getTimeInMillis();
            } else if (MotionEvent.ACTION_MOVE == action) {
                float y = event.getY();
                move(y - startY);
                startY = y;
            } else if (MotionEvent.ACTION_UP == action) {
                boolean isClick = false;
                final View firstChild = getChildAt(0);
                final View lastChild = getChildAt(getChildCount() - 1);
                final RectF rect = new RectF(firstChild.getX(), firstChild.getY(), lastChild.getX() + lastChild.getMeasuredWidth(), firstChild.getY() + lastChild.getMeasuredHeight());
                if (rect.contains(event.getX(), event.getY())) {
                    isClick = true;
                } else {
                    isClick = false;
                }
                if (isClick) {
                    Log.d(TAG, "onTouchEvents : moveEnd()");
                    moveEnd();
                }
                startY = 0;
                startDownTime = 0;
            }
        }
        boolean b = super.onTouchEvent(event);
        return b;
    }

    private boolean isClick(MotionEvent event) {
        final long time = Calendar.getInstance().getTimeInMillis();
        boolean isClick = false;
        if (time - startDownTime < 100 && event.getY() - startY < 10) {
            isClick = true;
        }
        return isClick;
    }

    public void open(int position) {
        if (animationTask.isAnimationPlaying()) {
            return;
        }
        if (showingCard == null) {
            final float titleHeight = getResources().getDimensionPixelSize(R.dimen.default_card_layout_title);
            final float height = getMeasuredHeight();
            AnimatorSet.Builder builder = null;
            final int mCount = getChildCount();
            for (int i = 0; i < mCount; i++) {
                View view = getChildAt(i);
                ObjectAnimator animator = null;
                if (i == position) {
                    showingCard = new ShowingCard(position, view.getY());
                    animator = ObjectAnimator.ofFloat(view, "y", view.getY(), 0);
                } else if (i < position) {
                    animator = ObjectAnimator.ofFloat(view, "y", view.getY(), 0);
                } else if (i > position) {
                    Log.d(TAG, String.format("p = %s", i - position - 3));
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
        if (showingCard != null) {
            final float titleHeight = getResources().getDimensionPixelSize(R.dimen.default_card_layout_title);
            final float height = getMeasuredHeight();
            AnimatorSet.Builder builder = null;
            final int mCount = getChildCount();
            for (int i = 0; i < mCount; i++) {
                View view = getChildAt(i);
                ObjectAnimator animator = null;
                if (i == showingCard.id) {
                    animator = ObjectAnimator.ofFloat(view, "y", view.getY(), showingCard.originalY);
                } else {
                    animator = ObjectAnimator.ofFloat(view, "y", view.getY(), showingCard.originalY + titleHeight * (i - showingCard.id));
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
            showingCard = null;
        }
    }


    private void move(float y) {
        if (animationTask.isAnimationPlaying()) {
            return;
        }
        final float height = getMeasuredHeight();
        final float titleHeight = getResources().getDimensionPixelSize(R.dimen.default_card_layout_title);
        final int mCount = getChildCount();
        if (getChildAt(mCount - 1).getY() < height - titleHeight * 2) {
            return;
        }
        for (int index = 0; index < mCount; index++) {
            View child = getChildAt(mCount - index - 1);
            child.setY(child.getY() + y);
        }

    }

    private void moveEnd() {
        if (animationTask.isAnimationPlaying()) {
            return;
        }
        final float height = getMeasuredHeight();
        final float titleHeight = getResources().getDimensionPixelSize(R.dimen.default_card_layout_title);
        final int mCount = getChildCount();
        if (height > getChildAt(mCount - 1).getY() + titleHeight) {
            AnimatorSet.Builder builder = null;
            final AnimatorSet set = new AnimatorSet();
            for (int index = 0; index < mCount; index++) {
                View child = getChildAt(mCount - index - 1);
                ObjectAnimator animator = ObjectAnimator.ofFloat(child, "y", child.getY(), height - titleHeight * (index + 1));
                if (builder == null) {
                    builder = set.play(animator);
                } else {
                    builder = builder.with(animator);
                }
            }
            set.start();
        } else if (height < getChildAt(0).getY() + titleHeight) {
            AnimatorSet.Builder builder = null;
            final AnimatorSet set = new AnimatorSet();
            for (int index = 0; index < mCount; index++) {
                View child = getChildAt(index);
                ObjectAnimator animator = ObjectAnimator.ofFloat(child, "y", child.getY(), height + titleHeight * (index - 1));
                if (builder == null) {
                    builder = set.play(animator);
                } else {
                    builder = builder.with(animator);
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

    private class OpenAnimatorListener implements Animator.AnimatorListener {
        private final int index;

        private OpenAnimatorListener(int index) {
            this.index = index;
        }

        @Override
        public void onAnimationStart(Animator animation) {
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            View child = getChildAt(index);
            Log.d(TAG, "---onAnimationEnd---");
            Log.d(TAG, String.format("x = %s", child.getX()));
            Log.d(TAG, String.format("y = %s", child.getY()));
            Log.d(TAG, String.format("height = %s", child.getMeasuredHeight()));
            Log.d(TAG, String.format("width = %s", child.getMeasuredWidth()));
            Log.d(TAG, String.format("top = %s", child.getTop()));
            Log.d(TAG, String.format("left = %s", child.getLeft()));
            Log.d(TAG, "---onAnimationEnd---");
            //new RelativeLayout.LayoutParams();
            //removeViewInLayout(child);
            //ViewGroup content = (ViewGroup) getRootView().findViewById(android.R.id.content);  ;
            //content.addView(child);
        }

        @Override
        public void onAnimationCancel(Animator animation) {
        }

        @Override
        public void onAnimationRepeat(Animator animation) {
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
