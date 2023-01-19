package com.example.uno.fragments;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.uno.R;
import com.example.uno.database.Firestore;
import com.example.uno.databinding.FragmentRegisterBinding;
import com.example.uno.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class RegisterFragment extends Fragment {

    private static final int PICK_IMAGE_GALLERY = 100;
    private static final int PICK_IMAGE_CAMERA = 101;

    NavController navController;

    Uri imageUri;
    String fileName;
    String email, password, firstName, lastName, city, gender;

    FragmentRegisterBinding binding;

    Firestore db;

    IRegister am;

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_GALLERY && data != null && resultCode == RESULT_OK) {
            imageUri = data.getData();
            binding.userImage.setImageURI(imageUri);
        }
        else if (requestCode == PICK_IMAGE_CAMERA && data != null && resultCode == RESULT_OK) {

            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            binding.userImage.setImageBitmap(imageBitmap);

            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            String path = MediaStore.Images.Media.insertImage(getContext().getContentResolver(), imageBitmap, "Title", null);
            imageUri =  Uri.parse(path);

        }

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof IRegister) {
            am = (IRegister) context;
        } else {
            throw new RuntimeException(context.toString());
        }
        db = am.getDb();
    }

    private void storeUserInfoToFirestore(String firstName, String lastName, String city, String gender, String email, String fileName) {

        HashMap<String, Object> data = new HashMap<>();
        data.put("firstname", firstName);
        data.put("lastname", lastName);
        data.put("city", city);
        data.put("gender", gender);
        data.put("email", email);
        data.put("photoref", fileName);
        data.put("rides", new ArrayList<>());

        db.firestore.collection(Firestore.DB_PROFILE)
                .document(db.mAuth.getUid())
                .set(data)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        am.setUser(new User(firstName, lastName, fileName, city, email, gender, db.mAuth.getUid()));
                        navController.navigate(R.id.action_registerFragment_to_lobbyFragment);
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getActivity().setTitle(R.string.createAccount);

        binding = FragmentRegisterBinding.inflate(inflater, container, false);

        View view = binding.getRoot();

        binding.userImage.setImageResource(R.drawable.profile_image);

        navController = Navigation.findNavController(getActivity(), R.id.fragmentContainerView2);

        binding.registerButtonId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                firstName = binding.createFragmentFirstNameId.getText().toString();
                lastName = binding.createFragmentLastNameId.getText().toString();
                city = binding.createFragmentCityNameId.getText().toString();
                email = binding.createFragmentEmailId.getText().toString();
                password = binding.createFragmentPasswordId.getText().toString();
                RadioButton radioButton = binding.getRoot().findViewById(binding.radioGroup.getCheckedRadioButtonId());
                gender = radioButton.getText().toString();

                if(firstName.isEmpty()){
                    am.alert(getResources().getString(R.string.enterFirstName));
                }else if(lastName.isEmpty()){
                    am.alert(getResources().getString(R.string.enterLastName));
                }else if(city.isEmpty()){
                    am.alert(getResources().getString(R.string.enterCity));
                } else if(email.isEmpty()){
                    am.alert(getResources().getString(R.string.enterEmail));
                }else if(password.isEmpty()){
                    am.alert(getResources().getString(R.string.enterPassword));
                }else if(gender.isEmpty()){
                    am.alert(getResources().getString(R.string.chooseGender));
                }else {

                    db.mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()) {

                                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                .setDisplayName(firstName + " " + lastName).build();
                                        FirebaseUser user = db.getCurrentUser();
                                        user.updateProfile(profileUpdates);

                                        if (imageUri != null) {
                                            fileName = UUID.randomUUID().toString() + ".jpg";

                                            storeUserInfoToFirestore(firstName, lastName, city, gender, email, fileName);

                                            db.storage.child(user.getUid()).child(fileName).putFile(imageUri);
                                        } else {
                                            storeUserInfoToFirestore(firstName, lastName, city, gender, email, null);
                                        }

                                    } else
                                        am.alert(task.getException().getMessage());

                                }
                            });
                }

            }
        });

        binding.cancelButtonId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.popBackStack();
            }
        });

        binding.userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });

        return view;
    }

    public void selectImage() {
        final CharSequence[] options = {"Gallery", "Camera"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.imagePick)
                .setItems(options, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if(options[which].equals("Gallery")){
                            dialog.dismiss();
                            Intent takePictureFromGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(takePictureFromGallery, PICK_IMAGE_GALLERY);
                        }else if(options[which].equals("Camera")){
                            dialog.dismiss();
                            Intent takePictureFromCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(takePictureFromCamera, PICK_IMAGE_CAMERA);
                        }
                    }
                });
        builder.create().show();
    }



    public interface IRegister {

        void setUser(User user);

        Firestore getDb();

        void alert(String msg);
    }
}