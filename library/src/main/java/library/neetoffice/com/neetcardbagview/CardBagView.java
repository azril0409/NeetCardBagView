package library.neetoffice.com.neetcardbagview;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Scroller;


/**
 * Created by Deo on 2015/10/12.
 */
public class CardBagView extends FrameLayout {
    private static final int STATUS_NORMAL = 0;
    private static final int STATUS_ONDOWN = 1;
    private static final int STATUS_ONSCROLL = 2;
    private static final int STATUS_SINGLETAPUP = 3;
    private static final int STATUS_FLING = 4;
    private static final String TAG = CardBagView.class.getSimpleName();
    private AdapterDataSetObserver mDataSetObserver;
    private CardBagAdapter mAdapter;
    private Scroller scroller;
    private GestureDetector gestureDetector;
    private int status = STATUS_NORMAL;
    private float topY;
    private float bottomY;
    private int showingCard = -1;
    private float topPercent = 0;
    float paddinTop = 0;
    private final OnClickListener titleListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (status != STATUS_ONSCROLL) {
                if (showingCard != -1) {
                    close();
                } else {
                    final int index = v.getId();
                    open(index);
                }
            }
        }
    };
    private final GestureDetector.OnGestureListener onGestureListener = new GestureDetector.OnGestureListener() {

        @Override
        public boolean onDown(MotionEvent e) {
            if (BugConfig.isPrintLog(getContext())) {
                Log.d(TAG + "_OnGestureListener", "onDown");
            }
            status = STATUS_ONDOWN;
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {
            if (BugConfig.isPrintLog(getContext())) {
                Log.d(TAG + "_OnGestureListener", "onShowPress");
            }
            status = STATUS_NORMAL;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            if (BugConfig.isPrintLog(getContext())) {
                Log.d(TAG + "_OnGestureListener", "onSingleTapUp");
            }
            status = STATUS_SINGLETAPUP;
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (BugConfig.isPrintLog(getContext())) {
                Log.d(TAG + "_OnGestureListener", "onScroll");
            }
            status = STATUS_ONSCROLL;
            scrollBy(0, computeTargetY(distanceY));
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            if (BugConfig.isPrintLog(getContext())) {
                Log.d(TAG + "_OnGestureListener", "onLongPress");
            }
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (BugConfig.isPrintLog(getContext())) {
                Log.d(TAG + "_OnGestureListener", String.format("onFling : velocityX = %s , velocityY = %s", velocityX, velocityY));
            }
            status = STATUS_FLING;
            snapToBottom((int) (-velocityY / 10));
            return false;
        }
    };

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
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CardBagView, defStyleAttr, defStyleRes);
            topPercent = typedArray.getFraction(R.styleable.CardBagView_library_cardbag_topPercent, 1, 1, 0f);
        }
        scroller = new Scroller(context);
        gestureDetector = new GestureDetector(context, onGestureListener);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int width = MeasureSpec.getSize(widthMeasureSpec);
        final int height = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        final int mCount = getChildCount();
        final float height = getMeasuredHeight();
        final float titleHeight = getResources().getDimensionPixelSize(R.dimen.default_card_layout_title);
        paddinTop = height * topPercent;
        bottomY = getPaddingBottom() + getPaddingBottom() - height;
        topY = bottomY + mCount * titleHeight;
        scrollTo(0, (int) -paddinTop);
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (showingCard != -1) {
            return false;
        }
        boolean returnTouchEvent = gestureDetector.onTouchEvent(event);
        if (MotionEvent.ACTION_UP == event.getAction()) {
            returnTouchEvent = status != STATUS_SINGLETAPUP;
            status = STATUS_NORMAL;
        }
        return returnTouchEvent;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (showingCard != -1) {
            if (MotionEvent.ACTION_DOWN == event.getAction()) {
                return true;
            } else if (MotionEvent.ACTION_MOVE == event.getAction()) {
                return true;
            } else if (MotionEvent.ACTION_UP == event.getAction()) {
                close();
                return false;
            }
        }
        return super.onTouchEvent(event);
    }

    public void open(int index) {
        if (BugConfig.isPrintLog(getContext())) {
            Log.d(TAG, "open = " + index);
        }
        showingCard = index;
        final int mCount = getChildCount();
        for (int i = 0; i < mCount; i++) {
            final CardBagController cardBagController = (CardBagController) getChildAt(i);
            cardBagController.open(index);
        }
    }

    public void close() {
        if (BugConfig.isPrintLog(getContext())) {
            Log.d(TAG, "close");
        }
        showingCard = -1;
        final int mCount = getChildCount();
        for (int i = 0; i < mCount; i++) {
            final CardBagController cardBagController = (CardBagController) getChildAt(i);
            cardBagController.close();
        }
    }

    private int computeTargetY(float y) {
        final float scrollY = getScrollY();
        final float titleHeight = getResources().getDimensionPixelSize(R.dimen.default_card_layout_title);
        int ty;
        if ((scrollY + y) < (bottomY + titleHeight)) {
            ty = (int) (bottomY + titleHeight - getScrollY());
        } else if ((scrollY + y) > topY) {
            ty = (int) topY - getScrollY();
        } else {
            ty = (int) y;
        }
        return ty;
    }

    private void snapToBottom(int y) {
        final int ty = computeTargetY(y);
        scroller.startScroll(0, getScrollY(), 0, ty, Math.abs(ty));
        invalidate();
    }

    @Override
    public void computeScroll() {
        if (scroller.computeScrollOffset()) {
            scrollTo(scroller.getCurrX(), scroller.getCurrY());
            postInvalidate();
        }
    }

    @Override
    protected synchronized void onScrollChanged(int l, int t, int oldl, int oldt) {
        final int mCount = getChildCount();
        for (int index = 0; index < mCount; index++) {
            CardBagController cardBagController = (CardBagController) getChildAt(index);
            cardBagController.move();
        }
        super.onScrollChanged(l, t, oldl, oldt);
    }

    public void setAdapter(CardBagAdapter cardBagAdapter) {
        if (mAdapter != null && mDataSetObserver == null) {
            mDataSetObserver = new AdapterDataSetObserver();
            mAdapter.registerDataSetObserver(mDataSetObserver);
        }
        removeAllViewsInLayout();
        mAdapter = cardBagAdapter;
        final int mCount = mAdapter.getCount();
        final int titleHeight = getResources().getDimensionPixelSize(R.dimen.default_card_layout_title);
        int position = 0;
        View titleView = null;
        View convertView = null;
        do {
            CardBagController child = new CardBagController(getContext(), position);
            final FrameLayout titleViewGroup = new FrameLayout(getContext());
            final ScrollView scrollView = new ScrollView(getContext());
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
            child.addView(titleViewGroup, new LayoutParams(LayoutParams.MATCH_PARENT, titleHeight));
            child.addView(scrollView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            scrollView.addView(contentViewGroup, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            titleViewGroup.addView(cardTitleView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            contentViewGroup.addView(cardConvertView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                child.setBackground(mAdapter.getCardBackground(position));
            } else {
                child.setBackgroundDrawable(mAdapter.getCardBackground(position));
            }
            titleViewGroup.setId(position);
            titleViewGroup.setOnClickListener(titleListener);
            addViewInLayout(child, position, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
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
            if (BugConfig.isPrintLog(getContext())) {
                Log.d(TAG, "onChanged");
            }
            requestLayout();
        }

        @Override
        public void onInvalidated() {
            if (BugConfig.isPrintLog(getContext())) {
                Log.d(TAG, "onInvalidated");
            }
            requestLayout();
        }
    }
}
