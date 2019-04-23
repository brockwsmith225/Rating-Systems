package interpreter.datatypes;

public class Time implements Comparable<Time> {
    private static final long YEAR_TO_MONTHS = 12;
    private static final long YEAR_TO_WEEKS = 52;
    private static final long YEAR_TO_DAYS = 365;
    private static final long[] MONTH_TO_DAYS = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    private static final long WEEK_TO_DAYS = 7;
    private static final long DAY_TO_HOURS = 24;
    private static final long HOUR_TO_MINUTES = 60;
    private static final long MINUTE_TO_SECONDS = 60;
    private static final long SECOND_TO_MILLISECONDS = 1000;

    private long milliseconds;

    /**
     * Creates a new Time object with the milliseconds passed since 1970-01-01 0:00:00.000
     *
     * @param milliseconds the time in milliseconds since 1970-01-01 0:00:00.000
     */
    public Time(long milliseconds) {
        this.milliseconds = milliseconds;
    }

    /**
     * Creates a new Time object at the specified date
     *
     * @param date the date of the Time object
     * @param month the month of the Time object
     * @param year the year of the Time object
     */
    public Time(long date, long month, long year) {
        long days = (year - 1970) * YEAR_TO_DAYS + MONTH_TO_DAYS[(int)month-1] + numberOfLeapYears(1970, year) + daysInCurrentYear(date, month, year);
        this.milliseconds = days * DAY_TO_HOURS * HOUR_TO_MINUTES * MINUTE_TO_SECONDS * SECOND_TO_MILLISECONDS;
    }

    /**
     * Creates a new Time object at the specified time and date
     *
     * @param second the second of the Time object
     * @param minute the minute of the Time object
     * @param hour the hour of the Time object
     * @param date the date of the Time object
     * @param month the month of the Time object
     * @param year the year of the Time object
     */
    public Time(long second, long minute, long hour, long date, long month, long year) {
        this(date, month, year);
        this.milliseconds += (((hour * HOUR_TO_MINUTES) + minute) * MINUTE_TO_SECONDS + second) * SECOND_TO_MILLISECONDS;
    }

    /**
     * Increments the Time object by milliseconds
     *
     * @param milliseconds the number of milliseconds to increment the Time object
     */
    public void increment(long milliseconds) {
        this.milliseconds += milliseconds;
    }

    /**
     * Increments the Time object by days
     *
     * @param days the number of days to increment the Time object
     */
    public void incrementByDays(long days) {
        increment(days * DAY_TO_HOURS * HOUR_TO_MINUTES * MINUTE_TO_SECONDS * SECOND_TO_MILLISECONDS);
    }

    /**
     * Gets the time between the start time and this time in milliseconds
     *
     * @param start the start time
     * @return the time since the start time
     */
    public long timeSince(Time start) {
        return this.milliseconds = start.milliseconds;
    }

    /**
     * Gets the days between the start time and this time
     *
     * @param start the start time
     * @return the days since the start time
     */
    public long daysSince(Time start) {
        long timeSince = timeSince(start);
        return timeSince / (DAY_TO_HOURS * HOUR_TO_MINUTES * MINUTE_TO_SECONDS * SECOND_TO_MILLISECONDS);
    }

    /**
     * Returns the day of the week of the date
     *
     * @return the integer representing the day of the week (Monday = 0,..., Sunday = 6)
     */
    public int dayOfTheWeek() {
        Time d = new Time(7, 1, 2019);
        int days = (int)this.daysSince(d);
        return (days % 7) < 0 ? (days % 7) + 7 : days % 7;
    }

    @Override
    public int compareTo(Time d) {
        return (int)this.daysSince(d);
    }

    @Override
    public String toString() {
        return "";
    }

    private long numberOfLeapYears(long startYear, long endYear) {
        long numberOfLeapYears = 0;
        for (long year = startYear; year < endYear; year++) {
            if (isLeapYear(year)) numberOfLeapYears++;
        }
        return numberOfLeapYears;
    }

    private static boolean isLeapYear(long year) {
        return (year % 4 == 0 && !(year % 100 == 0)) || (year % 400 == 0);
    }

    private static long daysInCurrentYear(long date, long month, long year) {
        long daysInCurrentYear = date;
        for (long m = 1; m < month; m++) {
            daysInCurrentYear += daysInMonth(month, year);
        }
        return daysInCurrentYear;
    }

    private static long daysInMonth(long month, long year) {
        if (isLeapYear(year) && month == 2) {
            return 29;
        }
        return MONTH_TO_DAYS[(int)month-1];
    }
}

