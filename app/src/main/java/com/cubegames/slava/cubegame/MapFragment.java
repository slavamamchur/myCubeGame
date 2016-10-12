package com.cubegames.slava.cubegame;

import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.cubegames.slava.cubegame.BaseItemDetailsActivity.WebErrorHandler;
import com.cubegames.slava.cubegame.api.RestApiService;
import com.cubegames.slava.cubegame.model.ErrorEntity;
import com.cubegames.slava.cubegame.model.Game;
import com.cubegames.slava.cubegame.model.GameInstance;
import com.cubegames.slava.cubegame.model.GameMap;
import com.cubegames.slava.cubegame.model.players.InstancePlayer;
import com.cubegames.slava.cubegame.model.points.AbstractGamePoint;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.cubegames.slava.cubegame.api.RestApiService.ACTION_MAP_IMAGE_RESPONSE;
import static com.cubegames.slava.cubegame.api.RestApiService.ACTION_UPLOAD_IMAGE_RESPONSE;
import static com.cubegames.slava.cubegame.api.RestApiService.EXTRA_ERROR_OBJECT;
import static com.cubegames.slava.cubegame.api.RestApiService.EXTRA_GAME_MAP_OBJECT;

public class MapFragment extends Fragment {

    private ImageView mMapImage;
    private WebErrorHandler webErrorHandler;
    private GameMap mapEntity = null;
    private Game gameEntity = null;
    private GameInstance gameInstanceEntity = null;

    public MapFragment() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
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

    public void setWebErrorHandler(WebErrorHandler webErrorHandler) {
        this.webErrorHandler = webErrorHandler;
    }

