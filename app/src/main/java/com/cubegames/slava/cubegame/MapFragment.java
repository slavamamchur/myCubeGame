package com.cubegames.slava.cubegame;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;

import com.cubegames.slava.cubegame.BaseItemDetailsActivity.WebErrorHandler;
import com.cubegames.slava.cubegame.api.RestApiService;
import com.cubegames.slava.cubegame.model.ErrorEntity;
import com.cubegames.slava.cubegame.model.Game;
import com.cubegames.slava.cubegame.model.GameInstance;
import com.cubegames.slava.cubegame.model.GameMap;
import com.cubegames.slava.cubegame.model.players.InstancePlayer;
import com.cubegames.slava.cubegame.model.points.AbstractGamePoint;

import java.util.ArrayList;
import java.util.List;

import static com.cubegames.slava.cubegame.Utils.loadBitmapFromFile;
import static com.cubegames.slava.cubegame.api.RestApiService.ACTION_MAP_IMAGE_RESPONSE;
import static com.cubegames.slava.cubegame.api.RestApiService.ACTION_UPLOAD_IMAGE_RESPONSE;
import static com.cubegames.slava.cubegame.api.RestApiService.EXTRA_ERROR_OBJECT;

public class MapFragment extends Fragment implements MapView.DrawMapViewDelegate {

    public static final float START_ROTATION_ANGLE = 35;
    public static final float INTERPOLATED_ROTATION_RANGE = 20;

    private MapView mMapImage;
    private ScrollView mScrollContainerY;
    private HorizontalScrollView mScrollContainerX;
    private FrameLayout mMapContainer;
    private WebErrorHandler webErrorHandler;
    private GameMap mapEntity = null;
    private Game gameEntity = null;
    private GameInstance gameInstanceEntity = null;
    private Rect mapViewPort = new Rect(0,0,0,0);
    private Bitmap cachedBitmap;
    private ViewTreeObserver.OnScrollChangedListener onScrollChangedListener;
    private boolean canRotateMap = false;
    private boolean isFirstScroll = true;
    private List<InstancePlayer> savedPlayers = null;
    private int movedPlayerIndex = -1;

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

        mScrollContainerY = (ScrollView) view.findViewById(R.id.map_scroll_container_y);
        mScrollContainerY.setSmoothScrollingEnabled(true);
        mScrollContainerX = (HorizontalScrollView)view.findViewById(R.id.map_scroll_container_x);
        mScrollContainerX.setSmoothScrollingEnabled(true);

