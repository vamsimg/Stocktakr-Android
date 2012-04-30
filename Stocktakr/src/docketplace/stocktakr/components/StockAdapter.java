package docketplace.stocktakr.components;

import java.util.ArrayList;

import docketplace.stocktakr.R;
import docketplace.stocktakr.data.StockRecord;
import android.content.Context;
import android.view.*;
import android.widget.*;



public class StockAdapter extends ArrayAdapter<StockRecord> {
    private ArrayList<StockRecord> stock;

    private Context context;
    
    public StockAdapter(Context context, ArrayList<StockRecord> stock) {
        super(context, R.layout.stock_record, stock);

        this.context = context;
        
        this.stock = stock;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        
        if (v == null) {
            LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.stock_record, null);
        }

        StockRecord record = stock.get(position);

        if (record != null) {
            TextView barcode     = (TextView)v.findViewById(R.id.stock_barcode);
            TextView description = (TextView)v.findViewById(R.id.stock_description);
            TextView quantity    = (TextView)v.findViewById(R.id.stock_quantity);

            if (barcode != null) {
                barcode.setText(record.barcode);
            }

            if (description != null) {
                description.setText(record.description);
            }

            if (quantity != null) {
                quantity.setText(String.format("%1$,.2f", record.quantity));
            }
        }

        return v;
    }
}
