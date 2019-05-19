package com.example.petsmate;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.ArrayList;

public class ReserveMain extends AppCompatActivity {

    ArrayList<PetInfo> petInfos;
    ListView listView;
    SparseBooleanArray sparseBooleanArray;
    CustomScrollView customScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reserve_main);

        customScrollView = (CustomScrollView) findViewById(R.id.reserve_CustomScrollView);

        /**  펫 읽어와서 띄우기 **/
        petInfos = MainActivity.memberInfo.getPetInfos();
        final ArrayList<String> arrayList = new ArrayList<>();

        for (int i = 0; i < petInfos.size(); i++) {
            arrayList.add(petInfos.get(i).getName());
            Log.i("PetArrayList", petInfos.get(i).getName());
        }

        listView = (ListView) findViewById(R.id.reserve_petListView);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,
                android.R.id.text1, arrayList);

        listView.setAdapter(adapter);
        listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

        // 이중 스크롤뷰 스크롤 가능하게 하기.
        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                customScrollView.setNeedIntercept(false);
                return false;
            }
        });

        customScrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                customScrollView.setNeedIntercept(true);
                return false;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                sparseBooleanArray = listView.getCheckedItemPositions();

                String ValueHolder = "";

                int i = 0;
                while (i < sparseBooleanArray.size()) {
                    if (sparseBooleanArray.valueAt(i)) {
                        ValueHolder += arrayList.get(sparseBooleanArray.keyAt(i)) + ",";
                    }
                    i++;
                }
                ValueHolder = ValueHolder.replaceAll("(,)*$", "");
                Toast.makeText(getApplicationContext(), "ListView Selected Values = " + ValueHolder, Toast.LENGTH_LONG).show();
            }
        });

    }

}