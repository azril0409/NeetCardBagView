package library.neetoffice.com.neetcardbagview;

import android.content.Context;
import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;

/**
 * Created by Deo on 2015/10/13.
 */
public abstract class CardBagAdapter implements Adapter {
    private final DataSetObservable mDataSetObservable = new DataSetObservable();
    private Context mContext;

    public CardBagAdapter(Context context) {
        this.mContext = context;
    }

    public Context getContext() {
        return mContext;
    }

    public abstract View getCardTitleView(int position, View titleView, ViewGroup parent);


    public Drawable getCardBackground(int position) {
        Drawable drawable = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            drawable = mContext.getResources().getDrawable(R.drawable.default_card_layout_background, mContext.getTheme());
        } else {
            drawable = mContext.getResources().getDrawable(R.drawable.default_card_layout_background);
        }
        return drawable;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        mDataSetObservable.registerObserver(observer);
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        mDataSetObservable.unregisterObserver(observer);
    }

    public void notifyDataSetChanged() {
        mDataSetObservable.notifyChanged();
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    public boolean isEmpty() {
        return getCount() == 0;
    }
}
