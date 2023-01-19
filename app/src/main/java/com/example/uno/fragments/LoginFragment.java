package com.example.uno.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.uno.R;
import com.example.uno.database.Firestore;
import com.example.uno.databinding.FragmentLoginBinding;
import com.example.uno.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.firestore.DocumentSnapshot;


public class LoginFragment extends Fragment {

    NavController navController;

    FragmentLoginBinding binding;

    String email, password;

    ILogin am;

    Firestore db;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof ILogin) {
            am = (ILogin) context;
        } else {
            throw new RuntimeException(context.toString());
        }
        db = am.getDb();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (db.getCurrentUser() != null) login();
    }

    public void login() {
        db.firestore.collection(Firestore.DB_PROFILE).document(db.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot snapshot = task.getResult();
                    User user = snapshot.toObject(User.class);
                    user.setId(snapshot.getId());
                    am.setUser(user);
                    navController.navigate(R.id.action_loginFragment_to_lobbyFragment);
                } else {
                    task.getException().printStackTrace();
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getActivity().setTitle(R.string.login);

        binding = FragmentLoginBinding.inflate(inflater, container, false);

        View view = binding.getRoot();

        navController = Navigation.findNavController(getActivity(), R.id.fragmentContainerView2);

        binding.createNewAccountId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.action_loginFragment_to_registerFragment);
            }
        });

        binding.forgetPasswordButtonId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.action_loginFragment_to_forgotPassFragment);
            }
        });

        binding.loginButtonId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = binding.emailTextFieldId.getText().toString();
                password = binding.passwordTextFieldId.getText().toString();

                if(email.isEmpty()){
                    am.alert(getResources().getString(R.string.enterEmail));
                }else if(password.isEmpty()){
                    am.alert(getResources().getString(R.string.enterPassword));
                }else{
                    db.mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()) {
                                        login();
                                    } else{
                                        am.alert(task.getException().getMessage());
                                    }

                                }
                            });
                }
            }
        });
        return view;
    }

    public interface ILogin {

        void setUser(User user);

        Firestore getDb();

        void alert(String msg);

    }

}