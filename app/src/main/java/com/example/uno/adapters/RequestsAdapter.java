package com.example.uno.adapters;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uno.MainActivity;
import com.example.uno.R;
import com.example.uno.database.Firestore;
import com.example.uno.databinding.RequestLayoutBinding;
import com.example.uno.helpers.Utils;
import com.example.uno.models.Game;
import com.example.uno.models.Request;
import com.example.uno.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class RequestsAdapter extends RecyclerView.Adapter<RequestsAdapter.UViewHolder> {

    ArrayList<Request> requests;

    RequestLayoutBinding binding;

    IReqAdapter am;

    Firestore db;

    ViewGroup parent;

    User user;

    public RequestsAdapter(ArrayList<Request> requests) {
        this.requests = requests;
    }

    @NonNull
    @Override
    public UViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = RequestLayoutBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        this.parent = parent;
        am = (IReqAdapter) parent.getContext();
        return new UViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull UViewHolder holder, int position) {
        Request request = requests.get(position);

        User requester = request.getRequester();

        holder.binding.textView5.setText(requester.getDisplayName());

        user = am.getUser();
        db = am.getDb();

        Utils.setImage(holder.binding.getRoot(), binding.imageView, requester.getId(), requester.getPhotoref());

        holder.binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (requester.getId().equals(user.getId())) return;
                AlertDialog.Builder builder = new AlertDialog.Builder(holder.binding.getRoot().getContext());
                builder.setTitle("Game Request");
                builder.setMessage("Do you want to join this Game?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ArrayList<String> deck = new ArrayList<>(
                                Arrays.asList(
                                    "R0", "R1", "R2", "R3", "R4", "R5", "R6", "R7", "R8", "R9", "RS",
                                    "G0", "G1", "G2", "G3", "G4", "G5", "G6", "G7", "G8", "G9", "GS",
                                    "B0", "B1", "B2", "B3", "B4", "B5", "B6", "B7", "B8", "B9", "BS",
                                    "Y0", "Y1", "Y2", "Y3", "Y4", "Y5", "Y6", "Y7", "Y8", "Y9", "YS",
                                    "+4", "+4", "+4", "+4"
                                )
                        );
                        Collections.shuffle(deck);

                        ArrayList<String> player1hand = new ArrayList<>(), player2hand = new ArrayList<>();
                        for(int j = 0; j < 14; j++){
                            if(j / 7 == 0)
                                player1hand.add(deck.remove(j));
                            else
                                player2hand.add(deck.remove(j));
                        }

                        while (Utils.isSpecialCard(deck.get(0))) // special cards shouldn't be the first top card
                            Collections.shuffle(deck);

                        ArrayList<String> discard = new ArrayList<>(Arrays.asList(deck.remove(0)));

                        Game game = new Game(request.getRequester(), user, deck, player1hand, player2hand, discard);

                        db.firestore.collection(Firestore.DB_GAME).add(game).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                if(!task.isSuccessful())    task.getException().printStackTrace();

                                game.setId(task.getResult().getId());

                                db.firestore.collection(Firestore.DB_REQUESTS).document(request.getId()).update("accepted", true, "game_id", game.getId()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(!task.isSuccessful())    task.getException().printStackTrace();

                                        user.setGame(game);
                                        Navigation.findNavController((MainActivity) parent.getContext(), R.id.fragmentContainerView2).navigate(R.id.action_lobbyFragment_to_gameRoomFragment);
                                        am.alert("Game started with " + request.getRequester().getDisplayName() + "!");
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        am.alert("Request was deleted!");
                                        db.firestore.collection(Firestore.DB_GAME).document(game.getId()).delete();
                                    }
                                });
                            }
                        });


                    }
                });
                builder.setNegativeButton("No", null);
                builder.setCancelable(false);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    public interface IReqAdapter {

        User getUser();

        Firestore getDb();

        void alert(String msg);

    }

    @Override
    public int getItemCount() {
        return this.requests.size();
    }


    public static class UViewHolder extends RecyclerView.ViewHolder {

        RequestLayoutBinding binding;

        public UViewHolder(@NonNull RequestLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }

}
