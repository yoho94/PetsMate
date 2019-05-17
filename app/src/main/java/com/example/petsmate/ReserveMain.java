package com.example.petsmate;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ReserveMain extends AppCompatActivity {

    TableLayout petLayout; // 펫 위치 시킬 레이아웃
    ArrayList<PetInfo> petInfos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reserve_main);
        petLayout = (TableLayout) findViewById(R.id.reserve_layout_tablePet);

        /**  펫 읽어와서 띄우기 **/
        petInfos = MainActivity.memberInfo.getPetInfos();
        // 레이아웃 warp_content 지정
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);

        for(int i=0; i<petInfos.size(); i++) {
            TableRow tableRow = new TableRow(this);
            tableRow.setLayoutParams(layoutParams);
            tableRow.setGravity(Gravity.CENTER);

            for(int j=0; j<petInfos.size()/2+0.95; j++) {

                CheckBox checkBox = new CheckBox(this);
                checkBox.setLayoutParams(layoutParams);

                int petCode = petInfos.get(i).getCodeInt();
                String petName = petInfos.get(i).getName();

                checkBox.setId(petCode);
                checkBox.setText(petName);

                tableRow.addView(checkBox);
            }
        }
    }
}