package com.example.duan1_nhom2.AdapterClass;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.duan1_nhom2.DAOClass.DAO_BinhLuan;
import com.example.duan1_nhom2.FragmentClass.NhacDangNgheFragment;
import com.example.duan1_nhom2.Model.BinhLuan;
import com.example.duan1_nhom2.Model.NguoiDung;
import com.example.duan1_nhom2.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;

public class BinhLuan_rvAdapter extends RecyclerView.Adapter<BinhLuan_rvAdapter.BinhLuan_ViewHolder>{
    private Context context;
    private ArrayList<BinhLuan> dsbl;
    private ArrayList<NguoiDung> dsnd;
    NhacDangNgheFragment fragment;
    public BinhLuan_rvAdapter(Context context, NhacDangNgheFragment fragment){
        this.context = context;
        dsbl = new ArrayList<>();
        dsnd = new ArrayList<>();
        this.fragment = fragment;
    }
    @NonNull
    @Override
    public BinhLuan_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BinhLuan_ViewHolder(
                LayoutInflater.from(context).inflate(R.layout.adapter_binhluan, parent, false),
                context
        );
    }

    @Override
    public void onBindViewHolder(@NonNull BinhLuan_ViewHolder holder, int position) {
        BinhLuan binhLuan = dsbl.get(position);
        DAO_BinhLuan.getUserCommentProfile(context ,binhLuan.getMaNguoiDung(), binhLuan.getBinhLuan(),
                holder.txtTenNguoiDung, holder.txtBinhLuan, holder.ivAvatarNguoiDung);
    }

    @Override
    public int getItemCount() {
        fragment.setTotalComment(dsbl.size());
        return dsbl.size();
    }

    static class BinhLuan_ViewHolder extends RecyclerView.ViewHolder{
        TextView txtTenNguoiDung, txtBinhLuan;
        ImageView ivAvatarNguoiDung;
        Context context;
        public BinhLuan_ViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            this.context = context;
            txtTenNguoiDung = itemView.findViewById(R.id.txtTenNguoiDung);
            txtBinhLuan = itemView.findViewById(R.id.txtBinhLuan);
            ivAvatarNguoiDung = itemView.findViewById(R.id.ivAvatarNguoiDung);
        }
    }
    public void updateAdapter(BinhLuan binhLuan){
        dsbl.add(binhLuan);
        notifyDataSetChanged();
    }
    public void clearItems(){
        dsbl.clear();
        notifyDataSetChanged();
    }
}
