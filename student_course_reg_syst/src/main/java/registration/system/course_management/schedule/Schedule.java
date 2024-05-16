package registration.system.course_management.schedule;

import java.util.List;

public record Schedule(List<String> days, String startTime, String endTime) {}
