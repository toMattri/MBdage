package it.motta.mbdage.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import it.motta.mbdage.R;
import it.motta.mbdage.models.ItemPassaggi;
import it.motta.mbdage.utils.Parameters;

@SuppressLint({"SimpleDateFormat","SetTextI18n"})
public class AdapterAccessi extends RecyclerView.Adapter<AdapterAccessi.ViewHolder> implements View.OnClickListener{

    private final Context mContext;
    private final ArrayList<ItemPassaggi> itemPassaggi;
    private final SimpleDateFormat stringDoData,dataToString;
    private final StorageReference imagesRef;
    public AdapterAccessi(Context mContext, ArrayList<ItemPassaggi> itemPassaggis) {
        super();
        this.mContext = mContext;
        this.itemPassaggi = itemPassaggis;
        FirebaseStorage storage = FirebaseStorage.getInstance(Parameters.PATH_STORAGE);
        StorageReference storageRef = storage.getReference();
        imagesRef = storageRef.child("imagesVarco");
        this.stringDoData = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.dataToString = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    }

    private ItemPassaggi getItem(int position){
        return position >= 0 && position < getItemCount() ? itemPassaggi.get(position) : null;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_passaggi, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterAccessi.ViewHolder holder, int position) {
        ItemPassaggi itemPassaggi = getItem(position);
        if(itemPassaggi != null){
            holder.txtVarco.setText(itemPassaggi.getVarco().getDescrizione());
            holder.txtLogLat.setText(itemPassaggi.getVarco().getLongitudine() +"," + itemPassaggi.getVarco().getLatitudine());
            try {
                holder.txtDate.setText(dataToString.format(stringDoData.parse(itemPassaggi.getData())));
            } catch (ParseException e) {
                e.printStackTrace();
                holder.txtDate.setText(itemPassaggi.getData());
            }

            if(StringUtils.isEmpty(itemPassaggi.getVarco().getImg()))
                Picasso.get().load(R.drawable.ic_image).into(holder.imgVarco);
            else {
                imagesRef.child(itemPassaggi.getVarco().getImg()).getDownloadUrl().addOnSuccessListener(uri -> {
                    Picasso.get().load(uri.toString()).into(holder.imgVarco);
                }).addOnFailureListener(exception -> Picasso.get().load(R.drawable.ic_image).into(holder.imgVarco));
            }
        }
    }

    @Override
    public int getItemCount() {
        return itemPassaggi != null ? itemPassaggi.size() : 0;
    }

    @Override
    public void onClick(View v) {

    }

    public ArrayList<ItemPassaggi> getItems() {
        return itemPassaggi;
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private final ImageView imgVarco;
        private final TextView txtVarco,txtDate,txtLogLat;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.imgVarco = itemView.findViewById(R.id.imgVarco);
            this.txtVarco = itemView.findViewById(R.id.txtDisplayVarco);
            this.txtDate = itemView.findViewById(R.id.txtDate);
            this.txtLogLat = itemView.findViewById(R.id.txDisplayLogLat);

        }
    }

}
