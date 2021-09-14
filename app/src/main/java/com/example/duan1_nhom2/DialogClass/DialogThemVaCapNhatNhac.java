package com.example.duan1_nhom2.DialogClass;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.duan1_nhom2.AdditionalFunctions.AdditionalFunctions;
import com.example.duan1_nhom2.DAOClass.DAO_Nhac;
import com.example.duan1_nhom2.Model.NgheSi;
import com.example.duan1_nhom2.Model.Nhac;
import com.example.duan1_nhom2.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DialogThemVaCapNhatNhac extends DialogFragment {
    EditText txtTenNhac, txtTenFile;
    AutoCompleteTextView txtTheLoai, txtTenNgheSi;
    TextView txtXacNhan, txtThemVaSuaNhac;
    ImageView ivCloseDialog;
    Button btnChonAudio;
    ArrayList<String> dstl;
    ArrayList<String> dsns;
    DatabaseReference myDatabaseRef1 = FirebaseDatabase.getInstance().getReference("TheLoai");
    DatabaseReference myDatabaseRef2 = FirebaseDatabase.getInstance().getReference("NgheSi");
    String fileName = "";
    Uri uri = null;
    Nhac nhac;
    boolean type = false;
    public DialogThemVaCapNhatNhac(Nhac nhac, boolean type){
        this.nhac = nhac;
        this.type = type;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_them_capnhat_nhac, container, false);
        findView(view);
        String toDo = type?"Cập Nhật Nhạc":"Thêm Nhạc";
        txtThemVaSuaNhac.setText(toDo);
        displayInfo();
        dstl = new ArrayList<>();
        dsns = new ArrayList<>();
        myDatabaseRef1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot i: snapshot.getChildren()){
                        String tenTheLoai = i.getValue(String.class);
                        dstl.add(tenTheLoai);
                    }
                    txtTheLoai.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, dstl));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        myDatabaseRef2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot i: snapshot.getChildren()){
                        NgheSi ngheSi = i.getValue(NgheSi.class);
                        dsns.add(ngheSi.getTenNgheSi());
                    }
                    txtTenNgheSi.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, dsns));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        txtXacNhan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type){
                    updateMusic();
                }else{
                    uploadMusic();
                }
            }
        });
        btnChonAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseAudioFile();
            }
        });
        ivCloseDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });
        return view;
    }
    private void findView(View view){
        txtTenNgheSi = view.findViewById(R.id.txtTenNgheSi);
        txtTenNhac = view.findViewById(R.id.txtTenNhac);
        txtTheLoai = view.findViewById(R.id.txtTheLoai);
        txtXacNhan = view.findViewById(R.id.txtXacNhan);
        txtTenFile = view.findViewById(R.id.txtTenFile);
        txtThemVaSuaNhac = view.findViewById(R.id.txtThemVaSuaNhac);
        btnChonAudio = view.findViewById(R.id.btnChonAudio);
        ivCloseDialog = view.findViewById(R.id.ivCloseDialog);
    }
    private void displayInfo(){
        if (nhac != null && type){
            txtTenNhac.setText(nhac.getTenNgheSi());
            txtTenNgheSi.setText(nhac.getTenNgheSi());
            txtTheLoai.setText(nhac.getTheLoai());
        }
    }
    private void uploadMusic(){
        String tenNhac = txtTenNhac.getText().toString();
        String tenNgheSi = txtTenNgheSi.getText().toString();
        String theLoai = txtTheLoai.getText().toString();
        String thoiLuong = AdditionalFunctions.getAudioDuration(uri, getContext());
        if (AdditionalFunctions.isStringEmpty(getContext(), tenNhac, tenNgheSi, theLoai, thoiLuong)){
            return;
        }
        Nhac nhac = new Nhac("", tenNhac, tenNgheSi, theLoai, thoiLuong, "", "", 0, 0);
        DAO_Nhac.uploadMusic(uri, nhac, txtThemVaSuaNhac);
    }
    private void updateMusic(){
        String tenNhac = txtTenNhac.getText().toString();
        String tenNgheSi = txtTenNgheSi.getText().toString();
        String theLoai = txtTheLoai.getText().toString();
        String thoiLuong = AdditionalFunctions.getAudioDuration(uri, getContext());
        if (AdditionalFunctions.isStringEmpty(getContext(), tenNhac, tenNgheSi, theLoai, thoiLuong)){
            return;
        }
        Nhac nhac = new Nhac("", tenNhac, tenNgheSi, theLoai, thoiLuong, "", "", 0, 0);
        DAO_Nhac.updateMusic(uri, nhac, txtThemVaSuaNhac);
    }
    public void chooseAudioFile(){
        Intent intent = new Intent();
        intent.setType("audio/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }
    public void closeDialog(View view){
        getDialog().dismiss();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null && data.getData() != null){
            Uri uri = data.getData();
            this.uri = uri;
            fileName = AdditionalFunctions.getFileName(getContext(),uri);
            txtTenFile.setText(fileName);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ViewGroup.LayoutParams layoutParams = getDialog().getWindow().getAttributes();
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setAttributes((WindowManager.LayoutParams)layoutParams);
    }

}
