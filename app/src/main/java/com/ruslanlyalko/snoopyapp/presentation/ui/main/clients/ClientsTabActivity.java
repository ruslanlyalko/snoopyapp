package com.ruslanlyalko.snoopyapp.presentation.ui.main.clients;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.ruslanlyalko.snoopyapp.R;
import com.ruslanlyalko.snoopyapp.presentation.ui.main.clients.birth.BirthActivity;
import com.ruslanlyalko.snoopyapp.presentation.ui.main.clients.birthdays.BirthdaysFragment;
import com.ruslanlyalko.snoopyapp.presentation.ui.main.clients.contacts.ContactsFragment;
import com.ruslanlyalko.snoopyapp.presentation.ui.main.clients.contacts.edit.ContactEditActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ClientsTabActivity extends AppCompatActivity implements OnFilterListener {

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.tabs) TabLayout mTabs;
    @BindView(R.id.appbar) AppBarLayout mAppbar;
    @BindView(R.id.container) ViewPager mViewPager;
    @BindView(R.id.fab) FloatingActionButton mFab;
    @BindView(R.id.main_content) CoordinatorLayout mMainContent;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private String mFilterName = "";
    private String mFilterPhone = "";

    public static Intent getLaunchIntent(final Context context) {
        return new Intent(context, ClientsTabActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clients_tab);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        // Set up the ViewPager with the sections adapter.
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabs));
        mTabs.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_mk_tab, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_plan) {
            startActivity(BirthActivity.getLaunchIntent(this));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.fab)
    public void onFabClicked() {
        startActivity(ContactEditActivity.getLaunchIntent(this, mFilterName, mFilterPhone));
    }

    @Override
    public void onFilterChanged(final String name, final String phone) {
        mFilterName = name;
        mFilterPhone = phone;
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return ContactsFragment.newInstance();
                case 1:
                    return BirthdaysFragment.newInstance();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
