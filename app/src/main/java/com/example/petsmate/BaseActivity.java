package com.example.petsmate;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

public class BaseActivity extends AppCompatActivity {
    public static final int INDEX_HOME_ACTIVITY = 0;
    public static final int INDEX_PET_ACTIVITY = 1;
    public static final int INDEX_MSG_ACTIVITY = 2;
    public static final int INDEX_MYPAGE_ACTIVITY = 3;

    private Context context;
    private BottomNavigationView bottomNavigationView;

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

    public void configBottomNavigation(final Context context, BottomNavigationView navigation) {
        final int contextIndex = getContextIndex(context);
        this.context = context; this.bottomNavigationView = navigation;
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                switch (item.getItemId()) {
                    case R.id.tab_home:
                        if (contextIndex != INDEX_HOME_ACTIVITY) {
                            intent = new Intent(context, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(intent);
                            overridePendingTransition(0, 0);
                        }
                        return true;
                    case R.id.tab_pet:
                        if (contextIndex != INDEX_PET_ACTIVITY) {
//                            intent = new Intent(context, DashboardActivity.class);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                            startActivity(intent);
//                            overridePendingTransition(0, 0);
                        }
                        return true;
                    case R.id.tab_msg:
                        if (contextIndex != INDEX_MSG_ACTIVITY) {
//                            intent = new Intent(context, NotificationsActivity.class);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                            startActivity(intent);
//                            overridePendingTransition(0, 0);
                        }
                        return true;
                    case R.id.tab_mypage:
                        if (contextIndex != INDEX_MYPAGE_ACTIVITY) {
                            intent = new Intent(context, mypage01.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(intent);
                            overridePendingTransition(0, 0);
                        }
                        return true;
                }
                return false;
            }
        });
    }

    private int getContextIndex(Context context) {

        if (context instanceof MainActivity || context instanceof LoginActivity || context instanceof DriverSignupActivity || context instanceof Idfind || context instanceof MapsNaverActivity || context instanceof  PwfindActivity || context instanceof ReserveMain || context instanceof SignupPetActivity || context instanceof SignupActivity) {
            return INDEX_HOME_ACTIVITY;
//        } else if (context instanceof DashboardActivity) {
//            return INDEX_DASHBOARD_ACTIVITY;
        } else if (context instanceof mypage01 || context instanceof mypage02 || context instanceof mypage03 || context instanceof mypage04 || context instanceof mypage06 || context instanceof mypage07) {
            return INDEX_MYPAGE_ACTIVITY;
        }

        return 0;
    }

    public void updateBottomMenu(Context context, BottomNavigationView navigation) {
        Menu menu = navigation.getMenu();
        MenuItem menuItem = menu.getItem(getContextIndex(context));
        menuItem.setChecked(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(context != null && bottomNavigationView != null)
            updateBottomMenu(context, bottomNavigationView);
    }
}