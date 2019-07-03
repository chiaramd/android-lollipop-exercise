package com.codepath.android.lollipopexercise.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.transition.Transition;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.codepath.android.lollipopexercise.R;
import com.codepath.android.lollipopexercise.models.Contact;

public class DetailsActivity extends AppCompatActivity {
    public static final String EXTRA_CONTACT = "EXTRA_CONTACT";
    private Contact mContact;
    private ImageView ivProfile;
    private TextView tvName;
    private TextView tvPhone;
    private View vPalette;
    private FloatingActionButton fab;
    private Transition.TransitionListener mEnterTransitionListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

       // Intent i = new Intent();

        //mContact = (Contact) Parcels.unwrap(getIntent().getParcelableExtra(DetailsActivity.EXTRA_CONTACT));

        //movie = (Movie) Parcels.unwrap(getIntent().getParcelableExtra(Movie.class.getSimpleName()));

        ivProfile = (ImageView) findViewById(R.id.ivProfile);
        tvName = (TextView) findViewById(R.id.tvName);
        tvPhone = (TextView) findViewById(R.id.tvPhone);
        vPalette = findViewById(R.id.vPalette);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.INVISIBLE);

        // Extract contact from bundle
        mContact = (Contact)getIntent().getExtras().getSerializable(EXTRA_CONTACT);


        SimpleTarget<Bitmap> target = new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {

                //insert bitmap into profile image view
                ivProfile.setImageBitmap(resource);

                //generate() from Palette API to get vibrant color from the bitmap
                Palette palette = Palette.from(resource).generate();
                Palette.Swatch vibrant = palette.getVibrantSwatch();

                //set result as background color for R.id.vPalette view
                if (vibrant != null) {
                    vPalette.setBackgroundColor(vibrant.getRgb());
                    //change text color maybe??
                    tvName.setTextColor(vibrant.getTitleTextColor());
                    tvPhone.setTextColor(vibrant.getTitleTextColor());
                }
            }
        };


        ivProfile.setTag(target);

        // Fill views with data
        Glide.with(DetailsActivity.this).load(mContact.getThumbnailDrawable()).asBitmap().centerCrop().into(target);
        tvName.setText(mContact.getName());
        tvPhone.setText(mContact.getNumber());

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uri = "tel:" + mContact.getNumber();
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse(uri));
                startActivity(intent);
            }
        });

        mEnterTransitionListener = new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {

            }

            @Override
            public void onTransitionEnd(Transition transition) {
                enterReveal();
            }

            @Override
            public void onTransitionCancel(Transition transition) {

            }

            @Override
            public void onTransitionPause(Transition transition) {

            }

            @Override
            public void onTransitionResume(Transition transition) {

            }
        };
        getWindow().getEnterTransition().addListener(mEnterTransitionListener);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                exitReveal();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void enterReveal() {
        final View faButton = fab;

        int cx = faButton.getMeasuredWidth() / 2;
        int cy = faButton.getMeasuredHeight() / 2;

        int finalRadius = Math.max(faButton.getWidth(), faButton.getHeight()) / 2;

        Animator anim = ViewAnimationUtils.createCircularReveal(faButton, cx, cy, 0, finalRadius);

        faButton.setVisibility(View.VISIBLE);
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                getWindow().getEnterTransition().removeListener(mEnterTransitionListener);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        anim.start();
    }

    void exitReveal() {
        final View faButton = fab;

        int cx = faButton.getMeasuredWidth() / 2;
        int cy = faButton.getMeasuredHeight() / 2;

        int initialRadius = faButton.getWidth() / 2;

        Animator anim = ViewAnimationUtils.createCircularReveal(faButton, cx, cy, initialRadius, 0);

        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                faButton.setVisibility(View.INVISIBLE);
                supportFinishAfterTransition();
            }
        });

        anim.start();
    }

    @Override
    public void onBackPressed() {
        exitReveal();
    }
}
