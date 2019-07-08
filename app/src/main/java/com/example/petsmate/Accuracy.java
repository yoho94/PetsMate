package com.example.petsmate;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

public class Accuracy extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_naver);
    }

    public void onPopupButtonClick(View button) {
        //PopupMenu 객체 생성.
        PopupMenu popup = new PopupMenu(this, button);

        //설정한 popup XML을 inflate.
        popup.getMenuInflater().inflate(R.menu.popup, popup.getMenu());

        //팝업메뉴 클릭 시 이벤트
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.accuracy:
                        /* Search를 선택했을 때 이벤트 실행 코드 작성 */
                        break;

                    case R.id.time:
                        /* Add를 선택했을 때 이벤트 실행 코드 작성 */
                        break;

                }
                return true;
            }
        });
        popup.show();
    }
}
