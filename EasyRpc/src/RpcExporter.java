import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 *
 *   RPC服务端服务发布者
 *      主要职责：
 *          （1） 作为服务端监听客户端的TCP连接，接受到新的客户端连接之后，将其封装成Task，由线程池执行
 *          （2） 将客户端发送的码流反序列化成对象，反射调用服务实现者，获取执行结果。
 *          （3） 将执行结果对象反序列化，通过Socket发送给客户端
 *          （4） 远程服务调用结束后，释放Socket等连接资源，防止句柄泄露。
 *          【注】 句柄泄露：http://blog.csdn.net/modiziri/article/details/49928489
 * @Author xxxindy
 * @Date 2018/1/17 下午3:49
 */
public class RpcExporter {

    //根据cpu核数新建线程池
    static Executor executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    //发布
    public static void exporter(String  hostName, int port) throws Exception {

        ServerSocket server = new ServerSocket();
        server.bind(new InetSocketAddress(hostName,port));
        try {
            while(true){
                executor.execute(new ExporterTask(server.accept()));
            }
        }
        finally {
            server.close();
        }

    }
    private static class ExporterTask implements Runnable {

        Socket client = null;

        public ExporterTask(Socket client) {
            this.client = client;
        }
        @Override
        public void run() {

            ObjectOutputStream output = null;
            ObjectInputStream input = null;

            try {

                input = new ObjectInputStream(client.getInputStream());
                //读取接口类名
                String interfaceName = input.readUTF();
                //加载接口类（代理类）
                Class<?> service = Class.forName(interfaceName);
                //读取方法名
                String methodName = input.readUTF();
                //读取形参类型
                Class<?>[] parameterTypes = (Class<?>[]) input.readObject();
                //读取参数对象
                Object[] arguments = (Object[])input.readObject();
                //得到Method对象
                Method method = service.getMethod(methodName,parameterTypes);
                //传入参数，执行method
                Object result = method.invoke(service.newInstance(), arguments);
                //输出结果（返回值）
                output = new ObjectOutputStream(client.getOutputStream());
                output.writeObject(result);


            } catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                if(output != null) {
                    try {
                        output.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(input != null) {
                    try {
                        input.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(client != null){
                    try {
                        client.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    }
}
