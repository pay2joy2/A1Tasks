package org.example.task3;

import org.example.task3.connfactory.ConnFactory;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Класс сервиса для получения
 */
public class PostingService {
    private static final ConnFactory cf = ConnFactory.getInstance();

    /**
     *
     * Функция для получения периода от текущей даты.
     */
    private LocalDate GetPeriod(String timePeriod){
        LocalDate StartPeriod = LocalDate.now();
        switch (timePeriod) {
            case("Day"):
                StartPeriod = StartPeriod.minusDays(1);
                break;
            case("Month"):
                StartPeriod = StartPeriod.minusMonths(1);
                break;
            case("Quarter"):
                StartPeriod = StartPeriod.minusMonths(3);
                break;
            case ("Year"):
                StartPeriod = StartPeriod.minusYears(1);
                break;
        }
        return StartPeriod;
    }

    public List<Posting> GetPostingsForPeriod(String timePeriod, Boolean ActiveStatus) throws SQLException {

        LocalDate StartPeriod = GetPeriod(timePeriod);

        String Authorized = "\"Authorized posting\"";
        String PstngDate = "\"Pstng Date\"";

        PreparedStatement ps = null;
        Connection conn = cf.getConnection();

        if(ActiveStatus != null) {
            String sql = "SELECT * FROM postings " +
                    "WHERE cast(" + PstngDate + " as date)" +
                    "BETWEEN ? AND ? " +
                    "AND " + Authorized + " = ?";
            ps = conn.prepareStatement(sql);
            ps.setBoolean(3, ActiveStatus);
        } else {                                             //Меняем запрос в зависимости от того, было ли отправлено
            String sql = "SELECT * FROM postings " +         //поле ActiveStatus
                    "WHERE cast(" + PstngDate + " as date)" +
                    "BETWEEN ? AND ? ";
            ps = conn.prepareStatement(sql);
        }
        ps.setDate(1, Date.valueOf(StartPeriod));
        ps.setDate(2, Date.valueOf(LocalDate.now()));
        ResultSet rs = ps.executeQuery();
        List<Posting> postings = new ArrayList<>(); // Объявляем массив из сущностей posting
        Posting posting = null;
        if(rs.isBeforeFirst())
        {
            while(rs.next())
            {
                posting = new Posting(rs.getString(1),rs.getInt(2),rs.getDate(3),
                        rs.getDate(4),rs.getString(5),
                        rs.getInt(6), rs.getString(7),
                        rs.getFloat(8),rs.getString(9),
                        rs.getString(10),rs.getBoolean(11));

                postings.add(posting); //Выделяем память под каждую новую сущность, и добавляем в массив
            }
        }
        return postings;
    }
}
