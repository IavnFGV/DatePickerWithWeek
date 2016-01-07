package ru.krytota.datepicker;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import jfxtras.scene.control.CalendarTextField;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Consumer;

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
    private BooleanProperty showWeekGreed = new SimpleBooleanProperty(true);


    public KrytotaDatePickerCheap(boolean showWeekGreed) {
        this();
        setShowWeekGreed(showWeekGreed);
    }

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
            node.requestFocus();
            Platform.runLater(() -> weekDaysToggleGroup.selectToggle((Toggle) node));
        });
        daysPane.visibleProperty().bind(showWeekGreedProperty());
    }

    protected void prepareWeekdays() {
        DateFormatSymbols symbols = new DateFormatSymbols(getCalendarLocale());
        List<String> dayNames = asList(symbols.getShortWeekdays()); // size 8, so sublist from 1 to 8
        setWeekdaysCaptions(dayNames.subList(1,dayNames.size()));
        daysPane.getChildren().stream()
                .filter(node -> (node instanceof RadioButton))
                .map(node -> (RadioButton) node)
                .forEach(node -> {
                    changeStyleFromRadioButtonToToggleButton.accept(node);
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

    //oneplace to get Locale
    private Locale getCalendarLocale() {
        return datePicker.getLocale();
    }

    public void setWeekdaysCaptions(List<String> weekDays) {
        if (weekDays == null) {
            throw new IllegalArgumentException("weekDays list can not be null");
        }
        if (weekDays.size() != 7) {
            throw new IllegalArgumentException("weekDays list must have size of 7");
        }
        ((RadioButton) daysPane.lookup("#wdMo")).setText(weekDays.get(1));
        ((RadioButton) daysPane.lookup("#wdTu")).setText(weekDays.get(2));
        ((RadioButton) daysPane.lookup("#wdWd")).setText(weekDays.get(3));
        ((RadioButton) daysPane.lookup("#wdTh")).setText(weekDays.get(4));
        ((RadioButton) daysPane.lookup("#wdFr")).setText(weekDays.get(5));
        ((RadioButton) daysPane.lookup("#wdSt")).setText(weekDays.get(6));
        ((RadioButton) daysPane.lookup("#wdSn")).setText(weekDays.get(0));
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

