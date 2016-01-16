package es.esy.iavnfgv.fx.component.datepicker;

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
public class DatePickerWithWeek extends VBox implements Initializable {
    private static final String DEF_STYLE_CLASS = "date-picker-with-week";
    private static String CLASS_RADIO_BUTTON = "radio-button";
    private static String CLASS_TOGGLE_BUTTON = "toggle-button";
    private static Map<String, Integer> idToDayConstantMap = new HashMap<>();
    private static Map<Integer, String> dayConstantToIdMap = new HashMap<>();

    static {
        idToDayConstantMap.put("wdMo", Calendar.MONDAY);
        idToDayConstantMap.put("wdTu", Calendar.TUESDAY);
        idToDayConstantMap.put("wdWd", Calendar.WEDNESDAY);
        idToDayConstantMap.put("wdTh", Calendar.THURSDAY);
        idToDayConstantMap.put("wdFr", Calendar.FRIDAY);
        idToDayConstantMap.put("wdSt", Calendar.SATURDAY);
        idToDayConstantMap.put("wdSn", Calendar.SUNDAY);

        dayConstantToIdMap.put(Calendar.MONDAY, "wdMo");
        dayConstantToIdMap.put(Calendar.TUESDAY, "wdTu");
        dayConstantToIdMap.put(Calendar.WEDNESDAY, "wdWd");
        dayConstantToIdMap.put(Calendar.THURSDAY, "wdTh");
        dayConstantToIdMap.put(Calendar.FRIDAY, "wdFr");
        dayConstantToIdMap.put(Calendar.SATURDAY, "wdSt");
        dayConstantToIdMap.put(Calendar.SUNDAY, "wdSn");
    }

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
    private int curWeekDay;

    public DatePickerWithWeek(boolean showWeekGreed) {
        this(showWeekGreed, null);
    }

    public DatePickerWithWeek(boolean showWeekGreed, Locale locale) {
        this();
        setShowWeekGreed(showWeekGreed);
        if (locale != null) {
            setLocale(locale);
        }
    }

    public DatePickerWithWeek() {
        super();
        FXMLLoader fxmlLoader = new FXMLLoader(DatePickerWithWeek.class.getResource(
                "/es/esy/iavnfgv/fx/component/datepicker/datePickerWithWeek.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        fxmlLoader.setClassLoader(DatePickerWithWeek.class.getClassLoader());
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        getStyleClass().add(DEF_STYLE_CLASS);
    }

    @Override
    public String getUserAgentStylesheet() {
        return getClass().getResource("datePickerWithWeek.css").toExternalForm();
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
        Integer day = idToDayConstantMap.get(id);
        if (day == null) {
            throw new IllegalArgumentException("Cant match id to day for id = [" + id + "]");
        }
        return day;
    }

    @FXML
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        putDayNames();
        datePicker.calendarProperty().addListener((observable, oldValue, newValue) -> {
            int newDay;
            int datepickerWeek;
            Calendar curCalendar = new GregorianCalendar();
            int curWeek = curCalendar.get(Calendar.WEEK_OF_YEAR);
            Node node = null;
            if (newValue == null) {
                datepickerWeek = curWeek;
            } else {
                newDay = newValue.get(Calendar.DAY_OF_WEEK);
                datepickerWeek = newValue.get(Calendar.WEEK_OF_YEAR);
                node = daysPane.lookup("#" + dayConstantToId(newDay));
                node.requestFocus();
            }
            weekDaysToggleGroup.selectToggle((Toggle) node);
            this.curWeekDay = curCalendar.get(Calendar.DAY_OF_WEEK);

            // TODO REPLACE WITH CORRECT LOGIC or make pseudoclass
            Node todayNode = daysPane.lookup("#" + dayConstantToId(this.curWeekDay));
            if (curWeek == datepickerWeek) {
                markTodayDay(todayNode);
            } else {
                unMarkTodayDay(todayNode);
            }
        });
        datePicker.localeProperty().addListener((observable, oldValue, newValue) -> {
                    putDayNames();
                }
        );
        daysPane.getChildren().stream()
                .filter(node -> (node instanceof RadioButton))
                .map(node -> (RadioButton) node)
                .forEach(node -> {
                    changeStyleFromRadioButtonToToggleButton.accept(node);
                });
        daysPane.visibleProperty().bind(showWeekGreedProperty());
        Calendar curCalendar = new GregorianCalendar();
        this.curWeekDay = curCalendar.get(Calendar.DAY_OF_WEEK);
        Node todayNode = daysPane.lookup("#" + dayConstantToId(this.curWeekDay));
        markTodayDay(todayNode);
    }

    protected void putDayNames() {
        DateFormatSymbols symbols = new DateFormatSymbols(getCalendarLocale());
        List<String> dayNames = asList(symbols.getShortWeekdays()); // size 8, so sublist from 1 to 8
        setWeekdaysCaptions(dayNames.subList(1, dayNames.size()));
    }

    private String dayConstantToId(int day) {
        String id = dayConstantToIdMap.get(day);
        if (id == null) {
            throw new IllegalArgumentException("Cant match day to id for day = [" + day + "]");
        }
        return id;
    }

    protected void markTodayDay(Node todayNode) {
        if (!todayNode.getStyleClass().contains("today")) {
            todayNode.getStyleClass().add("today");
        }
    }

    protected void unMarkTodayDay(Node todayNode) {
        if (todayNode.getStyleClass().contains("today")) {
            todayNode.getStyleClass().remove("today");
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

    public Locale getLocale() {
        return datePicker.getLocale();
    }

    public void setLocale(Locale locale) {
        datePicker.setLocale(locale);
    }
}

