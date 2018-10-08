package example.com.seereal;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class VehicleField extends Fragment {


    GridView gridView;
    GridViewAdapter gridViewAdapter;
    CardViewItem item;
    String tempPath, tempPath2;
    ArrayList<CardViewItem> list;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //FirebaseStorage

        View view = inflater.inflate(R.layout.grid_view, null);
        gridView = view.findViewById(R.id.gridView);

        gridViewAdapter  = new GridViewAdapter(getActivity(),getData(),MainActivity.width,MainActivity.height);
        gridView.setAdapter(gridViewAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getActivity(), ""+position, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getActivity(), ItemViewActivity.class);
                intent.putExtra("position",position);
                startActivity(intent);
            }
        });

        EditText editText = view.findViewById(R.id.vehicleSearch);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String filterText = s.toString();

                ((GridViewAdapter)gridView.getAdapter()).getFilter().filter(filterText);

            }
        });
        return view;
    }

    public ArrayList<CardViewItem> getData()
    {

       list = new ArrayList<>();
         item = new CardViewItem();

        String folderName = "vehicle";
        String imageName = "car1.png";

        // Storage 이미지 다운로드 경로
        String storagePath = folderName + "/" + imageName;
        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
        StorageReference imageRef = mStorageRef.child(storagePath);

        try {
            // Storage 에서 다운받아 저장시킬 임시파일
            Log.d("susu", "1");
            final File imageFile = File.createTempFile("images", "png");
            while(true){
                Log.d("susu", "2");
               if(imageFile.getPath()!=null){

                   tempPath  = imageFile.getPath();
                   Log.d("susu", "3"+tempPath);
                   break;

               }
            }
            imageRef.getFile(imageFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {

                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    // Success Case
                    Log.d("susu", "vehicle SS !! path"+ imageFile.getPath() );
                    tempPath  = imageFile.getPath();

                    //  Bitmap bitmapImage = BitmapFactory.decodeFile(imageFile.getPath());
                    // testStorage.setImageBitmap(bitmapImage);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Fail Case
                    e.printStackTrace();
                    Log.d("susu", "fF" );

                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("susu", "여기먼저 ?" );

        item.setTitle("타이어 지렁이 수리");
        item.setTag("#타이어#나갔음#지렁이#수리#짱");
        item.setImage(tempPath);
        item.setIsLike(R.drawable.baseline_favorite_border_black_18dp);
        list.add(item);

        //item2
        String imageName2 = "car3.png";
        String storagePath2 = folderName + "/" + imageName2;
        StorageReference imageRef2 = mStorageRef.child(storagePath2);
        try {
            // Storage 에서 다운받아 저장시킬 임시파일
            Log.d("susu", "1");
            final File imageFile = File.createTempFile("images", "png");
            while(true){
                Log.d("susu", "2");
                if(imageFile.getPath()!=null){

                    tempPath2  = imageFile.getPath();
                    Log.d("susu", "3"+tempPath);
                    break;

                }
            }
            imageRef2.getFile(imageFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {

                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    // Success Case
                    Log.d("susu", "vehicle SS !! path"+ imageFile.getPath() );
                    tempPath2  = imageFile.getPath();

                    //  Bitmap bitmapImage = BitmapFactory.decodeFile(imageFile.getPath());
                    // testStorage.setImageBitmap(bitmapImage);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Fail Case
                    e.printStackTrace();
                    Log.d("susu", "fF" );

                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        item = new CardViewItem();
        item.setTitle("자동차 본넷");
        item.setTag("#그랜져#자동차#본넷#고장#실화임?");
        item.setImage(tempPath2);
        item.setIsLike(R.drawable.baseline_favorite_border_black_18dp);
        list.add(item);
        return list;
    }
}
