import java.util.Date;
import java.util.Objects;

/**
 * @Author: xxxindy
 * @Date:2018/1/22 上午9:52
 * @Description:
 */
public class TTTTest {
    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int    BASE     = 62;
    public static void main(String[] args){
        System.out.println(convertLongTo62(9208989l));
        System.out.println(convert62ToLong("MNQf"));
        Date start = new Date();
        Long res = null;
        for(int i = 0; i < 100000 ; i++) {
            res = getUIDFromTrackinfo("123456" + "MNQf");
        }
        Date end = new Date();
        System.out.println(res);
        System.out.println(end.getTime() - start.getTime());
    }
    public static Long getUIDFromTrackinfo(String trackinfo) {

        User u = new User("123456");
        if(null == trackinfo || trackinfo.length() <= 6) {
            return null;
        }
        String secret = trackinfo.substring(0,6);
        String uid62 = trackinfo.substring(6,trackinfo.length());
        Long uid = convert62ToLong(uid62);
        if(u.getSecret().equals(secret)){

            return 1l;
        }
        return null;
    }

    public static String convertLongTo62(long num ) {

        StringBuilder sb = new StringBuilder();

        while ( num > 0 )
        {
            sb.append( ALPHABET.charAt( (int)(num % BASE) ) );
            num /= BASE;
        }

        return sb.reverse().toString();
    }
    public static long convert62ToLong(String str ) {

        long num = 0;
        for ( int i = 0, len = str.length(); i < len; i++ )
        {
            num = num * BASE + ALPHABET.indexOf( str.charAt(i) );
        }

        return num;

    }


}
