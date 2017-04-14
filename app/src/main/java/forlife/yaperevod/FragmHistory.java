package forlife.yaperevod;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

//Фрагмент с Историей
public class FragmHistory extends Fragment {

    ListView listView;
    DB db;
    SimpleCursorAdapter scAdapter;
    TextView mTextMessage;
    Cursor cursor;
    private static final int CM_DELETE_ID = 1;
    TextView zagolovok;
    ImageView imageView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup,
                             Bundle savedInstanceState) {
        View histView = inflater.inflate(R.layout.fragment2, viewGroup, false);

        mTextMessage = (TextView) histView.findViewById(R.id.text3);
        db = new DB(getActivity());
        db.open();
        cursor = db.getAllData();


        zagolovok = (TextView) histView.findViewById(R.id.zagolovok);
        zagolovok.setText("История");

        // формируем столбцы сопоставления
        String[] from = new String[] { DB.COLUMN_FROM, DB.COLUMN_TO, DB.COLUMN_LANG, DB.COLUMN_FAV };
        int[] to = new int[] { R.id.fromText , R.id.toText, R.id.langText, R.id.imageGrey};

        // создаем адаптер и настраиваем список
        scAdapter = new SimpleCursorAdapter(getContext(), R.layout.activity_hist, cursor, from, to, 0);
        listView = (ListView) histView.findViewById(R.id.listHist);
        listView.setAdapter(scAdapter);
        registerForContextMenu(listView);


        //Обработка нажатия на элемент списка - отправляем в Избранное
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View itemClicked, int position, long id) {

                TextView myFromView = (TextView) itemClicked.findViewById(R.id.fromText);
                String favFrom = myFromView.getText().toString();

                TextView myToView = (TextView) itemClicked.findViewById(R.id.toText);
                String favTo = myToView.getText().toString();

                TextView myLangView = (TextView) itemClicked.findViewById(R.id.langText);
                String favLang = myLangView.getText().toString();

                //Добавляем в таблицу Избранное
                db.addRecFav(favFrom, favTo, favLang);

                //4. Создаем всплывающий Toast
                Toast.makeText(getActivity(), "Добавлено в избранное", Toast.LENGTH_SHORT).show();

            }

        });

        //Очищаем историю при нажатии на корзину
        imageView = (ImageView) histView.findViewById(R.id.zImage);
        imageView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                db.trunc_table();
                //Обновляем список
                cursor = db.getAllData();
                scAdapter.swapCursor(cursor);
            }
        });

        return histView;
    }

    @Override
    //Контекстное меню для удаления одной записи
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, CM_DELETE_ID, 0, "Удалить запись");

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == CM_DELETE_ID) {
            // получаем из пункта контекстного меню данные по пункту списка
            AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) item
                    .getMenuInfo();
            // удаляем запись в БД
            db.delRec(acmi.id);
            //Обновляем список
            cursor = db.getAllData();
            scAdapter.swapCursor(cursor);

            return true;
        }
        return super.onContextItemSelected(item);
    }

}
