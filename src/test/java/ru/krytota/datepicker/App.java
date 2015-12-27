package ru.krytota.datepicker;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.security.auth.login.AppConfigurationEntry;
import java.applet.Applet;

/**
 * Created by GFH on 23.12.2015.
 */
public class App extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {

        KrytotaDatePickerCheap krytotaDatePickerCheap = new KrytotaDatePickerCheap();
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(krytotaDatePickerCheap, 300.0D, 275.0D));
        krytotaDatePickerCheap.setShowWeekGreed(false);
        primaryStage.show();
    }


}
