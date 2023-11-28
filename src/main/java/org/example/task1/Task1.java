package org.example.task1;

public class Task1 {
    public static void main(String[] args) {

        String ip = "192.168.1.1";
        //String ip = "128.32.10.0";
        System.out.println(ip);
        long longip = StringToIntIP(ip);
        System.out.println(longip);
        ip = IntToStringIp(longip);
        System.out.println(ip);

    }

    public static long StringToIntIP(String addr) {
        String[] addrArray = addr.split("\\.");

        long num = 0;

        for (int i=0;i<addrArray.length;i++) {

            int power = 3-i;

            num += ((Integer.parseInt(addrArray[i])%256 * Math.pow(256,power)));

        }
        return num;

    }
    public static String IntToStringIp(long ip){
        return String.format("%d.%d.%d.%d",
                (ip >> 24 & 0xff),
                (ip >> 16 & 0xff),
                (ip >> 8 & 0xff),
                (ip & 0xff));
    }


}