package com.shaheen.like4like;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import androidx.annotation.ArrayRes;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import android.util.AttributeSet;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Filter;



public class ComboBox extends AppCompatAutoCompleteTextView implements AdapterView.OnItemClickListener {

    private boolean isPopup;
    private int mPosition = -1;

    public ComboBox(Context context){
        super(context);
        setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_expandable_list_item_1, new String[0]));
        setOnItemClickListener(this);
        setKeyListener(null);
    }

    public ComboBox(Context context, AttributeSet attributes){
        super(context, attributes);
        setAdapter(new ComboBoxAdapter(context, attributes.getAttributeListValue("http://schemas.android.com/apk/res/android", "entries", new String[0], R.array.default_empty_list)));
        setOnItemClickListener(this);
        setKeyListener(null);
    }

    public ComboBox(Context context, AttributeSet attributes, int arg2){
        super(context, attributes, arg2);
        setAdapter(new ComboBoxAdapter(context, attributes.getAttributeListValue("http://schemas.android.com/apk/res/android", "entries", new String[0], R.array.default_empty_list)));
        setOnItemClickListener(this);
        setKeyListener(null);
    }

    public static class ComboBoxAdapter extends ArrayAdapter<String> {

        private final String[] list;

        public ComboBoxAdapter(Context context, @ArrayRes int array){
            super(context, android.R.layout.simple_expandable_list_item_1, context.getResources().getStringArray(array));
            list = context.getResources().getStringArray(array);
        }

        @Override
        public Filter getFilter(){
            return new Filter(){
                @Override
                protected FilterResults performFiltering(CharSequence constraint){
                    FilterResults out = new FilterResults();
                    out.values = list;
                    out.count = list.length;
                    return out;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results){
                    notifyDataSetChanged();
                }
            };
        }

    }

    @Override
    public boolean enoughToFilter(){
        return true;
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect){
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        if (focused){
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getWindowToken(), 0);
            showDropDown();
        }
    }

    @Override
    public boolean performClick(){
        if (isPopup){
            dismissDropDown();
        }
        else {
            showDropDown();
        }
        return super.performClick();
    }

    @Override
    public void showDropDown(){
        super.showDropDown();
        isPopup = true;
    }

    @Override
    public void dismissDropDown(){
        super.dismissDropDown();
        isPopup = false;
    }

    @Override
    public void setCompoundDrawablesWithIntrinsicBounds(Drawable left, Drawable top, Drawable right, Drawable bottom){
        Drawable dropdownIcon = ContextCompat.getDrawable(getContext(), R.drawable.ic_arrow_drop_down_black_24dp);
        if (dropdownIcon != null){
            right = dropdownIcon;
            right.mutate().setAlpha(66);
        }
        super.setCompoundDrawablesRelativeWithIntrinsicBounds(left, top, right, bottom);
    }

    public int getPosition(){
        return mPosition;
    }

    public String getCurrentText(){
        if (mPosition == -1){
            return "";
        }
        else {
            return getText().toString();
        }
    }

    public void registerDataSetObserver(DataSetObserver observer){
        getAdapter().registerDataSetObserver(observer);
    }

    public void unregisterDataSetObserver(DataSetObserver observer){
        getAdapter().unregisterDataSetObserver(observer);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id){
        // setText(parent.getItemAtPosition(position).toString());
        mPosition = position;
    }

}

