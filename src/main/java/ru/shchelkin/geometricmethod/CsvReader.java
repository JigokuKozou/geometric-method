package ru.shchelkin.geometricmethod;

import ru.shchelkin.geometricmethod.DataRow;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CsvReader {
    private String filePath;

    public CsvReader(String filePath) {
        this.filePath = filePath;
    }

    public List<DataRow> readData() {
        List<DataRow> dataRows = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean isFirstLine = true;
            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue; // skip header row
                }

                String[] values = line.split(",");
                DataRow dataRow = new DataRow(
                        values[0], // FileName
                        Integer.parseInt(values[1]), // HumanId
                        Integer.parseInt(values[2]), // WId
                        Integer.parseInt(values[3]), // WinS
                        Integer.parseInt(values[4]), // WinE
                        values[5].length(), // 1
                        values[6].length(), // 2
                        values[7].length(), // 3
                        values[8].length(), // 4
                        values[9].length(), // 5
                        values[10].length(), // 6
                        values[11].length(), // 7
                        values[12].length(), // 8
                        values[13].length(), // 9
                        values[14].length(), // 10
                        values[15].length(), // 11
                        values[16].length(), // 12
                        values[17].length(), // 13
                        values[18].length(), // 14
                        values[19].length(), // 15
                        values[20].length(), // 16
                        values[21].length(), // 17
                        values[22].length(), // 18
                        values[23].length()  // 19
                );
                dataRows.add(dataRow);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return dataRows;
    }


}
