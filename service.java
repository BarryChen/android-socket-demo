/**
* create by caoyinfei 
*/

public class MainActivity extends Activity {
    public static TextView client_content, ip;
    private String serverIp = "";
    String buffer = "";
    public static Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 1) {
                client_content.append("client" + msg.obj + "\n");
            }
        }

        ;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        client_content = (TextView) findViewById(R.id.client_content);
        ip = (TextView) findViewById(R.id.ip);
        serverIp = getlocalip();
        ip.setText("IP addresss:" + serverIp);
        LogUtil.d("dd","ddddddddddd");

        new Thread() {
            public void run() {
                OutputStream output;
                String serverContent = "hello hehe";
                try {
                    ServerSocket serverSocket = new ServerSocket(30000);
                    while (true) {
                        Message msg = new Message();
                        msg.what = 1;
                        try {
                            Socket socket = serverSocket.accept();
                            //向client发送消息
                            output = socket.getOutputStream();
                            output.write(serverContent.getBytes("utf-8"));
                            output.flush();
                            socket.shutdownOutput();

                            //获取输入信息
                            BufferedReader bff = new BufferedReader(new InputStreamReader

(socket.getInputStream()));
                            //读取信息
                            String result = "";
                            String buffer = "";
                            while ((buffer = bff.readLine()) != null) {
                                result = result + buffer;
                            }
                            msg.obj = result.toString();
                            mHandler.sendMessage(msg);
                            bff.close();
                            output.close();
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }

            ;
        }.start();
    }

    private String getlocalip() {
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        //  Log.d(Tag, "int ip "+ipAddress);
        if (ipAddress == 0) return null;
        return ((ipAddress & 0xff) + "." + (ipAddress >> 8 & 0xff) + "."
                + (ipAddress >> 16 & 0xff) + "." + (ipAddress >> 24 & 0xff));
    }

}