package it.motta.mbdage.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import it.motta.mbdage.models.ItemAccessi;


public class AdapterAccessi extends RecyclerView.Adapter<AdapterAccessi.ViewHolder> implements View.OnClickListener{


    private final Context mContext;
    private final ArrayList<ItemAccessi> itemAccessis;
    private int selected;

    public AdapterAccessi(Context mContext, ArrayList<ItemAccessi> itemAccessis) {
        super();
        this.mContext = mContext;
        this.itemAccessis = itemAccessis;
        this.selected = -1;
    }

    private void setSelected(int pos){
        int oldPosition = this.selected;
        this.selected = pos;
        notifyItemChanged(oldPosition);
        notifyItemChanged(selected);
    }

    private ItemAccessi getItem(int position){
        return position >= 0 && position < getItemCount() ? itemAccessis.get(position) : null;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }


    @Override
    public void onBindViewHolder(@NonNull AdapterAccessi.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return itemAccessis != null ? itemAccessis.size() : 0;
    }


    @Override
    public void onClick(View v) {

    }

    class ViewHolder extends RecyclerView.ViewHolder{

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

}
