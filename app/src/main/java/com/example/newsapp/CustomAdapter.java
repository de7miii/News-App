package com.example.newsapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder> {
    private List<Article> mArticles;
    private Context mContext;
    private OnItemClickListener mListener;

    CustomAdapter(Context context, OnItemClickListener listener) {
        mContext = context;
        mListener = listener;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_itme_layout, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        Article currentItem = mArticles.get(position);
        holder.bind(currentItem);
    }

    @Override
    public int getItemCount() {
        return mArticles != null ? mArticles.size() : 0;
    }

    class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView titleText;
        TextView sectionText;
        TextView authorText;
        TextView publicationDateText;

        CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.text_title);
            sectionText = itemView.findViewById(R.id.text_section);
            authorText = itemView.findViewById(R.id.text_author);
            publicationDateText = itemView.findViewById(R.id.text_date);
            itemView.setOnClickListener(this);
        }

        void bind(Article article) {
            titleText.setText(article.getTitle());
            sectionText.setText(article.getSectionName());
            if (article.getAuthorName() != null) {
                authorText.setText(article.getAuthorName());
            }
            publicationDateText.setText(article.getPublicationDate());
        }

        @Override
        public void onClick(View v) {
            mListener.onItemClicked(getAdapterPosition());
        }
    }

    void updateArticlesList(ArrayList<Article> articles) {
        mArticles = articles;
    }

    public interface OnItemClickListener {
        void onItemClicked(int pos);
    }
}
