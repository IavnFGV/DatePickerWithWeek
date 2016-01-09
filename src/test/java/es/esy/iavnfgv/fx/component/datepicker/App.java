package es.esy.iavnfgv.fx.component.datepicker;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Created by GFH on 23.12.2015.
 */
public class App extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {

        DatePickerWithWeek datePickerWithWeek = new DatePickerWithWeek();
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(datePickerWithWeek, 300.0D, 275.0D));
//        datePickerWithWeek.setShowWeekGreed(false);
        primaryStage.show();


    }


}
