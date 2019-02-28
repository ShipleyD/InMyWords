package com.example.inmywords.Model_Controller;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.inmywords.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

public class WordAdapter extends FirestoreRecyclerAdapter<Word, WordAdapter.WordHolder> {

    private OnItemClickListener listener;

    public WordAdapter(@NonNull FirestoreRecyclerOptions<Word> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull WordHolder holder, int position, @NonNull Word model) {

        holder.textViewWord.setText(model.getWord());
    }

    @NonNull
    @Override
    public WordHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.word_list, viewGroup, false);
        return new WordHolder(v);
    }

    class WordHolder extends RecyclerView.ViewHolder{

        TextView textViewWord;

        public WordHolder(@NonNull View itemView) {
            super(itemView);

            textViewWord = itemView.findViewById(R.id.text_view_word);

            itemView.findViewById(R.id.btnPlay).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION && listener != null){
                        listener.onItemClick(getSnapshots().getSnapshot(position), position);
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }
}
