package library.neetoffice.com.neetcardbagview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Deo on 2015/10/13.
 */
public abstract class CardBagTextAdapter extends CardBagAdapter {

    public CardBagTextAdapter(Context context) {
        super(context);
    }

    @Override
    public View getCardTitleView(int position, View titleView, ViewGroup parent) {
        if (titleView == null) {
            titleView = LayoutInflater.from(getContext()).inflate(R.layout.default_card_texttitle, parent, false);
        }
        CharSequence title = getCardTitle(position);
        ((TextView) titleView).setText(title);
        return titleView;
    }

    public abstract CharSequence getCardTitle(int position);
}
