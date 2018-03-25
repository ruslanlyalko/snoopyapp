package com.ruslanlyalko.snoopyapp.presentation.ui.main.mk;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.ruslanlyalko.snoopyapp.R;
import com.ruslanlyalko.snoopyapp.common.Keys;
import com.ruslanlyalko.snoopyapp.data.configuration.DefaultConfigurations;
import com.ruslanlyalko.snoopyapp.data.models.Mk;
import com.ruslanlyalko.snoopyapp.presentation.ui.main.mk.adapters.MksAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MkTabActivity extends AppCompatActivity {

    @BindView(R.id.fab) FloatingActionButton fab;
    @BindView(R.id.fab1) FloatingActionButton fab1;
    @BindView(R.id.fab2) FloatingActionButton fab2;
    @BindView(R.id.fab3) FloatingActionButton fab3;
    @BindView(R.id.textFab1) TextView textFab1;
    @BindView(R.id.textFab2) TextView textFab2;
    @BindView(R.id.textFab3) TextView textFab3;
    @BindView(R.id.faded_background) View fadedBackground;
    @BindView(R.id.container) ViewPager mViewPager;
    @BindView(R.id.tabs) TabLayout tabLayout;

    private Boolean isFabOpen = false;
    private Animation fab_open;
    private Animation fab_close;
    private Animation rotate_forward;
    private Animation rotate_backward;
    private Animation fade;
    private Animation fade_back;

    public static Intent getLaunchIntent(final Context launchIntent) {
        return new Intent(launchIntent, MkTabActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mk_tab);
        ButterKnife.bind(this);
        initToolbar();
        initFAB();
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        // Set up the ViewPager with the sections adapter.
        mViewPager.setAdapter(sectionsPagerAdapter);
        tabLayout.setupWithViewPager(mViewPager);
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initFAB() {
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_backward);
        fade = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade);
        fade_back = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_back);
        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateFAB();
                Intent intent = new Intent(MkTabActivity.this, MkEditActivity.class);
                intent.putExtra(Keys.Extras.EXTRA_TITLE2, getString(R.string.mk_type_k));
                startActivity(intent);
            }
        });
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateFAB();
                Intent intent = new Intent(MkTabActivity.this, MkEditActivity.class);
                intent.putExtra(Keys.Extras.EXTRA_TITLE2, getString(R.string.mk_type_t));
                startActivity(intent);
            }
        });
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateFAB();
                Intent intent = new Intent(MkTabActivity.this, MkEditActivity.class);
                intent.putExtra(Keys.Extras.EXTRA_TITLE2, getString(R.string.mk_type_d));
                startActivity(intent);
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateFAB();
            }
        });
        fadedBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateFAB();
            }
        });
    }

    public void animateFAB() {
        if (isFabOpen) {
            fab.startAnimation(rotate_backward);
            fab1.startAnimation(fab_close);
            fab2.startAnimation(fab_close);
            fab3.startAnimation(fab_close);
            fab1.setClickable(false);
            fab2.setClickable(false);
            fab3.setClickable(false);
            textFab1.startAnimation(fab_close);
            textFab2.startAnimation(fab_close);
            textFab3.startAnimation(fab_close);
            fadedBackground.setClickable(false);
            fadedBackground.startAnimation(fade_back);
            isFabOpen = false;
        } else {
            fab.startAnimation(rotate_forward);
            fab1.startAnimation(fab_open);
            fab2.startAnimation(fab_open);
            fab3.startAnimation(fab_open);
            textFab1.startAnimation(fab_open);
            textFab2.startAnimation(fab_open);
            textFab3.startAnimation(fab_open);
            fab1.setClickable(true);
            fab2.setClickable(true);
            fab3.setClickable(true);
            fadedBackground.setClickable(true);
            fadedBackground.startAnimation(fade);
            isFabOpen = true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_mk_tab, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        if (id == R.id.action_plan) {
            Intent intent = new Intent(MkTabActivity.this, MkPlanActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (isFabOpen) {
            animateFAB();
            return;
        }
        super.onBackPressed();
    }

    public static class PlaceholderFragment extends Fragment {

        private static final String ARG_SECTION_NUMBER = "section_number";
        private RecyclerView recyclerView;
        private MksAdapter adapter;
        private List<Mk> mkList;

        public PlaceholderFragment() {
        }

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_mk_tab, container, false);
            recyclerView = rootView.findViewById(R.id.recycler_view);
            mkList = new ArrayList<>();
            adapter = new MksAdapter(container.getContext(), mkList);
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(container.getContext(), 1);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(10), true));
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(adapter);
            String currentMkType = null;
            switch (getArguments().getInt(ARG_SECTION_NUMBER)) {
                case 0:
                    currentMkType = (getString(R.string.mk_type_k));
                    break;
                case 1:
                    currentMkType = (getString(R.string.mk_type_t));
                    break;
                case 2:
                    currentMkType = (getString(R.string.mk_type_d));
                    break;
            }
            loadMK(currentMkType);
            return rootView;
        }

        /**
         * Converting dp to pixel
         */
        private int dpToPx(int dp) {
            Resources r = getResources();
            return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
        }

        private void loadMK(final String currentMkType) {
            mkList.clear();
            FirebaseDatabase.getInstance().getReference(DefaultConfigurations.DB_MK).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Mk mk = dataSnapshot.getValue(Mk.class);
                    if (mk != null && mk.getTitle2().equals(currentMkType)) {
                        mkList.add(0, mk);
                        adapter.notifyItemInserted(0);
                        recyclerView.smoothScrollToPosition(0);
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    Mk mk = dataSnapshot.getValue(Mk.class);
                    updateMk(mk);
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    Mk mk = dataSnapshot.getValue(Mk.class);
                    removeMk(mk.getKey());
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }

        private void updateMk(Mk newMk) {
            int ind = 0;
            for (Mk m : mkList) {
                if (m.getKey().equals(newMk.getKey())) {
                    break;
                }
                ind++;
            }
            if (ind < mkList.size()) {
                mkList.set(ind, newMk);
                adapter.notifyItemChanged(ind);
                recyclerView.smoothScrollToPosition(ind);
            }
        }

        private void removeMk(String key) {
            int ind = 0;
            for (Mk m : mkList) {
                if (m.getKey().equals(key)) {
                    break;
                }
                ind++;
            }
            if (ind < mkList.size()) {
                mkList.remove(ind);
                adapter.notifyItemRemoved(ind);
            }
        }

        public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

            private int spanCount;
            private int spacing;
            private boolean includeEdge;

            GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
                this.spanCount = spanCount;
                this.spacing = spacing;
                this.includeEdge = includeEdge;
            }

            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int position = parent.getChildAdapterPosition(view); // item position
                int column = position % spanCount; // item column
                if (includeEdge) {
                    outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                    outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)
                    if (position < spanCount) { // top edge
                        outRect.top = spacing;
                    }
                    outRect.bottom = spacing; // item bottom
                } else {
                    outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                    outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                    if (position >= spanCount) {
                        outRect.top = spacing; // item top
                    }
                }
            }
        }
    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return PlaceholderFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.tab_k);
                case 1:
                    return getString(R.string.tab_t);
                case 2:
                    return getString(R.string.tab_d);
            }
            return null;
        }
    }
}
