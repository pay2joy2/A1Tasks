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

        //alterPosting();

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
     * Из-за этого, было решено считывать построчно, разбивая по ; , а после разбивать и сразу отправлять в БД.
     * В каждый массив строк, добавляем поле Activity Status в конце
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

        CSVReader PostingsReader = new CSVReader(new FileReader("postings.csv"));

        int counter = 0; //Счётчик для добавления шапки в 0 индекс

        Connection conn = cf.getConnection();

        // Здесь была возможность создания массива из сущностей Posting

        while ((nextLine = PostingsReader.readNext()) != null) {
            String[] line = Arrays.toString(nextLine).split(";");
            if(line.length > 1) {
                for(int i = 0; i < line.length; i++) {
                    line[i] = line[i].replaceAll("\t",""); //Удаляем все \t символы
                }
                line[0] = line[0].replace("[","");    //Удаляем ненужные символы из строк в начале
                line[9] = line[9].replace("]","");    //и в конце
                line[7] = line[7].replaceAll(" ", "");  // Удаляем лишний символ пробела в колонке цены
                if(counter == 0) {
                    line = addColumnToLine(line, "authorized posting"); //Добавляем в 0 индекс имя колонки для удобства
                } else {                                                     //Если понадобится возможность перезаписать postings c новым полем
                    if (loginStatus.containsKey(line[9]) && loginStatus.get(line[9])) { //Проверяем Activity Status
                        line = addColumnToLine(line, "true");                     //Сравнивая со значениями в карте
                    } else {
                        line = addColumnToLine(line, "false");
                    }
                    String sqlDate2 = null;
                    String sqlDate3 = null;
                    try {
                        sqlDate2 = new SimpleDateFormat("yyyy-MM-dd")           //Перевод даты из dd.MM.yyyy в формат
                                .format(new SimpleDateFormat("dd.MM.yyyy").parse(line[2]));  //Приемлемый для sql.Date
                        sqlDate3 = new SimpleDateFormat("yyyy-MM-dd")
                                .format(new SimpleDateFormat("dd.MM.yyyy").parse(line[3]));
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }

                    String sql = "INSERT INTO postings VALUES (?,?,?,?,?,?,?,?,?,?,?)";

                    PreparedStatement ps = conn.prepareStatement(sql); //Вводим все необходимые значения в SQL запрос

                    ps.setString(1, line[0]);
                    ps.setInt(2, Integer.parseInt(line[1]));
                    ps.setDate(3, Date.valueOf(sqlDate2));
                    ps.setDate(4, Date.valueOf(sqlDate3));
                    ps.setString(5, line[4]);
                    ps.setInt(6, Integer.parseInt(line[5]));
                    ps.setString(7, line[6]);
                    ps.setDouble(8,Double.parseDouble(line[7].replaceAll(",", ".")));
                    ps.setString(9, line[8]);
                    ps.setString(10, line[9]);
                    ps.setBoolean(11, Boolean.parseBoolean(line[10]));
                    ps.executeUpdate();      //Отправляем update
                }
                counter++;    //Счётчик для добавления шапки в 0 индекс
            }
        }
        PostingsReader.close();
        conn.close();
    }

    /**
     *
     * Добавляем новое поле в массив строк, и сразу заполняем его
     *
     */
    private static String[] addColumnToLine(String[] line, String data) {
        String[] newLine = new String[line.length + 1];
        System.arraycopy(line, 0, newLine, 0, line.length);
        newLine[line.length] = data;
        return newLine;
    }
}