    public boolean handleWebServiceResponseAction(Intent intent) {
        if (intent.getAction().equals(ACTION_UPLOAD_IMAGE_RESPONSE)){
            ErrorEntity error = intent.getParcelableExtra(EXTRA_ERROR_OBJECT);
            if (error != null && webErrorHandler != null) {
                webErrorHandler.onError(error);
            }

            return true;
        }
        else if (intent.getAction().equals(ACTION_MAP_IMAGE_RESPONSE)){
            ErrorEntity error = intent.getParcelableExtra(EXTRA_ERROR_OBJECT);
            if (error == null) {
                GameMap response = intent.getParcelableExtra(EXTRA_GAME_MAP_OBJECT);
                byte[] bitmapArray = response.getBinaryData();
                if (bitmapArray != null) {
                    final BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                    bitmapOptions.inMutable = true;
                    Bitmap mapImage = BitmapFactory.decodeByteArray(bitmapArray,
                            0, bitmapArray.length, bitmapOptions);

                    try {
                        saveBitmap2File(mapImage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    DrawMap(mapImage);
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

    private void saveBitmap2File(Bitmap bmp) throws IOException {
        String file_path = Environment.getExternalStorageDirectory().getAbsolutePath() +
                "/CubegameBitmapCache";
        File dir = new File(file_path);
        if(!dir.exists())
            dir.mkdirs();
        File file = new File(dir, mapEntity.getId() + ".png");
        FileOutputStream fOut = new FileOutputStream(file);

        bmp.compress(Bitmap.CompressFormat.PNG, 100, fOut);
        fOut.flush();
        fOut.close();
    }

    public void InitMap(GameMap map, WebErrorHandler errorHandler) {
        setMapEntity(map);
        setWebErrorHandler(errorHandler);

        loadMapImage(map);
    }

    public void InitMap(Game game, WebErrorHandler errorHandler) {
        setGameEntity(game);
        setWebErrorHandler(errorHandler);

        if (game != null && game.getMapId() != null) {
            GameMap map = new GameMap();
            map.setId(game.getMapId());
            setMapEntity(map);
            loadMapImage(map);
        }
    }

    public void InitMap(GameInstance gameInst, WebErrorHandler errorHandler) {
        setGameInstanceEntity(gameInst);
        setGameEntity(gameInst == null ? null : gameInst.getGame());
        setWebErrorHandler(errorHandler);

        if (gameEntity != null && gameEntity.getMapId() != null) {
            GameMap map = new GameMap();
            map.setId(gameEntity.getMapId());
            setMapEntity(map);
            loadMapImage(map);
        }
    }

    private void DrawMap(Bitmap mapImage) {
        Paint paint = new Paint();
        paint.setPathEffect(new DashPathEffect(new float[] {10, 5}, 0));

        Canvas canvas = new Canvas(mapImage);

        if (gameEntity != null) {
            //canvas.drawBitmap(mapImage, 0, 0, paint);

            Path path = new Path();
            if (gameEntity.getGamePoints() != null && gameEntity.getGamePoints().size() > 0) {
                AbstractGamePoint point = gameEntity.getGamePoints().get(0);
                path.moveTo(point.getxPos(), point.getyPos());

                for (int i = 1; i < gameEntity.getGamePoints().size(); i++) {
                    AbstractGamePoint endPoint = gameEntity.getGamePoints().get(i);
                    path.lineTo(endPoint.getxPos(), endPoint.getyPos());
                }
                paint.setColor(Color.GREEN);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(5);
                canvas.drawPath(path, paint);

                paint.setColor(Color.RED);
                paint.setStyle(Paint.Style.FILL);
                for (int i = 0; i < gameEntity.getGamePoints().size(); i++) {
                    AbstractGamePoint endPoint = gameEntity.getGamePoints().get(i);
                    canvas.drawCircle(endPoint.getxPos(), endPoint.getyPos(), 10f, paint);
                }
            }

//            Bitmap source = mapImage;
//            Matrix matrix = new Matrix();
//            matrix.postRotate(45f, source.getWidth()/2, source.getHeight()/2);
//            canvas.drawBitmap(Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true), 0, 0, paint);

//            RotateAnimation rotateAnimation = new RotateAnimation(from, to,
//                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
//                    0.5f);
//            rotateAnimation.setInterpolator(new LinearInterpolator());
//            rotateAnimation.setDuration(ANIMATION_DURATION);
//            rotateAnimation.setFillAfter(true);
//
//            imageView.startAnimation(rotateAnimation);

            //paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
            //canvas.drawText(String.format("Game points count: %d"), 88, 10, 10, paint);
        }

        if (gameInstanceEntity != null && gameEntity.getGamePoints() != null) {
            int[] playersOnWayPoints = new int[gameEntity.getGamePoints().size()];
            for (InstancePlayer player : gameInstanceEntity.getPlayers()) {
                int currentPointIdx = player.getCurrentPoint();
                playersOnWayPoints[currentPointIdx]++;
                int playersCnt = playersOnWayPoints[currentPointIdx] - 1;
                AbstractGamePoint point = gameEntity.getGamePoints().get(currentPointIdx);
                int x = point.getxPos() + ( 7 * playersCnt * (((playersCnt & 1) == 0) ? 1 : -1));
                int y = point.getyPos();

                paint.setColor(0xFF000000 | player.getColor());
                canvas.drawCircle(x, y, 15f, paint);
            }

        }

        runJustBeforeBeingDrawn(mMapImage);
        mMapImage.setImageBitmap(mapImage);
    }

    private void runJustBeforeBeingDrawn(final View view) {
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Point pt = getActualMapSize(mMapImage);
            }
        };

        final ViewTreeObserver.OnPreDrawListener preDrawListener =
            new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    view.getViewTreeObserver().removeOnPreDrawListener(this);
                    runnable.run();
                    return true;
                }
            };

        view.getViewTreeObserver().addOnPreDrawListener(preDrawListener);
    }

    private Point getActualMapSize(ImageView imageView) {
        int ih=imageView.getHeight();//height of imageView
        int iw=imageView.getWidth();//width of imageView
        int iH=imageView.getDrawable().getIntrinsicHeight();//original height of underlying image
        int iW=imageView.getDrawable().getIntrinsicWidth();//original width of underlying image

        if (ih/iH <= iw/iW)
            iw = iW*ih/iH;//rescaled width of image within ImageView
        else
            ih = iH*iw/iW;//rescaled height of image within ImageView

        return new Point(iw, ih);
    }

    private void loadMapImage(GameMap map) {
        RestApiService.startActionGetMapImage(getContext(), map);
    }

    public void saveMapImage(Intent data, GameMap map){
        Uri selectedImage = data.getData();

        mMapImage.setImageURI(selectedImage);

        try {
            saveBitmap2File(getBitmap());
        } catch (IOException e) {
            e.printStackTrace();
        }


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

    public void setMapEntity(GameMap mapEntity) {
        this.mapEntity = mapEntity;
    }
    public void setGameEntity(Game gameEntity) {
        this.gameEntity = gameEntity;
    }
    public void setGameInstanceEntity(GameInstance gameInstanceEntity) {
        this.gameInstanceEntity = gameInstanceEntity;
    }

    @Override
    public void onDetach() {
        clearImage();

        super.onDetach();
    }

    private void clearImage() {
        Bitmap bmp = getBitmap();
        mMapImage.setImageBitmap(null);
        if (bmp != null)
            bmp.recycle();
    }

    public Bitmap getBitmap() {
        return ((BitmapDrawable)mMapImage.getDrawable()).getBitmap();
    }

    private Bitmap loadFromFile(String path) {
        Bitmap bitmap = null;
        File f = new File(path, mapEntity.getId() + ".png");
        BitmapFactory.Options options = new BitmapFactory.Options();
        //options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        options.inMutable = true;
        try {
            bitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, options);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return bitmap;

    }

    public void updateMap() {
        if (gameEntity != null && gameEntity.getMapId() != null) {
            clearImage();

            GameMap map = new GameMap();
            map.setId(gameEntity.getMapId());

            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/CubegameBitmapCache";
            DrawMap(loadFromFile(path));
        }
    }

}
