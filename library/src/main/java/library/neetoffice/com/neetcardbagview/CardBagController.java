package library.neetoffice.com.neetcardbagview;

import android.content.Context;
import android.util.Log;
import android.widget.LinearLayout;

import java.util.Objects;

/**
 * Created by Deo-chainmeans on 2015/10/15.
 */
public class CardBagController extends LinearLayout {
    private float originalY = 0;
    int index;

    public CardBagController(Context context) {
        super(context);
    }

    @Override
    public void setY(float y) {
        originalY = y;
        super.setY(locus(y));
    }

    public float getOriginalY() {
        return originalY;
    }

    private float locus(float y) {
        CardBagView cardBagView = (CardBagView) getParent();
        Log.d(getClass().getSimpleName() + "_" + index, String.format("cardBagView getParent : %s", cardBagView.getClass().getSimpleName()));
        Log.d(getClass().getSimpleName() + "_" + index, String.format("cardBagView sumY : %s", cardBagView.sumY));
        final float pyt = cardBagView.getPaddingTop();
        if (y < pyt ) {
            return pyt;
        }
        return y;
    }

    public void setUnOriginalY(float y){
        super.setY(y);
    };
}
