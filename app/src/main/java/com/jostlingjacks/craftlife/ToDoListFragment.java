package com.jostlingjacks.craftlife;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;


import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.io.*;
import java.util.Scanner;

import static android.content.Context.MODE_PRIVATE;
import static com.mapbox.mapboxsdk.Mapbox.getApplicationContext;

public class ToDoListFragment extends Fragment {
    private View toDoListView;
    Context context;

    FloatingActionButton addNewToDoButton;

    //ListView listView;
    SwipeMenuListView listView;

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

        listView = (SwipeMenuListView) toDoListView.findViewById(R.id.listViewToDoList);
        arrayList = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<String>(this.context, android.R.layout.simple_list_item_1, arrayList);

        listView.setAdapter(arrayAdapter);

        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                // create "open" item
                SwipeMenuItem openItem = new SwipeMenuItem(context);
                // set item background  #f57f17
                openItem.setBackground(new ColorDrawable(Color.rgb(0xf5, 0x7f, 0x17)));
                // set item width
                openItem.setWidth(250);
                // set item title
                openItem.setTitle("Schedule");
                // set item title fontsize
                openItem.setTitleSize(18);
                // set item title font color
                openItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(openItem);
                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(context);
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9, 0x3F, 0x25)));
                // set item width
                deleteItem.setWidth(170);
                // set a icon
                deleteItem.setIcon(R.drawable.ic_action_delete);
                // add to menu
                menu.addMenuItem(deleteItem);

            }

        };

        listView.setMenuCreator(creator);

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


        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        Log.d("this", "onMenuItemClick: clicked item " + index + "position: " + position);
                        break;
                    case 1:
                        Log.d("this", "onMenuItemClick: clicked item " + index+ "position: " + position);
                        deleteItemInListView(position);
                        break;
                }
                // false : close the menu; true : not close the menu
                return false;
            }
        });

        /**
         * get shared preference...
         */

        SharedPreferences logInPreferences = getActivity().getSharedPreferences("CURRENT_USER_INFO", MODE_PRIVATE);
        fileName = logInPreferences.getString("CURRENT_USER_EMAIL", "") + "ToDo.txt";

        // load everything from todo list.
        this.loadToDoListFromFile();

        // if the todo list is empty add something into it...
        if (arrayList.isEmpty()){
            addDefaultToDoListItems(arrayList);
            arrayAdapter.notifyDataSetChanged();
        }else{

        }

        setHasOptionsMenu(true);
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


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        getActivity().getMenuInflater().inflate(R.menu.new_to_do_item, menu);

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_to_do_item:
                Intent intent = new Intent();
                intent.setClass(getActivity(), EditToDoFieldActivity.class);
                //intent.setClass(getActivity(), EditToDoFieldActivity.class);
                startActivityForResult(intent,  Tool.INTENT_REQUEST_CODE);
                return true;
        }
        return super.onOptionsItemSelected(item);

    }

    private void deleteItemInListView(int position){
        arrayList.remove(position);
        arrayAdapter.notifyDataSetChanged();
        saveToDoListToFile();
    }

    /**
     * this method MUST be called when the list is empty, or it will crash or delete user's own items...
     * @param arrayList the user to-do list arraylist...
     */
    private void addDefaultToDoListItems(ArrayList<String> arrayList){
        arrayList.add(0, "Sample item: Buy some milk");
        arrayList.add(1, "Sample item: Wash my clothes after work");
        arrayList.add(2, "Sample item: Buy sushi");
        arrayList.add(3, "Sample item: Get my parcel back from post office");
        arrayList.add(4, "");
        arrayList.add(5, "Just don't keep your list empty :)");
    }




}
