package com.easymoney.modules.detallePrestamo;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.easymoney.R;
import com.easymoney.entities.Prestamo;

public class DetallePrestamoActivity extends AppCompatActivity {

    DetallePrestamoPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_prestamo);
        //asegurar primero el presentador para poder enlazar los fragmentos
        Prestamo prestamo = (Prestamo) getIntent().getSerializableExtra("Prestamo");
        presenter = new DetallePrestamoPresenter(prestamo);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        ViewPager mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DetallePrestamoActivity.this.lanzarModalCobro();
            }
        });
        fab.setVisibility(View.GONE);
        presenter.setFab(fab);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                presenter.unsubscribe();
                this.finish();
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        presenter.unsubscribe();
        super.onBackPressed();
    }

    private void lanzarModalCobro() {
        CobroDialogFragment newFragment = new CobroDialogFragment(presenter);
        newFragment.show(getFragmentManager(), "cobro");
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return ConsultaFragment.getInstance(presenter);
                case 1:
                    return AbonoFragment.getInstance(presenter);
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

}
