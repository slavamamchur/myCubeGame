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
import static com.cubegames.slava.cubegame.api.RestApiService.startActionMooveGameInstance;

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
    public List<InstancePlayer> savedPlayers = null;
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
            drawPath(paint, canvas);
            if (gameInstanceEntity != null && gameEntity.getGamePoints() != null) {
                drawChips();
            }

            lockFrame.notifyAll();

            //clearImage();//???
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

            //todo: error if pos !=0
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(30,30);
            Point chipPlace = getChipPlace(point, playersCnt);
            lp.leftMargin = point.getxPos() - 15;
            lp.topMargin = point.getyPos() - 15;
            View chip = createChip(0xFF000000 | player.getColor());
            mMapContainer.addView(chip, lp);

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

//            animatorSet.play(moveX).with(moveY).before(moveY2).before(px).before(py).before(rotate);
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

        return Math.toRadians(playersCnt == 0 ? 0 : (2 * playersCnt - b) * angle);
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
    private boolean isFrameDrawnInTime = true;

    //TODO: use thtead-protected variables
    public void updateMap() {
        if (mapEntity == null || mapEntity.getId() == null)
            return;

        if (savedPlayers != null)
            for (int i = 0; i < savedPlayers.size(); i++)
                if (savedPlayers.get(i).getCurrentPoint() != gameInstanceEntity.getPlayers().get(i).getCurrentPoint()) {
                    movedPlayerIndex = i;
                    break;
                }

        if (    true //false
             || movedPlayerIndex < 0
             || !GameInstance.State.MOVING.equals(gameInstanceEntity.getState())
            ) {

            savedPlayers.clear();
            savedPlayers = new ArrayList<>(gameInstanceEntity.getPlayers());

            mMapImage.postInvalidate();
        }
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
            isFrameDrawnInTime = true;

            AbstractGamePoint startGamePoint = gameEntity.getGamePoints().get(savedPlayers.get(movedPlayerIndex).getCurrentPoint());
            Point start = new Point(startGamePoint.getxPos(), startGamePoint.getyPos());
            AbstractGamePoint endGamePoint = gameEntity.getGamePoints().get(gameInstanceEntity.getPlayers().get(movedPlayerIndex).getCurrentPoint());
            Point end = new Point(endGamePoint.getxPos(), endGamePoint.getyPos());

            if (end.x == start.x) {
                int delta = Math.abs(end.y - start.y);
                for (int y = 0; y <= delta; y+= delta /15 * (isFrameDrawnInTime ? 1 : 2)) { //1500ms / 50 = 30 frames by 50ms
                    nextPoint = new Point(start.x, end.y > start.y ? start.y + y : start.y - y);
                    waitWhileDrawingMap();
                }
            }
            else {
                int delta = Math.abs(end.x - start.x);
                for (int x = 0; x <= delta; x+= delta /15 * (isFrameDrawnInTime ? 1 : 2)) { //1500ms / 50 = 30 frames by 50ms
                    nextPoint = getNextPointOnLineByXvalue(start, end, end.x > start.x ? start.x + x : start.x - x);
                    waitWhileDrawingMap();
                }
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
            long startAnimationTime = System.currentTimeMillis();
            mMapImage.postInvalidate();
            try {lockFrame.wait();} catch (InterruptedException e) {}

            long wastedTime = 100 - System.currentTimeMillis() + startAnimationTime;
            isFrameDrawnInTime = wastedTime >= 0;
            try {Thread.sleep(wastedTime);} catch (InterruptedException | IllegalArgumentException e) {}
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

        ObjectAnimator.ofFloat(mMapContainer, "pivotX", cachedBitmap.getWidth()).setDuration(1).start();
        ObjectAnimator.ofFloat(mMapContainer, "pivotY", cachedBitmap.getHeight() * 2).setDuration(1).start();
        ObjectAnimator.ofFloat(mMapContainer, "rotationX", START_ROTATION_ANGLE /*+ getRotationAngle()*/).setDuration(isFirstScroll?1:500).start();

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

    private float getRotationAngle() {
        int y = gameEntity.getGamePoints().get(gameInstanceEntity.getPlayers()
                .get(gameInstanceEntity.getCurrentPlayer()).getCurrentPoint()).getyPos();

        return  INTERPOLATED_ROTATION_RANGE * y / mapViewPort.height();
    }

    private Point getNextPointOnLineByXvalue(Point start, Point end, int x) {
        if (start.x == end.x)
            return new Point(start.x, start.y);

        float k = (start.y - end.y)*1f / (start.x - end.x);
        float b = end.y - k * end.x;

        return new Point(x, Math.round(k * x + b));
    }

}
