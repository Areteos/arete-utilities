/*
 *    Copyright 2022 Glenn Mamacos
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package functions;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StackedXYAreaRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.DefaultTableXYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.*;
import java.util.function.DoubleUnaryOperator;

/**
 * A collection of static functions that make quickly generating graphs easier.
 */
@SuppressWarnings("unused")
public final class Visualiser {
    private Visualiser() {}

    public record FunctionInfo(String name, DoubleUnaryOperator function, Color color, float weight, boolean visibleInLegend){
        public FunctionInfo(String name, DoubleUnaryOperator function) {
            this(name, function, null, 0.5f, true);
        }
    }

    public record LineInfo(String name, List<Double> values, Color color, float weight, boolean visibleInLegend){
        public LineInfo(String name, List<Double> values) {
            this(name, values, null, 0.5f, true);
        }

        public synchronized void addValue(Double value) {
            values.add(value);
        }
    }

    public static void drawLineGraph(String title, List<Double> values) {
        List<Double> xValues = new ArrayList<>();
        for (double i = 0; i < values.size(); i++) {
            xValues.add(i);
        }
        drawLineGraph(title, List.of(new LineInfo("", values)), xValues, false, false, "", "", false);
    }

    public static void drawLineGraph(String title, List<FunctionInfo> functionInfoList, double minimum, double maximum, int intervals, boolean logarithmic, boolean xAxisIsTime, String xAxisLabel, String yAxisLabel, boolean showLegend) {
        double range = maximum - minimum;
        List<Double> domainValues = new ArrayList<>();
        for (int i = 0; i < intervals+1; i++) {
            domainValues.add(minimum + i * (range / intervals));
        }

        List<LineInfo> series = new ArrayList<>();

        for (FunctionInfo functionInfo : functionInfoList) {
            DoubleUnaryOperator function = functionInfo.function();

            List<Double> rangeValues = new ArrayList<>();
            for (double domainValue : domainValues) {
                rangeValues.add(function.applyAsDouble(domainValue));
            }
            series.add(new LineInfo(functionInfo.name(), rangeValues, functionInfo.color(), functionInfo.weight(), functionInfo.visibleInLegend()));
        }

        drawLineGraph(title, series, domainValues, logarithmic, xAxisIsTime, xAxisLabel, yAxisLabel, showLegend);
    }


    public static void drawLineGraph(String title, List<Double> values, List<Long> times) {
        List<LineInfo> series = new ArrayList<>();
        series.add(new LineInfo("", values));
        List<Double> doubleTimes = new ArrayList<>();
        for (Long time : times) {
            doubleTimes.add(time.doubleValue());
        }
        drawLineGraph(title, series, doubleTimes, false, true, "", "", false);
    }

    public static void drawLineGraph(String title, List<LineInfo> lineInfoList, List<Double> times, boolean logarithmic, boolean xAxisIsTime, String xAxisLabel, String yAxisLabel, boolean showLegend) {

        var dataset = new XYSeriesCollection();
        var renderer = new XYLineAndShapeRenderer();

        int i = 0;
        for (LineInfo lineInfo : lineInfoList) {
            Iterator<Double> timeIterator = times.iterator();
            var series = new XYSeries(lineInfo.name());

            for (Double value : lineInfo.values()) {
                series.add(timeIterator.next(), value);
            }
            dataset.addSeries(series);

            renderer.setSeriesStroke(i, new BasicStroke(lineInfo.weight()));
            renderer.setSeriesShapesVisible(i, false);
            if (lineInfo.color() != null) {
                renderer.setSeriesPaint(i, lineInfo.color());
            }
            i++;
        }

        var chart = ChartFactory.createXYLineChart(title, xAxisLabel, yAxisLabel, dataset, PlotOrientation.VERTICAL, showLegend, false, false);
        XYPlot plot = chart.getXYPlot();

        plot.setRenderer(renderer);
        plot.setBackgroundPaint(Color.white);

        try {
            if (logarithmic) {
                plot.setRangeAxis(new LogarithmicAxis(""));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        if (xAxisIsTime) {
            plot.setDomainAxis(new DateAxis());
        }

        plot.getDomainAxis().setLowerMargin(0);
        plot.getDomainAxis().setUpperMargin(0);

        chart.setAntiAlias(true);

        try {
            ChartUtils.saveChartAsJPEG(new File("Graph of " + title + ".jpg"), chart, 1000, 500);
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public static void drawStackedAreaChart(HashMap<String, LinkedList<Double[]>> inputSeriesList, String title) {
        var dataset = new DefaultTableXYDataset();

        for (String seriesName : inputSeriesList.keySet()) {
            var series = new XYSeries(seriesName, false, false);
            LinkedList<Double[]> inputSeries = inputSeriesList.get(seriesName);
            for (Double[] point : inputSeries) {
                series.add(point[0], point[1]);
            }
            dataset.addSeries(series);
        }

        var chart = ChartFactory.createStackedXYAreaChart(title, "", "", dataset, PlotOrientation.VERTICAL, true, false, false);
        XYPlot plot = chart.getXYPlot();
        var renderer = new StackedXYAreaRenderer();
        for (int i=0; i<plot.getSeriesCount(); i++) {
            renderer.setSeriesStroke(i, new BasicStroke(0.5f));
        }

        plot.setRenderer(renderer);

        try {
            ChartUtils.saveChartAsJPEG(new File("Graph of " + title + ".jpg"), chart, 1000, 500);
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

}
