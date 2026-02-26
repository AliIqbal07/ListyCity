package com.example.listycity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.CollectionReference;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    ListView cityList;
    ArrayAdapter<String> cityAdapter;
    ArrayList<String> dataList;
    FirebaseFirestore db;
    CollectionReference citiesRef;
    ArrayList<String> docIdList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        db = FirebaseFirestore.getInstance();
        citiesRef = db.collection("cities");
        dataList = new ArrayList<>();
        docIdList = new ArrayList<>();
        citiesRef.addSnapshotListener((value, error) -> {
            if (error != null || value == null) return;

            dataList.clear();
            docIdList.clear();

            for (var document : value) {
                String cityName = document.getString("name");
                String docId = document.getId();

                dataList.add(cityName);
                docIdList.add(docId);
            }

            cityAdapter.notifyDataSetChanged();
        });
        cityList = findViewById(R.id.city_list);
        android.widget.EditText addCityEdit = findViewById(R.id.add_city_field);
        android.widget.Button addButton = findViewById(R.id.add_button);
        android.widget.Button deleteButton = findViewById(R.id.delete_button);

        dataList = new ArrayList<>();

        cityAdapter = new ArrayAdapter<>(this, R.layout.content, dataList);
        cityList.setAdapter(cityAdapter);
        final int[] selectedPosition = {-1};
        cityList.setOnItemClickListener((parent, view, position, id) -> {
            selectedPosition[0] = position;
        });
        addButton.setOnClickListener(v -> {
            String newCity = addCityEdit.getText().toString();
            if (!newCity.isEmpty()) {
//                dataList.add(newCity);
//                cityAdapter.notifyDataSetChanged();
                Map<String, Object> city = new HashMap<>();
                city.put("name", newCity);
                citiesRef.add(city);
                addCityEdit.setText("");
            }
        });
        deleteButton.setOnClickListener(v -> {
            if (selectedPosition[0] != -1) {
                String docId = docIdList.get(selectedPosition[0]);
                citiesRef.document(docId).delete();
                selectedPosition[0] = -1;
            }
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}