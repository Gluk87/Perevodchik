package forlife.yaperevod;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;


public class FragmTranslate extends Fragment {

    TextView zagolovok; //Текст заголовка
    private TextView outputText; //Текст перевода
    private TextView yandexText; // Текст Яндекса
    EditText inputText; // Вводимый текст
    public static final String APP_PREFERENCES = "shared";
    public static final String APP_PREFERENCES_FROM = "from_text";
    public static final String APP_PREFERENCES_TO = "to_text";
    public static final String APP_PREFERENCES_LANG_FROM = "lang_from";
    public static final String APP_PREFERENCES_LANG_TO = "lang_to";
    SharedPreferences mShare;
    DB db;
    ImageView imageView;
    TranslateTask translateTask;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup,
                             Bundle savedInstanceState) {
        final View translView = inflater.inflate(R.layout.fragment1, viewGroup, false);

        zagolovok = (TextView) translView.findViewById(R.id.zagolovok);
        zagolovok.setText("Я Переводчик");

        mShare = getActivity().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        String mShareFrom = mShare.getString(APP_PREFERENCES_FROM, "");
        String mShareTo = mShare.getString(APP_PREFERENCES_TO, "");
        String mShareLangFrom = mShare.getString(APP_PREFERENCES_LANG_FROM, "");
        String mShareLangTO =  mShare.getString(APP_PREFERENCES_LANG_TO, "");

        inputText = (EditText) translView.findViewById(R.id.inputText);
        if (mShare.contains(APP_PREFERENCES_FROM)) inputText.setText(mShareFrom); //Если в SharedPreference лежит текст ввода, выводим

        outputText = (TextView) translView.findViewById(R.id.textView);
        if (mShare.contains(APP_PREFERENCES_TO)) outputText.setText(mShareTo); //Если в SharedPreference лежит текст перевода, выводим
        else outputText.setText("");

        outputText.setMovementMethod(new ScrollingMovementMethod()); //Прокрутка текста

        yandexText = (TextView) translView.findViewById(R.id.textView2);
        yandexText.setMovementMethod(LinkMovementMethod.getInstance()); //Сделать ссылку на сайт


        //Установка языков

        String[] list = getResources().getStringArray(R.array.lang_spisok);
        /*String[] listNew = new String[list.length];
        for(int j=0; j<list.length; j++){
            list[j] = list[j].toString().substring(list[j].toString().indexOf("-")+1);
            listNew[j]=list[j];
        }*/


        final Spinner spin_from = (Spinner) translView.findViewById(R.id.spinner_from);
        final ArrayAdapter<String> fromAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, list);
        fromAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin_from.setAdapter(fromAdapter);
        if (mShare.contains(APP_PREFERENCES_LANG_FROM)) spin_from.setSelection(Integer.parseInt(mShareLangFrom));
        else spin_from.setSelection(0);// английский яз. по умолчанию

        final Spinner spin_to = (Spinner) translView.findViewById(R.id.spinner_to);
        final ArrayAdapter<String> toAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, list);
        toAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin_to.setAdapter(toAdapter);
        if (mShare.contains(APP_PREFERENCES_LANG_TO)) spin_to.setSelection(Integer.parseInt(mShareLangTO));
        else spin_to.setSelection(1);// русский яз. по умолчанию

        //Обработка нажатия на стрелочки. Меняем языки местами
        ImageView img = (ImageView) translView.findViewById(R.id.imageView);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int m_to = spin_to.getSelectedItemPosition();
                int m_from = spin_from.getSelectedItemPosition();
                SpinnerAdapter a_to = spin_to.getAdapter();
                SpinnerAdapter a_from = spin_from.getAdapter();
                spin_to.setAdapter(a_from);
                spin_from.setAdapter(a_to);
                spin_to.setSelection(m_from);
                spin_from.setSelection(m_to);
            }

        });

        //Очищаем поля при нажатии на крестик
            imageView = (ImageView) translView.findViewById(R.id.zImage);
            imageView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    outputText.setText("");
                    inputText.setText("");
                }
            });

        //подключаемся к БД
        db = new DB(getActivity());
        db.open();

        //Следим за вводом текста. После паузы 1 сек. отправляем текст на перевод.
        //Показываем результат. Заполняем таблицу с историей
        inputText.addTextChangedListener(new TextWatcher(){
            private Timer timer=new Timer();
            private final long DELAY = 1000; // милисекунды
                  @Override
                  public void afterTextChanged(Editable s) {
                      timer.cancel();
                      timer = new Timer();

                      timer.schedule(new TimerTask() {
                                      @Override
                                      public void run () {
                                          getActivity().runOnUiThread(new Runnable() {
                                          @Override
                                          public void run () {
                                          translateTask = new TranslateTask();
                                          String k_from = spin_from.getSelectedItem().toString();
                                          String k_to = spin_to.getSelectedItem().toString();
                                          int langFromPos = spin_from.getSelectedItemPosition();
                                          int langToPos = spin_to.getSelectedItemPosition();
                                          final String strFrom = inputText.getText().toString(); //Вводимый текст
                                          String strLangFrom = k_from.substring(0, k_from.indexOf("-")); //Язык с
                                          String strLangTo = k_to.substring(0, k_to.indexOf("-")); //Язык на
                                          translateTask.execute(strFrom, strLangFrom + '-' + strLangTo);//запускаем отправку текста на перевод в асинхронном режиме
                                          try {
                                              String str = translateTask.get().toString();
                                              String strCode = str.substring(str.indexOf("<Translation") + 19, str.indexOf("<Translation") + 22);

                                              //Код ответа 200 - успешно
                                              if (strCode.equals("200")) {
                                                  if (str.indexOf("<text>") + 6 < str.indexOf("</text>") - 1) { //доп.проверка, что присланный текст в кавычках
                                                      String strTo = str.substring(str.indexOf("<text>") + 7, str.indexOf("</text>") - 1); //Переведенный текст
                                                      outputText.setText(strTo); //показываем перевод

                                                      //Заполняем SharedPreferences. При рестарте приложения или возврате в активити
                                                      //наши данные сохранятся
                                                      SharedPreferences.Editor editor = mShare.edit();
                                                      editor.putString(APP_PREFERENCES_FROM, strFrom);
                                                      editor.putString(APP_PREFERENCES_TO, outputText.getText().toString());
                                                      editor.putString(APP_PREFERENCES_LANG_FROM, String.valueOf(langFromPos));
                                                      editor.putString(APP_PREFERENCES_LANG_TO, String.valueOf(langToPos));
                                                      editor.apply();

                                                      //Показывам сообщение "Переведено с помощью Яндекса"
                                                      yandexText.setText(R.string.link);

                                                      //Добавляем перевод в таблицу с историей. Длинный текст обрезаем.
                                                      if ((strFrom.length() > 0) & (strFrom.length()<24))
                                                          db.addRec(strFrom,
                                                                  outputText.getText().toString(),
                                                                  (strLangFrom + '-' + strLangTo).toUpperCase(),
                                                                  R.drawable.ic_bookmark_grey_48dp);
                                                      else if (strFrom.length() > 25)
                                                          db.addRec(strFrom.substring(0, 21) + "...",
                                                                  outputText.getText().toString().substring(0, 21) + "...",
                                                                  (strLangFrom + '-' + strLangTo).toUpperCase(),
                                                                  R.drawable.ic_bookmark_grey_48dp);
                                                  }
                                                  //Возможные кода ответа от Яндекса
                                              } else if (strCode.equals("404"))
                                                  outputText.setText("Превышено суточное ограничение на объем переведенного текста");
                                              else if (strCode.equals("413"))
                                                  outputText.setText("Превышен максимально допустимый размер текста");
                                              else if (strCode.equals("422"))
                                                  outputText.setText("Текст не может быть переведен");
                                              else if (strCode.equals("501"))
                                                  outputText.setText("Заданное направление перевода не поддерживается");
                                              else outputText.setText("Ошибка соединения");
                                          } catch (InterruptedException | ExecutionException e) {
                                              e.printStackTrace();
                                          }
                                        }
                                  });
                                 }
                              },
                              DELAY
                      );
                  }


                  @Override
                  public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                  }
                   @Override
                   public void onTextChanged(CharSequence s, int start, int before, int count) {
                   }
                   }
        );
        setRetainInstance(true);
        return translView;
    }
}

