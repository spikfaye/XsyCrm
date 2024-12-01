package other.erp2crm.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rkhd.platform.sdk.api.annotations.RequestMethod;
import com.rkhd.platform.sdk.api.annotations.RestApi;
import com.rkhd.platform.sdk.api.annotations.RestMapping;
import com.rkhd.platform.sdk.data.model.OrderProduct;
import com.rkhd.platform.sdk.exception.ApiEntityServiceException;
import com.rkhd.platform.sdk.exception.AsyncTaskException;
import com.rkhd.platform.sdk.http.*;
import com.rkhd.platform.sdk.log.Logger;
import com.rkhd.platform.sdk.log.LoggerFactory;
import com.rkhd.platform.sdk.model.XObject;
import com.rkhd.platform.sdk.service.FutureTaskService;
import com.rkhd.platform.sdk.service.XObjectService;

import java.io.IOException;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 *
 * Title: erp数据同步crm--批量接口版本
 * @implNote
 * @author    ldl
 * @version   1.50, 2024-11-09
 *
 */
@RestApi(baseUrl = "/ybl")
public class ybldeliveryRecordBlukapi {

    /**
     * 定义一个日志
     */
    private final static Logger LOG = LoggerFactory.getLogger();


    /**
     * 定义一个交付图表
     */
    private static Map<String, String> deliveryRecord_map = null;


    /**
     * 定义一个客户图表
     */
    private static Map<String, String> account_map = null;


    /**
     * 定义一个订单表
     */
    private static Map<String, String> order_map = null;


    /**
     * 定义一个交付明细记录
     */
    private static Map<String, String> deliveryRecordDetail_map = null;


    /**
     * 定义一个产品
     */
    private static Map<String, String> product_map = null;


    /**
     * 获取erp的出库信息，传入参数
     *
     * @return JSONArray类型
     */
    public static String ybl_get_delivery() throws IOException {

        String base_url = "接口地址";

        String query = "FObjectID=21&FObjectType=3&HasSchema=true&Search=null";

        String filter = "&Filter=";

        LocalDate currentDate = LocalDate.now();

        // 获取当前日期和时间
        LocalDate currentDate2 = LocalDate.now();
        // 获取前一天的日期和时间
        LocalDate previousDayDate = currentDate2.minusDays(1);

        String pre_date = previousDayDate .toString();
        String date = currentDate.toString();
        //获取销售易最新一条的出库单信息用来做筛选
        String billno = null;

        //增加一个最新的单号来减少获取的数据量
        String filter_data = "{\"Fbillno1\":\"" +
                "XOUT000001" +
                "\",\"Fbillno2\":\"XOUT918793\"," +
                "\"FDate1\":\"" +
                pre_date +
                "\",\"FDate2\":\"" +
                date +
                "\"}";

        //"2024-08-25"
        // Encode the filter_data
        String filter_data2 = URLEncoder.encode(filter_data, "UTF-8");

        String json = "&json=";

        //Search大括号不能省略
        String json_data1 = "{\"FObjectID\":21,\"FObjectType\":3,\"HasSchema\":true,\"Search\":{},\"Filter\":";

        //String json_data4 = "{\"FObjectID\":21,\"FObjectType\":3,\"HasSchema\":true,\"Search\":{},\"Filter\":{\"FDate1\":\"2023-05-01\",\"FDate2\":\"2023-05-03\"}}";
        String json_data2 = json_data1 + "{\"Fbillno1\":\"" +
                "XOUT000001" +
                "\",\"Fbillno2\":\"XOUT918793\",\"FDate1\":\"" +
                pre_date +
                "\",\"FDate2\":\"" +
                date +
                "\"}}";

        String json_data3 = URLEncoder.encode(json_data2, "UTF-8");


        String url = base_url + "?" + query + filter + filter_data2 + json + json_data3;

        CommonData up = CommonData.newBuilder()
                .callString(url)
                .header("Authorization",Result.Authorization())
                .header("Pragma",Result.Pragma())
                .callType("GET")
                .build();


        HttpResult result = new CommonHttpClient().execute(up);
        //System.out.println(result.getResult());

        //JSONObject jsonObject = JSON.parseObject(result.getResult());

        //JSONObject jsonObject_result = jsonObject.getJSONObject("result");

        //输出查询的条数
        //LOG.info(jsonObject_result.getString("total"));

        return result.getResult();
    }


    /**
     * 再处理得到交付明细数据
     *
     * @return JSONArray类型
     */
    public static JSONArray deal_delivery() throws IOException {

        //先不传入参数
        JSONObject jsonObject= JSON.parseObject(ybl_get_delivery());

        //获取得到的请求状态
        if(jsonObject.getInteger("code")==200){
            LOG.info("获取出库单成功");
            JSONObject jsonObject_result = jsonObject.getJSONObject("result");
            //LOG.info(jsonObject_result.toString());
            JSONArray jsonArray = jsonObject_result.getJSONArray("items");
            //LOG.info(jsonArray.toString());
            return  jsonArray;

        }else{
            LOG.info(jsonObject.getString("code"));
            LOG.info(jsonObject.getString("message"));
            return null;
        }



    }


