package org.ragecastle.movies_udacity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements MainFragment.Callback{

    private boolean mTwoPane;
    private final String DETAILS_FRAG_TAG = "DetailsFragment";
    private final String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.details_container) != null) {
            mTwoPane = true;
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.details_container,
                                new DetailsFragment(),
                                DETAILS_FRAG_TAG)
                        .commit();
            }
        }
        else {
            mTwoPane = false;
        }
    }

    @Override
    public void onItemSelected(String movie_id) {
        if (mTwoPane) {
            Bundle args = new Bundle();
            args.putString("movie_id", movie_id);
            DetailsFragment detailsFragment = new DetailsFragment();
            detailsFragment.setArguments(args);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.details_container, detailsFragment, DETAILS_FRAG_TAG)
                    .commit();
        }
        else {
            Intent intent = new Intent(this, DetailsActivity.class);
                    intent.putExtra("movie_id", movie_id);
                    startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            default:
                return false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        MainFragment mainFragment  = (MainFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_movies);
    }
}
