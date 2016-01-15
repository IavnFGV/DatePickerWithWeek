package es.esy.iavnfgv.fx.component.datepicker;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Locale;

/**
 * Created by GFH on 23.12.2015.
 */
public class App extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        DatePickerWithWeek datePickerWithWeek = new DatePickerWithWeek(true, Locale.CANADA);
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(datePickerWithWeek, 300.0D, 275.0D));
//        datePickerWithWeek.setShowWeekGreed(false);
        primaryStage.show();
    }


}
