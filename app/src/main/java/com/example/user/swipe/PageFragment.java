package com.example.user.swipe;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.db.chart.Tools;
import com.db.chart.model.LineSet;
import com.db.chart.model.Point;
import com.db.chart.view.AxisController;
import com.db.chart.view.LineChartView;
import com.example.user.swipe.adapter.CustomAdapter;
import com.example.user.swipe.adapter.WordCount;

import java.util.ArrayList;
import java.util.Random;

public class PageFragment extends Fragment {
    static PageFragment newInstance(int page) {
        PageFragment pageFragment = new PageFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(ARGUMENT_PAGE_NUMBER, page);
        pageFragment.setArguments(arguments);
        return pageFragment;
    }

    ListView list_add;
    ArrayList<WordCount> wordCounts = new ArrayList<WordCount>();
    

    static final String ARGUMENT_PAGE_NUMBER = "arg_page_number";
    ArrayList<String> bad_words = new ArrayList<String>();
    ArrayAdapter<String> adapter;
    static String  login;
    Context context;

    int pageNumber;
    int backColor;

    View view = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageNumber = getArguments().getInt(ARGUMENT_PAGE_NUMBER);
        context = this.getContext();
        Random rnd = new Random();
        backColor = Color.argb(40, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));

        wordCounts.add(new WordCount(0,"Блин"));
        wordCounts.add(new WordCount(0,"Бы"));
        wordCounts.add(new WordCount(0,"Вот"));
        wordCounts.add(new WordCount(0,"Его"));
        wordCounts.add(new WordCount(0,"Как"));
        wordCounts.add(new WordCount(0,"Короче"));
        wordCounts.add(new WordCount(0,"Кстати"));

        bad_words.add("Блин");
        bad_words.add("Бы");
        bad_words.add("Вот");
        bad_words.add("Его");
        bad_words.add("Как");
        bad_words.add("Короче");
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                              final Bundle savedInstanceState) {

        if(pageNumber == 0) {
            view = inflater.inflate(R.layout.fragment, null);
            drawPlot(view,context);
            add_list(view);
        }else if(pageNumber == 1){

            view = inflater.inflate(R.layout.fragment_list, null);
            final ListView listView = (ListView) view.findViewById(R.id.listView);
            adapter = new ArrayAdapter<String>(context,android.R.layout.simple_list_item_1,bad_words);
            listView.setAdapter(adapter);
            FloatingActionButton button = (FloatingActionButton)view.findViewById(R.id.fab);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                 Log.d("GG", "first");

               /*  new FireMissilesDialogFragment().onCreateDialog(savedInstanceState).show();
                    bad_words.add("s");
                    listView.setAdapter(adapter);*/


                }
            });
        }
        return view;
    }
    public void drawPlot(View view,Context context){
        LineSet dataset = new LineSet();

        dataset.addPoint(new Point("Пн", 2));
        dataset.addPoint(new Point("Вт", 20));
        dataset.addPoint(new Point("Cр", 40));
        dataset.addPoint(new Point("Чт", 35));
        dataset.addPoint(new Point("Пт", 60));
        dataset.addPoint(new Point("Сб", 50));
        dataset.addPoint(new Point("Вс", 70));

        dataset.setColor(Color.parseColor("#009688"))
                .setFill(Color.parseColor("#FFECB3"))
                .setDotsColor(Color.parseColor("#009688"))
                .setThickness(4);
 //               .setDashed(new float[]{10f,10f})
         //       .beginAt(5);



        LineChartView chartEntry =(LineChartView)view.findViewById(R.id.linechart);
        chartEntry.setBorderSpacing(Tools.fromDpToPx(15))
                .setYAxis(true)
                .setAxisBorderValues(0, 100)
                .setYLabels(AxisController.LabelPosition.OUTSIDE)
                .setLabelsColor(Color.parseColor("#6a84c3"))
                .setXAxis(true)
                .setStep(20);

                //.setGrid(ChartView.GridType.VERTICAL, new Paint().set);

        chartEntry.addData(dataset);
        chartEntry.show();

    }



    public static class FireMissilesDialogFragment extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            if(savedInstanceState != null) {
                // Get the layout inflater


                // Use the Builder class for convenient dialog construction
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                LayoutInflater inflater = getActivity().getLayoutInflater();
                final View view = inflater.inflate(R.layout.dialog_signin, null);

                builder.setMessage(R.string.username)
                        .setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                login = ((EditText) view.findViewById(R.id.username))
                                        .getText().toString();

                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                FireMissilesDialogFragment.this.getDialog().cancel();
                            }
                        });

                // Create the AlertDialog object and return it
                return builder.create();
            }
            return  null;
        }


    }

    public void add_list(View view){
        CustomAdapter adapter = new CustomAdapter(context,wordCounts);
        list_add=(ListView)view.findViewById(R.id.list);
        list_add.setAdapter(adapter);
    }

}