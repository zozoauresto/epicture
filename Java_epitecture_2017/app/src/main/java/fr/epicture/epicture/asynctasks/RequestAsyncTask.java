package fr.epicture.epicture.asynctasks;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class RequestAsyncTask extends AsyncTask<Void, Integer, Void> {

    private static final Integer SECONDE = 1000;

    private static final Integer GET_CONNECTION_TIMEOUT = 3 * SECONDE;
    private static final Integer GET_READ_TIMEOUT = 15 * SECONDE;

    private static final Integer POST_CONNECTION_TIMEOUT = 15 * SECONDE;
    private static final Integer POST_READ_TIMEOUT = 15 * SECONDE;

    private HttpsURLConnection httpsURLConnection;
    private HttpURLConnection httpURLConnection;
    protected Integer httpResponseCode = null;
    protected String response;
    protected Bitmap image;
    private List<Object> params;

    private Map<String, String> header;

    private Context context;
    private boolean running;

    public RequestAsyncTask(@NonNull Context context) {
        this.context = context;
        header = new HashMap<>();
        params = new ArrayList<>();
    }

    protected void addHeader(String key, String value) {
        header.put(key, value);
    }

    public void execute() {
        executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public boolean isRunning() {
        return (running);
    }

    @NonNull
    protected Context getContext() {
        return (context);
    }

    @Override
    protected void onPreExecute() {
        running = true;
    }

    @Override
    @Nullable
    protected Void doInBackground(@Nullable Void... params) {
        return (null);
    }

    @Override
    protected void onPostExecute(Void result) {
        running = false;
    }

    @Override
    protected void onCancelled(@Nullable Void result) {
        running = false;
        try {
            if (httpsURLConnection != null) {
                httpsURLConnection.disconnect();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void addParam(Object object) {
        params.add(object);
    }

    @Override
    protected void onProgressUpdate(Integer... values){
        super.onProgressUpdate(values);
    }

    protected void GET(String url) {
        try {
            this.initHttps(url);

            httpsURLConnection.setConnectTimeout(GET_CONNECTION_TIMEOUT);
            httpsURLConnection.setReadTimeout(GET_READ_TIMEOUT);
            httpsURLConnection.setRequestMethod("GET");
            httpsURLConnection.setUseCaches(false);

            for (Map.Entry<String, String> entry : header.entrySet()) {
                httpsURLConnection.setRequestProperty(entry.getKey(), entry.getValue());
            }

            try {
                this.getResponse();
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                this.closeHttpsClient();
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void GETImage(String url) {
        try {
            this.initHttps(url);

            httpsURLConnection.setConnectTimeout(GET_CONNECTION_TIMEOUT);
            httpsURLConnection.setReadTimeout(GET_READ_TIMEOUT);
            httpsURLConnection.setRequestMethod("GET");
            httpsURLConnection.setUseCaches(false);

            for (Map.Entry<String, String> entry : header.entrySet()) {
                httpsURLConnection.setRequestProperty(entry.getKey(), entry.getValue());
            }

            try {
                this.getHttpsResponseImage();
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                this.closeHttpsClient();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception er) {
            er.printStackTrace();
        }
    }

    protected void POST(String url) {
        try {
            this.initHttps(url);

            httpsURLConnection.setConnectTimeout(POST_CONNECTION_TIMEOUT);
            httpsURLConnection.setReadTimeout(POST_READ_TIMEOUT);
            httpsURLConnection.setRequestMethod("POST");
            httpsURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            httpsURLConnection.setDoInput(true);
            httpsURLConnection.setDoOutput(true);
            httpsURLConnection.setUseCaches(false);

            for (Map.Entry<String, String> entry : header.entrySet()) {
                httpsURLConnection.setRequestProperty(entry.getKey(), entry.getValue());
            }

            StringBuilder postData = new StringBuilder();
            if (params != null) {
                for (int i = 0; i < params.size(); i++) {
                    Object param = params.get(i);

                    if (param instanceof ParamBody) {
                        ParamBody paramBody = (ParamBody) param;
                        if (postData.length() != 0) {
                            postData.append('&');
                        }
                        postData.append(URLEncoder.encode(paramBody.getName(), "UTF-8"));
                        postData.append('=');
                        postData.append(URLEncoder.encode(paramBody.getContent(), "UTF-8"));
                    }
                }
            }
            DataOutputStream wr = new DataOutputStream(httpsURLConnection.getOutputStream());
            wr.write(postData.toString().getBytes("UTF-8"));
            wr.flush();
            wr.close();

            try {
                this.getResponse();
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                this.closeHttpsClient();
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void POSTMultipart(String url/*, List<Object> params*/) {
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "---------------------------7d44e178b0434";

        try {
            this.initHttps(url);

            httpsURLConnection.setConnectTimeout(POST_CONNECTION_TIMEOUT);
            httpsURLConnection.setReadTimeout(POST_READ_TIMEOUT);
            httpsURLConnection.setDoInput(true);
            httpsURLConnection.setDoOutput(true);
            httpsURLConnection.setUseCaches(false);
            httpsURLConnection.setRequestMethod("POST");
            httpsURLConnection.setRequestProperty("Accept-Encoding", "");
            httpsURLConnection.setRequestProperty("Connection", "Keep-Alive");
            httpsURLConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

            for (Map.Entry<String, String> entry : header.entrySet()) {
                httpsURLConnection.setRequestProperty(entry.getKey(), entry.getValue());
            }

            DataOutputStream dos = new DataOutputStream(httpsURLConnection.getOutputStream());
            for(int i = 0; i < params.size(); i++)
            {
                Object param = params.get(i);
                if (param instanceof ParamFile) {

                    ParamFile paramFile = (ParamFile)param;
                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Type: " + paramFile.getContentType() + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=" + paramFile.getParamName() + "; filename=" + paramFile.getFileName() + lineEnd);
                    dos.writeBytes(lineEnd);

                    ByteArrayInputStream fileInputStream = new ByteArrayInputStream(paramFile.getData());

                    byte data[] = new byte[1024];
                    int count;
                    while ((count = fileInputStream.read(data)) != -1) {
                        dos.write(data, 0, count);
                    }
                    dos.writeBytes(lineEnd);

                    fileInputStream.close();

                } else if (param instanceof ParamBody) {
                    ParamBody paramBody = (ParamBody)param;
                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=" + paramBody.getName());
                    dos.writeBytes(lineEnd + lineEnd);
                    dos.write(paramBody.getContent().getBytes("UTF-8"));
                    dos.writeBytes(lineEnd);
                }
            }
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            dos.flush();
            dos.close();

            try {
                this.getResponse();
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                this.closeHttpsClient();
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initHttps(String path) throws IOException {
        URL url = new URL(path);
        httpsURLConnection = (HttpsURLConnection)url.openConnection();
    }

    private void getResponse() throws Exception {
        httpResponseCode = httpsURLConnection.getResponseCode();

        if (httpResponseCode == 200) {
            InputStream input = httpsURLConnection.getInputStream();
            if (input != null) {
                try {
                    StringBuilder responseStrBuilder = new StringBuilder();
                    BufferedReader streamReader = new BufferedReader(new InputStreamReader(input, "UTF-8"));
                    String inputStr;
                    while ((inputStr = streamReader.readLine()) != null)
                        responseStrBuilder.append(inputStr);
                    response = responseStrBuilder.toString();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                input.close();
            }
        } else {
            InputStream error = httpsURLConnection.getErrorStream();
            if (error != null) {
                try {
                    StringBuilder errorStrBuilder = new StringBuilder();
                    BufferedReader streamReader = new BufferedReader(new InputStreamReader(error, "UTF-8"));
                    String inputStr;
                    while ((inputStr = streamReader.readLine()) != null)
                        errorStrBuilder.append(inputStr);
                    response = errorStrBuilder.toString();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                error.close();
            }
        }
    }

    private void getHttpsResponseImage() throws Exception {
        httpResponseCode = httpsURLConnection.getResponseCode();

        if (httpResponseCode == 200) {
            InputStream input = httpsURLConnection.getInputStream();
            if (input != null) {
                try {
                    image = BitmapFactory.decodeStream(input);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                input.close();
            }
        } else {
            InputStream error = httpsURLConnection.getErrorStream();
            if (error != null) {
                try {
                    StringBuilder errorStrBuilder = new StringBuilder();
                    BufferedReader streamReader = new BufferedReader(new InputStreamReader(error, "UTF-8"));
                    String inputStr;
                    while ((inputStr = streamReader.readLine()) != null)
                        errorStrBuilder.append(inputStr);
                    response = errorStrBuilder.toString();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                error.close();
            }
        }
    }

    private void closeHttpsClient() {
        running = false;
        try {
            if (httpsURLConnection != null) {
                httpsURLConnection.disconnect();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class ParamBody {
        private String name;
        private String content;

        public ParamBody(String name, String content) {
            this.name = name;
            this.content = content;
        }

        public String getName() {
            return this.name;
        }
        public String getContent() {
            return this.content;
        }
    }

    /**
     * object param for multipart
     */
    public class ParamFile {
        //private String name;
        private String fileName;
        private String paramName;
        private byte[] data;
        private long length;
        private String contentType;

        public ParamFile(/*String name,*/ String fileName, String paramName, byte[] data, long length, String contentType) {
            //this.name = name;
            this.fileName = fileName;
            this.paramName = paramName;
            this.data = data;
            this.length = length;
            this.contentType = contentType;
        }

        /*public String getName() {
            return this.name;
        }*/

        public String getFileName() {
            return this.fileName;
        }
        public String getParamName() {
            return this.paramName;
        }
        public byte[] getData() {
            return this.data;
        }
        public long getLength() {
            return this.length;
        }
        public String getContentType() {
            return this.contentType;
        }
    }

}
