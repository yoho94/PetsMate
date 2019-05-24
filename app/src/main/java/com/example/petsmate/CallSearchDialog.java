package com.example.petsmate;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class CallSearchDialog {

    private Context context;
    private ArrayList<Place> places;

    public CallSearchDialog(Context context, ArrayList<Place> places) {
        this.context = context;
        this.places = places;
    }

    // 호출할 다이얼로그 함수를 정의한다.
    public void callFunction() {

        // 커스텀 다이얼로그를 정의하기위해 Dialog클래스를 생성한다.
        final Dialog dlg = new Dialog(context);

        // 액티비티의 타이틀바를 숨긴다.
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 커스텀 다이얼로그의 레이아웃을 설정한다.
        dlg.setContentView(R.layout.call_search_dialog);

        WindowManager.LayoutParams params = dlg.getWindow().getAttributes();
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();

        params.width = (int) (metrics.widthPixels * 0.9);
        params.height =(int) (metrics.heightPixels * 0.8);
        dlg.getWindow().setAttributes(params);

        ArrayList<TextView> placeNames = new ArrayList<>();
        ArrayList<TextView> distances = new ArrayList<>();
        ArrayList<TextView> phoneNumbers = new ArrayList<>();

        placeNames.add((TextView) dlg.findViewById(R.id.place_name1));
        placeNames.add((TextView) dlg.findViewById(R.id.place_name2));
        placeNames.add((TextView) dlg.findViewById(R.id.place_name3));
        placeNames.add((TextView) dlg.findViewById(R.id.place_name4));
        placeNames.add((TextView) dlg.findViewById(R.id.place_name5));

        distances.add((TextView) dlg.findViewById(R.id.distance_road_address1));
        distances.add((TextView) dlg.findViewById(R.id.distance_road_address2));
        distances.add((TextView) dlg.findViewById(R.id.distance_road_address3));
        distances.add((TextView) dlg.findViewById(R.id.distance_road_address4));
        distances.add((TextView) dlg.findViewById(R.id.distance_road_address5));

        phoneNumbers.add((TextView) dlg.findViewById(R.id.phone_number1));
        phoneNumbers.add((TextView) dlg.findViewById(R.id.phone_number2));
        phoneNumbers.add((TextView) dlg.findViewById(R.id.phone_number3));
        phoneNumbers.add((TextView) dlg.findViewById(R.id.phone_number4));
        phoneNumbers.add((TextView) dlg.findViewById(R.id.phone_number5));

        for(int i=0; i<places.size(); i++) {
            Place place = places.get(i);
            String name = place.getName();
            String phoneNumber = place.getPhoneNumber();
            String roadAddress = place.getRoadAddress();

            double distance = Double.parseDouble(place.getDistance()) / 1000;




            placeNames.get(i).setText(name);
            distances.get(i).setText(String.format("%.1fkm | %s", distance, roadAddress));
            if(phoneNumber == null || phoneNumber.equals("")) {
                phoneNumbers.get(i).setWidth(0);
                phoneNumbers.get(i).setHeight(0);
            } else
                phoneNumbers.get(i).setText(phoneNumber);
        }

        // 커스텀 다이얼로그를 노출한다.
        dlg.show();
    }
}
