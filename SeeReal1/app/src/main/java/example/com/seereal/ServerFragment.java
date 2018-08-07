package example.com.seereal;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;


public class ServerFragment extends Fragment {



    //static final String[] category = {"전자","기계","자동차","조선","석유화학","섬유","농림업","건설업","컴퓨터","길찾기","기타"};
    DatabaseReference mDatabase = InitApp.sDatabase.getReference();
    DatabaseReference mCate;
  //  final String[] category=new String[11];// = {"","","","","","","","","","",""};
    View view;
    ArrayAdapter adapter;
    String [] temp = new String[100];


    EditText search;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

         view = inflater.inflate(R.layout.fragment_server, null);

        mCate = mDatabase.child("category");
        mCate.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Iterator<DataSnapshot> child = dataSnapshot.getChildren().iterator();
                int i = 0;
                while(child.hasNext()){
                    //Log.i("category", " dd: "+child.next().getKey() );
                    temp[i] = child.next().getKey();
                    i++;
                }
/*
                for(int i = 0; child.hasNext() && i<12; i++){
                    temp[i] = child.next().getKey();
                }*/

                String [] category = new String [i];
                for(int j = 0; j<i; j++){
                    category[j] = temp[j];
                }

                 adapter = new ArrayAdapter(getActivity(),android.R.layout.simple_list_item_1,category);

                setting();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return view;

    }

    public void setting (){


        final ListView listView = (ListView) view.findViewById(R.id.listView1);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selected_item = (String)parent.getItemAtPosition(position);

                if(selected_item.equals("자동차")) {
                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(search.getWindowToken(), 0);
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.fragment_container,new VehicleField());
                    ft.addToBackStack(null);
                    ft.commit();
                }
            }
        });

        search = view.findViewById(R.id.editSearch);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String filterText = s.toString();

                ((ArrayAdapter)listView.getAdapter()).getFilter().filter(filterText);

            }
        });

    }




}
