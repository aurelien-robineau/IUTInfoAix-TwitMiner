import com.opencsv.CSVReader;

public class CSVToTransConverter {

    public CSVToTransConverter() {

    }

    public convertCSV() {
        CSVReader reader = new CSVReader(new FileReader("emps.csv"), ',');
    }
}
