package com.appbusters.robinkamboj.piechart;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.EmbossMaskFilter;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.SeekBar;
import android.widget.TextView;

import com.androidplot.pie.PieChart;
import com.androidplot.pie.PieRenderer;
import com.androidplot.pie.Segment;
import com.androidplot.pie.SegmentFormatter;
import com.androidplot.util.PixelUtils;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final int SELECTED_SEGMENT_OFFSET = 50;
    Context context;
    public PieChart pieChart;
    SeekBar donutSizeSeekBar;
    TextView donutSizeTextView;
    Segment s1,s2,s3,s4;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        context= getApplicationContext();

        pieChart= (PieChart) findViewById(R.id.pieChart);

        final float padding = PixelUtils.dpToPix(30);
        pieChart.getPie().setPadding(padding, padding, padding, padding);

        // detect segment clicks:
        pieChart.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                PointF click = new PointF(motionEvent.getX(), motionEvent.getY());
                if (pieChart.getPie().containsPoint(click)) {
                    Segment segment = pieChart.getRenderer(PieRenderer.class).getContainingSegment(click);

                    deselectAll();
                    if (segment != null) {
                        final boolean isSelected = getFormatter(segment).getOffset() != 0;
                        setSelected(segment, !isSelected);
                        pieChart.redraw();
                    }
                }
                return false;
            }


            private SegmentFormatter getFormatter(Segment segment) {
                return pieChart.getFormatter(segment, PieRenderer.class);
            }

            private void deselectAll() {
                List<Segment> segments = pieChart.getRegistry().getSeriesList();
                for(Segment segment : segments) {
                    setSelected(segment, false);
                }
            }

            private void setSelected(Segment segment, boolean isSelected) {
                SegmentFormatter f = getFormatter(segment);
                if(isSelected) {
                    f.setOffset(SELECTED_SEGMENT_OFFSET);
                } else {
                    f.setOffset(0);
                }
            }
        });


        donutSizeSeekBar = (SeekBar) findViewById(R.id.donutSizeSeekBar);
        donutSizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {}

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                pieChart.getRenderer(PieRenderer.class).setDonutSize(seekBar.getProgress()/100f,
                        PieRenderer.DonutMode.PERCENT);
                pieChart.redraw();
                updateDonutText();
            }
    });

        donutSizeTextView = (TextView) findViewById(R.id.donutSizeTextView);
        updateDonutText();

        s1 = new Segment("NS", 3);
        s2 = new Segment("RN", 1);
        s3 = new Segment("WYL", 7);
        s4 = new Segment("EA", 9);

        EmbossMaskFilter emf = new EmbossMaskFilter(
                new float[]{1, 1, 1}, 0.4f, 10, 8.2f);

        SegmentFormatter sf1 = new SegmentFormatter(this, R.xml.pie_segment_formatter1);
        sf1.getLabelPaint().setShadowLayer(3, 0, 0, Color.BLACK);
        sf1.getFillPaint().setMaskFilter(emf);

        SegmentFormatter sf2 = new SegmentFormatter(this, R.xml.pie_segment_formatter2);
        sf2.getLabelPaint().setShadowLayer(3, 0, 0, Color.BLACK);
        sf2.getFillPaint().setMaskFilter(emf);

        SegmentFormatter sf3 = new SegmentFormatter(this, R.xml.pie_segment_formatter3);
        sf3.getLabelPaint().setShadowLayer(3, 0, 0, Color.BLACK);
        sf3.getFillPaint().setMaskFilter(emf);

        SegmentFormatter sf4 = new SegmentFormatter(this, R.xml.pie_segment_formatter4);
        sf4.getLabelPaint().setShadowLayer(3, 0, 0, Color.BLACK);
        sf4.getFillPaint().setMaskFilter(emf);

        pieChart.addSegment(s1, sf1);
        pieChart.addSegment(s2, sf2);
        pieChart.addSegment(s3, sf3);
        pieChart.addSegment(s4, sf4);

        pieChart.getBorderPaint().setColor(Color.TRANSPARENT);
        pieChart.getBackgroundPaint().setColor(Color.TRANSPARENT);
    }

    @Override
    public void onStart() {
        super.onStart();
        setupIntroAnimation();
    }

    protected void updateDonutText() {
        donutSizeTextView.setText(donutSizeSeekBar.getProgress() + "%");
    }

    protected void setupIntroAnimation() {

        final PieRenderer renderer = pieChart.getRenderer(PieRenderer.class);
        // start with a zero degrees pie:

        renderer.setExtentDegs(0);
        // animate a scale value from a starting val of 0 to a final value of 1:
        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);

        // use an animation pattern that begins and ends slowly:
        animator.setInterpolator(new AccelerateDecelerateInterpolator());

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float scale = valueAnimator.getAnimatedFraction();
//                scalingSeries1.setScale(scale);
//                scalingSeries2.setScale(scale);
                renderer.setExtentDegs(360 * scale);
                pieChart.redraw();
            }
        });

        animator.setDuration(1500);
        animator.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        else if(id == R.id.about){

        }

        return super.onOptionsItemSelected(item);
    }
}