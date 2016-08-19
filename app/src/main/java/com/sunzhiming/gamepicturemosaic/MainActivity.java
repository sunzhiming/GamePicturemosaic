package com.sunzhiming.gamepicturemosaic;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class MainActivity extends Activity  implements View.OnClickListener {
    /**
     * 判断游戏是否开始
     */
    private boolean isGameStart = false;
    /*判断动画是否正在执行*/
    private boolean isStartAnim = false;
    /**
     * 利用二维数组创建若干个游戏小方块
     * @param savedInstanceState
     */
    private ImageView[][] iv_game_arr = new ImageView[3][5];
    private GridLayout mGl_main_game;
    /*当前空方块的实例*/
    private ImageView iv_null_image;
    /*当前手势*/
    private GestureDetector mDetector;
    private Button mBtnBack;
    private Button mExit;
    private Button mJishao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDetector = new GestureDetector(this, new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent motionEvent) {
                return false;
            }
            @Override
            public void onShowPress(MotionEvent motionEvent) {
            }
            @Override
            public boolean onSingleTapUp(MotionEvent motionEvent) {
                return false;
            }
            @Override
            public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
                return false;
            }
            @Override
            public void onLongPress(MotionEvent motionEvent) {
            }
            @Override
            public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
                int type = getDirByGes(motionEvent.getX(), motionEvent.getY(), motionEvent1.getX(), motionEvent1.getY());
                changByType(type);
                return false;
            }
        });
        setContentView(R.layout.activity_main);
         /*初始化view*/
        initView();



        /*初始化游戏的若干个小方块*/
        //获取一张大图
        Bitmap bigbm = ((BitmapDrawable) getResources().getDrawable(R.mipmap.dog)).getBitmap();
        int tuWandH = bigbm.getWidth() / 5;

        int smalliv_width = getWindowManager().getDefaultDisplay().getWidth() / 5;

        for (int i = 0; i < iv_game_arr.length; i++) {
            for (int j = 0; j < iv_game_arr[0].length; j++) {
                //根据行和列来切图
                Bitmap bm = Bitmap.createBitmap(bigbm, j * tuWandH, i * tuWandH, tuWandH, tuWandH);
                iv_game_arr[i][j] = new ImageView(this);
                iv_game_arr[i][j].setImageBitmap(bm);
                iv_game_arr[i][j].setLayoutParams(new RelativeLayout.LayoutParams(smalliv_width,smalliv_width));
                //设置方块之间的间距
                iv_game_arr[i][j].setPadding(2, 2, 2, 2);
                //绑定自定义的数据
                iv_game_arr[i][j].setTag(new GameData(i, j, bm));
                iv_game_arr[i][j].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        boolean flag = isHasByNullImageView((ImageView) view);
//                        Toast.makeText(MainActivity.this, "flag"+flag, Toast.LENGTH_SHORT).show();

                        if (flag) {
                            changeDataByImageView((ImageView) view);
                        }
                    }
                });
            }
        }
        /*初始化游戏主界面，并添加若干个小方块*/
        mGl_main_game = (GridLayout) findViewById(R.id.main_game);
        for (int i = 0; i < iv_game_arr.length; i++) {
            for (int j = 0; j < iv_game_arr[0].length; j++) {
                mGl_main_game.addView(iv_game_arr[i][j]);
            }
        }
        setNullImage(iv_game_arr[2][4]);

        /*
        初始化打乱顺序
         */
        randomMove();

        /**
         * 所有准备工作结束了，视为游戏开始
         */
        isGameStart = true;
    }

    /*8
    初始化view
     */
    private void initView() {

        mBtnBack = (Button) findViewById(R.id.btn_bank);
        mExit = (Button) findViewById(R.id.btn_exit);
        mJishao = (Button) findViewById(R.id.jieshao);
        Button again = (Button) findViewById(R.id.again);

        mBtnBack.setOnClickListener(this);
        mExit.setOnClickListener(this);
        mJishao.setOnClickListener(this);
        again.setOnClickListener(this);
    }

    /**
     * 当前要设置为空的方块
     */
    private void setNullImage(ImageView mImage) {
        mImage.setImageBitmap(null);
        iv_null_image = mImage;
    }
    /**
     * 设置手势监听为自己的
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mDetector.onTouchEvent(event);
    }
    /*
    *事件分发
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        mDetector.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }
    /**
     * 随机打乱顺序
     */
    public void randomMove() {
        //打乱的次数
        for (int i = 0; i < 10; i++) {
            int type = (int) (Math.random() * 4) + 1;
            changByType(type, false);
        }
        //开始交换，注意是无动画的
    }
    /**
     * 重载
     */
    public void changeDataByImageView(final ImageView mImageview) {
        changeDataByImageView(mImageview, true);
    }
    /**
     * 利用动画结束之后，交换两个位置的数据
     */
    public void changeDataByImageView(final ImageView mImageview, boolean isAnim) {
        if (isStartAnim) {
            return;
        }
        if (!isAnim) {
            GameData mData = (GameData) mImageview.getTag();
            iv_null_image.setImageBitmap(mData.bm);
            GameData mNullData = (GameData) iv_null_image.getTag();
            mNullData.bm = mData.bm;
            mNullData.p_X = mData.p_X;
            mNullData.p_Y = mData.p_Y;
            setNullImage(mImageview);

            if (isGameStart) {
                //成功时弹土司
                isGameOver();
            }
            return;
        }
        //创建动画，设置好方向，移动的距离
        TranslateAnimation translateAnimation = null;
        if (mImageview.getX() > iv_null_image.getX()) {//当前点击的方块在空方块的下边，
            //往上移动
            translateAnimation = new TranslateAnimation(0.1f, -mImageview.getWidth(), 0.1f, 0.1f);
        } else if (mImageview.getX() < iv_null_image.getX()) {
            translateAnimation = new TranslateAnimation(0.1f, mImageview.getWidth(), 0.1f, 0.1f);
        } else if (mImageview.getY() > iv_null_image.getY()) {
            translateAnimation = new TranslateAnimation(0.1f, 0.1f, 0.1f, -mImageview.getWidth());
        } else if (mImageview.getY() < iv_null_image.getY()) {
            translateAnimation = new TranslateAnimation(0.1f, 0.1f, 0.1f, mImageview.getWidth());
        }
        //设置动画的时长
        translateAnimation.setDuration(100);
        //设置动画结束之后是否停留
        translateAnimation.setFillAfter(true);
        //动画结束后，真正的吧数据交换
        translateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                isStartAnim = true;
            }
            @Override
            public void onAnimationEnd(Animation animation) {

                isStartAnim = false;
                mImageview.clearAnimation();
                GameData mData = (GameData) mImageview.getTag();
                iv_null_image.setImageBitmap(mData.bm);
                GameData mNullData = (GameData) iv_null_image.getTag();
                mNullData.bm = mData.bm;
                mNullData.p_X = mData.p_X;
                mNullData.p_Y = mData.p_Y;
                setNullImage(mImageview);

                if (isGameStart) {
                    //成功时弹土司
                    isGameOver();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        //执行动画
        mImageview.startAnimation(translateAnimation);
    }
    /**
     * 重载方法
     */
    private void changByType(int type) {
        changByType(type, true);
    }

    /**
     * 根据手势改变位置
     * @param type
     */
    private void changByType(int type, boolean isAnim) {
        //首先拿到空方块的数据
        GameData mNullGameData = (GameData) iv_null_image.getTag();
        //根据方向设置相邻位置的坐标
        int new_x = mNullGameData.x;
        int new_y = mNullGameData.y;

        if (type == 1) {//要移动的方块在当前空方块的下面  向上移
            new_x++;

        } else if (type == 2) {
            new_x--;
        } else if (type == 3) {//要移动的方块在当前空方块的右面面  向左移
            new_y++;
        } else if (type == 4) {
            new_y--;
        }

        //判断这个新的坐标是否存在
        if (new_x >= 0 && new_x < iv_game_arr.length && new_y > 0 && new_y < iv_game_arr[0].length) {
            //开始移动
            if (isAnim) {
                changeDataByImageView(iv_game_arr[new_x][new_y]);
            } else {
                changeDataByImageView(iv_game_arr[new_x][new_y], false);
            }

        } else {
            //什么也不做
        }
    }

    /**
     * 判断游戏是否结束
     */

    public void isGameOver() {

        //开关变量
        boolean isGameOver = true;
        //遍历每个小方块
        for (int i = 0; i < iv_game_arr.length; i++) {
            for (int j = 0; j < iv_game_arr[0].length; j++) {
                //判断是否为空，为空不遍历
                if (iv_game_arr[i][j] == iv_null_image) {
                    continue;
                }
                GameData mGameData = (GameData) iv_game_arr[i][j].getTag();
                if (!mGameData.isTrue()) {
                    isGameOver = false;
                    break;
                }
            }
        }
        //根据一个开关变量决定游戏是否结束，给提示
        if (isGameOver) {
            Toast.makeText(MainActivity.this, "游戏结束", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     *
     * @param start_x 开始x坐标
     * @param start_y 开始Y坐标
     * @param end_x 结束坐标
     * @param end_y 结束坐标
     * @return 返回值1上, 2下, 3左, 4右
     */
    public int getDirByGes(float start_x, float start_y, float end_x, float end_y) {
        boolean isLeftOrRight = (Math.abs(start_x - end_x) > Math.abs(start_y - end_y)) ? true : false;//是否是左右
        if (isLeftOrRight) {
            //左右结构
            boolean isLeft = start_x - end_x > 0 ? true : false;//左
            if (isLeft) {
                return 3;
            } else {
                return 4;
            }

        } else {
            //上下结构
            boolean isUp = start_y - end_y > 0 ? true : false;
            if (isUp) {
                //向上
                return 1;
            } else {
                return 2;
            }
        }
    }

    /**
     * 判断当前点击的方块是否与空方块为相邻关系
     * mImageView点击的方块
     * 返回结果：相邻，不相邻
     */

    private boolean isHasByNullImageView(ImageView mImageView) {
        //分别判断当前点击方块的位置和空方块的位置，通过x,y
        GameData mNunllImage = (GameData) iv_null_image.getTag();
        GameData mImage = (GameData) mImageView.getTag();
        if (mNunllImage.y == mImage.y && mImage.x + 1 == mNunllImage.x) {//当前点击的方块在空方块的上面
            return true;
        } else if (mNunllImage.y == mImage.y && mImage.x - 1 == mNunllImage.x) {//当前点击的方块在空方块的下面
            return true;
        } else if (mNunllImage.y == mImage.y + 1 && mImage.x == mNunllImage.x) {//当前点击的方块在空方块的左面
            return true;
        } else if (mNunllImage.y == mImage.y - 1 && mImage.x == mNunllImage.x) {//当前点击的方块在空方块的右面
            return true;
        }
        return false;
    }



    /**
     * 每个小方块上面绑定的数据
     */
    class GameData {
        //每个小方块的实际位置X
        public int x = 0;
        //每个小方块的实际位置Y
        public int y = 0;
        //每个小方块的图片
        public Bitmap bm;
        //每个小方块图片的实际位置X
        public int p_X = 0;
        //每个小方块图片的实际位置Y
        public int p_Y = 0;

        public GameData(int x, int y, Bitmap bm) {
            super();
            this.x = x;
            this.y = y;
            this.bm = bm;
            this.p_X = x;
            this.p_Y = y;
        }

        /*
        判断每个小刚快的位置，是否正确
         */
        public boolean isTrue() {
            if (x == p_X && y == p_Y) {
                return true;
            }
            return false;
        }
    }

    /**
     *点击按钮操作
     * @param view
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.jieshao:
                startActivity(new Intent(MainActivity.this,JieShaoActivity.class));

                overridePendingTransition(R.anim.inter_jieshao,R.anim.end);
                break;
            case R.id.btn_exit:
                finish();
                break;
            case R.id.again:
                randomMove();
                break;
            case R.id.btn_bank:
                Toast.makeText(MainActivity.this, "暂未开放", Toast.LENGTH_SHORT).show();
                break;
        }
    }

}
