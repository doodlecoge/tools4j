package wang.huaichao.ds;

import java.util.List;

/**
 * Created by Administrator on 2015/3/6.
 */
public class DataTableTest {
    public static void main(String[] args) {
        DataTable dt = new DataTable("id", "name", "age");

        DataTable.DataRow row = dt.newRow();
        row.add("name", "huaichao");
        row.add(0, 1);
        row.add(2, 20);

        List<DataTable.DataRow> rows = dt.getRows();
        for (DataTable.DataRow dataRow : rows) {
            System.out.println(dataRow);
        }
    }
}
