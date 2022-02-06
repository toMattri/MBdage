package it.motta.mbdage.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import it.motta.mbdage.models.ItemPassaggi;

public class AdapterAccessi extends RecyclerView.Adapter<AdapterAccessi.ViewHolder> implements View.OnClickListener{

    private final Context mContext;
    private final ArrayList<ItemPassaggi> itemPassaggi;
    private int selected;

    public AdapterAccessi(Context mContext, ArrayList<ItemPassaggi> itemPassaggis) {
        super();
        this.mContext = mContext;
        this.itemPassaggi = itemPassaggis;
        this.selected = -1;
    }

    private void setSelected(int pos){
        int oldPosition = this.selected;
        this.selected = pos;
        notifyItemChanged(oldPosition);
        notifyItemChanged(selected);
    }

    private ItemPassaggi getItem(int position){
        return position >= 0 && position < getItemCount() ? itemPassaggi.get(position) : null;
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
        return itemPassaggi != null ? itemPassaggi.size() : 0;
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
