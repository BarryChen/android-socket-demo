/**
 * create by caoyinfei 
 */
public class MainActivity extends Activity {
    TextView result;
    Button send;
    EditText input;
    public Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                result.append("server:" + msg.obj + "\n");
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        result = (TextView) findViewById(R.id.result);
        send = (Button) findViewById(R.id.send);
        input = (EditText) findViewById(R.id.input);
        send.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputContent = input.getText().toString();
                result.append("client:" + inputContent + "\n");
                //启动线程 向服务器发送和接收信息
                new MyThread(inputContent).start();
            }
        });

    }

    class MyThread extends Thread {

        public String content;

        public MyThread(String str) {
            content = str;
        }

        @Override
        public void run() {
            //定义消息
            Message msg = new Message();
            msg.what = 1;
            try {
                //连接服务器 并设置连接超时为5秒
                Socket socket = new Socket();
                socket.connect(new InetSocketAddress("10.100.165.33", 30000), 1000);

                //获取输入输出流
                OutputStream ou = socket.getOutputStream();
                //获取输出输出流
                BufferedReader bff = new BufferedReader(new InputStreamReader(
                        socket.getInputStream()));
                //向服务器发送信息
                ou.write(content.getBytes("utf-8"));
                ou.flush();

                //读取发来服务器信息
                String result = "";
                String buffer = "";
                while ((buffer = bff.readLine()) != null) {
                    result = result + buffer;
                }
                msg.obj = result.toString();
                //发送消息 修改UI线程中的组件
                myHandler.sendMessage(msg);
                //关闭各种输入输出流
                bff.close();
                ou.close();
                socket.close();
            } catch (SocketTimeoutException aa) {
                //连接超时 在UI界面显示消息
                msg.obj =  "服务器连接失败！请检查网络是否打开";
                //发送消息 修改UI线程中的组件
                myHandler.sendMessage(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}