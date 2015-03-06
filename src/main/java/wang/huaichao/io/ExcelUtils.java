package wang.huaichao.io;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

/**
 * Created by Administrator on 2015/2/27.
 */
public class ExcelUtils {
    public static void main(String[] args) throws IOException, BiffException, WriteException {
        String oldfile = "e:\\tmp\\disc.xls";
        String newfile = "e:\\tmp\\disc2.xls";

        Workbook oldbook = Workbook.getWorkbook(new File(oldfile));
        Sheet oldsheet = oldbook.getSheet(0);

        WritableWorkbook newbook = Workbook.createWorkbook(new File(newfile));
        WritableSheet newsheet = newbook.createSheet("xxx", 0);

        int rows = oldsheet.getRows();
        int cols = oldsheet.getColumns();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                String contents = oldsheet.getCell(j, i).getContents();
                newsheet.addCell(new Label(j, i, contents));
            }
            if (i == 0) continue;
            newsheet.addCell(new Label(cols, i, getType(
                    Integer.valueOf(oldsheet.getCell(2, i).getContents()),
                    Integer.valueOf(oldsheet.getCell(3, i).getContents()),
                    Integer.valueOf(oldsheet.getCell(4, i).getContents()),
                    Integer.valueOf(oldsheet.getCell(5, i).getContents())
            )));
        }
        newbook.write();
        newbook.close();
    }

    private static class KV {
        public char k;
        public int v;

        public KV(char k, int v) {
            this.k = k;
            this.v = v;
        }
    }

    public static String getType(int d, int i, int s, int c) {
        final ArrayList<KV> kvs = new ArrayList<KV>();
        if (d > 1) kvs.add(new KV('d', d));
        if (i > 0) kvs.add(new KV('i', i));
        if (s > 0) kvs.add(new KV('s', s));
        if (c > -2) kvs.add(new KV('c', c));


        if (kvs.size() == 0) return "-";

        Collections.sort(kvs, new Comparator<KV>() {
            @Override
            public int compare(KV o1, KV o2) {
                return o2.v - o1.v;
            }
        });

        String ret = "";
        ret += kvs.get(0).k;
        if (kvs.size() > 1)
            ret += kvs.get(1).k;
        return ret;
    }
}
