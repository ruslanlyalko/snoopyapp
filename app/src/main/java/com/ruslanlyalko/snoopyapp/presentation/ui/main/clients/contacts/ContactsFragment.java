package com.ruslanlyalko.snoopyapp.presentation.ui.main.clients.contacts;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ruslanlyalko.snoopyapp.R;
import com.ruslanlyalko.snoopyapp.data.configuration.DefaultConfigurations;
import com.ruslanlyalko.snoopyapp.data.models.Contact;
import com.ruslanlyalko.snoopyapp.presentation.ui.main.clients.OnFilterListener;
import com.ruslanlyalko.snoopyapp.presentation.ui.main.clients.contacts.adapter.ContactsAdapter;
import com.ruslanlyalko.snoopyapp.presentation.ui.main.clients.contacts.adapter.OnContactClickListener;
import com.ruslanlyalko.snoopyapp.presentation.ui.main.clients.contacts.details.ContactDetailsActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;

/**
 * Created by Ruslan Lyalko
 * on 29.01.2018.
 */
public class ContactsFragment extends Fragment implements OnContactClickListener {

    @BindView(R.id.list_contacts) RecyclerView mListContacts;
    @BindView(R.id.edit_filter_name) EditText mEditFilterName;
    @BindView(R.id.edit_filter_phone) EditText mEditFilterPhone;
    private ContactsAdapter mContactsAdapter;
    private OnFilterListener mOnFilterListener;

    public ContactsFragment() {
    }

    public static ContactsFragment newInstance() {
        ContactsFragment fragment = new ContactsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mOnFilterListener = (OnFilterListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_contacts, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        mContactsAdapter = new ContactsAdapter(this, getActivity());
        setupRecycler();
        loadContacts();
    }

    private void setupRecycler() {
        mListContacts.setLayoutManager(new LinearLayoutManager(getContext()));
        mListContacts.setAdapter(mContactsAdapter);
    }

    private void loadContacts() {
        Query ref = FirebaseDatabase.getInstance()
                .getReference(DefaultConfigurations.DB_CONTACTS)
                .orderByChild("name");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                List<Contact> contacts = new ArrayList<>();
                for (DataSnapshot clientSS : dataSnapshot.getChildren()) {
                    Contact contact = clientSS.getValue(Contact.class);
                    if (contact != null) {
                        contacts.add(contact);
                    }
                }
                mContactsAdapter.setData(contacts);
                onFilterTextChanged();
            }

            @Override
            public void onCancelled(final DatabaseError databaseError) {
            }
        });
    }

    @OnTextChanged({R.id.edit_filter_name, R.id.edit_filter_phone})
    void onFilterTextChanged() {
        String name = mEditFilterName.getText().toString().trim();
        String phone = mEditFilterPhone.getText().toString().trim();
        mContactsAdapter.getFilter().filter(name + "/" + phone);
        if (mOnFilterListener != null)
            mOnFilterListener.onFilterChanged(name, phone);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null && getView() != null) {
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClicked(final int position, ActivityOptionsCompat options) {
        startActivity(ContactDetailsActivity.getLaunchIntent(getContext(), mContactsAdapter.getItem(position)), options.toBundle());
    }
}
