package org.example.task3;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.example.task3.connfactory.ConnFactory;

import java.io.*;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Task3 {
    private static final ConnFactory cf = ConnFactory.getInstance();

    public static void main(String[] args) throws SQLException, IOException, CsvValidationException {


        /**
         *
         * Скрипты SQL для создания баз данных находятся в task3.postgres.ddl
         * Первая загрузка logins.csv в базу данных
         *
         */

        //LoadLogins();

        /**
         *
         *
         * Первая загрузка postings.csv в базу данных
         *
         */

        alterPosting();

    }

    /**
     *
     * Загрузка logins.csv в БД происходит через копирование содержания файла в таблицу
     *
     */

    private static void LoadLogins() throws IOException, SQLException {
        Connection conn = cf.getConnection();
        String root = "'" + System.getProperty("user.dir")  + "\\logins.csv'"; //Получаем путь до файла в папке
        String sql =
                "COPY logins " +
                " FROM " + root +
                " DELIMITERS ','  "+
                " CSV HEADER; ";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.executeUpdate();
        conn.close();
    }

    /**
     *
     * Файл postings.csv был размечен с ; из-за чего, если открывать его средствами Excel, то таблица сбивается
     * Хотя в IDE файл выглядит нормально, при считывании через readNext() данные багаются и так же сбиваются.
     * Из-за этого, было решено считывать построчно, разбивая по ; , а после разбивать и добавлять в матрицу.
     * В матрице после этого добавляем в каждую строку дополнительный столбец под поля Authorized Posting
     *
     */
    private static void alterPosting() throws IOException, CsvValidationException, SQLException {

        Map<String, Boolean> loginStatus = new HashMap<>();  //Создадим карту для пары Логин + Статус

        CSVReader LoginsReader = new CSVReader(new FileReader("logins.csv"));
        String [] nextLine;
        LoginsReader.readNext();
        while ((nextLine = LoginsReader.readNext()) != null) {           //Запишем пару в карту, без символов \t
            loginStatus.put(nextLine[1].replaceAll("\\s+", ""),
                    Boolean.valueOf(nextLine[2].replaceAll("\\s+", "")));
        }
        LoginsReader.close();

        // Здесь была возможность создания массива из сущностей Posting вместо матрицы

        ArrayList<String[]> Matrix = new ArrayList<>();               //Объявляем матрицу для хранения массивов строк
        CSVReader PostingsReader = new CSVReader(new FileReader("postings.csv"));

        while ((nextLine = PostingsReader.readNext()) != null) {
            String[] line = Arrays.toString(nextLine).split(";");
            if(line.length > 1) {
                for(int i = 0; i < line.length; i++) {
                    line[i] = line[i].replaceAll("\t",""); //Удаляем все \t символы
                }
                line[0] = line[0].replace("[","");    //Удаляем ненужные символы из строк в начале
                line[9] = line[9].replace("]","");    //И в конце
                line[7] = line[7].replaceAll(" ", "");  // Удаляем лишний символ пробела в колонке цены
                Matrix.add(line); //Добавляем массив в матрицу
            }
        }
        PostingsReader.close();

        for(int i = 0; i < Matrix.size(); i++) {
            String[] line = Matrix.get(i);
            if(i == 0) {
                line = addColumnToLine(line, "authorized posting"); //Добавляем в 0 индекс имя колонки для удобства
            } else {
                if (loginStatus.containsKey(line[9]) && loginStatus.get(line[9])){ //Проверяем Activity Status
                    line = addColumnToLine(line, "true");                     //Сравнивая с значениями в карте
                } else {
                    line = addColumnToLine(line, "false");
                }
            }
            Matrix.set(i,line);
        }
        Connection conn = cf.getConnection();
        for(int i = 1; i < Matrix.size(); i++) {  // Перевод всех значений в БД
            String[] array = Matrix.get(i); //Заполнение построчно
            String sqlDate2 = null;
            String sqlDate3 = null;
            try {
                sqlDate2 = new SimpleDateFormat("yyyy-MM-dd")           //Перевод даты из dd.MM.yyyy в формат
                        .format(new SimpleDateFormat("dd.MM.yyyy").parse(array[2]));  //Приемлемый для sql.Date
                sqlDate3 = new SimpleDateFormat("yyyy-MM-dd")
                        .format(new SimpleDateFormat("dd.MM.yyyy").parse(array[3]));
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            String sql = "INSERT INTO postings VALUES (?,?,?,?,?,?,?,?,?,?,?)";

            PreparedStatement ps = conn.prepareStatement(sql); //Вводим все необходимые значения из матрицы

            ps.setString(1, array[0]);
            ps.setInt(2, Integer.parseInt(array[1]));
            ps.setDate(3, Date.valueOf(sqlDate2));
            ps.setDate(4, Date.valueOf(sqlDate3));
            ps.setString(5, array[4]);
            ps.setInt(6, Integer.parseInt(array[5]));
            ps.setString(7, array[6]);
            ps.setDouble(8,Double.parseDouble(array[7].replaceAll(",", ".")));
            ps.setString(9,array[8]);
            ps.setString(10,array[9]);
            ps.setBoolean(11, Boolean.parseBoolean(array[10]));
            ps.executeUpdate();
        }
        conn.close();
    }


    private static String[] addColumnToLine(String[] line, String data) {
        String[] newLine = new String[line.length + 1];
        System.arraycopy(line, 0, newLine, 0, line.length);
        newLine[line.length] = data;
        return newLine;
    }
}
