package other.crm2crm.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rkhd.paas.openapi.sdk.config.NeoOAuthConfig;
import com.rkhd.paas.openapi.sdk.exception.NeoApiException;
import com.rkhd.paas.openapi.sdk.http.*;
import com.rkhd.platform.sdk.http.RkhdHttpClient;
import com.rkhd.platform.sdk.http.RkhdHttpData;
import com.rkhd.platform.sdk.log.Logger;
import com.rkhd.platform.sdk.log.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class data2customEntity10__c {

    //定义一个日志
    private final static Logger LOG = LoggerFactory.getLogger();

    static NeoOAuthConfig token;

    static NeoApiClient client2;


    //开票申请数据
    public static String PostXsyMethod_sql(){
        String test="SELECT id,createdAt," +
                "customItem17__c,customItem29__c,customItem36__c " +
                "FROM customEntity10__c " +
                " WHERE createdAt < 1700297140000 " +
                // "WHERE id = 2420740941237523 " +
                "ORDER BY createdAt desc " +
                "limit 5000 ";

        return test;
    }


    //请求数据
    public static String GetXsy_sql(String sqlStr) throws IOException {

        //构建一个请求样式
        RkhdHttpData rkhdHttpData = new RkhdHttpData();

        //请求地址，业务逻辑代码的时候这样
        rkhdHttpData.setCallString("/rest/data/v2.0/query/xoql");

        //请求方式application/x-www-form-urlencoded格式
        rkhdHttpData.putFormData("xoql", sqlStr);

        //请求类型
        rkhdHttpData.setCall_type("POST");

        //发送请求获取请求结果
        String data_deal_before = RkhdHttpClient.instance().performRequest(rkhdHttpData);
        //System.out.printf(data_deal_before);

        LOG.info(data_deal_before);
        return data_deal_before;
    }


    //处理获取的文件数据--旧系统
    public static List<String> extractUrls(String jsonString) throws Exception {
        List<String> urlList = new ArrayList<>();

        // 解析整个JSON字符串
        JSONObject jsonObj = JSON.parseObject(jsonString);
        JSONObject dataObj = jsonObj.getJSONObject("data");
        JSONArray recordsArray = dataObj.getJSONArray("records");

        // 遍历所有records
        for (int i = 0; i < recordsArray.size(); i++) {
            JSONObject recordObj = recordsArray.getJSONObject(i);

            String id = recordObj.getString("id");
            // 检查customItem233__c字段是否存在
            if (recordObj.getString("customItem29__c")!=null) {
                JSONArray customItemsArray = recordObj.getJSONArray("customItem29__c");


                //用一个数组存储复数文件
                JSONArray jsonArray_files = new JSONArray();

                // 遍历customItem233__c数组并获取每个对象中的url字段
                for (int j = 0; j < customItemsArray.size(); j++) {
                    JSONObject customItemObj = customItemsArray.getJSONObject(j);
                    String url = customItemObj.getString("url");
                    String saveDir = "/Users/dkcrm02/IdeaProjects/ysproject/src/main/java/downloadfile4";

                    //下载文件到指定路径
                    JSONArray jsonArray_temp = downloadFile(id,url,saveDir);
                    //将里面的第一个文件id取出来
                    JSONObject jsonObject_temp = JSONObject.parseObject(jsonArray_temp.get(0).toString());
                    //有多个文件的情况下就将id逐个入栈
                    jsonArray_files.add(jsonObject_temp);

                    //System.out.printf(url+"\n");
                    urlList.add(url);
                }
                //开始组装函数
                JSONObject jsonObject_ids = new JSONObject();
                jsonObject_ids.put("customItem37__c",jsonArray_files);
                jsonObject_ids.put("id",id);
                JSONObject jsonObject_body = new JSONObject();
                jsonObject_body.put("data",jsonObject_ids);
                update_account(id,jsonObject_body.toString());
            }
        }
        return urlList;
    }


    //下载文件--旧系统
    public static JSONArray downloadFile(String accountid,String fileURL, String saveDir) throws IOException {
        URL url = new URL(fileURL);
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        int responseCode = httpConn.getResponseCode();

        // 检查HTTP响应代码是否为200（成功）
        if (responseCode == HttpURLConnection.HTTP_OK) {
            String fileName = "";
            String disposition = httpConn.getHeaderField("Content-Disposition");
            String contentType = httpConn.getContentType();
            int contentLength = httpConn.getContentLength();

            if (disposition != null) {
                // 从头信息中提取文件名
                int index = disposition.indexOf("filename=");
                if (index > 0) {
                    fileName = disposition.substring(index + 10, disposition.length() - 1);
                }
            } else {
                // 如果从头信息中未获取到文件名，则从URL中提取文件名
                fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1);
            }

            System.out.println("Content-Type = " + contentType);
            System.out.println("Content-Disposition = " + disposition);
            System.out.println("Content-Length = " + contentLength);
            System.out.println("fileName = " + fileName);



            // 输出流
            InputStream inputStream = httpConn.getInputStream();
            String saveFilePath = saveDir + File.separator + fileName;

            // 将输入流写入文件输出流
            FileOutputStream outputStream = new FileOutputStream(saveFilePath);

            //将下载下来的文件名和文件路径上传到新系统里面


            int bytesRead = -1;
            byte[] buffer = new byte[4096];
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.close();
            inputStream.close();


            //将文件上传到新系统--处理返回的参数--将数组的文件id形式取出
            JSONArray jsonArray = extractFileInfo(NeouploadFile(fileName,saveFilePath));


            System.out.println("文件下载成功到 " + saveFilePath);

            return  jsonArray;
        } else {

            System.out.println("下载失败，HTTP响应代码: " + responseCode);
            return null;
        }
        //httpConn.disconnect();

    }


    //新的请求方式--获取权限
    private static NeoOAuthConfig getNeoOAuthConfig() {
        //销售易网址
        String domain = "https://api-p05.xiaoshouyi.com";
        //连接器的id
        String clientId = "连接器id";
        //连接器的密码
        String clientSecret = "连接器密码";
        //手机号
        String userName = "手机号";
        //密码+令牌
        String userPassword = "密码+令牌";
        NeoOAuthConfig config = NeoOAuthConfig.newBuilder(domain,
                clientId, clientSecret, userName, userPassword).build();
        return config;
    }


    //请求函数
    private static String post(NeoApiClient client, String filename, String filepath) {
        NeoRequestBody body = new NeoMultipartBody.Builder()
                .addFormDataPart("files", filename, filepath)
                .addFormDataPart("isImage","false")
                .addFormDataPart("needFileId","true")
                .build();
        NeoRequest request = NeoRequest.newBuilder()
                .url("/rest/file/v2.0/file/batch")
                .post(body)
                .build();
        try {
            NeoResponse response = client.send(request);
            System.out.println(response.body().string());
            return response.body().string();
        } catch (IOException | NeoApiException e) {
            e.printStackTrace();
            return null;
        }
    }


    //处理数组函数新系统返回函数
    public static JSONArray extractFileInfo(String jsonResponse) {
        // 将传入的 JSON 字符串解析为 JSONObject
        JSONObject jsonObject = JSON.parseObject(jsonResponse);
        JSONArray resultArray = jsonObject.getJSONArray("result");

        // 创建一个新的 JSONArray 来存储提取后的数据
        JSONArray extractedArray = new JSONArray();
        if(resultArray!=null) {
            for (int i = 0; i < resultArray.size(); i++) {
                JSONObject resultObject = resultArray.getJSONObject(i);

                // 创建一个新的 JSONObject，并提取所需字段
                JSONObject extractedObject = new JSONObject();
                extractedObject.put("id", resultObject.getLong("fileId"));
                //extractedObject.put("fileId", resultObject.getLong("fileId"));
                //extractedObject.put("name", resultObject.getString("fileName"));
                //extractedObject.put("fileLength", resultObject.getIntValue("fileLength"));
                //extractedObject.put("url", resultObject.getString("fileUrl"));

                // 将提取后的 JSONObject 添加到新的 JSONArray 中
                extractedArray.add(extractedObject);
            }
        }
        return extractedArray;
    }


    //文件上传新系统请求函数
    public static String NeouploadFile(String filename,String filepath){

        NeoOAuthConfig config = token;
        NeoApiClient client = client2;
        String res = post(client,filename,filepath);
        return res;
    }


    //将获取到的数据回传到旧系统
    public static String update_account(String id,String body) throws IOException {

        String url = "/rest/data/v2.0/xobjects/customEntity10__c/"+id;
        RkhdHttpData rkhdHttpData = new RkhdHttpData();
        rkhdHttpData.setCallString(url);
        rkhdHttpData.setBody(body);
        rkhdHttpData.setCall_type("PATCH");
        String result = RkhdHttpClient.instance().performRequest(rkhdHttpData);

        System.out.printf(result.toString());

        return result.toString();

    }


    public static void main(String[] args) throws Exception {

        //GetXsy_sql(PostXsyMethod_sql());

        token = getNeoOAuthConfig();
        client2 = NeoApiClient.newBuilder(token).build();

        extractUrls(GetXsy_sql(PostXsyMethod_sql()));


    }

}
