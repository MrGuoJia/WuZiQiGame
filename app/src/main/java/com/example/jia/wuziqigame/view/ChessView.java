package com.example.jia.wuziqigame.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.example.jia.wuziqigame.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jia on 2017/6/8.
 */

public class ChessView extends View {
    private  int mPanelWidth;
    private  float mLineHeight;
    private  int MAX_lINE=10;
    private Paint mPaint=new Paint();
    private Bitmap whiteChess;
    private Bitmap blackChess;
    private  float radioChessOfLineHeight=3*1.0f/4;//棋子宽高设置一定比列，不能任由图片大小
    private boolean isWhite=true;//用来判断是轮到白棋下还是黑棋
    private ArrayList<Point> mWhiteArray=new ArrayList<>();//存用户点击位置落下白棋的坐标
    private ArrayList<Point> mBlackArray=new ArrayList<>();
    private boolean isGameOver;
    private boolean isWhiteWinner;
    private int MAX_COUNT_IN_LINE=5;
    public ChessView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setBackgroundColor(0x66FFFF99);
        init();//初始化画笔与棋子样式
    }

    private void init() {
        mPaint.setColor(0x99000000);//半透明灰色
        mPaint.setAntiAlias(true);//抗锯齿
        mPaint.setDither(true);//设置防抖动
        mPaint.setStyle(Paint.Style.STROKE);

        whiteChess= BitmapFactory.decodeResource(getResources(), R.drawable.white_chess);
        blackChess= BitmapFactory.decodeResource(getResources(), R.drawable.black_chess);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize=MeasureSpec.getSize(widthMeasureSpec);
        int widthMode=MeasureSpec.getSize(heightMeasureSpec);
        int heightSize=MeasureSpec.getSize(heightMeasureSpec);
        int heightMode=MeasureSpec.getSize(heightMeasureSpec);
        int width=Math.min(widthSize,heightSize);
        if(widthMode==MeasureSpec.UNSPECIFIED){
            width=heightSize;
        }else if(heightMode==MeasureSpec.UNSPECIFIED){
            width=widthSize;
        }
        setMeasuredDimension(width,width);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mPanelWidth=w;
        mLineHeight=mPanelWidth*1.0f/MAX_lINE;
        int pieceWidth= (int) (mLineHeight*radioChessOfLineHeight);

        whiteChess=Bitmap.createScaledBitmap(whiteChess,pieceWidth,pieceWidth,false);//设置棋子的尺寸大小
        blackChess=Bitmap.createScaledBitmap(blackChess,pieceWidth,pieceWidth,false);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(isGameOver)return false;//如果游戏结束，不予许再下子


      int action=  event.getAction();
        if(action==MotionEvent.ACTION_UP){
            int x= (int) event.getX();
            int y= (int) event.getY();
         //   Point p=new Point(x,y);//这种方法很难判断该位置是否下过棋子
            Point p=getValidPoint(x,y);
            if(mWhiteArray.contains(p)||mBlackArray.contains(p)){//判断list对象中是否有指定对象,比较的是list中的值，而非地址
                return false;
            }
            if(isWhite){
                mWhiteArray.add(p);
            }else {
                mBlackArray.add(p);
            }
            invalidate();//重绘
            isWhite=!isWhite;//轮到对手下
            return true;
        }
        return true;
    }

    private Point getValidPoint(int x, int y) {
        return new Point((int)(x/mLineHeight),(int)(y/mLineHeight));
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBoard(canvas);//画棋盘横竖线
        drawChess(canvas);//绘制棋子
        checkGameOver();//判断游戏胜负
    }

    private void checkGameOver() {
        boolean whiteWin= checkFiveInLine(mWhiteArray);
        boolean blackWin= checkFiveInLine(mBlackArray);
        if(whiteWin||blackWin){
            isGameOver=true;//如果两者有一位胜利，则判断游戏结束
            isWhiteWinner=whiteWin;
            String text=isWhiteWinner?"白棋胜利":"黑棋胜利";
            Toast.makeText(getContext(),text,Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkFiveInLine(List<Point> points) {
        for(Point p:points){
            int x=p.x;
            int y=p.y;
           boolean win=checkHorizontal(x,y,points);//判断是否为横向五子
            if(win)return true;
             win=checkVertical(x,y,points);//判断是否为竖向五子
            if(win)return true;
             win=checkLeftDiagonal(x,y,points);//判断是否为左斜向五子
            if(win)return true;
             win=checkRightDiagonal(x,y,points);//判断是否为右斜向五子
            if(win)return true;

        }
        return false;
    }

    private boolean checkHorizontal(int x, int y, List<Point> points) {
        int count =1;
        for(int i=1;i<MAX_COUNT_IN_LINE;i++){ //判断往左边是否连续五个同色棋子
            if(points.contains(new Point(x-i,y))){
                count++;
            }else{
                break;
            }

        }
        if(count==MAX_COUNT_IN_LINE)return true;//确认左边连续5个是返回true，不用往下运行右边的检验
        for(int i=1;i<MAX_COUNT_IN_LINE;i++){ //判断往右边是否连续五个同色棋子
            if(points.contains(new Point(x+i,y))){
                count++;
            }else{
                break;
            }

        }
        if(count==MAX_COUNT_IN_LINE)return true;//确认是左右有一种连续五色返回true
        return false;
    }

    private boolean checkVertical(int x, int y, List<Point> points) { //判断是否为竖向五子
        int count =1;
        for(int i=1;i<MAX_COUNT_IN_LINE;i++){ //判断往上是否连续五个同色棋子
            if(points.contains(new Point(x,y-i))){
                count++;
            }else{
                break;
            }

        }
        if(count==MAX_COUNT_IN_LINE)return true;//确认左边连续5个是返回true，不用往下运行右边的检验
        for(int i=1;i<MAX_COUNT_IN_LINE;i++){ //判断往下边是否连续五个同色棋子
            if(points.contains(new Point(x,y+i))){
                count++;
            }else{
                break;
            }

        }
        if(count==MAX_COUNT_IN_LINE)return true;//确认是左右有一种连续五色返回true
        return false;
    }



    private boolean checkLeftDiagonal(int x, int y, List<Point> points) { //判断是否为左斜连续五子
        int count =1;
        for(int i=1;i<MAX_COUNT_IN_LINE;i++){ //判断往左下是否连续五个同色棋子
            if(points.contains(new Point(x-i,y+i))){
                count++;
            }else{
                break;
            }

        }
        if(count==MAX_COUNT_IN_LINE)return true;//确认左边连续5个是返回true，不用往下运行右边的检验
        for(int i=1;i<MAX_COUNT_IN_LINE;i++){ //判断往左上是否连续五个同色棋子
            if(points.contains(new Point(x+i,y-i))){
                count++;
            }else{
                break;
            }

        }
        if(count==MAX_COUNT_IN_LINE)return true;//确认是左右有一种连续五色返回true
        return false;
    }



    private boolean checkRightDiagonal(int x, int y, List<Point> points) { //判断是否为右斜连续五子
        int count =1;
        for(int i=1;i<MAX_COUNT_IN_LINE;i++){ //判断往右上是否连续五个同色棋子
            if(points.contains(new Point(x-i,y-i))){
                count++;
            }else{
                break;
            }

        }
        if(count==MAX_COUNT_IN_LINE)return true;//确认左边连续5个是返回true，不用往下运行右边的检验
        for(int i=1;i<MAX_COUNT_IN_LINE;i++){ //判断往右下是否连续五个同色棋子
            if(points.contains(new Point(x+i,y+i))){
                count++;
            }else{
                break;
            }

        }
        if(count==MAX_COUNT_IN_LINE)return true;//确认是左右有一种连续五色返回true
        return false;
    }

    private void drawChess(Canvas canvas) {
        for(int i=0,n=mWhiteArray.size();i<n;i++){//将白棋画在棋盘上
            Point whitePoint=mWhiteArray.get(i);
            canvas.drawBitmap(whiteChess,
                    (whitePoint.x+(1-radioChessOfLineHeight)/2)*mLineHeight,
                    (whitePoint.y+(1-radioChessOfLineHeight)/2)*mLineHeight,null);
        }
        for(int i=0,n=mBlackArray.size();i<n;i++){//将黑棋画在棋盘上
            Point blackPoint=mBlackArray.get(i);
            canvas.drawBitmap(blackChess,
                    (blackPoint.x+(1-radioChessOfLineHeight)/2)*mLineHeight,
                    (blackPoint.y+(1-radioChessOfLineHeight)/2)*mLineHeight,null);
        }
    }

    private void drawBoard(Canvas canvas) {
        int w=mPanelWidth;
        float lineHeight=mLineHeight;
        for(int i=0;i<MAX_lINE;i++){
            int startX= (int) (lineHeight/2);
            int endX= (int) (w-lineHeight/2);//获取起点和终点的横坐标

            int y= (int) ((0.5+i)*lineHeight);
            canvas.drawLine(startX,y,endX,y,mPaint);//横线绘制
            canvas.drawLine(y,startX,y,endX,mPaint);//竖线绘制
        }
    }
    private static  final  String INSTANCE="instance";
    private static  final  String INSTANCE_GAME_OVER="instance_game_over";
    private static  final  String INSTANCE_WHITE_ARRAY="instance_white_array";
    private static  final  String INSTANCE_BLACK_ARRAY="instance_black_array";

    @Override
    protected Parcelable onSaveInstanceState() {//记录当前下棋状态与结果
        Bundle bundle=new Bundle();
        bundle.putParcelable(INSTANCE,super.onSaveInstanceState());
        bundle.putBoolean(INSTANCE_GAME_OVER,isGameOver);
        bundle.putParcelableArrayList(INSTANCE_WHITE_ARRAY,mWhiteArray);
        bundle.putParcelableArrayList(INSTANCE_BLACK_ARRAY,mBlackArray);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {//注意：必须将view加上id
        if(state instanceof  Bundle){
            Bundle bundle= (Bundle) state;
            isGameOver=bundle.getBoolean(INSTANCE_GAME_OVER);
            mWhiteArray=bundle.getParcelableArrayList(INSTANCE_WHITE_ARRAY);
            mBlackArray=bundle.getParcelableArrayList(INSTANCE_BLACK_ARRAY);
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE));
            return;
        }
        super.onRestoreInstanceState(state);
    }
    public void reStart(){//重新开始
        mWhiteArray.clear();
        mBlackArray.clear();
        isGameOver=false;
        isWhiteWinner=false;
        invalidate();
    }
    public void regret(){//悔棋
        boolean whiteWin= checkFiveInLine(mWhiteArray);//判断是否已经胜利，没有分出胜负才能悔棋
        boolean blackWin= checkFiveInLine(mBlackArray);
        if(isWhite){
            if(mBlackArray.size()>0 && blackWin==false && whiteWin==false){
                isWhite=!isWhite;//除掉之后还是轮到该棋子下
                int nowPoint=mBlackArray.size();//获取当前棋子数
                nowPoint--;
                mBlackArray.remove(nowPoint);//将最新下的棋子除掉
                invalidate();
            }
        }else {

            if(mWhiteArray.size()>0 && blackWin==false && whiteWin==false){
                isWhite=!isWhite;
                int nowPoint=mWhiteArray.size();//获取当前棋子数
                nowPoint--;
                mWhiteArray.remove(nowPoint);//将最新下的棋子除掉
                invalidate();
            }
        }
    }


}
