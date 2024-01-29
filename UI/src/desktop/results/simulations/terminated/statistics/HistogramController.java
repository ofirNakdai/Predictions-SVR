package desktop.results.simulations.terminated.statistics;

import engineAnswers.HistogramDTO;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HistogramController {
    @FXML
    private BarChart<String, Number> barChart;

    @FXML
    private CategoryAxis xAxis;

    @FXML
    private NumberAxis yAxis;
    public void setDataFromDTO(HistogramDTO histogramDTO) {

        XYChart.Series<String, Number> series = new XYChart.Series<>();

        //histogramDTO.getPropertyHistogram().entrySet().stream().sorted().collect(Collectors.toList()).forEach(entry ->
                //series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue())));
        for(Map.Entry<String, Long> entry : histogramDTO.getPropertyHistogram().entrySet())
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));


        // Add the series to the BarChart
        barChart.getData().add(series);
    }

}
