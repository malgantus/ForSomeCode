package ru.olkhovikdenis.olkhovikdenisapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity  {

    final String LINK_POLITIC = "http://ria.ru/export/rss2/politics/index.xml";
    final String LINK_IN_WORLD = "http://ria.ru/export/rss2/world/index.xml";
    final String LINK_ECONOMY = "http://ria.ru/export/rss2/economy/index.xml";
    final String LINK_MOSCOW = "http://ria.ru/export/rss2/moscow/index.xml";

    MyTask SendGetTask;

    String XMLstr;
    String currentlink;

    final String ATTRIBUTE_NAME_TITLE = "title";
    final String ATTRIBUTE_NAME_DESCRIPTION = "description";

    List<News> ListNews = new ArrayList<News>();

    ListView lvMain;

    SimpleAdapter sAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }
    //Получает XML с новостями. Парсит. Выводит список.
    public void SendGet(String URLStr) throws Exception {
    //Информируем пользователя
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(getApplicationContext(), "Загрузка новостей.", Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        String line = "";
    //Получаем XML с новостями
        URL urlobj = new URL(URLStr);
        HttpURLConnection conn = (HttpURLConnection) urlobj.openConnection();

        InputStreamReader isr = new InputStreamReader(conn.getInputStream());
        BufferedReader reader = new BufferedReader(isr);
        StringBuilder sb = new StringBuilder();

        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        XMLstr = sb.toString();
    //Парсим. Результатом является список объектов класса News.
        ListNews = News.SAXParsing(XMLstr);
    //Формируем ListView
        ArrayList<Map<String, Object>> data = new ArrayList<Map<String, Object>>(ListNews.size());
        Map<String, Object> m;
        for (int i = 0; i < ListNews.size(); i++) {
            m = new HashMap<String, Object>();
            m.put(ATTRIBUTE_NAME_TITLE, ListNews.get(i).title);
            m.put(ATTRIBUTE_NAME_DESCRIPTION, ListNews.get(i).description);
            data.add(m);
        }

        String[] from = { ATTRIBUTE_NAME_TITLE, ATTRIBUTE_NAME_DESCRIPTION};

        int[] to = { R.id.tvtitle, R.id.tvdescription };

        sAdapter = new SimpleAdapter(this, data, R.layout.itemlv,
                from, to);

        lvMain = (ListView) findViewById(R.id.lvMain);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                lvMain.setAdapter(sAdapter);
            }
        });

    }

    class MyTask extends AsyncTask<Void, Void, Void> {
        @Override
        public Void doInBackground(Void... params){
            try {
                SendGet(currentlink);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    //Запускаем фоновый процесс
    public void onClick() {
        SendGetTask = new MyTask();
        SendGetTask.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id){
            case R.id.action_politic:
                currentlink = LINK_POLITIC;
                break;
            case R.id.action_in_world:
                currentlink = LINK_IN_WORLD;
                break;
            case R.id.action_economy:
                currentlink = LINK_ECONOMY;
                break;
            case R.id.action_moscow:
                currentlink = LINK_MOSCOW;
                break;
        }

        onClick();

        return super.onOptionsItemSelected(item);
    }

}
