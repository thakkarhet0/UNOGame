package com.example.uno.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.uno.R;
import com.example.uno.database.Firestore;
import com.example.uno.databinding.FragmentForgotPassBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class ForgotPassFragment extends Fragment {

    FragmentForgotPassBinding binding;

    IForgot am;

    Firestore db;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof IForgot) {
            am = (IForgot) context;
        } else {
            throw new RuntimeException(context.toString());
        }
        db = am.getDb();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getActivity().setTitle(R.string.forgotPassword);

        binding = FragmentForgotPassBinding.inflate(inflater, container, false);

        View view = binding.getRoot();

        NavController navController = Navigation.findNavController(getActivity(), R.id.fragmentContainerView2);

        binding.resetPasswordButtonId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailAddress = binding.emailTextFieldId.getText().toString();

                if(!emailAddress.isEmpty()){
                    db.mAuth.sendPasswordResetEmail(emailAddress)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getContext(), getResources().getString(R.string.passwordResetEmail), Toast.LENGTH_LONG).show();
                                        navController.popBackStack();
                                    }
                                }
                            });
                }else{
                    am.alert(getResources().getString(R.string.enterEmail));
                }

            }
        });

        //....Cancel Button......
        binding.cancelResetPasswordId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.popBackStack();
            }
        });


        return view;
    }

    public interface IForgot{
        void alert(String msg);
        Firestore getDb();
    }
}