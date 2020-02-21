package com.example.testingmaps.Util.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.testingmaps.R;
import com.example.testingmaps.Storage.Address;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AdapterAddress extends RecyclerView.Adapter<AdapterAddress.AdapterViewHolder> {


    private List<Address> addressClients;
    private LayoutInflater layoutInflater;
    private Context context;

    public AdapterAddress(Context context, List<Address> addressClients){
        this.context=context;
        layoutInflater = LayoutInflater.from(context);
        this.addressClients = addressClients;
    }


    @Override
    public AdapterAddress.AdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_address,parent,false);
        return new AdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AdapterAddress.AdapterViewHolder holder, int position) {
        Address addressClient = addressClients.get(position);
        holder.address.setText(addressClient.getAddress());
    }

    @Override
    public int getItemCount() {
        return addressClients.size();
    }

    public class AdapterViewHolder extends RecyclerView.ViewHolder{

        TextView address;

        public AdapterViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
}
