package com.ruslanlyalko.snoopyapp.presentation.ui.main.mk.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.ruslanlyalko.snoopyapp.R;
import com.ruslanlyalko.snoopyapp.common.Keys;
import com.ruslanlyalko.snoopyapp.data.models.Mk;
import com.ruslanlyalko.snoopyapp.presentation.ui.main.mk.MkDetailsActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MksAdapter extends RecyclerView.Adapter<MksAdapter.MyViewHolder> {

    private Context mContext;
    private List<Mk> mkList;
    private FirebaseStorage storage = FirebaseStorage.getInstance();

    public MksAdapter(Context mContext, List<Mk> mkList) {
        this.mContext = mContext;
        this.mkList = mkList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_mk, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Mk mk = mkList.get(position);
        holder.bindData(mk);
    }

    @Override
    public int getItemCount() {
        return mkList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.text_title1) TextView textTitle1;
        @BindView(R.id.text_title2) TextView textTitle2;
        @BindView(R.id.text_description) TextView textDescription;
        @BindView(R.id.image_view) ImageView imageView;
        @BindView(R.id.button_expand) ImageButton buttonExpand;
        @BindView(R.id.button_share) Button buttonShare;
        @BindView(R.id.button_link) Button buttonLink;
        @BindView(R.id.panel_expand) LinearLayout expandPanel;
        @BindView(R.id.card_view) CardView cardView;

        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        void bindData(final Mk mk) {
            textTitle1.setText(mk.getTitle1());
            textTitle2.setText(mk.getTitle2());
            textDescription.setText(mk.getDescription());
            //load image if already defined
            if (mk.getImageUri() != null && !mk.getImageUri().isEmpty()) {
                Glide.with(mContext).load(mk.getImageUri()).into(imageView);
            }
            // button share
            buttonShare.setOnClickListener(v -> {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_SUBJECT, mk.getTitle1());
                sendIntent.putExtra(Intent.EXTRA_TEXT, mk.getTitle1() + "\n" + mk.getTitle2() + "\n\n" + mk.getDescription());
                sendIntent.setType("text/plain");
                mContext.startActivity(sendIntent);
            });
            buttonLink.setOnClickListener(v -> {
                if (mk.getLink() != null && !mk.getLink().isEmpty()) {
                    CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                    CustomTabsIntent customTabsIntent = builder.build();
                    builder.setToolbarColor(ResourcesCompat.getColor(mContext.getResources(), R.color.colorPrimary, null));
                    customTabsIntent.launchUrl(mContext, Uri.parse(mk.getLink()));
                }
            });
            // expand to show description
            buttonExpand.setOnClickListener(v -> {
                if (expandPanel.getVisibility() == View.VISIBLE) {
                    buttonExpand.setImageResource(R.drawable.ic_action_expand_more);
                    expandPanel.setVisibility(View.GONE);
                } else {
                    buttonExpand.setImageResource(R.drawable.ic_action_expand_less);
                    expandPanel.setVisibility(View.VISIBLE);
                }
            });
            cardView.setOnClickListener(v -> {
                Intent intent = new Intent(mContext, MkDetailsActivity.class);
                intent.putExtra(Keys.Extras.EXTRA_ITEM_ID, mk.getKey());
                mContext.startActivity(intent);
            });
        }
    }
}