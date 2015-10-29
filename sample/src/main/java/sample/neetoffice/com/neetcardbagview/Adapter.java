package sample.neetoffice.com.neetcardbagview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import library.neetoffice.com.neetcardbagview.CardBagTextAdapter;

/**
 * Created by Deo-chainmeans on 2015/10/13.
 */
public class Adapter extends CardBagTextAdapter {
    private String[] titles;

    public Adapter(Context context) {
        super(context);
        titles = context.getResources().getStringArray(R.array.list);
    }

    @Override
    public Drawable getCardBackground(int position) {
        if (position % 4 == 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                return getContext().getResources().getDrawable(R.drawable.android1, getContext().getTheme());
            } else {
                return getContext().getResources().getDrawable(R.drawable.android1);
            }
        } else if (position % 4 == 1) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                return getContext().getResources().getDrawable(R.drawable.android2, getContext().getTheme());
            } else {
                return getContext().getResources().getDrawable(R.drawable.android2);
            }
        } else if (position % 4 == 2) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                return getContext().getResources().getDrawable(R.drawable.android3, getContext().getTheme());
            } else {
                return getContext().getResources().getDrawable(R.drawable.android3);
            }
        } else if (position % 4 == 3) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                return getContext().getResources().getDrawable(R.drawable.android4, getContext().getTheme());
            } else {
                return getContext().getResources().getDrawable(R.drawable.android4);
            }
        }
        return super.getCardBackground(position);
    }

    @Override
    public CharSequence getCardTitle(int position) {
        return titles[position];
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public Object getItem(int position) {
        return titles[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (position % 2 == 0) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.cell1, parent, false);
        } else {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.cell2, parent, false);
        }
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), titles[position] + " onClick", Toast.LENGTH_SHORT).show();
            }
        });
        return convertView;
    }
}
