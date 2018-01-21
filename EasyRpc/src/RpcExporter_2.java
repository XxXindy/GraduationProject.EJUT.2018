import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @Author: xxxindy
 * @Date:2018/1/18 下午4:14
 * @Description:
 */
public class RpcExporter_2 {
    //根据cpu核数新建线程池
    static Executor executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    //发布
    public static void exporter(String  hostName, int port) throws Exception {

        ServerSocket server = new ServerSocket();
        server.bind(new InetSocketAddress(hostName,port));
        try {
            while(true){
                executor.execute(new RpcExporter_2.ExporterTask(server.accept()));
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
                Class<?> returnType = method.getReturnType();
                //传入参数，执行method
                MethodHandles.Lookup lookup = MethodHandles.lookup();
                MethodType methodType = MethodType.methodType(returnType,parameterTypes);
                MethodHandle methodHandle = lookup.findVirtual(service,methodName,methodType);
                Object result = null;
                try {
                    result = methodHandle.invokeWithArguments(service,arguments);
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
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
