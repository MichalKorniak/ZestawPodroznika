package com.example.admin.zestawpodroznika;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class LocalisationAdapter extends BaseAdapter
{
    private Context context;
    private LayoutInflater layoutInflater;
    private LocDbDirector dbDirector;

    public LocalisationAdapter(Context context)
    {
        this.context = context;
        this.dbDirector=new LocDbDirector(context);
        this.layoutInflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);;
    }

    @Override
    public int getCount()
    {

        return dbDirector.Count();
    }

    @Override
    public Object getItem(int position)
    {
        return dbDirector.GetLoc(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View rowView = layoutInflater.inflate(R.layout.list_item_localisation, parent, false);

        TextView location=(TextView)rowView.findViewById(R.id.txt_location);
        TextView lati=(TextView)rowView.findViewById(R.id.txt_Lati);
        TextView longi=(TextView)rowView.findViewById(R.id.txt_Longi);

        Localisation loc=dbDirector.GetLoc(position);

        location.setText(loc.getName());
        longi.setText(String.format("%.4f", loc.getLongi()));
        lati.setText(String.format("%.4f", loc.getLati()));

        return rowView;

    }
}
