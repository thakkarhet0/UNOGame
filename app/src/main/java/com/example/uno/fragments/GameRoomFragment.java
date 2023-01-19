package com.example.uno.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.uno.MainActivity;
import com.example.uno.R;
import com.example.uno.adapters.DeckAdapter;
import com.example.uno.database.Firestore;
import com.example.uno.databinding.FragmentGameRoomBinding;
import com.example.uno.databinding.FragmentLobbyBinding;
import com.example.uno.helpers.Utils;
import com.example.uno.models.Game;
import com.example.uno.models.User;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class GameRoomFragment extends Fragment {

    FragmentGameRoomBinding binding;

    IGameRoom am;

    NavController navController;

    User user;

    Game game;

    Firestore db;

    DocumentReference dbref;

    ColorStateList oldcolor;

    ListenerRegistration listener;

    DeckAdapter deckAdapter;

    public interface IGameRoom{

        User getUser();

        Firestore getDb();

        void alert(String msg);

        void actionBar(boolean show);

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof IGameRoom) {
            am = (IGameRoom) context;
        } else {
            throw new RuntimeException(context.toString());
        }
        db = am.getDb();
    }

    @Override
    public void onStop() {
        super.onStop();
        stopGame();
        am.actionBar(false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public void stopGame(){
        dbref.update("active", false);
    }

    @Override
    public void onResume() {
        super.onResume();
        am.actionBar(true);
        if(game == null)    navController.popBackStack();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getActivity().setTitle("Game Room");
        binding = FragmentGameRoomBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        user = am.getUser();
        game = user.getGame();
        navController = Navigation.findNavController(getActivity(), R.id.fragmentContainerView2);
        if(game == null)    navController.popBackStack();
        dbref = db.firestore.collection(Firestore.DB_GAME).document(game.getId());

        binding.deckView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.deckView.setLayoutManager(llm);

        oldcolor = binding.player1.getTextColors();

        binding.player1.setText(game.getPlayer1().getFirstname());
        binding.player2.setText(game.getPlayer2().getFirstname());

        Utils.setImage(view, binding.imageView4, game.getPlayer1().getId(), game.getPlayer2().getPhotoref());
        Utils.setImage(view, binding.imageView5, game.getPlayer2().getId(), game.getPlayer2().getPhotoref());

        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!game.isMyTurn(user)){
                    Toast.makeText(getContext(), "It's not your turn!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(game.canPlay(user)){
                    Toast.makeText(getContext(), "You cannot draw if you have a playable card in deck.", Toast.LENGTH_SHORT).show();
                    return;
                }

                game.addCardsToUser(user, new ArrayList<>(Arrays.asList(game.getDeck().remove(0))));

                if(!game.canPlay(user))
                    game.switchTurn();

                dbref.set(game);
            }
        });



        listener = dbref.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (value == null) {
                    return;
                }
                game = value.toObject(Game.class);
                if(game == null) return;
                game.setId(value.getId());
                user.setGame(game);

                if(!game.isActive()){
                    if(game.getWinner() == null)
                        am.alert("Game ended abruptly! No winners!");
                    else{
                        if(game.isWinner(user))
                            am.alert("You won the game!!!");
                        else
                            am.alert("You lose! Better luck next time!");
                    }
                    listener.remove();
                    dbref.delete();
                    user.setGame(null);
                    navController.popBackStack();
                    return;
                }

                if(game.isPlayer1Turn()){
                    if(game.isPlayer1(user)) binding.textView8.setText("Your Turn");
                    else binding.textView8.setText("Their Turn");

                    binding.player2.setText(game.getPlayer2().getFirstname());
                    binding.player1.setTextColor(Color.parseColor(Utils.COLOR_ACTIVE));
                    binding.player2.setTextColor(oldcolor);
                } else{
                    if(!game.isPlayer1(user)) binding.textView8.setText("Your Turn");
                    else binding.textView8.setText("Their Turn");

                    binding.player1.setText(game.getPlayer1().getFirstname());
                    binding.player2.setTextColor(Color.parseColor(Utils.COLOR_ACTIVE));
                    binding.player1.setTextColor(oldcolor);
                }

                binding.include.textView4.setTextColor(Color.parseColor(Utils.getCardColor(game.getTopCard())));
                binding.include.textView4.setTextSize(70);
                if(Utils.isSkip(game.getTopCard()))
                    binding.include.textView4.setTextSize(50);
                binding.include.textView4.setText(Utils.getCardDisplay(game.getTopCard()));
                if(Utils.isSkip(game.getTopCard()))  binding.include.textView4.setTextSize(30);
                binding.include.imageView6.setImageDrawable(ContextCompat.getDrawable(getActivity(), Utils.getCardDrawable(game.getTopCard())));

                deckAdapter = new DeckAdapter(game.getUserHand(user));
                binding.deckView.setAdapter(deckAdapter);

            }
        });

        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 16908332:
                // add alert dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Leave Game?");
                builder.setMessage("Do you want to leave this Game?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        navController.popBackStack();

                    }
                });
                builder.setNegativeButton("No", null);
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}