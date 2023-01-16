package com.example.expensemanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.expensemanager.databinding.ActivityPieChartBinding;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class PieChart extends AppCompatActivity {
    ActivityPieChartBinding binding;
    private long income = 0, expense = 0;

    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPieChartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();

        loadData();
    }

    private void loadData() {
        firebaseFirestore.collection("Expenses").document(firebaseAuth.getUid()).collection("Notes").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (DocumentSnapshot ds : task.getResult()) {
                    TransactionModel model = new TransactionModel(ds.getString("ID"), ds.getString("Note"), ds.getString("Amount"), ds.getString("Type"), ds.getString("Date"));
                    if (model.getType().equals("Income")) {
                        String amnt = model.getAmount();
                        income += Integer.parseInt(amnt);
                    } else {
                        String amnt = model.getAmount();
                        expense += Integer.parseInt(amnt);
                    }
                }
                setUpGraph();
            }
        });
    }

    private void setUpGraph() {
        List<PieEntry> pieEntryList = new ArrayList<>();
        List<Integer> colorsList = new ArrayList<>();
        if (income != 0) {
            pieEntryList.add(new PieEntry(income, "INCOME"));
            colorsList.add(getResources().getColor(R.color.teal_700));
        }
        if (expense != 0) {
            pieEntryList.add(new PieEntry(expense, "Expense"));
            colorsList.add(getResources().getColor(R.color.teal_200));
        }
        PieDataSet pieDataSet = new PieDataSet(pieEntryList, "Balance:"+String.valueOf(income - expense));
        pieDataSet.setColors(colorsList);
        PieData pieData = new PieData(pieDataSet);
        binding.PieChart.setData(pieData);
        binding.PieChart.invalidate();
    }
}