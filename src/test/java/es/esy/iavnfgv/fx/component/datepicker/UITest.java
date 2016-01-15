package es.esy.iavnfgv.fx.component.datepicker;

import javafx.scene.Scene;
import javafx.scene.control.RadioButton;
import javafx.stage.Stage;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testfx.framework.junit.ApplicationTest;

import java.text.DateFormatSymbols;
import java.util.List;
import java.util.Locale;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertTrue;

/**
 * Created by GFH on 15.01.2016.
 */
public class UITest extends ApplicationTest {

    protected static List<String> daysIds = asList("wdSn", "wdMo", "wdTu", "wdWd", "wdTh", "wdFr", "wdSt");
    private static Logger log = LoggerFactory.getLogger(UITest.class);
    DatePickerWithWeek datePickerWithWeek;

    @Override
    public void start(Stage stage) throws Exception {
        datePickerWithWeek = new DatePickerWithWeek();
        Scene scene = new Scene(datePickerWithWeek);
        stage.setScene(scene);
        stage.show();
    }

    @Test
    public void testDayNames() {
        String onButton;
        Locale locale = datePickerWithWeek.getLocale();
        log.info("locale = " + locale);
        DateFormatSymbols symbols = new DateFormatSymbols(locale);
        List<String> inCalendar = asList(symbols.getShortWeekdays());
        int i = 1;// zero is empty
        for (String id : daysIds) {
            onButton = lookup("#" + id).<RadioButton>queryFirst().getText();
            log.info("onButton:inCalendar = " + onButton + ":" + inCalendar.get(i));
            assertTrue(onButton.equals(inCalendar.get(i++)));
        }
    }


}
