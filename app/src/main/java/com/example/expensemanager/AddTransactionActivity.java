package com.example.expensemanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.expensemanager.databinding.ActivityAddTransactionBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class AddTransactionActivity extends AppCompatActivity {
    ActivityAddTransactionBinding binding;
    String type="";
    FirebaseFirestore fStore;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityAddTransactionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        fStore=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();

        binding.expenseCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type="Expense";
                binding.expenseCheckbox.setChecked(true);
                binding.incomeCheckbox.setChecked(false);
            }
        });

        binding.incomeCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type="Income";
                binding.incomeCheckbox.setChecked(true);
                binding.expenseCheckbox.setChecked(false);
            }
        });

        binding.btnAddTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amount = binding.userAmount.getText().toString().trim();
                String note = binding.userNote.getText().toString().trim();
                if (amount.length()<=0)
                {
                    return;
                }
                if(type.length()<=0)
                {
                    Toast.makeText(AddTransactionActivity.this, "Select Transaction Type", Toast.LENGTH_SHORT).show();
                }
                SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd mm yyyy_HH:mm", Locale.getDefault());
                String currentDateandTime = simpleDateFormat.format(new Date());


                Map<String,Object> transaction = new HashMap<>();
                String id = UUID.randomUUID().toString();
                transaction.put("ID",id);
                transaction.put("Amount",amount);
                transaction.put("Note",note);
                transaction.put("Type",type);
                transaction.put("Date",currentDateandTime);
                fStore.collection("Expenses").document(firebaseAuth.getUid()).collection("Notes").document(id).set(transaction).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(AddTransactionActivity.this,"ADDED",Toast.LENGTH_SHORT).show();
                        binding.userAmount.setText("");
                        binding.userNote.setText("");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddTransactionActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        }
}