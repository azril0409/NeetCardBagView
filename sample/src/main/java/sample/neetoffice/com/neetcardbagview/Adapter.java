package sample.neetoffice.com.neetcardbagview;

import android.content.Context;
import android.graphics.drawable.Drawable;
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
        titles = new String[]{"Android 1", "Android 2", "Android 3", "Android 4"};
    }

    @Override
    public Drawable getCardBackground(int position) {
        if (position == 0) {
            return getContext().getResources().getDrawable(R.drawable.android1,getContext().getTheme());
        } else if (position == 1) {
            return getContext().getResources().getDrawable(R.drawable.android2,getContext().getTheme());
        } else if (position == 2) {
            return getContext().getResources().getDrawable(R.drawable.android3,getContext().getTheme());
        } else if (position == 3) {
            return getContext().getResources().getDrawable(R.drawable.android4,getContext().getTheme());
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
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.cell, parent, false);
        } else {

        }
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "onClick", Toast.LENGTH_SHORT).show();
            }
        });
        return convertView;
    }
}
