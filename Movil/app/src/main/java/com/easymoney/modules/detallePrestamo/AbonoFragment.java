package com.easymoney.modules.detallePrestamo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.easymoney.R;
import com.easymoney.entities.Abono;
import com.easymoney.utils.UtilsDate;
import com.easymoney.utils.baseClases.BaseFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by ulises on 16/01/2018.
 */
public class AbonoFragment extends BaseFragment {

    private AbonoAdapter abonoAdapter = new AbonoAdapter(new ArrayList<Abono>());

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detalle_prestamo_abonos, container, false);
        ListView listView = rootView.findViewById(R.id.abonoList);
        listView.setAdapter(abonoAdapter);
        return rootView;
    }

    public void replaceData(List<Abono> abonos) {
        List<Abono> abonosFiltrado = new ArrayList<>();
        for (Abono abono : abonos) {
            if (abono.getAbonado()) abonosFiltrado.add(abono);
        }
        Collections.sort(abonosFiltrado, new Comparator<Abono>() {
            @Override
            public int compare(Abono a1, Abono a2) {
                return a1.getAbonoPK().getFecha().compareTo(a2.getAbonoPK().getFecha());
            }
        });
        abonoAdapter.replaceData(abonosFiltrado);
    }

    private static class AbonoAdapter extends BaseAdapter {

        private List<Abono> abonos;
        private TextView tvFecha;
        private TextView tvAbono;
        private TextView tvMulta;
        private TextView tvDescripcion;

        AbonoAdapter(List<Abono> abonos) {
            this.abonos = abonos;
        }

        void replaceData(List<Abono> abonos) {
            this.abonos = abonos;
            this.notifyDataSetChanged();
        }


        @Override
        public int getCount() {
            return abonos.size();
        }

        @Override
        public Object getItem(int i) {
            return abonos.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View rowView = view;
            if (rowView == null) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                rowView = inflater.inflate(R.layout.adapter_abono, viewGroup, false);
            }
            Abono abono = abonos.get(i);

            tvFecha = rowView.findViewById(R.id.tvFecha);
            tvAbono = rowView.findViewById(R.id.tvAbono);
            tvMulta = rowView.findViewById(R.id.tvMulta);
            tvDescripcion = rowView.findViewById(R.id.tvDescripcion);

            tvFecha.setText(UtilsDate.format_D_MM_YYYY(abono.getAbonoPK().getFecha()));
            tvAbono.setText("$" + abono.getCantidad());
            tvMulta.setText("$" + abono.getMulta());
            tvDescripcion.setText(abono.getMultaDes());

            return rowView;
        }
    }

}
