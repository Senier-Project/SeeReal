package example.com.seereal;


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
import android.widget.EditText;
import android.widget.GridView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MyList extends Fragment {

    GridView gridView;
    GridViewAdapter gridViewAdapter;
    String tempPath="";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.grid_view, null);
        gridView = view.findViewById(R.id.gridView);

        gridViewAdapter  = new GridViewAdapter(getActivity(),getData(),MainActivity.width,MainActivity.height);
        gridView.setAdapter(gridViewAdapter);


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
        ArrayList<CardViewItem> list = new ArrayList<>();
        CardViewItem item = new CardViewItem();

//TODO : firebase Storage
        /*
        CardViewItem item = new CardViewItem();
        item.setTitle("사이드 미러 교체");
        item.setTag("#사이드_미러#직접#교체#예랑이#해줘따");
        item.setImage(R.drawable.side_mirror);
        item.setIsLike(R.drawable.baseline_favorite_border_black_18dp);
        list.add(item);
*/

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

        return list;
    }

}
