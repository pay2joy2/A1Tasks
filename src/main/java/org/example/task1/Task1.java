package org.example.task1;

public class Task1 {
    public static void main(String[] args) {

        /**
         *
         * Не совсем понятно как уместить IpV4 в 4 байта, если в java самый левый бит
         * используется для знака (+ или -), а беззнакового эквивалента int32 нет,
         * (unsigned int)
         * Соответственно максимальное значение int - 2147483647 или
         * 01111111111111111111111111111111 в битовом варианте (31 бит)
         *
         * Пришлось использовать long - 64 бита.
         *
         */

        String ip = "192.168.1.1";
        System.out.println(ip);
        long longip = StringToIntIP(ip);
        System.out.println(longip);
        ip = IntToStringIp(longip);
        System.out.println(ip);

    }

    /**
     *
     * Переводим каждый октет в десятичное число смещённое по битам
     * при помощи возведения в степень, а после суммируем
     * Каждый октет возводится в необходимую степень смещения - 3,2,1,0
     * для смещения на 8 бит
     *
     *
     */
    public static long StringToIntIP(String addr) {
        String[] addrArray = addr.split("\\.");
        long num = 0;
        for (int i=0;i<addrArray.length;i++) {
            int power = 3-i;
            num += ((Integer.parseInt(addrArray[i])%256 * Math.pow(256,power)));
        }
        return num;
    }

    /**
     *
     * Делаем побитовый сдвиг, для получения строки ip
     *
     */
    public static String IntToStringIp(long ip){
        return String.format("%d.%d.%d.%d",
                (ip >> 24 & 0xff),
                (ip >> 16 & 0xff),
                (ip >> 8 & 0xff),
                (ip & 0xff));
    }
}