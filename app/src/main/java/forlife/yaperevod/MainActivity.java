package forlife.yaperevod;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

/* Created by Azat Sultangulov 25.03.2017 for Yandex.Mobilization
 * Application "YaPerevod"
 */

public class MainActivity extends AppCompatActivity {

    Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Если первый запуск, подставляем фрагмент с переводом
        if (savedInstanceState == null) {
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.addToBackStack(null);
            transaction.replace(R.id.fragment1, new FragmTranslate());
            transaction.commit();
        }
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    // Обработка нажатий на кнопки меню (меняем фрагменты)
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();

            switch (item.getItemId()) {
                case R.id.navigation_translate:
                    fragment = new FragmTranslate();
                    transaction.addToBackStack(null);
                    transaction.replace(R.id.fragment1, fragment);
                    transaction.commit();
                    break;
                case R.id.navigation_history:
                    fragment = new FragmHistory();
                    transaction.addToBackStack(null);
                    transaction.replace(R.id.fragment1, fragment);
                    transaction.commit();
                    break;
                case R.id.navigation_favourite:
                    fragment = new FragmFavor();
                    transaction.addToBackStack(null);
                    transaction.replace(R.id.fragment1, fragment);
                    transaction.commit();
                    break;
            }
            return true;
        }
    };
}

