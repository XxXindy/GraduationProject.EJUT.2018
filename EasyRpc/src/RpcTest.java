import java.net.InetSocketAddress;

/**
 * @Author: xxxindy
 * @Date:2018/1/17 下午5:51
 * @Description:
 */
public class RpcTest {
    public static void main(String[] args){
       new Thread(new Runnable() {
           @Override
           public void run() {
               try {
                   RpcExporter_2.exporter("localhost",8088);
               } catch (Exception e) {
                   e.printStackTrace();
               }
           }
       }).start();
       RpcImporter<EchoService> importer = new RpcImporter<EchoService>();
       EchoService echo = importer.importer(EchoServiceImpl.class, new InetSocketAddress("localhost",8088));
       System.out.println(echo.echo("Easy Rpc !!!Are u OK?"));
    }
}
