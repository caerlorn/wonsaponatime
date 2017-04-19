package project.prototype;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import java.util.ArrayList;
import java.util.List;

public class SimpleDrawView extends View implements OnTouchListener {

    private static final float STROKE_WIDTH = 5f;
    boolean drawFlag = false;

    public boolean getDrawFlag() {
		return drawFlag;
	}

	public void setDrawFlag(boolean drawFlag) {
		this.drawFlag = drawFlag;
	}

	List<Point> points = new ArrayList<Point>();

    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    
    public SimpleDrawView(Context context) {
        super(context);
        
        drawFlag = true;
        this.setOnTouchListener(this);
        
    }
    
    public void setLineColor(String colorString) {
    	
    	paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(STROKE_WIDTH);
        paint.setColor(Color.parseColor(colorString.toLowerCase()));
        
    }
    

    @Override
    public void onDraw(Canvas canvas) {
        Path path = new Path();
        boolean first = true;
        for (Point point : points) {
            if (first) {
                first = false;
                path.moveTo(point.x, point.y);
            } else {
                path.lineTo(point.x, point.y);
            }
        }

        canvas.drawPath(path, paint);
    }

    public boolean onTouch(View view, MotionEvent event) {
    	if(drawFlag == false){
    	return true;
    	}
    		
        if (event.getAction() != MotionEvent.ACTION_UP) {
            for (int i = 0; i < event.getHistorySize(); i++) {
                Point point = new Point();
                point.x = event.getHistoricalX(i);
                point.y = event.getHistoricalY(i);
                points.add(point);
            }
            invalidate();
            view.setClickable(false);
            return true;
        } 
        else if(event.getAction() == MotionEvent.ACTION_UP) {
        	
        }
    	
        return super.onTouchEvent(event);
    }

    public void clear() {
        points.clear();
    }

    class Point {
        float x, y;
        float dx, dy;

        @Override
        public String toString() {
            return x + ", " + y;
        }
    }
}