package other.erp2crm.api;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rkhd.platform.sdk.api.annotations.RequestMethod;
import com.rkhd.platform.sdk.api.annotations.RestApi;
import com.rkhd.platform.sdk.api.annotations.RestMapping;
import com.rkhd.platform.sdk.data.model.*;
import com.rkhd.platform.sdk.exception.ApiEntityServiceException;
import com.rkhd.platform.sdk.exception.AsyncTaskException;
import com.rkhd.platform.sdk.exception.BatchJobException;
import com.rkhd.platform.sdk.http.*;
import com.rkhd.platform.sdk.log.Logger;
import com.rkhd.platform.sdk.log.LoggerFactory;
import com.rkhd.platform.sdk.model.OperateResult;
import com.rkhd.platform.sdk.model.XObject;
import com.rkhd.platform.sdk.service.BatchJobProService;
import com.rkhd.platform.sdk.service.FutureTaskService;
import com.rkhd.platform.sdk.service.XObjectService;

import java.io.IOException;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.util.*;

/**
 *
 * Title:erp出库数据同步crm
 * Description:略
 * @author    ldl
 * @version   1.0, 2024-08-21
 *
 */

@RestApi(baseUrl = "/ybl")
public class ybldeliveryRecord {

    //先定义一个日志
    private final static Logger LOG = LoggerFactory.getLogger();

    //获取销售易最新一条交付记录信息sql
    public static String deRecord(){

        String str = "select id,customItem5__c" +
                " from deliveryRecord "+
                " where totalQuantity <> 0"+
                " order by name desc"+
                " limit 1";
        return str;
    }

    //定义一个交付图表
    private static Map<String, String> deliveryRecord_map = null;

    //定义一个交付明细图表
    private static Map<String, String> deliveryRecordDetail_map = null;

    //定义一个客户的表
    private static Map<String, String> account_map = null;


    //定义一个订单的表
    private static Map<String, String> order_map = null;


    //定一个产品表
    private static Map<String, String> product_map = null;


    //获取销售易交付记录信息-部分1
    public static String GetdeliveryRecord_1(){

        String query_one ="select id,customItem1__c " +
                " from deliveryRecord" +
                " order by updatedAt desc" +
                " limit 0,3000";
        return query_one;

    }

    //获取销售易交付记录信息-部分2
    public static String GetdeliveryRecord_2(){

        String query_one ="select id,customItem1__c " +
                " from deliveryRecord" +
                " order by updatedAt desc" +
                " limit 3000,3000";
        return query_one;

    }


    //获取销售易交付记录明细-部分1
    public static String GetdeliveryRecordDetail_1(){

        String query_one ="select id,customItem1__c " +
                " from deliveryRecordDetail" +
                " order by updatedAt desc" +
                " limit 0,3000";
        return query_one;

    }


    //获取销售易交付记录明细-部分2
    public static String GetdeliveryRecordDetail_2(){

        String query_one ="select id,customItem1__c " +
                " from deliveryRecordDetail" +
                " order by updatedAt desc" +
                " limit 3000,3000";
        return query_one;

    }


    //获取销售易交付记录明细-部分3
    public static String GetdeliveryRecordDetail_3(){

        String query_one ="select id,customItem1__c " +
                " from deliveryRecordDetail" +
                " order by updatedAt desc" +
                " limit 6000,3000";
        return query_one;

    }


    //获取客户信息
    public  static  String getaccount(){

        String query_one ="select id,accountName " +
                " from account" +
                " order by updatedAt desc" +
                " limit 4000";
        return query_one;

    }


    //获取订单信息
    public static String getorder(){

        String query_one ="select id,order_interid__c " +
                " from order" +
                " order by updatedAt desc" +
                " limit 4000";
        return query_one;

    }


    //获取产品信息部分1
    public static String getproduct_1(){

        String query_one ="select id,product_FItem__c " +
                " from product" +
                " order by updatedAt desc" +
                " limit 0,4000";
        return query_one;

    }


