package com.example.uno;

import android.os.Bundle;

import androidx.core.splashscreen.SplashScreen;

import com.example.uno.adapters.DeckAdapter;
import com.example.uno.adapters.RequestsAdapter;
import com.example.uno.database.Firestore;
import com.example.uno.fragments.ForgotPassFragment;
import com.example.uno.fragments.GameRoomFragment;
import com.example.uno.fragments.LobbyFragment;
import com.example.uno.fragments.LoginFragment;
import com.example.uno.fragments.RegisterFragment;
import com.example.uno.models.User;

public class MainActivity extends CommonActivity implements RequestsAdapter.IReqAdapter, DeckAdapter.IDeckAdapter, GameRoomFragment.IGameRoom, LoginFragment.ILogin, RegisterFragment.IRegister, ForgotPassFragment.IForgot, LobbyFragment.ILobby {

    private Firestore db;

    User user = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SplashScreen.installSplashScreen(this);

        setContentView(R.layout.activity_main);
    }

    public Firestore getDb() {
        if(this.db == null) this.db = new Firestore(this);
        return this.db;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}