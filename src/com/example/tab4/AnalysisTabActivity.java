package com.example.tab4;

import java.util.ArrayList;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

public class AnalysisTabActivity extends Activity {
	private XYMultipleSeriesDataset mDataset;
	private XYMultipleSeriesRenderer mRenderer;
	//private XYSeries mCurrentSeries;
	//private XYSeriesRenderer mCurrentRenderer;
	private GraphicalView mChartView;
	private LinearLayout layout = null;

	public static double[] curArr = null;// 曲率数组
	public static double[] meanCurArr = null;// 平均曲率数组
	public static int handleIndex;// 叶柄位置
	public static int handleLength;// 叶柄长度


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.xy_chart);
	}

	protected void onResume() {
		super.onResume();

		// 设置x轴坐标
		List<double[]> xList = new ArrayList<double[]>();
		double[] xArr = null;
		// 设置y轴坐标
		List<double[]> yList = new ArrayList<double[]>();
		if (curArr != null) {
			// 设置图例文字
			String[] titles = new String[] { "曲率", "局部平均曲率", "叶柄位置",
					"叶柄长度", "叶柄长度" };
			int curLength = curArr.length;
			xArr = new double[curLength];
			for (int i = 0; i < curLength; i++) {
				xArr[i] = i;
			}
			for (int i = 0; i < 2; i++) {
				xList.add(xArr);
			}
			xList.add(new double[] { handleIndex, handleIndex });
			xList.add(new double[] {
					(handleIndex + handleLength) % curLength,
					(handleIndex + handleLength) % curLength });
			xList.add(new double[] {
					(handleIndex - handleLength + curLength) % curLength,
					(handleIndex - handleLength + curLength) % curLength });
			yList.add(curArr);
			yList.add(meanCurArr);
			yList.add(new double[] { 0, 1 });
			yList.add(new double[] { 0, 1 });
			yList.add(new double[] { 0, 1 });
			// 设置曲线颜色
			int[] colors = new int[] { Color.BLUE, Color.RED, Color.GREEN,
					Color.GRAY, Color.GRAY };
			// 设置坐标点
			PointStyle[] styles = new PointStyle[] { PointStyle.POINT,
					PointStyle.POINT, PointStyle.POINT, PointStyle.POINT,
					PointStyle.POINT };
			mRenderer = buildRenderer(colors, styles);
			int length = mRenderer.getSeriesRendererCount();
			for (int i = 0; i < length; i++) {
				((XYSeriesRenderer) mRenderer.getSeriesRendererAt(i))
						.setFillPoints(true);
				((XYSeriesRenderer) mRenderer.getSeriesRendererAt(i))
						.setLineWidth(2);
			}
			// 设置图表标题、坐标轴标注、坐标轴范围、坐标轴颜色
			setChartSettings(mRenderer, "叶子曲率分布", "位置", "曲率", 0, curLength,
					0, 1, Color.LTGRAY, Color.LTGRAY);
			mRenderer.setXLabels(18);
			mRenderer.setYLabels(10);
			// mRenderer.setShowGrid(true);
			mRenderer.setXLabelsAlign(Align.RIGHT);
			mRenderer.setYLabelsAlign(Align.RIGHT);
			mRenderer.setZoomButtonsVisible(true);
			mRenderer.setPanLimits(new double[] { 0, curLength, 0, 1 });
			mRenderer.setZoomLimits(new double[] { 0, curLength, 0, 1.2 });
			// 得到要绘制的数据集
			mDataset = buildDataset(titles, xList, yList);

			// 得到图表视图，并绘制到布局中
			layout = (LinearLayout) findViewById(R.id.chart);
			mChartView = ChartFactory.getLineChartView(this, mDataset,
					mRenderer);
			 mChartView.repaint();
			// 移除原有的LinearLayout中的视图控件
			layout.removeAllViewsInLayout();
			xList.clear();
			yList.clear();
			layout.addView(mChartView, new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		} else {
			// 设置图例文字
			String[] titles = new String[] { "曲率", "局部平均曲率" };
			// 设置x轴坐标
			xArr = new double[100];
			for (int i = 0; i < 100; i++) {
				xArr[i] = Math.PI * i / 100;
			}

			// 设置y轴坐标
			curArr = new double[100];
			meanCurArr = new double[100];
			for (int i = 0; i < 100; i++) {
				curArr[i] = Math.sin(Math.PI * i / 100);
				meanCurArr[i] = Math.cos(Math.PI * i / 100);
			}
			for (int i = 0; i < titles.length; i++) {
				xList.add(xArr);
			}
			yList.add(curArr);
			yList.add(meanCurArr);
			// 设置曲线颜色
			int[] colors = new int[] { Color.BLUE, Color.RED };
			// 设置坐标点
			PointStyle[] styles = new PointStyle[] { PointStyle.POINT,
					PointStyle.POINT };
			mRenderer = buildRenderer(colors, styles);
			int length = mRenderer.getSeriesRendererCount();
			for (int i = 0; i < length; i++) {
				((XYSeriesRenderer) mRenderer.getSeriesRendererAt(i))
						.setFillPoints(true);
				((XYSeriesRenderer) mRenderer.getSeriesRendererAt(i))
						.setLineWidth(2);
			}
			// 设置图表标题、坐标轴标注、坐标轴范围、坐标轴颜色
			setChartSettings(mRenderer, "叶子曲率分布", "位置", "曲率", 0, Math.PI,
					-1, 1, Color.LTGRAY, Color.LTGRAY);
			mRenderer.setXLabels(20);
			mRenderer.setYLabels(10);
			// mRenderer.setShowGrid(true);
			mRenderer.setXLabelsAlign(Align.RIGHT);
			mRenderer.setYLabelsAlign(Align.RIGHT);
			mRenderer.setZoomButtonsVisible(true);
			mRenderer.setPanLimits(new double[] { 0, Math.PI, -1, 1 });
			mRenderer.setZoomLimits(new double[] { 0, 4, -1.2, 1.2 });
			// mRenderer.setOrientation(XYMultipleSeriesRenderer.Orientation.VERTICAL);
			// 得到要绘制的数据集
			mDataset = buildDataset(titles, xList, yList);
			// 得到图表视图，并绘制到布局中
			layout = (LinearLayout) findViewById(R.id.chart);
			mChartView = ChartFactory.getLineChartView(this, mDataset,
					mRenderer);
			mChartView.repaint();
			// 移除原有的LinearLayout中的视图控件
			layout.removeAllViewsInLayout();

			xList.clear();
			yList.clear();
			layout.addView(mChartView, new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		}

	}

	// 向DataSet中添加序列.
	public void addXYSeries(XYMultipleSeriesDataset dataset, String[] titles,
			List<double[]> xValues, List<double[]> yValues, int scale) {
		int length = titles.length;
		for (int i = 0; i < length; i++) {
			XYSeries series = new XYSeries(titles[i], scale);
			double[] xV = xValues.get(i);
			double[] yV = yValues.get(i);
			int seriesLength = xV.length;
			for (int k = 0; k < seriesLength; k++) {
				series.add(xV[k], yV[k]);
			}
			dataset.addSeries(series);
		}
	}

	protected XYMultipleSeriesDataset buildDataset(String[] titles,
			List<double[]> xValues, List<double[]> yValues) {
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		addXYSeries(dataset, titles, xValues, yValues, 0);
		return dataset;
	}


	// 整个图表属性设置
	protected void setRenderer(XYMultipleSeriesRenderer renderer, int[] colors,
			PointStyle[] styles) {
		renderer.setAxisTitleTextSize(16);
		renderer.setChartTitleTextSize(30);
		renderer.setLabelsTextSize(15);
		renderer.setLegendTextSize(15);
		renderer.setPointSize(5f);
		renderer.setMargins(new int[] { 35, 35, 15, 20 });
		int length = colors.length;
		for (int i = 0; i < length; i++) {
			XYSeriesRenderer r = new XYSeriesRenderer();
			r.setColor(colors[i]);
			r.setPointStyle(styles[i]);
			renderer.addSeriesRenderer(r);
		}
	}

	// 构建XYMultipleSeriesRenderer.
	protected XYMultipleSeriesRenderer buildRenderer(int[] colors,
			PointStyle[] styles) {
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		setRenderer(renderer, colors, styles);
		return renderer;
	}

	// 设置renderer的一些属性.
	protected void setChartSettings(XYMultipleSeriesRenderer renderer,
			String title, String xTitle, String yTitle, double xMin,
			double xMax, double yMin, double yMax, int axesColor,
			int labelsColor) {
		renderer.setChartTitle(title);
		renderer.setXTitle(xTitle);
		renderer.setYTitle(yTitle);
		renderer.setXAxisMin(xMin);
		renderer.setXAxisMax(xMax);
		renderer.setYAxisMin(yMin);
		renderer.setYAxisMax(yMax);
		renderer.setAxesColor(axesColor);
		renderer.setLabelsColor(labelsColor);
	}

}
