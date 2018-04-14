package dev.lazyllamas.rizzyclient;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import dev.lazyllamas.rizzyclient.Business.Person;

public class PersonActivity extends AppCompatActivity implements AbsListView.OnScrollListener {

    Person person;
    private Toolbar toolbar;
    private TextView floatTitle;
    private ImageView headerBg;
    private ListView listView;
    private float headerHeight;
    private float minHeaderHeight;
    private float floatTitleLeftMargin;
    private float floatTitleSize;
    private float floatTitleSizeLarge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);

        Bundle b = getIntent().getExtras();
        person = b.getParcelable("dev.lazyllamas.rizzyclient");

        initMeasure();
        initView();
        initListViewHeader();
        initListView();
        initEvent();


    }

    private void initMeasure() {
        headerHeight = getResources().getDimension(R.dimen.header_height);
        minHeaderHeight = getResources().getDimension(R.dimen.abc_action_bar_default_height_material);
        floatTitleLeftMargin = getResources().getDimension(R.dimen.float_title_left_margin);
        floatTitleSize = getResources().getDimension(R.dimen.float_title_size);
        floatTitleSizeLarge = getResources().getDimension(R.dimen.float_title_size_large);
    }

    private void initView() {
        listView = findViewById(R.id.lv_main);
        floatTitle = findViewById(R.id.tv_main_title);
        floatTitle.setText(person.getName());
        toolbar = findViewById(R.id.tb_main);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initListView() {
        List<String> data = new ArrayList<>();

        data.add("Description:");
        data.add(person.getDescription());

        data.add("Favourite sports: ");
        if (person.getLikedActivities() != null)
            data.add(person.getLikedActivities().toString());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.ListTextView, android.R.id.text1, data);
        listView.setAdapter(adapter);

        listView.setDividerHeight(0);

    }

    private void initListViewHeader() {
        View headerContainer = LayoutInflater.from(this).inflate(R.layout.header, listView, false);
        headerBg = headerContainer.findViewById(R.id.img_header_bg);

        Bitmap tmp = loadImg();

        if (tmp != null)
            headerBg.setImageBitmap(tmp);

        listView.addHeaderView(headerContainer);
    }

    private Bitmap loadImg() {
        File cacheDir = getBaseContext().getCacheDir();
        File f = new File(cacheDir, person.getId());
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(f);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
        Bitmap bitmap = BitmapFactory.decodeStream(fis);
        return bitmap;
    }

    private void initEvent() {
        listView.setOnScrollListener(this);
    }


    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        float scrollY = getScrollY(view);


        float headerBarOffsetY = headerHeight - minHeaderHeight;
        float offset = 1 - Math.max((headerBarOffsetY - scrollY) / headerBarOffsetY, 0f);


        toolbar.setBackgroundColor(Color.argb((int) (offset * 255), 0, 0, 0));

        headerBg.setTranslationY(scrollY / 2);


        floatTitle.setPivotX(floatTitle.getLeft() + floatTitle.getPaddingLeft());

        float titleScale = floatTitleSize / floatTitleSizeLarge;

        floatTitle.setTranslationX(floatTitleLeftMargin * offset);

        floatTitle.setTranslationY(
                (-(floatTitle.getHeight() - minHeaderHeight) +
                        floatTitle.getHeight() * (1 - titleScale))
                        / 2 * offset +
                        (headerHeight - floatTitle.getHeight()) * (1 - offset));

        floatTitle.setScaleX(1 - offset * (1 - titleScale));

        floatTitle.setScaleY(1 - offset * (1 - titleScale));


        if (scrollY > headerBarOffsetY) {
            toolbar.setTitle(person.getName() + ", " + person.getAge());
            floatTitle.setVisibility(View.GONE);
        } else {
            toolbar.setTitle("");
            floatTitle.setVisibility(View.VISIBLE);
        }
    }


    public float getScrollY(AbsListView view) {
        View c = view.getChildAt(0);

        if (c == null)
            return 0;

        int firstVisiblePosition = view.getFirstVisiblePosition();
        int top = c.getTop();

        float headerHeight = 0;
        if (firstVisiblePosition >= 1)
            headerHeight = this.headerHeight;

        return -top + firstVisiblePosition * c.getHeight() + headerHeight;
    }

}
