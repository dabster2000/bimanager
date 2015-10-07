package dk.trustworks.bimanager.web.widgets;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.*;
import dk.trustworks.bimanager.client.RestClient;
import dk.trustworks.bimanager.dto.ProjectYearEconomy;
import dk.trustworks.bimanager.dto.User;
import dk.trustworks.bimanager.service.ProjectBudgetService;

import java.text.DateFormatSymbols;
import java.util.List;

/**
 * Created by hans on 23/09/15.
 */
@SuppressWarnings("serial")
public class ClientRevenueStatus {

    public static Chart getChart() {

        final Chart chart = new Chart(ChartType.COLUMN);
        chart.setId("chart");

        final Configuration conf = chart.getConfiguration();

        conf.setTitle("Browser market share, April, 2011");
        conf.setSubTitle("Click the columns to view versions. Click again to view brands.");
        conf.getLegend().setEnabled(false);

        XAxis x = new XAxis();
        x.setType(AxisType.CATEGORY);
        conf.addxAxis(x);

        YAxis y = new YAxis();
        y.setTitle("Total percent market share");
        conf.addyAxis(y);

        PlotOptionsColumn column = new PlotOptionsColumn();
        column.setCursor(Cursor.POINTER);
        column.setDataLabels(new Labels(true));
        column.getDataLabels().setFormatter("this.y + ' kr");

        conf.setPlotOptions(column);

        Tooltip tooltip = new Tooltip();
        tooltip.setHeaderFormat("{series.name}");
        tooltip.setPointFormat("{point.name}: {point.y:.2f} kr");
        conf.setTooltip(tooltip);

        DataSeries series = new DataSeries();
        series.setName("Indtjening ");
        PlotOptionsColumn plotOptionsColumn = new PlotOptionsColumn();
        plotOptionsColumn.setColorByPoint(true);
        series.setPlotOptions(plotOptionsColumn);

        RestClient restClient = new RestClient();

        for (User user : restClient.getUsers()) {
            Number[] realized = {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
            double total = 0.0;
            List<ProjectYearEconomy> economies = new ProjectBudgetService().getProjectYearEconomies(System.currentTimeMillis(), 2015, user.getUUID(), true);
            System.out.println("economies = " + economies);
            for (ProjectYearEconomy projectYearActual : economies) {
                for (int i = 0; i < projectYearActual.getActual().length; i++) {
                    double d = projectYearActual.getActual()[i];
                    realized[i] = realized[i].doubleValue()+d;
                    total += d;
                }
            }
            DataSeriesItem item = new DataSeriesItem(user.getLastname(), total);
            DataSeries drillSeries = new DataSeries(user.getLastname()+" by month");
            drillSeries.setId(user.getUUID());
            String[] categories = new DateFormatSymbols().getMonths();
            Number[] ys = realized;
            drillSeries.setData(categories, ys);
            series.addItemWithDrilldown(item, drillSeries);
        }


/*
        item = new DataSeriesItem("Firefox", 21.63);
        drillSeries = new DataSeries("Firefox versions");
        drillSeries.setId("Firefox");
        categories = new String[] { "Firefox 2.0", "Firefox 3.0",
                "Firefox 3.5", "Firefox 3.6", "Firefox 4.0" };
        ys = new Number[] { 0.20, 0.83, 1.58, 13.12, 5.43 };
        drillSeries.setData(categories, ys);
        series.addItemWithDrilldown(item, drillSeries);

        item = new DataSeriesItem("Chrome", 11.94);
        drillSeries = new DataSeries("Chrome versions");
        drillSeries.setId("Chrome");
        categories = new String[] { "Chrome 5.0", "Chrome 6.0", "Chrome 7.0",
                "Chrome 8.0", "Chrome 9.0", "Chrome 10.0", "Chrome 11.0",
                "Chrome 12.0" };
        ys = new Number[] { 0.12, 0.19, 0.12, 0.36, 0.32, 9.91, 0.50, 0.22 };
        drillSeries.setData(categories, ys);
        series.addItemWithDrilldown(item, drillSeries);

        item = new DataSeriesItem("Safari", 7.15);
        drillSeries = new DataSeries("Safari versions");
        drillSeries.setId("Safari");
        categories = new String[] { "Safari 5.0", "Safari 4.0",
                "Safari Win 5.0", "Safari 4.1", "Safari/Maxthon", "Safari 3.1",
                "Safari 4.1" };
        ys = new Number[] { 4.55, 1.42, 0.23, 0.21, 0.20, 0.19, 0.14 };
        drillSeries.setData(categories, ys);
        series.addItemWithDrilldown(item, drillSeries);

        item = new DataSeriesItem("Opera", 2.14);
        drillSeries = new DataSeries("Opera versions");
        drillSeries.setId("Opera");
        categories = new String[] { "Opera 9.x", "Opera 10.x", "Opera 11.x" };
        ys = new Number[] { 0.12, 0.37, 1.65 };
        drillSeries.setData(categories, ys);
        series.addItemWithDrilldown(item, drillSeries);*/
        conf.addSeries(series);

        return chart;

    }
}
