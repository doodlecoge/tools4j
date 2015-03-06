package wang.huaichao.ds;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/3/6.
 */
public class DataTable {
    private Map<String, Integer> nameIdxMap = new HashMap<String, Integer>();
    private Map<Integer, String> idxNameMap = new HashMap<Integer, String>();
    private List<DataRow> rows = new ArrayList<DataRow>();

    public DataTable(int cols) {
        for (int i = 0; i < cols; i++) {
            nameIdxMap.put(i + "", i);
            idxNameMap.put(i, i + "");
        }
    }

    public DataTable(String... colNames) {
        int i = 0;
        for (String colName : colNames) {
            nameIdxMap.put(colName, i);
            idxNameMap.put(i, colName);
            i++;
        }
    }

    public DataRow newRow() {
        DataRow row = new DataRow(this);
        this.rows.add(row);
        return row;
    }

    public DataRow getRow(int index) {
        return rows.get(index);
    }

    public List<DataRow> getRows() {
        return rows;
    }

    public static final class DataRow {
        private Object[] values;

        private Map<String, Integer> nameIdxMap;
        private Map<Integer, String> idxNameMap;


        private DataRow(DataTable table) {
            nameIdxMap = table.nameIdxMap;
            idxNameMap = table.idxNameMap;
            values = new Object[nameIdxMap.size()];
        }

        public void add(int index, Object value) {
            values[index] = value;
        }

        public void add(String colName, Object value) {
            add(nameIdxMap.get(colName), value);
        }

        public Object get(int index) {
            return values[index];
        }

        public Object get(String colName) {
            return get(nameIdxMap.get(colName));
        }

        public String getString(int index) {
            return get(index).toString();
        }

        public String getString(String colName) {
            return get(colName).toString();
        }

        public String toString() {
            int len = idxNameMap.size();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < len; i++) {
                if (i != 0) sb.append(",");
                sb.append(idxNameMap.get(i) + ":" + get(i).toString());
            }
            return sb.toString();
        }
    }
}
