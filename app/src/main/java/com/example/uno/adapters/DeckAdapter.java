package com.example.uno.adapters;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uno.MainActivity;
import com.example.uno.database.Firestore;
import com.example.uno.databinding.CardLayoutBinding;
import com.example.uno.helpers.Utils;
import com.example.uno.models.Game;
import com.example.uno.models.User;
import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;

public class DeckAdapter extends RecyclerView.Adapter<DeckAdapter.UViewHolder> {

    ArrayList<String> deck;

    CardLayoutBinding binding;

    IDeckAdapter am;

    Firestore db;

    DocumentReference dbref;

    User user;

    Game game;

    ViewGroup parent;

    public DeckAdapter(ArrayList<String> deck) {
        this.deck = deck;
    }

    @NonNull
    @Override
    public UViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = CardLayoutBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        am = (IDeckAdapter) parent.getContext();
        this.parent = parent;
        user = am.getUser();
        db = am.getDb();
        game = user.getGame();

        dbref = db.firestore.collection(Firestore.DB_GAME).document(game.getId());
        return new UViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull UViewHolder holder, int position) {
        String card = deck.get(position);

        binding.textView4.setText(Utils.getCardDisplay(card));
        binding.textView4.setTextColor(Color.parseColor(Utils.getCardColor(card)));
        if(Utils.isSkip(card))  binding.textView4.setTextSize(30);
        binding.imageView6.setImageDrawable(ContextCompat.getDrawable((MainActivity) parent.getContext(), Utils.getCardDrawable(card)));

        binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // playing their turn, write game logic
                if(!game.isMyTurn(user)){
                    Toast.makeText(parent.getContext(), "It's not your turn!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!Utils.canPlay(game.getTopCard(), card)){
                    Toast.makeText(parent.getContext(), "Cannot play that card!", Toast.LENGTH_SHORT).show();
                    return;
                }

                deck.remove(card);
                game.getDiscard().add(card);

                if(!Utils.isSpecialCard(card))  game.switchTurn();

                if(Utils.isDraw4(card)) game.addCardsToUser(game.getOpponentUser(user), game.getDeckCards(4));

                if(deck.size() <= 0){
                    game.setActive(false);
                    game.addWinnerUser(user);
                }

                dbref.set(game);

            }
        });

    }

    public interface IDeckAdapter {

        User getUser();

        Firestore getDb();

    }

    @Override
    public int getItemCount() {
        return this.deck.size();
    }


    public static class UViewHolder extends RecyclerView.ViewHolder {

        CardLayoutBinding binding;

        public UViewHolder(@NonNull CardLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }

}
