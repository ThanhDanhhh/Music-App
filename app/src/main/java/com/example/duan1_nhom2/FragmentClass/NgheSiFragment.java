package com.example.duan1_nhom2.FragmentClass;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.duan1_nhom2.AdapterClass.AlbumTrangChu_rvAdapter;
import com.example.duan1_nhom2.AdapterClass.NhacTrangChu_rvAdapter;
import com.example.duan1_nhom2.DAOClass.DAO_NgheSi;
import com.example.duan1_nhom2.DialogClass.DialogThemVaCapNhatNgheSi;
import com.example.duan1_nhom2.DialogClass.DialogXoaDuLieu;
import com.example.duan1_nhom2.MainActivity;
import com.example.duan1_nhom2.Model.NgheSi;
import com.example.duan1_nhom2.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class NgheSiFragment extends Fragment {
    CircleImageView ivAnhNgheSi;
    TextView txtTenNgheSi, txtSoBaiHat, txtLuotXem, txtThongTinThem, txtXemTatCaAlbum, txtXemTatCaNhac;
    Button btnPhatTatCa;
    RecyclerView rvNhacNgheSi, rvAlbumsNgheSi;
    NgheSi ngheSi;
    NhacTrangChu_rvAdapter adapterNhacNgheSi;
    AlbumTrangChu_rvAdapter adapterAlbumsNgheSi;
    LinearLayoutManager layoutManager1, layoutManager2;
    public NgheSiFragment(NgheSi ngheSi) {
        this.ngheSi = ngheSi;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_nghesi, container, false);
        findView(view);
        displayInfo();
        ifUserIsAnAdmin();
        return view;
    }
    private void ifUserIsAnAdmin(){
        MainActivity mainActivity = (MainActivity)getContext();
        if (mainActivity.isUserAnAdmin()){
            View.OnClickListener listener1 = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogXoaDuLieu dialog = new DialogXoaDuLieu(ngheSi.getMaNgheSi(), ngheSi.getURLAnh(), ngheSi.getTenNgheSi(), "");
                    dialog.setType(3);
                    dialog.show(getChildFragmentManager(), "DialogXoaDuLieu");
                }
            };
            mainActivity.setOnClickToolbarAction1(listener1, R.drawable.ic_delete);
            View.OnClickListener listener2 = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogThemVaCapNhatNgheSi dialog = new DialogThemVaCapNhatNgheSi(ngheSi, true);
                    dialog.show(getChildFragmentManager(), "DialogThemVaCapNhatNgheSi");
                }
            };
            mainActivity.setOnClickToolbarAction2(listener2, R.drawable.ic_edit);
        }
    }
    private void findView(View view){
        txtLuotXem = view.findViewById(R.id.txtLuotXem);
        txtTenNgheSi = view.findViewById(R.id.txtTenNgheSi);
        txtSoBaiHat = view.findViewById(R.id.txtSoBaiHat);
        txtThongTinThem = view.findViewById(R.id.txtThongTinThem);
        txtXemTatCaAlbum = view.findViewById(R.id.txtXemTatCaAlbums);
        txtXemTatCaNhac  = view.findViewById(R.id.txtXemTatCaNhac);
        ivAnhNgheSi = view.findViewById(R.id.ivAnhNgheSi);
        btnPhatTatCa = view.findViewById(R.id.btnPhatTatCa);
        rvNhacNgheSi = view.findViewById(R.id.rvNhacNgheSi);
        rvAlbumsNgheSi = view.findViewById(R.id.rvAlbumsNgheSi);
    }
    private void displayInfo(){
        if (ngheSi != null){
            adapterNhacNgheSi = new NhacTrangChu_rvAdapter(getContext(), 10);
            adapterAlbumsNgheSi = new AlbumTrangChu_rvAdapter(getContext(), 5);
            layoutManager1 = new LinearLayoutManager(getContext());
            layoutManager2 = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false);
            rvNhacNgheSi.setAdapter(adapterNhacNgheSi);
            rvNhacNgheSi.setLayoutManager(layoutManager1);
            rvAlbumsNgheSi.setAdapter(adapterAlbumsNgheSi);
            rvAlbumsNgheSi.setLayoutManager(layoutManager2);
            DAO_NgheSi.readOneArtist(ngheSi.getTenNgheSi(), adapterNhacNgheSi, adapterAlbumsNgheSi);
            txtTenNgheSi.setText(ngheSi.getTenNgheSi());
            txtLuotXem.setText(String.valueOf(ngheSi.getLuotXem()));
            DAO_NgheSi.getArtistTotalSongs(ngheSi.getMaNgheSi(), txtSoBaiHat);
            txtThongTinThem.setText(ngheSi.getThongTinThem());
        }
    }
}
