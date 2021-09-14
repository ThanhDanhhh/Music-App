package com.example.duan1_nhom2.DAOClass;

import android.net.Uri;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.duan1_nhom2.AdapterClass.AlbumTrangChu_rvAdapter;
import com.example.duan1_nhom2.AdapterClass.NhacTrangChu_rvAdapter;
import com.example.duan1_nhom2.AdapterClass.ThemNhacVaoAlbum_Adapter;
import com.example.duan1_nhom2.AdapterClass.TimKiemAlbum_rvAdapter;
import com.example.duan1_nhom2.AdditionalFunctions.AdditionalFunctions;
import com.example.duan1_nhom2.Model.Albums;
import com.example.duan1_nhom2.Model.Nhac;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DAO_Album {
    public static void readAllAlbums(final ThemNhacVaoAlbum_Adapter adapter){
        DatabaseReference myDatabaseRef = FirebaseDatabase.getInstance().getReference("Albums");
        myDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot i: snapshot.getChildren()){
                        Albums albums = i.getValue(Albums.class);
                        adapter.updateAdapter(albums);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public static void readSpecificAlbums(final String tenAlbum, final TimKiemAlbum_rvAdapter adapter, final int max){
        final int[] count = {0};
        DatabaseReference myDatabaseRef = FirebaseDatabase.getInstance().getReference("Albums");
        myDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot i: snapshot.getChildren()){
                        Albums album = i.getValue(Albums.class);
                        if (album.getTenAlbum().toLowerCase().contains(tenAlbum.toLowerCase()) && count[0]<= max){
                            adapter.updateAdapter(album);
                            count[0]++;
                        }else if (count[0]>max){
                            break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public static void readSpecificAlbums(final String tenAlbum, final AlbumTrangChu_rvAdapter adapter, final int max){
        final int[] count = {0};
        final ArrayList<Albums> dsalbums = new ArrayList<>();
        DatabaseReference myDatabaseRef = FirebaseDatabase.getInstance().getReference("Albums");
        myDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot i: snapshot.getChildren()){
                        Albums album = i.getValue(Albums.class);
                        if (album.getTenAlbum().toLowerCase().contains(tenAlbum.toLowerCase()) && count[0]<=max){
                            dsalbums.add(album);
                            count[0]++;
                        }else{
                            break;
                        }
                    }
                    adapter.updateAdapter(dsalbums);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static void readAlbumSongs(String maAlbum, final NhacTrangChu_rvAdapter adapter){
        final ArrayList<Nhac> dsn = new ArrayList<>();
        DatabaseReference myDatabaseRef = FirebaseDatabase.getInstance().getReference("Nhac");
        myDatabaseRef.orderByChild("MaAlbum").equalTo(maAlbum).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot i: snapshot.getChildren()){
                        Nhac nhac = i.getValue(Nhac.class);
                        dsn.add(nhac);
                    }
                    adapter.updateAdapter(dsn);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static void readPopularAlbum(int max, final AlbumTrangChu_rvAdapter adapter){
        final ArrayList<Albums> dsalbums = new ArrayList<>();
        DatabaseReference myDatabaseRef = FirebaseDatabase.getInstance().getReference("Albums");
        Query query = myDatabaseRef.orderByChild("luotXemThang").limitToLast(max);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot i: snapshot.getChildren()){
                        Albums albums = i.getValue(Albums.class);
                        dsalbums.add(albums);
                    }
                    adapter.updateAdapter(dsalbums);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static void updateAlbumViewAmount(Albums album){
        DatabaseReference myDatabaseRef = FirebaseDatabase.getInstance().getReference("Albums");
        Map<String, Object> postValues = new HashMap<>();
        postValues.put("luotXem", album.getLuotXem()+1);
        postValues.put("luotXemThang", album.getLuotXemThang()+1);
        myDatabaseRef.child(album.getMaAlbum()).updateChildren(postValues);
    }

    public static void updateAlbum(Uri uri, final Albums albums, final TextView textView){
        if (uri == null){
            updateAlbumWithoutImage(albums, textView);
        }else{
            updateAlbumWithImage(uri, albums, textView);
        }
    }

    public static void updateAlbumWithImage(Uri uri, final Albums albums, final TextView textView){
        if (!albums.getURLAnh().equals("NoImage")){
            FirebaseStorage.getInstance().getReferenceFromUrl(albums.getURLAnh()).delete();
        }
        String fileExtension = uri.toString().substring(uri.toString().lastIndexOf("."));
        final DatabaseReference myDatabaseRef = FirebaseDatabase.getInstance().getReference("Albums");
        final StorageReference myStorageRef = FirebaseStorage.getInstance().getReference("AnhAlbums").child(albums.getMaAlbum() + fileExtension);
        UploadTask uploadTask = myStorageRef.putFile(uri);
        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (task.isSuccessful()){
                    throw task.getException();
                }
                return myStorageRef.getDownloadUrl();
            }
        }).addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String urlAnh = uri.toString();
                Map<String, Object> postValue = new HashMap<>();
                postValue.put("tenAlbum", albums.getTenAlbum());
                postValue.put("tenNgheSi", albums.getTenNgheSi());
                postValue.put("soBaiHat", albums.getSoBaiHat());
                postValue.put("urlAnh", urlAnh);
                myDatabaseRef.child(albums.getMaAlbum()).updateChildren(postValue).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        AdditionalFunctions.changeTextInMilisecond(textView, "Cập Nhật Thành Công", "Thêm Album");
                    }
                });
            }
        });
    }

    public static void updateAlbumWithoutImage(Albums albums, final TextView textView){
        DatabaseReference myDatabaseRef = FirebaseDatabase.getInstance().getReference("Albums");
        Map<String, Object> postValues = new HashMap<>();
        postValues.put("tenAlbum", albums.getTenAlbum());
        postValues.put("tenNgheSi", albums.getTenNgheSi());
        postValues.put("soBaiHat", albums.getSoBaiHat());
        myDatabaseRef.child(albums.getMaAlbum()).updateChildren(postValues).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                AdditionalFunctions.changeTextInMilisecond(textView, "Cập Nhật Thành Công", "Cập Nhật Albums");
            }
        });
    }

    public static void uploadAlbum(Uri uri, final Albums albums, final TextView textView){
        if (uri == null){
            uploadAlbumWithoutImage(albums, textView);
        }else{
            uploadAlbumWithImage(uri, albums, textView);
        }
    }

    public static void uploadAlbumWithImage(Uri uri, final Albums albums, final TextView textView){
        final DatabaseReference myDatabaseRef = FirebaseDatabase.getInstance().getReference("Albums");
        final String maAlbum = myDatabaseRef.push().getKey();
        final StorageReference myStorageRef = FirebaseStorage.getInstance().getReference("AnhAlbums").child(maAlbum + ".jpg");
        UploadTask uploadTask = myStorageRef.putFile(uri);
        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()){
                    throw task.getException();
                }
                return myStorageRef.getDownloadUrl();
            }
        }).addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String urlAnh = uri.toString();
                String tenAlbum = albums.getTenAlbum();
                String tenNgheSi = albums.getTenNgheSi();
                int soBaiHat = albums.getSoBaiHat();
                Albums albums = new Albums(maAlbum, tenAlbum, tenNgheSi, soBaiHat, urlAnh,0, 0);
                myDatabaseRef.child(maAlbum).setValue(albums).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        AdditionalFunctions.changeTextInMilisecond(textView, "Thêm Thành Công", "Thêm Album");
                    }
                });
            }
        });
    }

    public static void uploadAlbumWithoutImage(Albums albums, final TextView textView){
        final DatabaseReference myDatabaseRef = FirebaseDatabase.getInstance().getReference("Albums");
        String maAlbum = myDatabaseRef.push().getKey();
        String tenAlbum = albums.getTenAlbum();
        String tenNgheSi = albums.getTenNgheSi();
        int soBaiHat = albums.getSoBaiHat();
        Albums newAlbum = new Albums(maAlbum, tenAlbum, tenNgheSi, soBaiHat, "NoImage", 0, 0);
        myDatabaseRef.child(maAlbum).setValue(newAlbum).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                AdditionalFunctions.changeTextInMilisecond(textView, "Thêm Thành Công", "Thêm Album");
            }
        });
    }

    public static void deleteAlbum(String maAlbum, String urlAnh, final TextView textView){
        DatabaseReference myDatabaseRef = FirebaseDatabase.getInstance().getReference("Albums");
        final StorageReference myStorageRef = FirebaseStorage.getInstance().getReferenceFromUrl(urlAnh);
        myDatabaseRef.child(maAlbum).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                myStorageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        AdditionalFunctions.changeTextInMilisecond(textView, "Xóa Thành Công", "Xóa Album");
                    }
                });
            }
        });
    }

    public static void resetMonthlyViewAmount(final TextView textView){
        DatabaseReference myDatabaseRef = FirebaseDatabase.getInstance().getReference("Nhac");
        myDatabaseRef.child("LuotXemThang").setValue(0).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                textView.setText("Reset View Album Thành Công");
            }
        });
    }
}
