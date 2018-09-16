package com.jostlingjacks.craftlife;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;


import java.io.PrintWriter;
import java.util.ArrayList;
import java.io.*;
import java.util.Scanner;

import static android.content.Context.MODE_PRIVATE;

public class ToDoListFragment extends Fragment {
    private View toDoListView;
    Context context;

    FloatingActionButton addNewToDoButton;

    ListView listView;
    ArrayList<String> arrayList;

    int position;

    // You can use this adapter to provide views for an AdapterView,
    // Returns a view for each object in a collection of data objects you provide,
    // and can be used with list-based user interface widgets such as ListView or Spinner.
    ArrayAdapter<String> arrayAdapter;
    String toDoText;
    String fileName;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        toDoListView = inflater.inflate(R.layout.fragment_to_do_list, container,false);
        context = toDoListView.getContext();

        listView = (ListView) toDoListView.findViewById(R.id.listViewToDoList);
        arrayList = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<String>(this.context, android.R.layout.simple_list_item_1, arrayList);

        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), EditToDoItemActivity.class);
                intent.putExtra(Tool.INTENT_TO_DO_MESSAGE_DATA, arrayList.get(position).toString());
                //String string = arrayList.get(position).toString();
                intent.putExtra(Tool.INTENT_TO_DO_ITEM_POSITION, position);
                startActivityForResult(intent, Tool.INTENT_REQUEST_CODE_TWO);
            }
        });


        addNewToDoButton = (FloatingActionButton) toDoListView.findViewById(R.id.addNewInTheToDoListFloatingButton);
        addNewToDoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), EditToDoFieldActivity.class);
                startActivityForResult(intent,  Tool.INTENT_REQUEST_CODE);
            }
        });

        /**
         * get shared preference...
         */

        SharedPreferences logInPreferences = getActivity().getSharedPreferences("CURRENT_USER_INFO", MODE_PRIVATE);
        fileName = logInPreferences.getString("CURRENT_USER_EMAIL", "") + "ToDo.txt";


        this.loadToDoListFromFile();



        return toDoListView;

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Tool.INTENT_REQUEST_CODE){
            toDoText = data.getStringExtra(Tool.INTENT_TO_DO_MESSAGE_FIELD);
            arrayList.add(toDoText);
            arrayAdapter.notifyDataSetChanged();
            saveToDoListToFile();
        } else if (resultCode == Tool.INTENT_REQUEST_CODE_TWO){
            toDoText = data.getStringExtra(Tool.INTENT_TO_DO_CHANGED_MESSAGE);
            position = data.getIntExtra(Tool.INTENT_TO_DO_ITEM_POSITION, -1);
            arrayList.remove(position);
            arrayList.add(position, toDoText);
            arrayAdapter.notifyDataSetChanged();
            saveToDoListToFile();
        }
    }

    public void saveToDoListToFile(){
        try {
            PrintWriter printWriter = new PrintWriter(getContext().openFileOutput(fileName, MODE_PRIVATE));
            for (String data: arrayList){
                printWriter.println(data);
            }
            printWriter.close();
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }
    }

    public void loadToDoListFromFile(){
        try {
            Scanner sc = new Scanner(getContext().openFileInput(fileName));
            while (sc.hasNextLine()){
                String data = sc.nextLine();
                arrayAdapter.add(data);
            }
            sc.close();
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }
    }


}
