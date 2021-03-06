package com.example.revalapp;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;
import static androidx.constraintlayout.widget.Constraints.TAG;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ApplicationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ApplicationFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private String name;
    private String code;
    private String grade;
    private String newGrade;
    private Uri filePath;

    private final int PICK_IMAGE_REQUEST = 71;
    private TextInputLayout textInputLayout;

    TextView Tname ;
    TextView Tcode ;
    TextView Tgrade;
    // Access a Cloud Firestore instance from your Activity
    FirebaseFirestore db;
    Uri pdfUri;
    TextView notif;
    //Firebase
    FirebaseStorage storage;
    StorageReference storageReference;

    public ApplicationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ApplicationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ApplicationFragment newInstance(String param1, String param2) {
        ApplicationFragment fragment = new ApplicationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_application, container, false);
        TextView tv = (TextView)view.findViewById(R.id.textView3);
        final String regNo = ((MyApplication) getActivity().getApplication()).getSomeVariable();
        tv.setText("Hello " + regNo);

        //Writing to FireStore
        Button button = view.findViewById(R.id.button2);
        Tname = view.findViewById(R.id.edit_name);

        //Checking if subject has already been applied
        Tname.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b)
                {
                    FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
                    CollectionReference yourCollRef = rootRef.collection(regNo);
                    Query query = yourCollRef.whereEqualTo("name", Tname.getText().toString());
                    query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                    MaterialAlertDialogBuilder ab = new MaterialAlertDialogBuilder(getContext());
                                    ab.setTitle("Already Applied");
                                    ab.setMessage("You have already appllied for this subject. Kindly try again");
                                    ab.show();
                                    Tname.setText("");
                                    Tcode.setText("");
                                    Tgrade.setText("");
                                    notif.setText("");
                                }
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });

                }

            }
        });

        Tcode = view.findViewById(R.id.edit_code);
        Tgrade = view.findViewById(R.id.edit_grade);
        Button button1 = view.findViewById(R.id.button3);
        Button selectFile = view.findViewById(R.id.selectFile);
        notif = view.findViewById(R.id.textView4);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        selectFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getActivity(),"clicked", Toast.LENGTH_SHORT).show();
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED)
                {
                    //Toast.makeText(getActivity(),"in if", Toast.LENGTH_SHORT).show();
                    chooseImage();
                }
                else
                {
                    //Toast.makeText(getActivity(),"else", Toast.LENGTH_SHORT).show();
                    ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},9);
                }

            }
        });

//        button1.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View view) {
//                uploadImage();
//
//            }
//        });
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
              //  uploadImage();
                db = FirebaseFirestore.getInstance();

                name = Tname.getText().toString();
                code = Tcode.getText().toString();
                grade = Tgrade.getText().toString();

                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
                builder.setTitle("Confirm Submission");
                builder.setMessage("Please confirm details:\n\nSubject Name: " + name +"\nSubject Code: "+ code + "\nCurrent Grade: "+ grade);
                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Map<String, Object> student = new HashMap<>();
                        student.put("name",name);
                        student.put("code",code);
                        student.put("grade",grade);
                        student.put("newGrade",newGrade);
                        uploadImage();
                        db.collection(regNo)
                                .add(student)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        MaterialAlertDialogBuilder builderApply = new MaterialAlertDialogBuilder(getContext());
                                        builderApply.setTitle("Application Successful");
                                        builderApply.setMessage("Re-evaluation applied for subject "+ code + "\nKindly fill form again, if you wish to apply for other subjects.");
                                        builderApply.show();
                                        Tname.setText("");
                                        Tcode.setText("");
                                        Tgrade.setText("");
                                        notif.setText("");


                                    }
                                });

                    }
                });
                builder.show();

            }
        });

        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 9 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
        {
            chooseImage();
        }
        else
            Toast.makeText(getActivity(),"Please provide Permission", Toast.LENGTH_SHORT).show();
    }

//    private void selectPdf()
//    {
//        Intent intent = new Intent();
//        intent.setType("application/pdf");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(intent, 13);
//    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//
//        if(requestCode == 13 && resultCode == RESULT_OK && data!= null)
//        {
//            pdfUri = data.getData();
//            notif.setText(pdfUri.toString());
//
//        }
//        else
//        {
//            Toast.makeText(getActivity(),"Please select file", Toast.LENGTH_SHORT).show();
//        }
//        super.onActivityResult(requestCode, resultCode, data);
//    }
    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getApplicationContext().getContentResolver(), filePath);
                notif.setText(filePath.toString());
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
    private void uploadImage() {

        if(filePath != null)
        {
            code = Tcode.getText().toString();
            final ProgressDialog progressDialog = new ProgressDialog(getActivity());
//            progressDialog.setTitle("Uploading...");
//            progressDialog.show();
            final String regNo = ((MyApplication) getActivity().getApplication()).getSomeVariable();
            StorageReference ref = storageReference.child("images/"+regNo +"-"+code);
            ref.putFile(filePath)
//                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                            progressDialog.dismiss();
//                            Toast.makeText(getActivity(), "Uploaded", Toast.LENGTH_SHORT).show();
//                        }
//                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
//                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
//                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
//                                    .getTotalByteCount());
//                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
//                        }
//                    });
        }
    }
}
