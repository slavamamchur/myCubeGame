package com.cubegames.slava.cubegame;

import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
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
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.opengl.GLSurfaceView;
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

import static com.cubegames.slava.cubegame.api.RestApiService.ACTION_MAP_IMAGE_RESPONSE;
import static com.cubegames.slava.cubegame.api.RestApiService.ACTION_UPLOAD_IMAGE_RESPONSE;
import static com.cubegames.slava.cubegame.api.RestApiService.EXTRA_ERROR_OBJECT;
import static com.cubegames.slava.cubegame.api.RestApiService.startActionMooveGameInstance;

public class MapFragment extends Fragment implements MapView.DrawMapViewDelegate {

    public static final float START_ROTATION_ANGLE = 35;

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
    public List<InstancePlayer> savedPlayers = null;

    public GLSurfaceView glMapSurfaceView = null;
    public MapGLRenderer glRenderer;

    public MapFragment() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ///OGL:
        glMapSurfaceView = new GLSurfaceView(getContext());
        glMapSurfaceView.setEGLContextClientVersion(2);
        glRenderer = new MapGLRenderer(getContext());
        ///glMapSurfaceView.setRenderer(glRenderer);
        return  glMapSurfaceView; //inflater.inflate(R.layout.map_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        glMapSurfaceView.setRenderer(glRenderer);