        onScrollChangedListener = new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                int scrollY = mScrollContainerY.getScrollY();
                int scrollX = mScrollContainerX.getScrollX();
                mapViewPort.offset(scrollX - mapViewPort.left, scrollY - mapViewPort.top);
            }
        };
        mScrollContainerY.getViewTreeObserver().addOnScrollChangedListener(onScrollChangedListener);

        mMapContainer = (FrameLayout) view.findViewById(R.id.map_container);

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
            if (error != null) {
                mMapImage.invalidate();

                if (webErrorHandler != null)
                    webErrorHandler.onError(error);
            }
            else {
                clearImage();
                RestApiService.startActionGetMapImage(getContext(), mapEntity);
            }

            return true;
        }
        else if (intent.getAction().equals(ACTION_MAP_IMAGE_RESPONSE)){
            ErrorEntity error = intent.getParcelableExtra(EXTRA_ERROR_OBJECT);

            if (error == null) {
                clearImage();
                cachedBitmap = loadBitmapFromFile(mapEntity.getId());

                createNewMapView();
                rotateMap();
                if (gameInstanceEntity != null)
                    scrollMap();
            }
            else
                if (webErrorHandler != null)
                    webErrorHandler.onError(error);

            return true;
        }
        else
            return false;
    }

    private void createNewMapView() {
        mMapImage = new MapView(getContext(), this);
        mMapImage.setLayerType(View.LAYER_TYPE_HARDWARE, null);

        mMapContainer.removeAllViews();
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(cachedBitmap.getWidth() * 2, cachedBitmap.getHeight() * 2);
        mMapContainer.addView(mMapImage, lp);
        mMapImage.invalidate();
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
        canRotateMap = true;

        setGameInstanceEntity(gameInst);
        setGameEntity(gameInst == null ? null : gameInst.getGame());
        savedPlayers = new ArrayList<>(gameInst.getPlayers());

        setWebErrorHandler(errorHandler);

        if (gameEntity != null && gameEntity.getMapId() != null) {
            GameMap map = new GameMap();
            map.setId(gameEntity.getMapId());
            setMapEntity(map);
            loadMapImage(map);
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        DrawMap(cachedBitmap, canvas);
    }

    private void DrawMap(Bitmap mapImage, Canvas canvas) {
        synchronized (lockFrame) {
            if (mapImage == null || canvas == null)
                return;

            final Paint paint = new Paint();
            paint.setPathEffect(new DashPathEffect(new float[]{10, 5}, 0));
            paint.setFilterBitmap(true);

            canvas.drawBitmap(mapImage, null, new Rect(0, 0, mapImage.getWidth() * 2, mapImage.getHeight() * 2), paint);
            drawPath(paint, canvas, nextPoint);
            lockFrame.notifyAll();
        }
    }

    private void drawPath(Paint paint, Canvas canvas, Point movedPt) {
        if (gameEntity != null) {
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

        }

        if (gameInstanceEntity != null && gameEntity.getGamePoints() != null) {
            int[] playersOnWayPoints = new int[gameEntity.getGamePoints().size()];

            for (int i = 0; i < gameInstanceEntity.getPlayers().size(); i++) {
                InstancePlayer player = gameInstanceEntity.getPlayers().get(i);
                int currentPointIdx = player.getCurrentPoint();
                playersOnWayPoints[currentPointIdx]++;
                int playersCnt = playersOnWayPoints[currentPointIdx] - 1;
                AbstractGamePoint point = gameEntity.getGamePoints().get(currentPointIdx);
                boolean isMovingPlayer = movedPlayerIndex == i;

                int x = movedPt != null && isMovingPlayer ? movedPt.x : point.getxPos() + ( 7 * playersCnt * (((playersCnt & 1) == 0) ? 1 : -1));
                int y = movedPt != null && isMovingPlayer ? movedPt.y : point.getyPos();

                paint.setColor(0xFF000000 | player.getColor());
                canvas.drawCircle(x, y, 15f, paint);
            }
        }

    }

    private void runJustBeforeBeingDrawn(final View view) {
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                mapViewPort = getActualViewPort(view);
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

    private Rect getActualViewPort(View view) {
        return new Rect(0, 0, view.getWidth(), view.getHeight());
    }

    private void loadMapImage(GameMap map) {
        runJustBeforeBeingDrawn(mScrollContainerY);
        RestApiService.startActionGetMapImage(getContext(), map);
    }

    public void saveMapImage(Intent data, GameMap map){
        Uri selectedImage = data.getData();
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
        if (cachedBitmap != null) {
            cachedBitmap.recycle();
            cachedBitmap = null;
        }
    }

    public Bitmap getBitmap() {
        return cachedBitmap;
    }


    private Point nextPoint = null;
    public final Object lockFrame = new Object();
    public boolean isChipAnimates = false;

    public void updateMap() {
        if (mapEntity == null || mapEntity.getId() == null)
            return;

        if (savedPlayers != null)
            for (int i = 0; i < savedPlayers.size(); i++)
                if (savedPlayers.get(i).getCurrentPoint() != gameInstanceEntity.getPlayers().get(i).getCurrentPoint()) {
                    movedPlayerIndex = i;
                    break;
                }

        if (    false
             || movedPlayerIndex < 0
             || !GameInstance.State.MOVING.equals(gameInstanceEntity.getState())
            )
            mMapImage.invalidate();
        else
            new Thread(new Runnable() {
                @Override
                public void run() {
                    playChipAnimation();
                }
            }).start();
    }

    private void playChipAnimation() {
            isChipAnimates = true;

            AbstractGamePoint startGamePoint = gameEntity.getGamePoints().get(savedPlayers.get(movedPlayerIndex).getCurrentPoint());
            Point start = new Point(startGamePoint.getxPos(), startGamePoint.getyPos());
            AbstractGamePoint endGamePoint = gameEntity.getGamePoints().get(gameInstanceEntity.getPlayers().get(movedPlayerIndex).getCurrentPoint());
            Point end = new Point(endGamePoint.getxPos(), endGamePoint.getyPos());

            //TODO: interpolate step by fixed time ???
            for (int x = 0; x <= Math.abs(end.x - start.x); x+=3) {
                nextPoint = getNextPointOnLineByXvalue(start, end, end.x > start.x ? start.x + x : start.x - x);//TODO: check formula !!!
                waitWhileDrawingMap();
            }

            nextPoint = null;
            movedPlayerIndex = -1;
            savedPlayers.clear();
            savedPlayers = new ArrayList<>(gameInstanceEntity.getPlayers());

            waitWhileDrawingMap();

            isChipAnimates = false;

    }

    private void waitWhileDrawingMap() {
        synchronized (lockFrame) {
            mMapImage.postInvalidate();
            try {lockFrame.wait();} catch (InterruptedException e) {}
        }
    }

    private Point getCurrentPointOffsetInViewPort() {
        AbstractGamePoint point = gameEntity.getGamePoints().get(gameInstanceEntity.getPlayers()
                .get(gameInstanceEntity.getCurrentPlayer()).getCurrentPoint());

        int xOffset;
        int yOffset;

        if (!mapViewPort.contains(point.getxPos(), mapViewPort.top))
            xOffset = Math.abs(point.getxPos() - mapViewPort.left) < Math.abs(point.getxPos() - mapViewPort.right) ?
                    point.getxPos() - mapViewPort.left : point.getxPos() - mapViewPort.right;
        else
            xOffset = 0;

        if (!mapViewPort.contains(mapViewPort.left, point.getyPos()))
            yOffset = Math.abs(point.getyPos() - mapViewPort.top) < Math.abs(point.getyPos() - mapViewPort.bottom) ?
                    point.getyPos() - mapViewPort.top : point.getyPos() - mapViewPort.bottom;
        else
            yOffset = 0;

        return new Point(Math.round(xOffset * 1.2f), Math.round(yOffset * 1.2f));
    }

    public void scrollMap() {
        Point offset = getCurrentPointOffsetInViewPort();
        mScrollContainerX.smoothScrollBy(offset.x, 0);

        ObjectAnimator.ofFloat(mMapImage, "pivotX", cachedBitmap.getWidth()).setDuration(1).start();
        ObjectAnimator.ofFloat(mMapImage, "pivotY", cachedBitmap.getHeight() * 2).setDuration(1).start();
        ObjectAnimator.ofFloat(mMapImage, "rotationX", START_ROTATION_ANGLE /*+ getRotationAngle()*/).setDuration(isFirstScroll?1:500).start();

        isFirstScroll = false;
    }


    private void rotateMap() {
        isFirstScroll = true;

        if (!canRotateMap)
            return;

        mScrollContainerY.scrollBy(0, Math.abs(cachedBitmap.getHeight()*2 - mapViewPort.height()));

        ObjectAnimator.ofFloat(mapViewPort.height() < cachedBitmap.getHeight()*2 ? mMapImage : mScrollContainerX,
                    "translationY", getCameraDistance()).setDuration(1).start();
    }

    private float getCameraDistance() {
        if (mapViewPort == null || cachedBitmap == null)
            return 0;
        else
            return (mapViewPort.height() - cachedBitmap.getHeight()*2) / 1f;
    }

    private float getRotationAngle() {
        int y = gameEntity.getGamePoints().get(gameInstanceEntity.getPlayers()
                .get(gameInstanceEntity.getCurrentPlayer()).getCurrentPoint()).getyPos();

        return  INTERPOLATED_ROTATION_RANGE * y / mapViewPort.height();
    }

    private Point getNextPointOnLineByXvalue(Point start, Point end, int x) {
        if (start.x == end.x)
            return new Point(start.x, start.y);

        float k = (start.y - end.y) / (start.x - end.x);
        float b = end.y - k * end.x;

        return new Point(x, Math.round(k * x + b));
    }

    private Point getNextPointOnLineByYvalue(Point start, Point end, int y) {
        if (start.x == end.x)
            return new Point(start.x, y);

        else {
            float k = (start.y - end.y) / (start.x - end.x);
            float b = end.y - k * end.x;

            return new Point(Math.round((y - b) / k), y);
        }
    }

}
