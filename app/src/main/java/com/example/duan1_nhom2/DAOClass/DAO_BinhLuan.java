package com.example.duan1_nhom2.DAOClass;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.duan1_nhom2.AdapterClass.BinhLuan_rvAdapter;
import com.example.duan1_nhom2.AdditionalFunctions.AdditionalFunctions;
import com.example.duan1_nhom2.FragmentClass.NhacDangNgheFragment;
import com.example.duan1_nhom2.Model.BinhLuan;
import com.example.duan1_nhom2.Model.NguoiDung;
import com.example.duan1_nhom2.Model.Nhac;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DAO_BinhLuan {
    public static void readMusicComments(String maNguoiDung , String maNhac, final BinhLuan_rvAdapter adapter){
        adapter.clearItems();
        DatabaseReference myDatabaseRef = FirebaseDatabase.getInstance().getReference("BinhLuan");
        Query query = myDatabaseRef.orderByChild("maNhac").equalTo(maNhac);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot i: snapshot.getChildren()){
                        BinhLuan binhLuan = i.getValue(BinhLuan.class);
                        adapter.updateAdapter(binhLuan);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public static void updateMusicComment(String maBinhLuan, String binhLuan){
        DatabaseReference myDatabaseRef = FirebaseDatabase.getInstance().getReference("BinhLuan");
        Map<String, Object> postValues = new HashMap<>();
        postValues.put("binhLuan", binhLuan);
        myDatabaseRef.child(maBinhLuan).updateChildren(postValues).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
    }
    public static void deleteMusicComment(String maBinhLuan){
        DatabaseReference myDatabaseRef = FirebaseDatabase.getInstance().getReference("BinhLuan");
        myDatabaseRef.child(maBinhLuan).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
    }
    public static void postComment(String maNguoiDung, String maNhac, final String binhLuan, final BinhLuan_rvAdapter adapter){
        DatabaseReference myDatabaseRef = FirebaseDatabase.getInstance().getReference("BinhLuan");
        String maBinhLuan = myDatabaseRef.push().getKey();
        final BinhLuan objBinhLuan = new BinhLuan(maBinhLuan, maNguoiDung, binhLuan, maNhac);
        myDatabaseRef.child(maBinhLuan).setValue(objBinhLuan).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    adapter.updateAdapter(objBinhLuan);
                }
            }
        });
    }
    public static void getUserCommentProfile(final Context context, String maNguoiDung, final String binhLuan, final TextView txtTenNguoiDung, final TextView txtBinhLuan, final ImageView ivAvatarNguoiDung){
        DatabaseReference myDatabaseRef = FirebaseDatabase.getInstance().getReference("NguoiDung");
        myDatabaseRef.orderByChild("maNguoiDung").equalTo(maNguoiDung).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot i: snapshot.getChildren()){
                        NguoiDung nguoiDung = i.getValue(NguoiDung.class);
                        txtTenNguoiDung.setText(nguoiDung.getTenHienThi());
                        Picasso.with(context).load(nguoiDung.getURLAnh()).into(ivAvatarNguoiDung);
                        txtBinhLuan.setText(binhLuan);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
