package com.cubegames.slava.cubegame;

import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.cubegames.slava.cubegame.api.RestApiService;
import com.cubegames.slava.cubegame.model.ErrorEntity;
import com.cubegames.slava.cubegame.model.GameMap;

import static com.cubegames.slava.cubegame.api.RestApiService.ACTION_MAP_IMAGE_RESPONSE;
import static com.cubegames.slava.cubegame.api.RestApiService.ACTION_UPLOAD_IMAGE_RESPONSE;
import static com.cubegames.slava.cubegame.api.RestApiService.EXTRA_GAME_MAP_OBJECT;

public class MapFragment extends Fragment {

    private ImageView mMapImage;
    private BaseItemDetailsActivity.WebErrorHandler webErrorHandler;

    public MapFragment() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.map_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mMapImage = (ImageView)view.findViewById(R.id.map_image);
    }

    public void setIntentFilters(IntentFilter intentFilter) {
        intentFilter.addAction(ACTION_MAP_IMAGE_RESPONSE);
        intentFilter.addAction(ACTION_UPLOAD_IMAGE_RESPONSE);
    }

    public void setWebErrorHandler(BaseItemDetailsActivity.WebErrorHandler webErrorHandler) {
        this.webErrorHandler = webErrorHandler;
    }

    public boolean handleWebServiceResponseAction(Intent intent) {
        if (intent.getAction().equals(ACTION_UPLOAD_IMAGE_RESPONSE)){
            ErrorEntity error = intent.getParcelableExtra(RestApiService.EXTRA_ERROR_OBJECT);
            if (error != null && webErrorHandler != null) {
                webErrorHandler.onError(error);
            }

            return true;
        }
        else if (intent.getAction().equals(ACTION_MAP_IMAGE_RESPONSE)){
            ErrorEntity error = intent.getParcelableExtra(RestApiService.EXTRA_ERROR_OBJECT);
            if (error == null) {
                GameMap response = intent.getParcelableExtra(EXTRA_GAME_MAP_OBJECT);
                if (response.getBinaryData() != null) {
                    Bitmap mapImage = BitmapFactory.decodeByteArray(response.getBinaryData(), 0, response.getBinaryData().length);
                    mMapImage.setImageBitmap(mapImage);
                    mMapImage.invalidate();
                }
            }
            else {
                if (webErrorHandler != null)
                    webErrorHandler.onError(error);
            }

            return true;
        }
        else
            return false;
    }

    public void loadMapImage(GameMap map) {
        RestApiService.startActionGetMapImage(getContext(), map);
    }

    public void saveMapImage(Intent data, GameMap map){
        Uri selectedImage = data.getData();

        mMapImage.setImageURI(selectedImage);

        String[] filePathColumn = { MediaStore.Images.Media.DATA };
        Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                filePathColumn, null, null, null);
        if (cursor != null){
            if(cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                RestApiService.startActionUploadMapImage(getContext(), map, picturePath);
            }

            cursor.close();
        }
    }

    public ImageView getmMapImage() {
        return mMapImage;
    }
}
