package com.example.duan1_nhom2.DAOClass;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.duan1_nhom2.AdapterClass.NhacTrangChu_rvAdapter;
import com.example.duan1_nhom2.AdapterClass.TimKiemNhac_rvAdapter;
import com.example.duan1_nhom2.AdditionalFunctions.AdditionalFunctions;
import com.example.duan1_nhom2.FragmentClass.NhacTheoChuDeFragment;
import com.example.duan1_nhom2.Model.Nhac;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class DAO_Nhac {
    public static void readSpecificMusics(final String songName, final TimKiemNhac_rvAdapter adapter, final int max) {
        final ArrayList<Nhac> dsn = new ArrayList<>();
        final int[] count = {0};
        DatabaseReference myDatabaseRef = FirebaseDatabase.getInstance().getReference("Nhac");
        myDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot i : snapshot.getChildren()) {
                        Nhac nhac = i.getValue(Nhac.class);
                        if (nhac != null && nhac.getTenNhac().toLowerCase().contains(songName.toLowerCase()) && count[0]<=max) {
                            dsn.add(nhac);
                        }else if (count[0]>max){
                            break;
                        }
                    }
                    adapter.updateAdapter(dsn);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static void uploadMusic(Uri uri, final Nhac nhac, final TextView textView) {
        final DatabaseReference myDatabaseRef = FirebaseDatabase.getInstance().getReference("Nhac");
        final String maNhac = myDatabaseRef.push().getKey();
        final StorageReference myStorageRef = FirebaseStorage.getInstance().getReference("Nhac").child(maNhac + ".mp3");
        UploadTask uploadTask = myStorageRef.putFile(uri);
        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return myStorageRef.getDownloadUrl();
            }
        }).addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String url = uri.toString();
                String tenNhac = nhac.getTenNhac();
                String tenNgheSi = nhac.getTenNgheSi();
                String theLoai = nhac.getTheLoai();
                String thoiLuong = nhac.getThoiLuong();
                Nhac newNhac = new Nhac(maNhac, tenNhac, tenNgheSi, theLoai, thoiLuong, url, "", 0, 0);
                myDatabaseRef.child(maNhac).setValue(newNhac).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        AdditionalFunctions.changeTextInMilisecond(textView, "Thành Công", "Thêm Nhạc");
                    }
                });
            }
        });
    }

    public static void updateMusic(Uri uri, Nhac nhac, TextView textView) {
        if (uri == null){
            updateMusicWithoutAudioFile(nhac, textView);
        }else{
            updateMusicWithAudioFile(uri, nhac, textView);
        }
    }

    public static void updateMusicWithoutAudioFile(Nhac nhac, final TextView textView){
        final DatabaseReference myDatabaseRef = FirebaseDatabase.getInstance().getReference("Nhac");
        Map<String, Object> postValues = new HashMap<>();
        postValues.put("TenNhac", nhac.getTenNhac());
        postValues.put("TenNgheSi", nhac.getTenNgheSi());
        postValues.put("TheLoai", nhac.getTheLoai());
        postValues.put("ThoiLuong", nhac.getThoiLuong());
        myDatabaseRef.child(nhac.getMaNhac()).updateChildren(postValues).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //Thành công
                AdditionalFunctions.changeTextInMilisecond(textView, "Cập Nhật Thành Công", "Cập Nhật Nhạc");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //Thất bại
            }
        });

    }

    public static void updateMusicWithAudioFile(Uri uri, final Nhac nhac, final TextView textView){
        if (!nhac.getURL().isEmpty()){
            FirebaseStorage.getInstance().getReferenceFromUrl(nhac.getURL()).delete();
        }
        final DatabaseReference myDatabaseRef = FirebaseDatabase.getInstance().getReference("Nhac");
        String maNhac = myDatabaseRef.push().getKey();
        final StorageReference myStorageRef = FirebaseStorage.getInstance().getReference("Nhac").child(maNhac + ".mp3");
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
                String url = uri.toString();
                Map<String , Object> postValues = new HashMap<>();
                postValues.put("TenNhac", nhac.getTenNhac());
                postValues.put("TenNgheSi", nhac.getTenNgheSi());
                postValues.put("TheLoai", nhac.getTheLoai());
                postValues.put("ThoiLuong", nhac.getThoiLuong());
                postValues.put("URL", url);
                myDatabaseRef.child(nhac.getMaNhac()).updateChildren(postValues).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        AdditionalFunctions.changeTextInMilisecond(textView, "Cập Nhật Thành Công", "Cập Nhật Nhạc");
                    }
                });
            }
        });
    }

    public static void updateMusicViewAmount(Nhac nhac) {
        DatabaseReference myDatabaseRef = FirebaseDatabase.getInstance().getReference("Nhac");
        Map<String, Object> postValues = new HashMap<>();
        postValues.put("LuotXem", nhac.getLuotXem() + 1);
        postValues.put("LuotXemThang", nhac.getLuotXemThang() + 1);
        myDatabaseRef.child(nhac.getMaNhac()).updateChildren(postValues);
    }

    public static void resetMonthlyViewAmount(final TextView textView){
        DatabaseReference myDatabaseRef = FirebaseDatabase.getInstance().getReference("Nhac");
        myDatabaseRef.child("LuotXemThang").setValue(0).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                textView.setText("Reset View Nhạc Thành Công");
            }
        });
    }

    public static void deleteMusic(String maNhac) {
        final DatabaseReference myDatabaseRef = FirebaseDatabase.getInstance().getReference("Nhac");
        myDatabaseRef.child(maNhac).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static MediaPlayer createMediaPlayer(Nhac nhac) {
        final MediaPlayer mediaPlayer = new MediaPlayer();
        if (nhac.getURL() != null) {
            try {
                mediaPlayer.setDataSource(nhac.getURL());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return mediaPlayer;
    }

    public static void readPopularMusic(int max, final NhacTrangChu_rvAdapter adapter) {
        final ArrayList<Nhac> dsn = new ArrayList<>();
        DatabaseReference myDatabaseRef = FirebaseDatabase.getInstance().getReference("Nhac");
        Query query = myDatabaseRef.orderByChild("LuotXemThang").limitToLast(max);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot i : snapshot.getChildren()) {
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

    public static void readPopularMusic(String tenTheLoai, final TimKiemNhac_rvAdapter adapter, NhacTheoChuDeFragment fragment){
        final ArrayList<Nhac> dsn = new ArrayList<>();
        DatabaseReference myDatabaseRef = FirebaseDatabase.getInstance().getReference("Nhac");
        myDatabaseRef.orderByChild("TenTheLoai").equalTo(tenTheLoai).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot i: snapshot.getChildren()){
                        Nhac nhac = i.getValue(Nhac.class);
                        dsn.add(nhac);
                    }
                    Collections.sort(dsn, new Comparator<Nhac>() {
                        @Override
                        public int compare(Nhac o1, Nhac o2) {
                            return o1.getLuotXemThang()>o2.getLuotXemThang()?1:-1;
                        }
                    });
                    adapter.updateAdapter(dsn);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public static void readPlaylistMusic(String maNhac, final NhacTrangChu_rvAdapter adapter){
        final ArrayList<Nhac> dsn = new ArrayList<>();
        DatabaseReference myDatabaseRef = FirebaseDatabase.getInstance().getReference("Nhac");
        myDatabaseRef.orderByChild("MaNhac").equalTo(maNhac).addListenerForSingleValueEvent(new ValueEventListener() {
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
    public static void addMusicToAlbum(String maNhac, String maAlbum, final TextView textView){
        DatabaseReference myDatabaseRef = FirebaseDatabase.getInstance().getReference("Nhac");
        Map<String, Object> postValue = new HashMap<>();
        postValue.put("MaAlbum", maAlbum);
        myDatabaseRef.child(maNhac).updateChildren(postValue).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                AdditionalFunctions.changeTextInMilisecond(textView, "Thêm Thành Công", "Thêm Vào Album");
            }
        });
    }
}