        /*//OGL:
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

        mMapContainer = (FrameLayout) view.findViewById(R.id.map_container);*/

    }

    @Override
    public void onPause() {
        super.onPause();

        ///OGL:
        glMapSurfaceView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

        ///OGL:
        glMapSurfaceView.onResume();
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

                /*//OGL:
                cachedBitmap = loadBitmapFromDB(getContext(), mapEntity.getId());

                createNewMapView();
                rotateMap();
                if (gameInstanceEntity != null)
                    scrollMap();*/
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

        ///OGL:
        glRenderer.setMapID(map.getId());
        //loadMapImage(map);
    }

    public void InitMap(Game game, WebErrorHandler errorHandler) {
        setGameEntity(game);
        setWebErrorHandler(errorHandler);

        if (game != null && game.getMapId() != null) {
            GameMap map = new GameMap();
            map.setId(game.getMapId());
            setMapEntity(map);

            ///OGL:
            glRenderer.setMapID(map.getId());
            //loadMapImage(map);
        }
    }

    public void InitMap(GameInstance gameInst, WebErrorHandler errorHandler) {
        canRotateMap = true;

        setGameInstanceEntity(gameInst);
        setGameEntity(gameInst == null ? null : gameInst.getGame());
        savedPlayers = new ArrayList<>(gameInst != null ? gameInst.getPlayers() : null);

        setWebErrorHandler(errorHandler);

        if (gameEntity != null && gameEntity.getMapId() != null) {
            GameMap map = new GameMap();
            map.setId(gameEntity.getMapId());
            setMapEntity(map);

            ///OGL:
            glRenderer.setMapID(map.getId());
            //loadMapImage(map);
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
            drawPath(paint, canvas);
            if (gameInstanceEntity != null && gameEntity.getGamePoints() != null) {
                drawChips();
            }

            lockFrame.notifyAll();
        }
    }

    private void drawPath(Paint paint, Canvas canvas) {
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
    }

    private void drawChips() {
        if (mMapContainer.getChildCount() > 1)
            mMapContainer.removeViews(1, gameInstanceEntity.getPlayers().size());

        int[] playersOnWayPoints = new int[gameEntity.getGamePoints().size()];

        for (int i = 0; i < gameInstanceEntity.getPlayers().size(); i++) {
            InstancePlayer player = gameInstanceEntity.getPlayers().get(i);
            int currentPointIdx = player.getCurrentPoint();
            playersOnWayPoints[currentPointIdx]++;
            int playersCnt = playersOnWayPoints[currentPointIdx] - 1;
            AbstractGamePoint point = gameEntity.getGamePoints().get(currentPointIdx);
            AbstractGamePoint base_point = gameEntity.getGamePoints().get(0);

            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(30,30);
            lp.leftMargin = base_point.getxPos() - 15;
            lp.topMargin = base_point.getyPos() - 15;
            View chip = createChip(0xFF000000 | player.getColor());
            mMapContainer.addView(chip, lp);

            Point chipPlace = getChipPlace(point, playersCnt);

            AnimatorSet animatorSet = new AnimatorSet();

            ObjectAnimator moveX2 = ObjectAnimator.ofFloat(chip, "translationX", chipPlace.x - 50);
            ObjectAnimator moveY2 = ObjectAnimator.ofFloat(chip, "translationY", chipPlace.y - 50);
            animatorSet.play(moveX2).with(moveY2);

            animatorSet.setDuration(1);
            animatorSet.start();
        }
    }

    public void movingChipAnimation(AnimatorListenerAdapter delegate) {
        int[] playersOnWayPoints = new int[gameEntity.getGamePoints().size()];
        int movedPlayerIndex = -1;

        AbstractGamePoint endGamePoint = null;
        int playersCnt = 0;

        if (savedPlayers != null)
            for (int i = 0; i < gameInstanceEntity.getPlayers().size(); i++) {
                int currentPointIdx = gameInstanceEntity.getPlayers().get(i).getCurrentPoint();
                playersOnWayPoints[currentPointIdx]++;

                if (savedPlayers.get(i).getCurrentPoint() != currentPointIdx) {
                    movedPlayerIndex = i;
                    endGamePoint = gameEntity.getGamePoints().get(currentPointIdx);
                    playersCnt = playersOnWayPoints[currentPointIdx] - 1;

                    break;
                }
            }

        if (movedPlayerIndex >= 0)
            try {
                animateChip(delegate, endGamePoint, playersCnt, mMapContainer.getChildAt(movedPlayerIndex + 1));
            } catch (Exception e) {
                e.printStackTrace();
            }
        else
            startActionMooveGameInstance(getContext(), gameInstanceEntity);

        if (savedPlayers != null)
            savedPlayers.clear();
        savedPlayers = new ArrayList<>(gameInstanceEntity.getPlayers());
    }

    private void animateChip(AnimatorListenerAdapter delegate, AbstractGamePoint endGamePoint, int playersCnt, View chip) throws Exception {
        playersCnt = playersCnt < 0 ? 0 : playersCnt;

        if (endGamePoint == null)
            throw new Exception("Invalid game point.");

        AnimatorSet animatorSet = new AnimatorSet();

        if (gameInstanceEntity.getStepsToGo() == 0) {
            Point chipPlace = getChipPlace(endGamePoint, playersCnt);
            ObjectAnimator moveX2 = ObjectAnimator.ofFloat(chip, "translationX", chipPlace.x - 50);
            ObjectAnimator moveY2 = ObjectAnimator.ofFloat(chip, "translationY", chipPlace.y - 50);
            animatorSet.play(moveX2).with(moveY2);
        }
        else {
            ObjectAnimator moveX = ObjectAnimator.ofFloat(chip, "translationX", endGamePoint.getxPos() - 50);
            ObjectAnimator moveY = ObjectAnimator.ofFloat(chip, "translationY", endGamePoint.getyPos() - 50);
            animatorSet.play(moveX).with(moveY);
        }

        animatorSet.setDuration(1500);
        animatorSet.addListener(delegate);
        animatorSet.start();
    }

    private Point getChipPlace(AbstractGamePoint endGamePoint, int playersCnt) {
        double angle = getChipRotationAngle(playersCnt);

        int toX2 = (int) (endGamePoint.getxPos() - 35 * Math.sin(angle));
        int toY2 = (int) (endGamePoint.getyPos() - 35 * Math.cos(angle));

        return new Point(toX2, toY2);
    }

    private double getChipRotationAngle(int playersCnt) {
        if (playersCnt == 0)
            return  0;

        int part = 8, b;
        double angle;

        do {
            angle = 360 / part;
            b = part - 1;

            part /= 2;
        } while ( ((playersCnt & part) == 0) && (part != 1) );

        return Math.toRadians((2 * playersCnt - b) * angle);
    }

    private View createChip(int color) {
        ShapeDrawable chip = new ShapeDrawable(new OvalShape());
        chip.getPaint().setColor(color);
        View chipView = new View(getContext());
        chipView.setBackground(chip);

        return chipView;
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
        /*//OGL:
        runJustBeforeBeingDrawn(mScrollContainerY);*/

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

    public final Object lockFrame = new Object();

    public void updateMap() {
        if (mapEntity == null || mapEntity.getId() == null)
            return;

        savedPlayers.clear();
        savedPlayers = new ArrayList<>(gameInstanceEntity.getPlayers());

        mMapImage.postInvalidate();
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

        ObjectAnimator.ofFloat(mMapContainer, "pivotX", cachedBitmap.getWidth()).setDuration(1).start();
        ObjectAnimator.ofFloat(mMapContainer, "pivotY", cachedBitmap.getHeight() * 2).setDuration(1).start();
        ObjectAnimator.ofFloat(mMapContainer, "rotationX", START_ROTATION_ANGLE).setDuration(isFirstScroll?1:500).start();

        isFirstScroll = false;
    }


    private void rotateMap() {
        isFirstScroll = true;

        if (!canRotateMap)
            return;

        mScrollContainerY.scrollBy(0, Math.abs(cachedBitmap.getHeight()*2 - mapViewPort.height()));

        ObjectAnimator.ofFloat(mapViewPort.height() < cachedBitmap.getHeight()*2 ? mMapContainer : mScrollContainerX,
                    "translationY", getCameraDistance()).setDuration(1).start();
    }

    private float getCameraDistance() {
        if (mapViewPort == null || cachedBitmap == null)
            return 0;
        else
            return (mapViewPort.height() - cachedBitmap.getHeight()*2) / 1f;
    }

}
