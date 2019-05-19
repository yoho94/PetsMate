package com.example.petsmate;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;

import java.util.ArrayList;

class MyAdapter extends ArrayAdapter{
    private Activity m_context;
    private ArrayList<String> m_arrayList;
    private ArrayList<CheckBox> m_checkBoxList;

    public MyAdapter(Activity context, int textViewResourceId,
                     ArrayList<String> objects) {
        super(context, textViewResourceId, objects);

        m_context = context;
        m_arrayList = objects;
        setCheckBoxList();
    }

    private void setCheckBoxList()
    {
        m_checkBoxList = new ArrayList<CheckBox>();
        for(int i = 0; i < m_arrayList.size(); i++)
        {
            CheckBox checkBox = new CheckBox(m_context);
            checkBox.setFocusable(false);
            checkBox.setClickable(false);
            checkBox.setText(m_arrayList.get(i));
            m_checkBoxList.add(checkBox);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        return m_checkBoxList.get(position);
    }
}

public class ReserveMain extends AppCompatActivity {

    ArrayList<PetInfo> petInfos;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reserve_main);

        /**  펫 읽어와서 띄우기 **/
        petInfos = MainActivity.memberInfo.getPetInfos();
        ArrayList<String> arrayList = new ArrayList<>();

        for(int i=0; i<petInfos.size(); i++) {
            arrayList.add(petInfos.get(i).getName());
        }

        MyAdapter adapter = new MyAdapter(this, android.R.layout.simple_list_item_1, arrayList);
        listView = (ListView) findViewById(R.id.reserve_petListView);
        listView.setAdapter(adapter);
    }
}