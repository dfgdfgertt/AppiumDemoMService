package ExpenseManagement.Report;

import ExpenseManagement.AbstractExpenseManagementTest;
import org.testng.annotations.Test;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GetReportCategoryCounterByIDTest extends AbstractExpenseManagementTest {

    @Test
    public void demo() throws ParseException {
        List<Date> dates = new ArrayList<>();

        String str_date = "2021-11-02 11:07:49";
        String end_date = "2022-07-01 11:07:49";
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime startDate = LocalDateTime.parse(str_date, myFormatObj);
        LocalDateTime endDate = LocalDateTime.parse(end_date, myFormatObj);
        while (!endDate.equals(startDate)){
            System.out.println(endDate.format(myFormatObj));
            endDate.plusMonths(1);
        }
    }

}
