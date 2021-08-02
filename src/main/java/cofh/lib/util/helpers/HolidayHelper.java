package cofh.lib.util.helpers;

import java.time.LocalDate;
import java.time.MonthDay;

import static cofh.lib.util.helpers.HolidayHelper.Holiday.CHRISTMAS;

/**
 * The class contains helper functions related to Holidays!
 * <p>
 * Yes, they are US-centric. Feel free to suggest others!
 *
 * @author King Lemming
 */
public final class HolidayHelper {

    private HolidayHelper() {

    }

    public static boolean isChristmas(int daysBefore, int daysAfter) {

        return holidayCheck(CHRISTMAS, daysBefore, daysAfter);
    }

    public static boolean holidayCheck(Holiday holiday, int daysBefore, int daysAfter) {

        LocalDate dateHoliday = LocalDate.now();
        holiday.getDate().adjustInto(dateHoliday);

        LocalDate dateBefore = dateHoliday.minusDays(daysBefore);
        LocalDate dateAfter = dateHoliday.plusDays(daysAfter);

        return !(dateHoliday.isBefore(dateBefore) && dateHoliday.isAfter(dateAfter));

    }

    // region HOLIDAYS
    public enum Holiday {

        NEW_YEAR(1, 1),
        VALENTINES(2, 14),
        LEAP(2, 29),
        ST_PATRICKS(3, 17),
        APRIL_FOOLS(4, 1),
        EARTH(4, 22),
        US_INDEPENDENCE(7, 4),
        HALLOWEEN(10, 31),
        DAY_OF_DEAD(11, 1),
        VETERANS(11, 11),
        CHRISTMAS(12, 25),
        BOXING_DAY(12, 26),
        NEW_YEARS_EVE(12, 31);

        protected final MonthDay date;

        Holiday(int month, int dayOfMonth) {

            this.date = MonthDay.of(month, dayOfMonth);
        }

        MonthDay getDate() {

            return date;
        }
    }
    // endregion
}