    /**
     * 再处理得到交付数据--只取第一行
     *
     * @return JSONArray类型
     */
    public  static  JSONArray deal_deliveryRecord() throws IOException {




        JSONArray jsonArray = deal_delivery();
        //LOG.info(jsonArray.toString());
        JSONArray delivery_data = new JSONArray();

        for (Object object : jsonArray){
            JSONObject jsonObject = (JSONObject) object;
            //如果是出库明细的第一单就将数据存储到新的数组里面
            if(Objects.equals(jsonObject.getString("FEntryID"), "1")){
                delivery_data.add(jsonObject);
            }
        }
        //LOG.info(delivery_data.toString());
        return  delivery_data;
    }


    /**
     * xsy交付记录SQL语句
     *
     * @return String类型
     */
    public static String GetdeliveryRecord_1(){

        String query_one ="select id,customItem1__c " +
                " from deliveryRecord" +
                " order by updatedAt desc" +
                " limit 0,3000";
        return query_one;

    }


    /**
     * xsy交付记录SQL语句
     *
     * @return String类型
     */
    public static String GetdeliveryRecord_2(){

        String query_one ="select id,customItem1__c " +
                " from deliveryRecord" +
                " order by updatedAt desc" +
                " limit 3000,3000";
        return query_one;

    }


    /**
     * xsy交付记录明细SQL语句
     *
     * @return String类型
     */
    public static String GetdeliveryRecordDetail_1(){

        String query_one ="select id,customItem1__c " +
                " from deliveryRecordDetail" +
                " order by updatedAt desc" +
                " limit 0,3000";
        return query_one;

    }


    /**
     * xsy交付记录明细SQL语句
     *
     * @return String类型
     */
    public static String GetdeliveryRecordDetail_2(){

        String query_one ="select id,customItem1__c " +
                " from deliveryRecordDetail" +
                " order by updatedAt desc" +
                " limit 3000,3000";
        return query_one;

    }


    /**
     * xsy交付记录明细SQL语句
     *
     * @return String类型
     */
    public static String GetdeliveryRecordDetail_3(){

        String query_one ="select id,customItem1__c " +
                " from deliveryRecordDetail" +
                " order by updatedAt desc" +
                " limit 6000,3000";
        return query_one;

    }



    /**
     * xsy产品SQL语句
     *
     * @return String类型
     */
    public static String getproduct_1(){

        String query_one ="select id,product_FItem__c " +
                " from product" +
                " order by updatedAt desc" +
                " limit 0,4000";
        return query_one;

    }


    /**
     * xsy产品SQL语句
     *
     * @return String类型
     */
    public static String getproduct_2(){

        String query_one ="select id,product_FItem__c " +
                " from product" +
                " order by updatedAt desc" +
                " limit 4000,4000";
        return query_one;

    }


    /**
     * xsy产品SQL语句
     *
     * @return String类型
     */
    public static String getproduct_3(){

        String query_one ="select id,product_FItem__c " +
                " from product" +
                " order by updatedAt desc" +
                " limit 8000,4000";
        return query_one;

    }


    /**
     * xsy产品SQL语句
     *
     * @return String类型
     */
    public static String getproduct_4(){

        String query_one ="select id,product_FItem__c " +
                " from product" +
                " order by updatedAt desc" +
                " limit 12000,4000";
        return query_one;

    }


    /**
     * xsy客户信息SQL语句
     *
     * @return String类型
     */
    public  static  String getaccount(){

        String query_one ="select id,accountName " +
                " from account" +
                " order by updatedAt desc" +
                " limit 4000";
        return query_one;

    }


    /**
     * xsy订单SQL语句
     *
     * @return String类型
     */
    public static String getorder(){

        String query_one ="select id,order_interid__c " +
                " from order" +
                " order by updatedAt desc" +
                " limit 4000";
        return query_one;

    }


    /**
     * @param  sqlStr SQl语句 xsy请求函数
     *
     * @return String类型
     */
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

