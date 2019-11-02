package com.example.newsapp;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 */
public class WorldNewsFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Article>>, CustomAdapter.OnItemClickListener {
    private final String TAG = WorldNewsFragment.class.getSimpleName();

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.text_empty)
    TextView emptyText;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    private CustomAdapter mAdapter;
    private ArrayList<Article> mArticles;

    private final String BASE_URL = "http://content.guardianapis.com/search";
    private final String API_KEY = "07bac07b-f5c2-4f8c-9587-5afaf0cdb669";

    public WorldNewsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news_layout, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mAdapter = new CustomAdapter(getContext(), this);
        mRecyclerView.setAdapter(mAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        assert getActivity() != null;
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            getLoaderManager().initLoader(0, null, this).forceLoad();
        } else {
            mRecyclerView.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
            emptyText.setText(getContext().getString(R.string.no_internet_conection));
            emptyText.setVisibility(View.VISIBLE);
        }
    }

    @NonNull
    @Override
    public Loader<List<Article>> onCreateLoader(int id, @Nullable Bundle args) {
        Uri baseUri = Uri.parse(BASE_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        uriBuilder.appendQueryParameter("api-key", API_KEY);
        uriBuilder.appendQueryParameter("format", "json");
        uriBuilder.appendQueryParameter("section", "world");
        uriBuilder.appendQueryParameter("show-tags", "contributor");
        uriBuilder.appendQueryParameter("page-size", "10");
        Log.i(TAG, "onCreateLoader: " + uriBuilder.toString());
        return new ArticalesAsyncLoader(getContext(), uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Article>> loader, List<Article> data) {
        progressBar.setVisibility(View.INVISIBLE);
        if (data != null) {
            if (!data.isEmpty()) {
                emptyText.setVisibility(View.INVISIBLE);
                mRecyclerView.setVisibility(View.VISIBLE);
                mArticles = (ArrayList<Article>) data;
                mAdapter.updateArticlesList(mArticles);
                mAdapter.notifyDataSetChanged();
            } else {
                mRecyclerView.setVisibility(View.INVISIBLE);
                emptyText.setText(getContext().getString(R.string.no_news_found));
                emptyText.setVisibility(View.VISIBLE);
            }
        } else {
            mRecyclerView.setVisibility(View.INVISIBLE);
            emptyText.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Article>> loader) {
        mAdapter.updateArticlesList(new ArrayList<>());
    }

    @Override
    public void onItemClicked(int pos) {
        Article current = mArticles.get(pos);
        Uri webUri = Uri.parse(current.getUrl());
        Intent intent = new Intent(Intent.ACTION_VIEW, webUri);
        startActivity(intent);
    }


    private static class ArticalesAsyncLoader extends AsyncTaskLoader<List<Article>> {

        private String mUrl = "";

        private ArticalesAsyncLoader(@NonNull Context context, String stringUrl) {
            super(context);
            mUrl = stringUrl;
        }

        @Nullable
        @Override
        public List<Article> loadInBackground() {
            if (mUrl == null) {
                return null;
            }
            ArrayList<Article> articles;
            articles = NetworkHelper.extractDataFromJson(mUrl);
            return articles;
        }
    }
}
