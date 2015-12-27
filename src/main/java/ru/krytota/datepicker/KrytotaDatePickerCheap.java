package ru.krytota.datepicker;

import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import jfxtras.internal.scene.control.skin.CalendarTextFieldSkin;
import jfxtras.scene.control.CalendarTextField;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.ToDoubleBiFunction;

import static java.util.Arrays.asList;

/**
 * Created by GFH on 23.12.2015.
 */
public class KrytotaDatePickerCheap extends VBox implements Initializable {
    private static String CLASS_RADIO_BUTTON = "radio-button";
    private static String CLASS_TOGGLE_BUTTON = "toggle-button";

    @FXML
    protected GridPane daysPane;
    @FXML
    protected CalendarTextField datePicker;
    @FXML
    protected Button incButton;
    @FXML
    protected Button decButton;
    @FXML
    protected ToggleGroup weekDaysToggleGroup;

    private Consumer<RadioButton> changeStyleFromRadioButtonToToggleButton =
            radioButton -> {
                radioButton.getStyleClass().remove(CLASS_RADIO_BUTTON);
                radioButton.getStyleClass().add(CLASS_TOGGLE_BUTTON);
            };
    private Map<String, String> fxIdToCaptionMap = new HashMap<>();

    private Consumer<RadioButton> setCaptionById = radioButton ->
            radioButton.setText(fxIdToCaptionMap.getOrDefault
                    (radioButton.getId(), radioButton.getId())
            );
    private BooleanProperty showWeekGreed = new SimpleBooleanProperty(true);

    public KrytotaDatePickerCheap() {
        super();
        FXMLLoader fxmlLoader = new FXMLLoader(KrytotaDatePickerCheap.class.getResource(
                "/ru/krytota/datepicker/krytotaDatePickerCheap.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public CalendarTextField getDatePicker() {
        return datePicker;
    }

    public void setWeekdaysCaptions(List<String> weekDays) {
        fxIdToCaptionMap.put("wdMo", weekDays.get(0));
        fxIdToCaptionMap.put("wdTu", weekDays.get(1));
        fxIdToCaptionMap.put("wdWd", weekDays.get(2));
        fxIdToCaptionMap.put("wdTh", weekDays.get(3));
        fxIdToCaptionMap.put("wdFr", weekDays.get(4));
        fxIdToCaptionMap.put("wdSt", weekDays.get(5));
        fxIdToCaptionMap.put("wdSn", weekDays.get(6));
        daysPane.getChildren().stream()
                .filter(node -> (node instanceof RadioButton))
                .map(node -> (RadioButton) node)
                .forEach(node -> {
                    setCaptionById.accept(node);
                });

    }

    @FXML
    protected void decDayHandle() {
        if (datePicker.getCalendar() == null) return;
        Calendar calendar = (Calendar) datePicker.getCalendar().clone();
        calendar.add(Calendar.DATE, -1);
        datePicker.setCalendar(calendar);
    }

    @FXML
    protected void incDayHandle() {
        if (datePicker.getCalendar() == null) return;
        Calendar calendar = (Calendar) datePicker.getCalendar().clone();
        calendar.add(Calendar.DATE, +1);
        datePicker.setCalendar(calendar);
    }

    @FXML
    protected void weekdayHandle() {
        Calendar newCalendar = new GregorianCalendar();

        int weekNumber = newCalendar.get(Calendar.WEEK_OF_YEAR);
        int yearNumber = newCalendar.getWeekYear();

        if (datePicker.getCalendar() != null) {
            newCalendar = (Calendar) datePicker.getCalendar().clone();
            weekNumber = newCalendar.get(Calendar.WEEK_OF_YEAR);
            yearNumber = newCalendar.getWeekYear();
        }
        Node node = (Node) weekDaysToggleGroup.getSelectedToggle();
        int newday = idToDayConstant(node.getId());
        newCalendar.setWeekDate(yearNumber, weekNumber, newday);
        datePicker.setCalendar(newCalendar);
    }

    private int idToDayConstant(String id) {
        switch (id) {
            case "wdMo": {
                return Calendar.MONDAY;
            }
            case "wdTu": {
                return Calendar.TUESDAY;
            }
            case "wdWd": {
                return Calendar.WEDNESDAY;
            }
            case "wdTh": {
                return Calendar.THURSDAY;
            }
            case "wdFr": {
                return Calendar.FRIDAY;
            }
            case "wdSt": {
                return Calendar.SATURDAY;
            }
            case "wdSn": {
                return Calendar.SUNDAY;
            }
            default: {
                throw new IllegalArgumentException("Cant match id to day for id = [" + id + "]");
            }
        }
    }

    @FXML
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        prepareWeekdays();

        datePicker.calendarProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) return;
            int newDay = newValue.get(Calendar.DAY_OF_WEEK);
            Node node = daysPane.lookup("#" + dayConstantToId(newDay));
            Platform.runLater(() -> weekDaysToggleGroup.selectToggle((Toggle) node));
         });
        daysPane.visibleProperty().bind(showWeekGreedProperty());
    }

    protected void prepareWeekdays() {
        daysPane.getChildren().stream()
                .filter(node -> (node instanceof RadioButton))
                .map(node -> (RadioButton) node)
                .forEach(node -> {
                    changeStyleFromRadioButtonToToggleButton.accept(node);
                    setCaptionById.accept(node);
                });
    }

    private String dayConstantToId(int day) {
        switch (day) {
            case Calendar.MONDAY: {
                return "wdMo";
            }
            case Calendar.TUESDAY: {
                return "wdTu";
            }
            case Calendar.WEDNESDAY: {
                return "wdWd";
            }
            case Calendar.THURSDAY: {
                return "wdTh";
            }
            case Calendar.FRIDAY: {
                return "wdFr";
            }
            case Calendar.SATURDAY: {
                return "wdSt";
            }
            case Calendar.SUNDAY: {
                return "wdSn";
            }
            default: {
                throw new IllegalArgumentException("Cant match id to day for day = [" + day + "]");
            }
        }
    }

    public BooleanProperty showWeekGreedProperty() {
        return showWeekGreed;
    }

    public void setDatePattern(String pattern) {
        datePicker.setDateFormat(new SimpleDateFormat(pattern));
    }

    public boolean getShowWeekGreed() {
        return showWeekGreed.get();
    }

    public void setShowWeekGreed(boolean showWeekGreed) {
        this.showWeekGreed.set(showWeekGreed);
    }
}

