package com.example.revalapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

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

    TextView Tname ;
    TextView Tcode ;
    TextView Tgrade;
    // Access a Cloud Firestore instance from your Activity
    FirebaseFirestore db;


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
        TextView tv = (TextView)view.findViewById(R.id.textView5);
        final String regNo = ((MyApplication) getActivity().getApplication()).getSomeVariable();
        tv.setText("Hello " + regNo);

        //Writing to FireStore
        Button button = view.findViewById(R.id.button2);
        Tname = view.findViewById(R.id.edit_name);
        Tcode = view.findViewById(R.id.edit_code);
        Tgrade = view.findViewById(R.id.edit_grade);


        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                db = FirebaseFirestore.getInstance();

                name = Tname.getText().toString();
                code = Tcode.getText().toString();
                grade = Tgrade.getText().toString();

                Map<String, Object> student = new HashMap<>();
                student.put("name",name);
                student.put("code",code);
                student.put("grade",grade);
                student.put("newGrade",newGrade);

                db.collection(regNo)
                        .add(student)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error adding document", e);
                            }
                        });

            }
        });

        return view;
    }


}