    //获取产品信息部分2
    public static String getproduct_2(){

        String query_one ="select id,product_FItem__c " +
                " from product" +
                " order by updatedAt desc" +
                " limit 4000,4000";
        return query_one;

    }


    //获取产品信息部分3
    public static String getproduct_3(){

        String query_one ="select id,product_FItem__c " +
                " from product" +
                " order by updatedAt desc" +
                " limit 8000,4000";
        return query_one;

    }

    //获取产品信息部分4
    public static String getproduct_4(){

        String query_one ="select id,product_FItem__c " +
                " from product" +
                " order by updatedAt desc" +
                " limit 12000,4000";
        return query_one;

    }


    //销售易请求函数
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

        //LOG.info(data_deal_before);
        return data_deal_before;
    }


    //对交付记录进行拼接
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


    //对交付记录明细进行拼接
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


    //对产品进行拼接
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



    //将交付记录存储为哈希表
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


    //将交付记录明细存储为哈希表
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


    //将产品数据存储为哈希表
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


    //将客户数据存储为哈希表
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


    //将订单信息存储为哈希表
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





    //处理得到的最新一条系统单号数据
    public static String deal_deRecord_data() throws IOException {

        String str = GetXsy_sql(deRecord());
        JSONObject jsonObject = JSON.parseObject(str);
        if(jsonObject.getInteger("code")>200){
            return null;
        }
        JSONObject jsonObject_data=jsonObject.getJSONObject("data");

        if(jsonObject_data.getInteger("totalSize")==0){
            return null;
        }

        JSONArray jsonArray = jsonObject_data.getJSONArray("records");

        String str2 = jsonArray.get(0).toString();

        JSONObject jsonObject1 = JSON.parseObject(str2);

        //System.out.printf(jsonObject1.getString("customItem5__c"));

        return  jsonObject1.getString("customItem5__c");

    }

    //获取erp出库信息
    public static String ybl_get_delivery() throws IOException {

        String base_url = "具体接口地址";

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
        String billno = deal_deRecord_data();

        //增加一个最新的单号来减少获取的数据量
        String filter_data = "{\"Fbillno1\":\"" +
                billno +
                "\",\"Fbillno2\":\"XOUT918793\"," +
                "\"FDate1\":\"" +
                date +
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
                billno +
                "\",\"Fbillno2\":\"XOUT918793\",\"FDate1\":\"" +
                date +
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

        return result.getResult();
    }


    //处理得到出库数据--先做一次数据请求判断--也是出库明细数据
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


    //得到具体出库数据，筛选出库单--根据erp做的特殊操作
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


    //出库增加和更新接口模版
    public static Result deal_deliveryRecord_data() throws IOException {

        JSONArray data = deal_deliveryRecord();

        // 定义一个列表来存储处理结果
        List<String> results = new ArrayList<>();


        //交付记录图表赋值
        HashdeliveryRecord();

        //客户图表赋值
        HashAccount();

        //订单表赋值
        Hashorder();



        // 因为获取的接口参数形式直接是一个对象数组，所以需要进行循环判定
        for (Object object : data) {
            JSONObject jsonObject = (JSONObject) object;

            //出库单状态
            jsonObject.put("deliveryStatus", "1");


            //通过erp的出库内码来查询
            String number = jsonObject.getString("FInterID");

            //做一个开关
            int ns = 0;
            if(deliveryRecord_map.containsKey(number)){
                ns=1;
            }



            LOG.info("ERP出库编号:" + number);

            try {

                //LOG.info("查询CRM系统中是否存在ERP的出库sql:" + "SELECT id FROM deliveryRecord WHERE customItem1__c = " + number);

                //List<XObject> records = XObjectService.instance().query("SELECT id FROM deliveryRecord WHERE customItem1__c = " + number).getRecords();

                //LOG.info("查询结果:" + records);

                //旧的
                //records == null || records.isEmpty()
                if (ns==0) {

                    LOG.info("结果为空,走创建逻辑");
                    OperateResult cdr = createdeliveryRecord(new DeliveryRecord(), jsonObject);
                    results.add("创建出库单,创建结果:" + JSONObject.toJSONString(cdr));
                } else {

                    LOG.info("结果不为空,走更新逻辑");
                    //DeliveryRecord deliveryRecord = (DeliveryRecord) records.get(0);
                    DeliveryRecord deliveryRecord = new DeliveryRecord();
                    deliveryRecord.setId(Long.valueOf(deliveryRecord_map.get(number)));
                    OperateResult udr = updatedeliveryRecord(deliveryRecord, jsonObject);
                    results.add("更新出库单 ,更新结果:" + JSONObject.toJSONString(udr));
                }
            } catch (Exception e) {
                LOG.error("同步出库" + number + "数据失败" + e.getMessage(), e);
                //注释掉遇到异常继续循环，一组数据里面可能只是部分符合所以得全部循环完成
                //return Result.resultFailWrapper(5000001L, e.getMessage());
            }
        }

        // 返回所有产品处理结果
        return Result.resultSuccessWrapper(results);

    }


    //出库明细增加和更新接口模板
    //@RestMapping(value = "/ybldeliveryRecord", method = RequestMethod.GET)
    public static Result deal_deliveryRecordDetail_data() throws IOException {


        //先处理出库信息
        //deal_deliveryRecord_data();

        JSONArray data = deal_delivery();

        // 定义一个列表来存储处理结果
        List<String> results = new ArrayList<>();

        //交付明细赋值
        HashdeliveryRecordDetail();

        //交付单赋值
        HashdeliveryRecord();

        //产品表赋值
        HashProduct();

        //订单表赋值--交付记录模块一起调用的时候不需要再赋值
        Hashorder();


        for (Object object : data) {
            JSONObject jsonObject = (JSONObject) object;

            jsonObject.put("settleStatus", "0");

            //拼接出库内码和明细码作为出库明细的唯一id
            String number = jsonObject.getString("FInterID")+jsonObject.getString("FEntryID");

            int ns= 0;
            if(deliveryRecordDetail_map.containsKey(number)){
                ns = 1;
            }

            LOG.info("ERP出库明细编号:" + number);

            try {

                //LOG.info("查询CRM系统中是否存在ERP的出库明细sql:" + "SELECT id FROM deliveryRecordDetail WHERE customItem1__c = " + number);

                //List<XObject> records = XObjectService.instance().query("SELECT id FROM deliveryRecordDetail WHERE customItem1__c = " + number).getRecords();

                //LOG.info("查询结果:" + records);

                //旧的
                //records == null || records.isEmpty()
                if (ns==0) {
                    LOG.info("结果为空,走创建逻辑");
                    OperateResult cdde = createdeliveryRecordDetailEntry(new DeliveryRecordDetail(), jsonObject);
                    results.add("创建出库明细,创建结果:" + JSONObject.toJSONString(cdde));
                } else {
                    LOG.info("结果不为空,走更新逻辑");
                    //DeliveryRecordDetail deliveryRecordDetail = (DeliveryRecordDetail) records.get(0);
                    DeliveryRecordDetail deliveryRecordDetail = new DeliveryRecordDetail();
                    deliveryRecordDetail.setId(Long.valueOf(deliveryRecordDetail_map.get(number)));
                    OperateResult udrd = updatedeliveryRecordDetail(deliveryRecordDetail, jsonObject);
                    results.add("更新出库明细,更新结果:" + JSONObject.toJSONString(udrd));
                }
            } catch (Exception e) {
                LOG.error("同步出库明细" + number + "数据失败" + e.getMessage(), e);
                //注释掉遇到异常继续循环，一组数据里面可能只是部分符合所以得全部循环完成
                //return Result.resultFailWrapper(5000001L, e.getMessage());
            }
        }

        // 返回处理结果
        return Result.resultSuccessWrapper(results);
    }



    //新增出库配置函数
    private static OperateResult createdeliveryRecord(DeliveryRecord deliveryRecord, JSONObject data) throws ApiEntityServiceException {

        //获取用户
        String accountName = data.getString("FSupplyIDName");

        if(account_map.containsKey(accountName)){
            deliveryRecord.setAccountId(Long.valueOf(account_map.get(accountName)));
        }else {
            LOG.info("没有查询到客户名称");
            return null;
        }
        //List<XObject> records = XObjectService.instance().query("SELECT id FROM account WHERE accountName = " + "'"+accountName+"'").getRecords();
        //Account acc = (Account) records.get(0);
        //用户赋值
        //deliveryRecord.setAccountId(acc.getId());


        //获取订单编号
        String orderId = data.getString("FOrderInterID");
        if(order_map.containsKey(orderId)){
            deliveryRecord.setOrderId(Long.valueOf(order_map.get(orderId)));
        }else {
            LOG.info("没有查询到相关订单");
            return null;
        }



        //List<XObject> records2 = XObjectService.instance().query("SELECT id FROM order WHERE order_interid__c = " + orderId).getRecords();
        //Order or = (Order) records2.get(0);
        //配置订单编号
        //deliveryRecord.setOrderId(or.getId());




        //出库状态
        deliveryRecord.setDeliveryStatus(data.getInteger("deliveryStatus"));

        //erp收款单内码
        deliveryRecord.setCustomItem1__c(data.getString("FInterID"));

        //红蓝标志
        deliveryRecord.setCustomItem2__c(data.getString("FROB"));

        //源单单号
        deliveryRecord.setCustomItem3__c(data.getString("FSourceBillNo"));

        //发货日期
        deliveryRecord.setCustomItem4__c(data.getString("FDate"));

        //系统单号
        deliveryRecord.setCustomItem5__c(data.getString("FBillNo"));

        //Bom单号
        deliveryRecord.setCustomItem6__c(data.getString("FBomNumber"));

        //含税金额--这个是明细的金额注释掉
        //deliveryRecord.setCustomItem7__c(data.getString("FConsignAmount"));

        //未含税金额--这个是明细的金额不需要
        //deliveryRecord.setCustomItem8__c(data.getString("FAmountNoTax"));

        //仓库类型
        deliveryRecord.setCustomItem9__c(data.getString("FDCStockIDName"));

        //订单类型
        deliveryRecord.setCustomItem11__c(data.getString("FText3"));

        //收件人电话
        deliveryRecord.setCustomItem12__c(data.getString("FText2"));



        //快递单号
        deliveryRecord.setCustomItem14__c(data.getString("FText5"));



        if(Objects.equals(data.getString("FText4"), "顺丰快递")){
            //顺丰快递
            deliveryRecord.setCustomItem15__c(7);
        }
        else if (Objects.equals(data.getString("FText4"), "跨速物流")){
            deliveryRecord.setCustomItem15__c(24);
        }
        else if (Objects.equals(data.getString("FText4"), "优速快递")){
            deliveryRecord.setCustomItem15__c(37);
        }
        else if (Objects.equals(data.getString("FText4"), "德邦物流")){
            deliveryRecord.setCustomItem15__c(12);
        }


        return create_deliveryRecord_res(deliveryRecord);
    }


    //新增出库返回函数
    private static OperateResult create_deliveryRecord_res(DeliveryRecord deliveryRecord) throws ApiEntityServiceException {
        OperateResult result = XObjectService.instance().insert(deliveryRecord);
        if (!result.getSuccess()) {
            throw new ApiEntityServiceException(result.getErrorMessage());
        }
        return result;
    }


    //新增出库明细配置函数
    private static OperateResult createdeliveryRecordDetailEntry(DeliveryRecordDetail deliveryRecordDetail, JSONObject data) throws ApiEntityServiceException {

        //获取出库单的id
        String deliveryRecordId = data.getString("FInterID");

        if(deliveryRecord_map.containsKey(deliveryRecordId)){
            deliveryRecordDetail.setDeliveryRecordId(Long.valueOf(deliveryRecord_map.get(deliveryRecordId)));
        }else {
            LOG.info("没有查询到这个出库单");
            return null;
        }

        //List<XObject> records = XObjectService.instance().query("SELECT id FROM deliveryRecord WHERE customItem1__c = " + deliveryRecordId).getRecords();
        //DeliveryRecord deliveryRecord = (DeliveryRecord) records.get(0);
        //配置出库单编号
        //deliveryRecordDetail.setDeliveryRecordId(deliveryRecord.getId());


        //获取订单编号
        String orderId = data.getString("FOrderInterID");
        if(order_map.containsKey(orderId)){
            deliveryRecordDetail.setOrderId(Long.valueOf(order_map.get(orderId)));
        }else {
            LOG.info("没有查询到这个订单");
            return null;
        }

        //List<XObject> records2 = XObjectService.instance().query("SELECT id FROM order WHERE order_interid__c = " + orderId).getRecords();
        //Order or = (Order) records2.get(0);
        //配置订单编号
        //deliveryRecordDetail.setOrderId(or.getId());


        //获取产品编号--这里需要改8月14日
        String productId = data.getString("FItemID");

        if(product_map.containsKey(productId)){
            deliveryRecordDetail.setProductId(Long.valueOf(product_map.get(productId)));
        }else {
            LOG.info("没有查到产品");
            return null;
        }


        //List<XObject> records3 = XObjectService.instance().query("SELECT id FROM product WHERE product_FItem__c = " + "'"+productId+"'").getRecords();
        //Product pro =(Product) records3.get(0);
        //配置产品编号
        //deliveryRecordDetail.setProductId(pro.getId());

        //获取订单明细编号
        List<XObject> records4 = XObjectService
                .instance()
                .query("SELECT id FROM orderProduct "+"WHERE orderId = "+"'"+order_map.get(orderId)+"'"+ " and " +" productId = "+"'"+product_map.get(productId)+"'")
                .getRecords();

        OrderProduct orderProduct = (OrderProduct) records4.get(0);

        //配置订单明细编号
        deliveryRecordDetail.setOrderProductId(orderProduct.getId());

        //配置结清状态
        deliveryRecordDetail.setSettleStatus(data.getBoolean("settleStatus"));

        //交付数量
        deliveryRecordDetail.setQuantityDelivered(data.getDouble("FAuxQty"));

        String Finterid = data.getString("FInterID")+data.getString("FEntryID");

        //出库明细内码
        deliveryRecordDetail.setCustomItem1__c(Finterid);

        //不含税单价
        deliveryRecordDetail.setCustomItem2__c(data.getDoubleValue("FAuxPriceNoTax"));

        //不含税金额
        deliveryRecordDetail.setCustomItem3__c(data.getDoubleValue("FAmountNoTax"));

        //含税单价
        deliveryRecordDetail.setCustomItem4__c(data.getDoubleValue("FAuxTaxPrice"));

        //含税金额
        deliveryRecordDetail.setCustomItem5__c(data.getDoubleValue("FConsignAmount"));

        //税率
        deliveryRecordDetail.setCustomItem6__c(data.getString("FTaxRate"));

        //收货地址
        deliveryRecordDetail.setContactAddress(data.getString("FFetchAdd"));

        //联系人
        deliveryRecordDetail.setContactName(data.getString("FLXR"));

        //联系人电话
        deliveryRecordDetail.setContactTel(data.getString("FLXRDH"));

        return create_deliveryRecordDetail_res(deliveryRecordDetail);
    }


    //新增出库明细返回函数
    private static OperateResult create_deliveryRecordDetail_res(DeliveryRecordDetail deliveryRecordDetail) throws ApiEntityServiceException {
        OperateResult result = XObjectService.instance().insert(deliveryRecordDetail);
        if (!result.getSuccess()) {
            throw new ApiEntityServiceException(result.getErrorMessage());
        }
        return result;
    }


    //更新出库配置函数
    private static OperateResult updatedeliveryRecord(DeliveryRecord deliveryRecord, JSONObject data) throws ApiEntityServiceException {


        //获取订单编号
        String orderId = data.getString("FOrderInterID");
        if (order_map.containsKey(orderId)){
            deliveryRecord.setOrderId(Long.valueOf(order_map.get(orderId)));
        }else {
            LOG.info("没有查询到相关订单");
            return null;
        }


        //List<XObject> records2 = XObjectService.instance().query("SELECT id FROM order WHERE order_interid__c = " + orderId).getRecords();
        //Order or = (Order) records2.get(0);
        //配置订单编号
        //deliveryRecord.setOrderId(or.getId());



        //红蓝标志
        deliveryRecord.setCustomItem2__c(data.getString("FROB"));

        //源单单号
        deliveryRecord.setCustomItem3__c(data.getString("FSourceBillNo"));

        //发货日期
        deliveryRecord.setCustomItem4__c(data.getString("FDate"));

        //系统单号
        deliveryRecord.setCustomItem5__c(data.getString("FBillNo"));

        //Bom单号
        deliveryRecord.setCustomItem6__c(data.getString("FBomNumber"));


        //含税金额
        //deliveryRecord.setCustomItem7__c(data.getString("FConsignAmount"));

        //未含税金额
        //deliveryRecord.setCustomItem8__c(data.getString("FAmountNoTax"));

        //仓库类型
        deliveryRecord.setCustomItem9__c(data.getString("FDCStockIDName"));


        //订单类型
        deliveryRecord.setCustomItem11__c(data.getString("FText3"));

        //收件人电话
        deliveryRecord.setCustomItem12__c(data.getString("FText2"));

        //快递公司
        //deliveryRecord.setCustomItem13__c(data.getString("FText4"));

        //快递单号
        deliveryRecord.setCustomItem14__c(data.getString("FText5"));

        //deliveryRecord.setCustomItem15__c(7);


        if(Objects.equals(data.getString("FText4"), "顺丰快递")){
            //顺丰快递
            deliveryRecord.setCustomItem15__c(7);
        }
        else if (Objects.equals(data.getString("FText4"), "跨速物流")){
            deliveryRecord.setCustomItem15__c(24);
        }
        else if (Objects.equals(data.getString("FText4"), "优速快递")){
            deliveryRecord.setCustomItem15__c(37);
        }
        else if (Objects.equals(data.getString("FText4"), "德邦物流")){
            deliveryRecord.setCustomItem15__c(12);
        }

        // 跨越速运：一般是14位数字，以KYE开头
        if (data.getString("FText5").matches("^KYE\\d{11}$")) {
            //跨速物流
            deliveryRecord.setCustomItem15__c(24);
        }

        OperateResult result = update(deliveryRecord);
        LOG.info("更新出库结果:" +  JSONObject.toJSONString(result));
        return result;
    }



    //更新出库明细配置函数
    private static OperateResult updatedeliveryRecordDetail(DeliveryRecordDetail deliveryRecordDetail, JSONObject data) throws ApiEntityServiceException {


        //获取出库单的id
        //String deliveryRecordId = data.getString("FInterID");
        //List<XObject> records = XObjectService.instance().query("SELECT id FROM deliveryRecord WHERE customItem1__c = "  +deliveryRecordId).getRecords();
        //DeliveryRecord deliveryRecord = (DeliveryRecord) records.get(0);

        //配置出库单编号
        //deliveryRecordDetail.setDeliveryRecordId(deliveryRecord.getId());


        //获取订单编号
        String orderId = data.getString("FOrderInterID");
        if(order_map.containsKey(orderId)){
            deliveryRecordDetail.setOrderId(Long.valueOf(order_map.get(orderId)));
        }else {
            LOG.info("没有查询到订单信息");
            return null;
        }

        //List<XObject> records2 = XObjectService.instance().query("SELECT id FROM order WHERE order_interid__c = " + orderId).getRecords();
        //Order or = (Order) records2.get(0);
        //配置订单编号
        //deliveryRecordDetail.setOrderId(or.getId());


        //获取产品编号
        String productId = data.getString("FItemID");
        if(product_map.containsKey(productId)){
            deliveryRecordDetail.setProductId(Long.valueOf(product_map.get(productId)));
        }else {
            LOG.info("没有查询到这个产品");
            return null;
        }
        //List<XObject> records3 = XObjectService.instance().query("SELECT id FROM product WHERE product_FItem__c = " + "'"+productId+"'").getRecords();
        //Product pro =(Product) records3.get(0);
        //配置产品编号
        //deliveryRecordDetail.setProductId(pro.getId());

        //获取订单明细编号
//        List<XObject> records4 = XObjectService
//                .instance()
//                .query("SELECT id FROM orderProduct "+"WHERE orderId = "+order_map.get(orderId)+ " and " +" productId = "+product_map.get(productId))
//                .getRecords();

        //OrderProduct orderProduct = (OrderProduct) records4.get(0);

        //配置订单明细编号
        //deliveryRecordDetail.setOrderProductId(orderProduct.getId());

        //配置结清状态
        deliveryRecordDetail.setSettleStatus(data.getBoolean("settleStatus"));

        //交付数量
        deliveryRecordDetail.setQuantityDelivered(data.getDouble("FAuxQty"));

        String Finterid = data.getString("FInterID")+data.getString("FEntryID");
        //出库明细内码
        deliveryRecordDetail.setCustomItem1__c(Finterid);

        //不含税单价
        deliveryRecordDetail.setCustomItem2__c(data.getDoubleValue("FAuxPriceNoTax"));

        //不含税金额
        deliveryRecordDetail.setCustomItem3__c(data.getDoubleValue("FAmountNoTax"));

        //含税单价
        deliveryRecordDetail.setCustomItem4__c(data.getDoubleValue("FAuxTaxPrice"));

        //含税金额
        deliveryRecordDetail.setCustomItem5__c(data.getDoubleValue("FConsignAmount"));

        //税率
        deliveryRecordDetail.setCustomItem6__c(data.getString("FTaxRate"));

        OperateResult result = update(deliveryRecordDetail);
        LOG.info("更新出库明细结果:" +  JSONObject.toJSONString(result));
        return result;
    }


    //更新数据
    private static OperateResult update(XObject xObject) throws ApiEntityServiceException {
        OperateResult result = XObjectService.instance().update(xObject);
        if (!result.getSuccess()) {
            throw new ApiEntityServiceException(result.getErrorMessage());
        }
        return result;
    }


    //异步作业销售易这个设定很鸡肋只是为了获得90秒的运行时间，配合自定义接口使用
    //@RestMapping(value = "/ybldeliveryRecord", method = RequestMethod.GET)
    public static String deliFutureTask() throws AsyncTaskException {

        String messageId = FutureTaskService
                .instance()
                .addFutureTask(deliFutureTask.class, "");
        LOG.info("同步交付记录的异步任务的事务id:" + messageId);
        return messageId;
    }


    //同步交付记录明细函数
    public static String deliDtailFutureTask() throws AsyncTaskException {

        String messageId = FutureTaskService
                .instance()
                .addFutureTask(deliDtailFutureTask.class, "");
        LOG.info("同步交付记录明细异步任务的事务id:" + messageId);
        return messageId;
    }


    //交付记录集合函数
    //@RestMapping(value = "/ybldeliveryRecord", method = RequestMethod.GET)
    public static void deli_all() throws AsyncTaskException {
        deliFutureTask();
        deliDtailFutureTask();
    }






    public static void main(String[] args) throws IOException, BatchJobException, AsyncTaskException {

        deal_deliveryRecord_data();

        deal_deliveryRecordDetail_data();


    }


}
