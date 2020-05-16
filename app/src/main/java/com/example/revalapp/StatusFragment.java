package com.example.revalapp;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StatusFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StatusFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private RecyclerView mFirestoreList;
    private FirebaseFirestore firebaseFirestore;

    private FirestoreRecyclerAdapter adapter;

    public StatusFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StatusFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StatusFragment newInstance(String param1, String param2) {
        StatusFragment fragment = new StatusFragment();
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
        View view = inflater.inflate(R.layout.fragment_status, container, false);
        //TextView tv = (TextView)view.findViewById(R.id.textView4);
        String regNo = ((MyApplication) getActivity().getApplication()).getSomeVariable();
        //tv.setText("Hello " + regNo);
        //tv.setText("RE-EVALUATION STATUS");
        firebaseFirestore = FirebaseFirestore.getInstance();
        mFirestoreList = view.findViewById(R.id.firestore_list);

        //Query
        Query query = firebaseFirestore.collection(regNo);
        //RecyclerOptions
        FirestoreRecyclerOptions<StudentRecord> options = new FirestoreRecyclerOptions.Builder<StudentRecord>()
                .setQuery(query,StudentRecord.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<StudentRecord, StudentViewHolder>(options) {
            @NonNull
            @Override
            public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_single, parent, false);
                return new StudentViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull StudentViewHolder holder, int position, @NonNull StudentRecord model) {
                holder.list_name.setText(model.getName());
                holder.list_code.setText(model.getCode());
                holder.list_grade.setText("ORIGINAL GRADE:  " + model.getGrade());
                //Check if reval done
                if(model.getNewGrade() == null)
                {
                    holder.list_new_grade.setText("Re-Evaluation Pending. Please check back later");
                    holder.list_new_grade.setTypeface(holder.list_new_grade.getTypeface(), Typeface.BOLD);
                }
                else {
                    holder.list_new_grade.setText("NEW GRADE:  " + model.getNewGrade());
                    //if change, show in green. else red
                    if (!model.getGrade().equalsIgnoreCase(model.getNewGrade())) {
                        holder.list_new_grade.setTextColor(Color.parseColor("#00b300"));
                    } else
                        holder.list_new_grade.setTextColor(Color.parseColor("#ff0000"));
                }

            }
        };

        mFirestoreList.setHasFixedSize(true);
        mFirestoreList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mFirestoreList.setAdapter(adapter);
        //ViewHolder

        return view;
    }

    private class StudentViewHolder extends RecyclerView.ViewHolder{

        private TextView list_name;
        private TextView list_code;
        private TextView list_grade;
        private TextView list_new_grade;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);

            list_name = itemView.findViewById(R.id.list_name);
            list_code = itemView.findViewById(R.id.list_code);
            list_grade = itemView.findViewById(R.id.list_grade);
            list_new_grade = itemView.findViewById(R.id.list_new_grade);
        }


    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }
}