        //LOG.info(data_deal_before);
        return data_deal_before;
    }


    /**
     *  将获取的交付记录数据拼接
     *  @return JSONArray类型
     */
    public static JSONArray GetdeliveryRecord_all() throws IOException {


        JSONObject jsonObject = JSON.parseObject(GetXsy_sql(GetdeliveryRecord_1()));

        JSONObject jsonObject_data = jsonObject.getJSONObject("data");

        JSONArray jsonArray = jsonObject_data.getJSONArray("records");



        JSONObject jsonObject_2 = JSON.parseObject(GetXsy_sql(GetdeliveryRecord_2()));

        JSONObject jsonObject_data_2 = jsonObject_2.getJSONObject("data");

        JSONArray jsonArray_2 = jsonObject_data_2.getJSONArray("records");

        //合并两个交付记录的信息
        jsonArray.addAll(jsonArray_2);

        return jsonArray;
    }


    /**
     *  将获取的交付记录明细数据拼接
     *  @return JSONArray类型
     */
    public static JSONArray GetdeliveryRecordDetail_all() throws IOException {

        JSONObject jsonObject_1 = JSON.parseObject(GetXsy_sql(GetdeliveryRecordDetail_1()));

        JSONObject jsonObject_data = jsonObject_1.getJSONObject("data");

        JSONArray jsonArray = jsonObject_data.getJSONArray("records");

        JSONObject jsonObject_2 = JSON.parseObject(GetXsy_sql(GetdeliveryRecordDetail_2()));

        JSONObject jsonObject_data2 = jsonObject_2.getJSONObject("data");

        JSONArray jsonArray2 = jsonObject_data2.getJSONArray("records");

        JSONObject jsonObject_3 = JSON.parseObject(GetXsy_sql(GetdeliveryRecordDetail_3()));

        JSONObject jsonObject_data3 = jsonObject_3.getJSONObject("data");

        JSONArray jsonArray3 = jsonObject_data3.getJSONArray("records");

        jsonArray.addAll(jsonArray2);

        jsonArray.addAll(jsonArray3);

        return jsonArray;

    }


    /**
     *  将获取的产品数据拼接
     *  @return JSONArray类型
     */
    public static JSONArray product_all() throws IOException {

        JSONObject jsonObject_1 = JSON.parseObject(GetXsy_sql(getproduct_1()));

        JSONObject jsonObject_data = jsonObject_1.getJSONObject("data");

        JSONArray jsonArray = jsonObject_data.getJSONArray("records");

        JSONObject jsonObject_2 = JSON.parseObject(GetXsy_sql(getproduct_2()));

        JSONObject jsonObject_data2 = jsonObject_2.getJSONObject("data");

        JSONArray jsonArray2 = jsonObject_data2.getJSONArray("records");

        JSONObject jsonObject_3 = JSON.parseObject(GetXsy_sql(getproduct_3()));

        JSONObject jsonObject_data3 = jsonObject_3.getJSONObject("data");

        JSONArray jsonArray3 = jsonObject_data3.getJSONArray("records");

        JSONObject jsonObject_4 = JSON.parseObject(GetXsy_sql(getproduct_4()));

        JSONObject jsonObject_data4 = jsonObject_4.getJSONObject("data");

        JSONArray jsonArray4 = jsonObject_data4.getJSONArray("records");

        jsonArray.addAll(jsonArray2);

        jsonArray.addAll(jsonArray3);

        jsonArray.addAll(jsonArray4);

        return jsonArray;

    }


    /**
     * 产品hash
     */
    public static void HashProduct() throws IOException {


        JSONArray account_records = product_all();

        Map<String, String> product_hash = new HashMap<>();

        for (int i = 0; i < account_records.size();i++){

            JSONObject jsonObject_product = account_records.getJSONObject(i);

            String id = jsonObject_product.getString("id");

            String productName = jsonObject_product.getString("product_FItem__c");

            product_hash.put(productName,id);
        }

        product_map=product_hash;



    }


    /**
     * 交付记录明细hash
     */
    public  static  void HashdeliveryRecordDetail() throws IOException {

        JSONArray jsonArray_hash = GetdeliveryRecordDetail_all();

        Map<String, String> Map = new HashMap<>();

        for (int i = 0;i<jsonArray_hash.size();i++){
            JSONObject jsonObject = jsonArray_hash.getJSONObject(i);
            String id = jsonObject.getString("id");
            String erp_id = jsonObject.getString("customItem1__c");
            Map.put(erp_id,id);
        }

        deliveryRecordDetail_map=Map;

    }


    /**
     * 交付记录hash
     */
    public static void HashdeliveryRecord() throws IOException {

        JSONArray jsonArray_hash = GetdeliveryRecord_all();

        Map<String, String> Map = new HashMap<>();

        for (int i = 0;i<jsonArray_hash.size();i++){
            JSONObject jsonObject = jsonArray_hash.getJSONObject(i);
            String id = jsonObject.getString("id");
            String erp_id = jsonObject.getString("customItem1__c");
            Map.put(erp_id,id);
        }
        deliveryRecord_map = Map;

    }


    /**
     * 客户hash
     */
    public static void HashAccount() throws IOException {


        JSONObject account_json = JSON.parseObject(GetXsy_sql(getaccount()));

        JSONObject account_data = account_json.getJSONObject("data");

        JSONArray account_records = account_data.getJSONArray("records");

        Map<String, String> account_hash = new HashMap<>();
        for (int i = 0; i < account_records.size(); i++) {
            JSONObject jsonObject_account = account_records.getJSONObject(i);
            String id = jsonObject_account.getString("id");
            String accountName = jsonObject_account.getString("accountName");
            account_hash.put(accountName, id);
        }
        //将客户表存入全局变量
        account_map=account_hash;
    }


    /**
     * 订单hash
     */
    public static void Hashorder() throws IOException {

        //预处理销售易得到的订单数据
        JSONObject crm_json_order = JSON.parseObject(GetXsy_sql(getorder()));

        //获取data数据
        JSONObject crm_json_data_order = crm_json_order.getJSONObject("data");

        //获取data里面的数组数据
        JSONArray crm_json_data_order_records = crm_json_data_order.getJSONArray("records");

        //将订单的内码存入哈希表
        Map<String, String> merorderIdMap = new HashMap<>();
        for (int i = 0; i < crm_json_data_order_records.size(); i++) {
            JSONObject itemC = crm_json_data_order_records.getJSONObject(i);
            String merorderid = itemC.getString("order_interid__c");
            String id = itemC.getString("id");
            merorderIdMap.put(merorderid, id);
        }

        //将订单表赋值全局变量
        order_map=merorderIdMap;


    }


    /**
     * 构建一个交付记录请求参数--新增
     *
     * @return String类型
     */
    public  static  String deliveryRecord_job_cre(){

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("operation","insert");
        jsonObject.put("object","deliveryRecord");

        JSONArray jsonArray = new JSONArray();
        jsonArray.add("CHECK_RULE");
        jsonArray.add("CHECK_DUPLICATE");

        jsonObject.put("execOption",jsonArray);

        JSONObject jsonData = new JSONObject();
        jsonData.put("data",jsonObject);

        return jsonData.toString();
    }

    /**
     * 构建一个交付记录请求参数--更新
     *
     * @return String类型
     */
    public  static  String deliveryRecord_job_up(){

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("operation","update");
        jsonObject.put("object","deliveryRecord");

        JSONArray jsonArray = new JSONArray();
        jsonArray.add("CHECK_RULE");
        jsonArray.add("CHECK_DUPLICATE");

        jsonObject.put("execOption",jsonArray);

        JSONObject jsonData = new JSONObject();
        jsonData.put("data",jsonObject);

        return jsonData.toString();
    }


    /**
     * 构建一个交付记录明细请求参数--新增
     *
     * @return String类型
     */
    public  static  String deliveryRecordDetail_job_cre(){

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("operation","insert");
        jsonObject.put("object","deliveryRecordDetail");

        JSONArray jsonArray = new JSONArray();
        jsonArray.add("CHECK_RULE");
        jsonArray.add("CHECK_DUPLICATE");

        jsonObject.put("execOption",jsonArray);

        JSONObject jsonData = new JSONObject();
        jsonData.put("data",jsonObject);

        return jsonData.toString();
    }


    /**
     * 构建一个交付记录明细请求参数--更新
     *
     * @return String类型
     */
    public  static  String deliveryRecordDetail_job_up(){

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("operation","update");
        jsonObject.put("object","deliveryRecordDetail");

        JSONArray jsonArray = new JSONArray();
        jsonArray.add("CHECK_RULE");
        jsonArray.add("CHECK_DUPLICATE");

        jsonObject.put("execOption",jsonArray);

        JSONObject jsonData = new JSONObject();
        jsonData.put("data",jsonObject);

        return jsonData.toString();
    }


    /**
     * 创建异步作业请求函数
     *
     * @return String类型
     */
    public static String bulk_job(String sqlStr) throws IOException {

        //构建一个请求样式
        RkhdHttpData rkhdHttpData = new RkhdHttpData();
        //请求地址，业务逻辑代码的时候这样
        rkhdHttpData.setCallString("/rest/bulk/v2/job");

        //获取请求体
        rkhdHttpData.setBody(sqlStr);

        //请求类型
        rkhdHttpData.setCall_type("POST");

        //发送请求获取请求结果
        String data_deal_before = RkhdHttpClient.instance().performRequest(rkhdHttpData);
        //System.out.printf(data_deal_before);
        return data_deal_before;
    }


    /**
     * 异步作业增加交付记录新增返回处理参数
     *
     * @return JSONObject类型得到创建异步任务的id
     */
    public static JSONObject deal_job_deliveryRecord_cre() throws IOException {

        JSONObject jsonObject = JSON.parseObject(bulk_job(deliveryRecord_job_cre()));

        if(jsonObject.getInteger("code")==200){
            LOG.info("交付新增异步作业创建成功");
        }else{
            LOG.info("交付新增异步任务创建失败:");
            LOG.info(jsonObject.getString("msg"));
            return null;
        }

        JSONObject json_result = jsonObject.getJSONObject("result");

        //输出异步作业参数
        //System.out.printf(json_result.toString());
        LOG.info(json_result.toString());
        return json_result;


    }


    /**
     * 异步作业增加交付记录更新返回处理参数
     *
     * @return JSONObject类型得到创建异步任务的id
     */
    public static JSONObject deal_job_deliveryRecord_up() throws IOException {

        JSONObject jsonObject = JSON.parseObject(bulk_job(deliveryRecord_job_up()));

        if(jsonObject.getInteger("code")==200){
            LOG.info("交付更新异步作业创建成功");
        }else{
            LOG.info("交付更新异步任务创建失败:");
            LOG.info(jsonObject.getString("msg"));
            return null;
        }

        JSONObject json_result = jsonObject.getJSONObject("result");

        //输出异步作业参数
        //System.out.printf(json_result.toString());
        LOG.info(json_result.toString());
        return json_result;


    }


    /**
     * 异步作业更新交付明细记录新增返回处理参数
     *
     * @return JSONObject类型得到创建异步任务的id
     */
    public static JSONObject deal_job_deliveryRecordDetail_cre() throws IOException {

        JSONObject jsonObject = JSON.parseObject(bulk_job(deliveryRecordDetail_job_cre()));

        if(jsonObject.getInteger("code")==200){
            LOG.info("交付明细异步作业创建成功");
        }else{
            LOG.info("交付明细异步任务创建失败:");
            LOG.info(jsonObject.getString("msg"));
            return null;
        }

        JSONObject json_result = jsonObject.getJSONObject("result");

        //输出异步作业参数
        //System.out.printf(json_result.toString());
        LOG.info(json_result.toString());
        return json_result;


    }


    /**
     * 异步作业更新交付明细记录更新返回处理参数
     *
     * @return JSONObject类型得到创建异步任务的id
     */
    public static JSONObject deal_job_deliveryRecordDetail_up() throws IOException {

        JSONObject jsonObject = JSON.parseObject(bulk_job(deliveryRecordDetail_job_up()));

        if(jsonObject.getInteger("code")==200){
            LOG.info("交付明细异步作业创建成功");
        }else{
            LOG.info("交付明细异步任务创建失败:");
            LOG.info(jsonObject.getString("msg"));
            return null;
        }

        JSONObject json_result = jsonObject.getJSONObject("result");

        //输出异步作业参数
        //System.out.printf(json_result.toString());
        LOG.info(json_result.toString());
        return json_result;


    }


    /**
     * 异步任务请求体交付记录新增
     * @param data_josn erp出库数据
     * @param  bulkId 异步作业id
     *
     * @return 返回String
     */
    public static String deliveryRecord_cre_bluk_body(JSONArray data_josn,JSONObject bulkId) throws IOException {



        //定义一个空的对象数组用来存储筛选的数据
        JSONArray datas = new JSONArray();

        for(int i=0;i<data_josn.size();i++){

            JSONObject jsonObject = data_josn.getJSONObject(i);

            //定义一个人临时对象来存储数据
            JSONObject data =new JSONObject();

            //交付状态
            data.put("deliveryStatus", "1");

            //客户id
            if(account_map.containsKey(jsonObject.getString("FSupplyIDName"))){
                data.put("accountId",account_map.get(jsonObject.getString("FSupplyIDName")));
            }else {
                LOG.info("没有查询到这个客户");
                continue;
            }

            //订单id赋值
            if(order_map.containsKey(jsonObject.getString("FOrderInterID"))){
                data.put("orderId",order_map.get(jsonObject.getString("FOrderInterID")));
            }else {
                LOG.info("没有查询到相关订单");
                continue;
            }

            //出库单内码
            data.put("customItem1__c",jsonObject.getString("FInterID"));

            //红蓝标志
            data.put("customItem2__c",jsonObject.getString("FROB"));

            //源单单号
            data.put("customItem3__c",jsonObject.getString("FSourceBillNo"));

            //发货日期
            data.put("customItem4__c",jsonObject.getString("FDate"));

            //系统单号
            data.put("customItem5__c",jsonObject.getString("FBillNo"));

            //Bom单号
            data.put("customItem6__c",jsonObject.getString("FBomNumber"));

            //7，8移动到明细了

            //仓库类型
            data.put("customItem9__c",jsonObject.getString("FDCStockIDName"));

            //订单类型
            data.put("customItem11__c",jsonObject.getString("FText3"));

            //收件人电话
            data.put("customItem12__c",jsonObject.getString("FText2"));

            //快递单号
            data.put("customItem14__c",jsonObject.getString("FText5"));

            //快递公司
            if(Objects.equals(jsonObject.getString("FText4"), "顺丰快递")){
                data.put("customItem15__c",7);
            }else if(Objects.equals(jsonObject.getString("FText4"), "顺丰快递")){
                data.put("customItem15__c",24);
            }else if(Objects.equals(jsonObject.getString("FText4"), "优速快递")){
                data.put("customItem15__c",37);
            }else if(Objects.equals(jsonObject.getString("FText4"), "德邦物流")){
                data.put("customItem15__c",12);
            }

            datas.add(data);


        }

        JSONObject data_end = new JSONObject();

        data_end.put("jobId",bulkId.getString("id"));

        data_end.put("datas",datas);

        JSONObject data_end2 = new JSONObject();

        data_end2.put("data",data_end);

        return data_end2.toString();

    }


    /**
     * 异步任务请求体交付记录更新
     * @param data_josn erp出库数据
     * @param  bulkId 异步作业id
     *
     * @return 返回String
     */
    public static String deliveryRecord_up_bluk_body(JSONArray data_josn,JSONObject bulkId) throws IOException {





        //定义一个空的对象数组用来存储筛选的数据
        JSONArray datas = new JSONArray();

        for(int i=0;i<data_josn.size();i++){

            JSONObject jsonObject = data_josn.getJSONObject(i);

            //定义一个人临时对象来存储数据
            JSONObject data =new JSONObject();


            if(deliveryRecord_map.containsKey(jsonObject.getString("FInterID"))){
                data.put("id",deliveryRecord_map.get(jsonObject.getString("FInterID")));
            }else{
                LOG.info("没有查询到这个销售记录");
                continue;
            }

            //交付状态
            data.put("deliveryStatus", "1");


            //订单id赋值
            if(order_map.containsKey(jsonObject.getString("FOrderInterID"))){
                data.put("orderId",order_map.get(jsonObject.getString("FOrderInterID")));
            }else {
                LOG.info("没有查询到相关订单");
                continue;
            }

            //出库单内码
            data.put("customItem1__c",jsonObject.getString("FInterID"));

            //红蓝标志
            data.put("customItem2__c",jsonObject.getString("FROB"));

            //源单单号
            data.put("customItem3__c",jsonObject.getString("FSourceBillNo"));

            //发货日期
            data.put("customItem4__c",jsonObject.getString("FDate"));

            //系统单号
            data.put("customItem5__c",jsonObject.getString("FBillNo"));

            //Bom单号
            data.put("customItem6__c",jsonObject.getString("FBomNumber"));

            //7，8移动到明细了

            //仓库类型
            data.put("customItem9__c",jsonObject.getString("FDCStockIDName"));

            //订单类型
            data.put("customItem11__c",jsonObject.getString("FText3"));

            //收件人电话
            data.put("customItem12__c",jsonObject.getString("FText2"));

            //快递单号
            data.put("customItem14__c",jsonObject.getString("FText5"));

            //快递公司
            if(Objects.equals(jsonObject.getString("FText4"), "顺丰快递")){
                data.put("customItem15__c",7);
            }else if(Objects.equals(jsonObject.getString("FText4"), "顺丰快递")){
                data.put("customItem15__c",24);
            }else if(Objects.equals(jsonObject.getString("FText4"), "优速快递")){
                data.put("customItem15__c",37);
            }else if(Objects.equals(jsonObject.getString("FText4"), "德邦物流")){
                data.put("customItem15__c",12);
            }

            datas.add(data);


        }

        JSONObject data_end = new JSONObject();

        data_end.put("jobId",bulkId.getString("id"));

        data_end.put("datas",datas);

        JSONObject data_end2 = new JSONObject();

        data_end2.put("data",data_end);

        return data_end2.toString();



    }


    /**
     * 异步任务请求体交付明细记录新增
     * @param data_json erp出库数据
     * @param  bulkId 异步作业id
     *
     * @return 返回String
     */
    public static String deliveryRecordDetail_cre_bluk_body(JSONArray data_json,JSONObject bulkId) throws IOException, ApiEntityServiceException {



        //定义一个空的对象数组用来存储筛选的数据
        JSONArray datas = new JSONArray();

        for(int i=0;i<data_json.size();i++){

            JSONObject jsonObject = data_json.getJSONObject(i);

            //定义一个人临时对象来存储数据
            JSONObject data =new JSONObject();

            //结清状态
            data.put("settleStatus", "0");

            //交付记录
           if(deliveryRecord_map.containsKey(jsonObject.getString("FInterID"))){
               data.put("deliveryRecordId",deliveryRecord_map.get(jsonObject.getString("FInterID")));
           }else{
               LOG.info("没有查询到这个交付记录表"+i+":内码为"+jsonObject.getString("FInterID"));
               continue;
           }

            //订单id赋值
            if(order_map.containsKey(jsonObject.getString("FOrderInterID"))){
                data.put("orderId",order_map.get(jsonObject.getString("FOrderInterID")));
            }else {
                LOG.info("没有查询到相关订单");
                continue;
            }

            //产品赋值
            if(product_map.containsKey(jsonObject.getString("FItemID"))){
                data.put("productId",product_map.get(jsonObject.getString("FItemID")));
            }else{
                LOG.info("没有查询到相关产品");
                continue;
            }


            //获取订单明细编号--看一下还没有什么办法优化没有
            List<XObject> records4 = XObjectService
                    .instance()
                    .query("SELECT id FROM orderProduct "
                            +"WHERE orderId = "
                            +"'"+order_map.get(jsonObject.getString("FOrderInterID"))+"'"
                            + " and "
                            +" productId = "
                            +"'"+product_map.get(jsonObject.getString("FItemID"))+"'")
                    .getRecords();

            try {
                OrderProduct orderProduct = (OrderProduct) records4.get(0);
                //配置订单明细编号
                data.put("orderProductId",orderProduct.getId());
            }catch (Exception e){
                LOG.info("没有查询到这个订单明细");
                continue;
            }


            //配置交付数量
            data.put("quantityDelivered",jsonObject.getDouble("FAuxQty"));

            String Finterid = jsonObject.getString("FInterID")+jsonObject.getString("FEntryID");

            //配置出库明细内码
            data.put("customItem1__c",Finterid);

            //不含税单价
            data.put("customItem2__c",jsonObject.getDoubleValue("FAuxPriceNoTax"));

            //不含税金额
            data.put("customItem3__c",jsonObject.getDoubleValue("FAmountNoTax"));

            //含税单价
            data.put("customItem4__c",jsonObject.getDoubleValue("FAuxTaxPrice"));

            //含税金额
            data.put("customItem5__c",jsonObject.getDoubleValue("FConsignAmount"));

            //税率
            data.put("customItem6__c",jsonObject.getDoubleValue("FTaxRate"));

            //收货地址
            data.put("contactAddress",jsonObject.getString("FFetchAdd"));

            //联系人
            data.put("contactName",jsonObject.getString("FLXR"));

            //联系电话
            data.put("contactTel",jsonObject.getString("FLXRDH"));

            datas.add(data);

        }

        JSONObject data_end = new JSONObject();

        data_end.put("jobId",bulkId.getString("id"));

        data_end.put("datas",datas);

        JSONObject data_end2 = new JSONObject();

        data_end2.put("data",data_end);

        return data_end2.toString();
    }


    /**
     * 异步任务请求体交付明细记录更新
     * @param data_json erp出库数据
     * @param  bulkId 异步作业id
     *
     * @return 返回String
     */
    public static String deliveryRecordDetail_up_bluk_body(JSONArray data_json,JSONObject bulkId) throws ApiEntityServiceException, IOException {





        //定义一个空的对象数组用来存储筛选的数据
        JSONArray datas = new JSONArray();

        for(int i=0;i<data_json.size();i++){

            JSONObject jsonObject = data_json.getJSONObject(i);

            //定义一个人临时对象来存储数据
            JSONObject data =new JSONObject();

            //结清状态
            data.put("settleStatus", "0");

            String number = jsonObject.getString("FInterID")+jsonObject.getString("FEntryID");
            if(deliveryRecordDetail_map.containsKey(number)){
                data.put("id",deliveryRecordDetail_map.get(number));
            }else{
                LOG.info("没有查询到这个交付记录明细");
                continue;
            }


            //订单id赋值
            if(order_map.containsKey(jsonObject.getString("FOrderInterID"))){
                data.put("orderId",order_map.get(jsonObject.getString("FOrderInterID")));
            }else {
                LOG.info("没有查询到相关订单");
                continue;
            }

            //产品赋值
            if(product_map.containsKey(jsonObject.getString("FItemID"))){
                data.put("productId",product_map.get(jsonObject.getString("FItemID")));
            }else{
                LOG.info("没有查询到相关产品");
                continue;
            }


            //获取订单明细编号--看一下还没有什么办法优化没有
//            List<XObject> records4 = XObjectService
//                    .instance()
//                    .query("SELECT id FROM orderProduct "
//                            +"WHERE orderId = "
//                            +"'"+order_map.get(jsonObject.getString("FOrderInterID"))+"'"
//                            + " and "
//                            +" productId = "
//                            +"'"+product_map.get(jsonObject.getString("FItemID"))+"'")
//                    .getRecords();
//
//            OrderProduct orderProduct = (OrderProduct) records4.get(0);
//
//            //配置订单明细编号
//            data.put("orderProductId",orderProduct.getId());

            //配置交付数量
            data.put("quantityDelivered",jsonObject.getDouble("FAuxQty"));

            String Finterid = jsonObject.getString("FInterID")+jsonObject.getString("FEntryID");

            //配置出库明细内码
            data.put("customItem1__c",Finterid);

            //不含税单价
            data.put("customItem2__c",jsonObject.getDoubleValue("FAuxPriceNoTax"));

            //不含税金额
            data.put("customItem3__c",jsonObject.getDoubleValue("FAmountNoTax"));

            //含税单价
            data.put("customItem4__c",jsonObject.getDoubleValue("FAuxTaxPrice"));

            //含税金额
            data.put("customItem5__c",jsonObject.getDoubleValue("FConsignAmount"));

            //税率
            data.put("customItem6__c",jsonObject.getDoubleValue("FTaxRate"));

            //收货地址
            data.put("contactAddress",jsonObject.getString("FFetchAdd"));

            //联系人
            data.put("contactName",jsonObject.getString("FLXR"));

            //联系电话
            data.put("contactTel",jsonObject.getString("FLXRDH"));

            datas.add(data);

        }

        JSONObject data_end = new JSONObject();

        data_end.put("jobId",bulkId.getString("id"));

        data_end.put("datas",datas);

        JSONObject data_end2 = new JSONObject();

        data_end2.put("data",data_end);

        return data_end2.toString();
    }


    /**
     * 异步任务请求函数
     * @param sqlStr 异步任务请求参数
     *
     * @return 返回String
     */
    public static String batch_job(String sqlStr) throws IOException {

        //构建一个请求样式
        RkhdHttpData rkhdHttpData = new RkhdHttpData();
        //请求地址，业务逻辑代码的时候这样
        rkhdHttpData.setCallString("/rest/bulk/v2/batch");

        //获取请求体
        rkhdHttpData.setBody(sqlStr);

        //请求类型
        rkhdHttpData.setCall_type("POST");

        //发送请求获取请求结果
        String data_deal_before = RkhdHttpClient.instance().performRequest(rkhdHttpData);

        //异步任务返回参数
        //System.out.printf(data_deal_before);
        LOG.info(data_deal_before);
        return data_deal_before;
    }


    /**
     * 交付记录的新增和更新最终处理函数
     */
    public static void deal_deliveryRecord_end() throws IOException {

        //交付记录hash表赋值
        HashdeliveryRecord();

        //客户表赋值
        HashAccount();

        //订单表赋值
        Hashorder();

        //处理erp出库数据判读新增和更新
        JSONArray jsonArray = deal_deliveryRecord();

        //新增的数据
        JSONArray jsonArray_cre = new JSONArray();

        //更新的数据
        JSONArray jsonArray_up = new JSONArray();

        //处理数据
        for(int i = 0;i<jsonArray.size();i++){
            JSONObject jsonObject_temp = jsonArray.getJSONObject(i);
            if(deliveryRecord_map.containsKey(jsonObject_temp.getString("FInterID"))){
                jsonArray_up.add(jsonObject_temp);
            }else{
                jsonArray_cre.add(jsonObject_temp);
            }
        }


        //新增交付异步作业id
        JSONObject bluk_id_cre = deal_job_deliveryRecord_cre();

        //更新交付异步作业id
        JSONObject bluk_id_up =  deal_job_deliveryRecord_up();

        //组装新增异步任务请求体
        String bluk_job_body_cre = deliveryRecord_cre_bluk_body(jsonArray_cre,bluk_id_cre);

        //组装更新异步任务请求体
        String bluk_job_body_up = deliveryRecord_up_bluk_body(jsonArray_up,bluk_id_up);

        //新增异步任务请求函数
        batch_job(bluk_job_body_cre);

        //更新异步任务请求函数
        batch_job(bluk_job_body_up);

    }


    /**
     * 交付记录明细新增和更新最终处理函数
     */
    public static void deal_deliveryRecordDetail_end() throws IOException, ApiEntityServiceException {

        //交付记录明细表赋值
        HashdeliveryRecordDetail();

        //交付记录表赋值
        HashdeliveryRecord();

        //订单表赋值
        Hashorder();

        //产品表赋值
        HashProduct();

        JSONArray jsonArray = deal_delivery();

        //新增的数据
        JSONArray jsonArray_cre = new JSONArray();

        //更新的数据
        JSONArray jsonArray_up = new JSONArray();

        //处理数据
        for(int i = 0;i<jsonArray.size();i++){
            JSONObject jsonObject_temp = jsonArray.getJSONObject(i);
            String number = jsonObject_temp.getString("FInterID")+jsonObject_temp.getString("FEntryID");
            if(deliveryRecordDetail_map.containsKey(number)){
                jsonArray_up.add(jsonObject_temp);
            }else{
                jsonArray_cre.add(jsonObject_temp);
            }
        }

        //新增交付异步作业id
        JSONObject bluk_id_cre = deal_job_deliveryRecordDetail_cre();

        //更新交付异步作业id
        JSONObject bluk_id_up =  deal_job_deliveryRecordDetail_up();

        //组装新增异步任务请求体
        String bluk_job_body_cre = deliveryRecordDetail_cre_bluk_body(jsonArray_cre,bluk_id_cre);

        //组装更新异步任务请求体
        String bluk_job_body_up = deliveryRecordDetail_up_bluk_body(jsonArray_up,bluk_id_up);

        //新增异步任务请求函数
        batch_job(bluk_job_body_cre);

        //更新异步任务请求函数
        batch_job(bluk_job_body_up);

    }


    /**
     * 集合函数方便调用
     */
    public static void deal_delivery_all() throws IOException, ApiEntityServiceException {
        deal_deliveryRecord_end();
        deal_deliveryRecordDetail_end();
    }

    /**
     * 异步函数以及自定义接口调用
     */
    @RestMapping(value = "/ybldeliveryRecord", method = RequestMethod.GET)
    public static String deliReFutureTaskBlukApi() throws AsyncTaskException {

        String messageId = FutureTaskService
                .instance()
                .addFutureTask(deliReFutureTaskBlukApi.class, "");
        LOG.info("同步交付记录和交付明细异步批量接口id:" + messageId);
        return messageId;
    }


    public static void main(String[] args) throws IOException, ApiEntityServiceException {

        deal_deliveryRecord_end();

        deal_deliveryRecordDetail_end();


    }






}
