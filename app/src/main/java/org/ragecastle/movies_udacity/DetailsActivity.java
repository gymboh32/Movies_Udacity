package org.ragecastle.movies_udacity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by jahall on 11/11/15.
 *
 * Activity to hold the movie details
 *
 */
public class DetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new DetailsFragment())
                    .commit();
        }
    }
}
