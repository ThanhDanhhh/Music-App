package com.example.duan1_nhom2.DialogClass;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.duan1_nhom2.DAOClass.DAO_NguoiDung;
import com.example.duan1_nhom2.R;
import com.squareup.picasso.Picasso;

public class DialogDoiAnhDaiDien extends DialogFragment {
    TextView txtXacNhan;
    Button btnChonAnh;
    ImageView ivCloseDialog, ivAnhNguoiDung;
    String userID = "";
    String urlAnh = "";
    Uri uri;
    public DialogDoiAnhDaiDien(String userID, String urlAnh){
        this.userID = userID;
        this.urlAnh = urlAnh;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_doianh_daidien, container, false);
        findView(view);
        if (!urlAnh.isEmpty()){
            Picasso.with(getContext()).load(urlAnh).into(ivAnhNguoiDung);
        }
        ivCloseDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });
        btnChonAnh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImageFile();
            }
        });
        txtXacNhan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DAO_NguoiDung.changeUserPhoto(uri, getContext(), DialogDoiAnhDaiDien.this, userID);
            }
        });
        return view;
    }
    private void findView(View view){
        txtXacNhan = view.findViewById(R.id.txtXacNhan);
        ivAnhNguoiDung = view.findViewById(R.id.ivAnhNguoiDung);
        btnChonAnh = view.findViewById(R.id.btnChonAnh);
        ivCloseDialog = view.findViewById(R.id.ivCloseDialog);
    }
    private void chooseImageFile(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null && data.getData() != null){
            Uri uri = data.getData();
            this.uri = uri;
            Picasso.with(getContext()).load(uri).into(ivAnhNguoiDung);
        }
    }
}
