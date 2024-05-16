package course_management_tests;

import org.junit.jupiter.api.Test;
import registration.system.course_management.schedule.Schedule;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ScheduleTest {
    @Test
    void testConstructor() {
        List<String> days = List.of("Monday", "Wednesday", "Friday");
        String startTime = "08:00";
        String endTime = "10:00";

        Schedule schedule = new Schedule(days, startTime, endTime);

        assertEquals(days, schedule.days());
        assertEquals(startTime, schedule.startTime());
        assertEquals(endTime, schedule.endTime());
    }

    @Test
    void testEqualsAndHashCode() {
        List<String> days1 = List.of("Monday", "Wednesday", "Friday");
        List<String> days2 = List.of("Tuesday", "Thursday");
        String startTime = "08:00";
        String endTime = "10:00";

        Schedule schedule1 = new Schedule(days1, startTime, endTime);
        Schedule schedule2 = new Schedule(days1, startTime, endTime);
        Schedule schedule3 = new Schedule(days2, startTime, endTime);

        assertEquals(schedule1, schedule2);
        assertNotEquals(schedule1, schedule3);

        assertEquals(schedule1.hashCode(), schedule2.hashCode());
        assertNotEquals(schedule1.hashCode(), schedule3.hashCode());
    }

    @Test
    void testToString() {
        List<String> days = List.of("Monday", "Wednesday", "Friday");
        String startTime = "08:00";
        String endTime = "10:00";

        Schedule schedule = new Schedule(days, startTime, endTime);

        String expectedString = "Schedule[days=[Monday, Wednesday, Friday], startTime=08:00, endTime=10:00]";
        assertEquals(expectedString, schedule.toString());
    }
}
