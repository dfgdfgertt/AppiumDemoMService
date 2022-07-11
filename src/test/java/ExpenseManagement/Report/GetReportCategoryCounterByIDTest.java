package ExpenseManagement.Report;

import ExpenseManagement.AbstractExpenseManagementTest;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class GetReportCategoryCounterByIDTest extends AbstractExpenseManagementTest {

    @Test
    public void demo(){
        List<String> listMonths = new ArrayList<>();
        String str_date = "2021-11-02 11:07:49";
        String end_date = "2022-07-01 11:07:49";
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startDate = LocalDateTime.parse(str_date, myFormatObj);
        LocalDateTime endDate = LocalDateTime.parse(end_date, myFormatObj);
        while (startDate.getYear() != endDate.getYear() || startDate.getMonth() != endDate.getMonth().plus(1)){
            listMonths.add(startDate.format(DateTimeFormatter.ofPattern("MM/yyyy")));
            startDate = startDate.plusMonths(1);
        }
    }

}
