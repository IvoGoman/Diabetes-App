package uni.mannheim.teamproject.diabetesplaner.DailyRoutine;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

/**
 * Created by Stefan on 29.01.2016.
 */
public class TimeLineView extends View {
    private int end;
    private int start;
    Paint paint = new Paint();

    public TimeLineView(Context context, int start, int end) {
        super(context);
        this.start = start+getpx(10);
        this.end = end-getpx(10);
    }

    private void init(){
        paint.setColor(Color.BLACK);
    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.drawLine(start, getpx(10), end, getpx(10), paint);
    }

    public int getdp(int px){
        return (int)(px/getResources().getDisplayMetrics().density);
    }

    public int getpx(int dp){
        return (int)(dp*getResources().getDisplayMetrics().density);
    }
}
