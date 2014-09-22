package be.kuleuven.cs.flexsim.io;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYSeries;

import be.kuleuven.cs.flexsim.view.Chartable;

import com.csvreader.CsvWriter;

/**
 * @author Kristof Coninx (kristof.coninx AT cs.kuleuven.be)
 *
 */
public class CSVWriter {
    private final String filename;
    private boolean headerWritten = false;

    public CSVWriter(String filename) {
        this.filename = filename;
    }

    public void writeCSV(Chartable c) {
        try {
            CsvWriter out = new CsvWriter(new FileWriter(filename, true), ',');
            if (!headerWritten) {
                // c.getSeries().get(1).
                out.write("time");
                for (int i = 1; i <= c.getSeries().size(); i++) {
                    out.write("data" + i);
                }
                out.endRecord();
            }
            SortedSet<Double> keys = new TreeSet<>();
            for (XYSeries s : c.getSeries()) {
                List<XYDataItem> items = s.getItems();
                for (XYDataItem i : items) {
                    keys.add(i.getX().doubleValue());
                }
            }
            for (Double k : keys) {
                out.write(k.toString());
                for (XYSeries s : c.getSeries()) {
                    List<XYDataItem> items = s.getItems();
                    boolean written = false;
                    for (XYDataItem i : items) {
                        if (i.getX().equals(k)) {
                            out.write(i.getY().toString());
                            written = true;
                        }
                    }
                    if (!written) {
                        out.write("null");
                    }
                }
                out.endRecord();
            }
            out.close();

        } catch (IOException e) {
            Logger.getLogger(CSVWriter.class).error(
                    "Error writing to CSV file", e.fillInStackTrace());
        }
    }
}
