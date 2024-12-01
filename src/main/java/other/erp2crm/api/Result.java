package other.erp2crm.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rkhd.platform.sdk.api.annotations.RequestMethod;
import com.rkhd.platform.sdk.api.annotations.RestApi;
import com.rkhd.platform.sdk.api.annotations.RestMapping;
import com.rkhd.platform.sdk.data.model.Product;
import com.rkhd.platform.sdk.exception.ApiEntityServiceException;
import com.rkhd.platform.sdk.exception.AsyncTaskException;
import com.rkhd.platform.sdk.http.*;
import com.rkhd.platform.sdk.log.Logger;
import com.rkhd.platform.sdk.log.LoggerFactory;
import com.rkhd.platform.sdk.model.XObject;
import com.rkhd.platform.sdk.service.FutureTaskService;
import com.rkhd.platform.sdk.service.XObjectService;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * className:  Result
 * Package:  other.autotest.fulltest.Result
 * Description:授权配置和一些返回配置，资源不够把物料的同步代码挪过来了
 *
 * @Date: 2024/2/4 16:40
 * @Author: shicc
 */
@RestApi(baseUrl = "/ybl")
public class Result {
    public static final Long SUCCESS = 200L;
    private Long code;
    private JSONObject result;
    private String errorMessage;
    private Boolean success;





    private static  String Authorization="test";
    private static String Pragma="test";

    public static String Authorization(){
        String id = Authorization;
        return id;
    }

    public static String Pragma(){
        String number = Pragma;
        return number;
    }


    public Result(Long code, JSONObject result, String errorMessage, Boolean success) {
        this.code = code;
        this.result = result;
        this.errorMessage = errorMessage;
        this.success = success;
    }

    public Result() {
    }

    public Long getCode() {
        return code;
    }

    public void setCode(Long code) {
        this.code = code;
    }

    public JSONObject getResult() {
        return result;
    }

    public void setResult(JSONObject result) {
        this.result = result;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public static <T> Result resultSuccessWrapper(T data) {
        JSONObject o = new JSONObject();
        o.put("data", data);
        return new Result(Result.SUCCESS, o, null, true);
    }

    public static <T> Result resultFailWrapper(Long code, String msg) {
        return new Result(code, null, msg, false);
    }


    //定义一个日志
    private final static Logger LOG = LoggerFactory.getLogger();

    //定一个全局哈希表变量
    static Map<String, String> productMap = new HashMap<>();


    //按照库存内码查询产品id-sql
    public  static  String get_productId(String str){

        String query_one="SELECT id,productName,parentId"+
                " FROM product " +
                " WHERE productName = "+"'"+str+"'"+
                " ORDER BY updatedAt desc ";
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

    //获取产品id--采用每次请求的速度太慢了
    public  static String GetProductId(String str) throws ApiEntityServiceException {

        List<XObject> records = XObjectService.instance().query("SELECT id FROM product WHERE productName = " + "'"+str+"'").getRecords();
        if(records.toString()!=null){

            Product product = (Product) records.get(0);
            if(!product.getId().toString().equals("")){
                return product.getId().toString();
            }else {
                return null;
            }

        }
        return null;

    }

    //获取除了原材料之外数物料数据
    public  static  String get_product(String range) throws ApiEntityServiceException {



        String query_one="SELECT id,productName,parentId"+
                " FROM product " +
                " WHERE parentId <> 3412065859721834"+" and "+"parentId <> 3412066271943238"+" and "+"parentId <> 3412066271943269"+
                " ORDER BY updatedAt desc "+
                " limit "+range;
        return query_one;

    }

    //获取原材料数据
    public  static  String get_product2(String range) throws ApiEntityServiceException {

        String query_one="SELECT id,productName,parentId"+
                " FROM product " +
                " WHERE parentId = 3412065859721834"+" or "+"parentId = 3412066271943238"+" or "+"parentId = 3412066271943269"+
                " ORDER BY updatedAt desc "+
                " limit "+range;
        return query_one;

    }


    //将数据转换成哈希表
    public static void parseJsonAndFillMap(String json, Map<String, String> productMap) {
        // 使用 FastJSON 解析 JSON 字符串为 JSONObject
        JSONObject rootObject = JSON.parseObject(json);

        // 获取 data 对象中的 records 数组
        JSONArray recordsArray = rootObject.getJSONObject("data").getJSONArray("records");

        // 遍历 records 数组
        for (int i = 0; i < recordsArray.size(); i++) {
            JSONObject record = recordsArray.getJSONObject(i);
            String id = record.getString("id");
            String productName = record.getString("productName");

            // 将 productName 作为键，id 作为值，放入哈希表
            productMap.put(productName, id);
        }
    }

    //组合多个哈希表
    public static void HashCombine() throws ApiEntityServiceException, IOException {

        //除了电子料以外料组成一个哈希表大概3000多条
        parseJsonAndFillMap(GetXsy_sql(get_product("0,2000")), productMap);

        parseJsonAndFillMap(GetXsy_sql(get_product("2001,2000")), productMap);

        //原材料加入这个表大致9000条
        parseJsonAndFillMap(GetXsy_sql(get_product2("0,2500")), productMap);

        parseJsonAndFillMap(GetXsy_sql(get_product2("2501,2500")), productMap);

        parseJsonAndFillMap(GetXsy_sql(get_product2("5001,2500")), productMap);

        parseJsonAndFillMap(GetXsy_sql(get_product2("7501,2500")), productMap);

        //System.out.printf(productMap.toString());

    }


    //处理获取的请求数据
    public static String deal_xsy_data(String str){

        JSONObject jsonObject = JSON.parseObject(str);

        if(jsonObject.getInteger("code")==200) {

            JSONObject jsonObject_data = jsonObject.getJSONObject("data");

            if(jsonObject_data.getInteger("totalSize")==0){
                return null;
            }else{
                JSONArray jsonArray = jsonObject_data.getJSONArray("records");

                String str_id = jsonArray.get(0).toString();

                JSONObject jsonObject1 = JSON.parseObject(str_id);

                String id = jsonObject1.getString("id");

                return id;
            }
            //System.out.printf(id);
        }else{

            return null;
        }

    }


    //物料数据请求体--按照库存变化筛选
    public static String ybl_get_product_pojo(String data){

        // 创建外层的 JSONObject
        JSONObject outerJsonObject = new JSONObject();

        // 向外层 JSONObject 添加键值对
        outerJsonObject.put("FObjectID", 1003);
        outerJsonObject.put("FObjectType", 4);
        outerJsonObject.put("HasSchema", true);
        outerJsonObject.put("Search", new JSONObject()); // 空的 JSONObject
        outerJsonObject.put("FBrNo", "");


        LocalDate currentDate = LocalDate.now();
        String data_start = currentDate.toString();
        // 创建 Filter JSONObject
        JSONObject filterObject1 = new JSONObject();
        filterObject1.put("Flv1", data);

        //时间筛选这个是物料修改的筛选
        //filterObject1.put("FLastUpdateTimeStart",data_start);
        //filterObject1.put("FLastUpdateTimeEnd",data_start);

        //这个是库存变化的筛选
        filterObject1.put("FLastUpdateTimeStock",data_start);

        outerJsonObject.put("Filter", filterObject1);

        // 创建内部的 JSON 对象（即 json 键对应的值）
        JSONObject innerJsonObject = new JSONObject();
        innerJsonObject.put("FObjectID", 1003);
        innerJsonObject.put("FObjectType", 4);
        innerJsonObject.put("HasSchema", true);
        innerJsonObject.put("Search", new JSONObject()); // 空的 JSONObject

        // 创建另一个 Filter JSONObject
        JSONObject filterObject2 = new JSONObject();
        filterObject2.put("Flv1", data);

        //时间筛选
        //filterObject1.put("FLastUpdateTimeStart",data_start);
        //filterObject1.put("FLastUpdateTimeEnd",data_start);

        filterObject1.put("FLastUpdateTimeStock",data_start);
        innerJsonObject.put("Filter", filterObject2);

        // 将内部的 JSON 对象放入外层 JSON 对象
        outerJsonObject.put("json", innerJsonObject);

        return outerJsonObject.toString();
    }


    //物料数据请求体--按照新增和修改时间筛选
    public static String ybl_get_product_pojo2(String data){

        // 创建外层的 JSONObject
        JSONObject outerJsonObject = new JSONObject();

        // 向外层 JSONObject 添加键值对
        outerJsonObject.put("FObjectID", 1003);
        outerJsonObject.put("FObjectType", 4);
        outerJsonObject.put("HasSchema", true);
        outerJsonObject.put("Search", new JSONObject()); // 空的 JSONObject
        outerJsonObject.put("FBrNo", "");


        LocalDate currentDate = LocalDate.now();
        String data_start = currentDate.toString();
        // 创建 Filter JSONObject
        JSONObject filterObject1 = new JSONObject();
        filterObject1.put("Flv1", data);

        //时间筛选这个是物料修改的筛选
        filterObject1.put("FLastUpdateTimeStart",data_start);
        filterObject1.put("FLastUpdateTimeEnd",data_start);

        //这个是库存变化的筛选
        //filterObject1.put("FLastUpdateTimeStock","2024-09-24");

        outerJsonObject.put("Filter", filterObject1);

        // 创建内部的 JSON 对象（即 json 键对应的值）
        JSONObject innerJsonObject = new JSONObject();
        innerJsonObject.put("FObjectID", 1003);
        innerJsonObject.put("FObjectType", 4);
        innerJsonObject.put("HasSchema", true);
        innerJsonObject.put("Search", new JSONObject()); // 空的 JSONObject

        // 创建另一个 Filter JSONObject
        JSONObject filterObject2 = new JSONObject();
        filterObject2.put("Flv1", data);

        //时间筛选
        filterObject1.put("FLastUpdateTimeStart",data_start);
        filterObject1.put("FLastUpdateTimeEnd",data_start);

        //filterObject1.put("FLastUpdateTimeStock","2024-09-24");
        innerJsonObject.put("Filter", filterObject2);

        // 将内部的 JSON 对象放入外层 JSON 对象
        outerJsonObject.put("json", innerJsonObject);

        return outerJsonObject.toString();
    }


    //获取ybl物料信息
    public static String ybl_get_product(String data) throws UnsupportedEncodingException {

        String base_url = "接口地址";


        CommonData up = CommonData.newBuilder()
                .callString(base_url)
                .header("Authorization",Result.Authorization())
                .header("Pragma",Result.Pragma())
                .body(data)
                .callType("POST")
                .build();


        HttpResult result = new CommonHttpClient().execute(up);

        //不能用souf输出
        //LOG.info(result.getResult());

        return result.getResult();


    }


    //对获取的物料进行初步处理
    public static JSONArray deal_product(String str) throws UnsupportedEncodingException {


        JSONObject jsonObject= JSON.parseObject(str);

        //获取得到的请求状态
        if(jsonObject.getInteger("code")==200){
            LOG.info("获取产品成功");
            JSONObject jsonObject_result = jsonObject.getJSONObject("result");
            JSONArray jsonArray = jsonObject_result.getJSONArray("items");

            //对数据进行筛选，将仓库类型为0001到0006或者仓库类型为空的数据筛选出来
            JSONArray filteredArray = new JSONArray();

            // 遍历 JSONArray
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                String stockIDNumber = obj.getString("FStockIDNumber");

                // 检查 FStockIDNumber 是否为空或者在 "0001" 到 "0006" 之间
                if (stockIDNumber == null || stockIDNumber.isEmpty() ||
                        (stockIDNumber.compareTo("0001") >= 0 && stockIDNumber.compareTo("0006") <= 0)) {
                    filteredArray.add(obj);
                }
            }
            return  filteredArray;

        }else{
            LOG.info(jsonObject.getString("code"));
            LOG.info(jsonObject.getString("message"));
            return null;
        }



    }


    //构建一个异步作业的请求参数--新增产品
    public  static  String product_data_bulkapi_cre(){

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("operation","insert");
        jsonObject.put("object","product");

        JSONArray jsonArray = new JSONArray();
        jsonArray.add("CHECK_RULE");
        jsonArray.add("CHECK_DUPLICATE");

        jsonObject.put("execOption",jsonArray);

        JSONObject jsonData = new JSONObject();
        jsonData.put("data",jsonObject);

        return jsonData.toString();
    }

    //构建一个异步作业的请求参数--更新产品
    public  static  String product_data_bulkapi_up(){

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("operation","update");
        jsonObject.put("object","product");

        JSONArray jsonArray = new JSONArray();
        jsonArray.add("CHECK_RULE");
        jsonArray.add("CHECK_DUPLICATE");

        jsonObject.put("execOption",jsonArray);

        JSONObject jsonData = new JSONObject();
        jsonData.put("data",jsonObject);

        return jsonData.toString();
    }


    //创建异步作业请求函数
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


    //异步作业增加产品返回处理参数
    public static JSONObject deal_job_data() throws IOException {

        JSONObject jsonObject = JSON.parseObject(bulk_job(product_data_bulkapi_cre()));

        if(jsonObject.getInteger("code")==200){
            LOG.info("异步作业创建成功");
        }else{
            LOG.info("异步任务创建失败:");
            LOG.info(jsonObject.getString("msg"));
            return null;
        }

        JSONObject json_result = jsonObject.getJSONObject("result");

        //输出异步作业参数
        //System.out.printf(json_result.toString());
        LOG.info(json_result.toString());
        return json_result;



    }

    //异步作业更新产品返回处理参数
    public static JSONObject deal_job_data_up() throws IOException {

        JSONObject jsonObject = JSON.parseObject(bulk_job(product_data_bulkapi_up()));

        if(jsonObject.getInteger("code")==200){
            LOG.info("异步作业创建成功");
        }else{
            LOG.info("异步任务创建失败:");
            LOG.info(jsonObject.getString("msg"));
            return null;
        }

        JSONObject json_result = jsonObject.getJSONObject("result");

        //输出异步作业参数
        //System.out.printf(json_result.toString());
        LOG.info(json_result.toString());
        return json_result;

    }

    //异步任务增加产品组合参数
    public static  String product_pojo(JSONArray data_josn,String parentId,JSONObject bulkId,String type){

        //接收一个及时库存的数组
        JSONArray jsonArray = data_josn;

        //接收异步作业id
        JSONObject object = bulkId;

        //定义一个空的对象数组用来存储筛选的数据
        JSONArray datas = new JSONArray();

        //开始对获取的物料信息进行数据筛选
        for(Object object2 : jsonArray){

            JSONObject jsonObject2 = (JSONObject) object2;

            //定义一个临时的对象来存储数据
            JSONObject data = new JSONObject();

            //固定参数--默认业务类型
            data.put("entityType", "11010000400001");

            //产品目录--这个需要判断的
            data.put("parentId", parentId);

            //仓库名称--不用改
            data.put("customItem149__c",jsonObject2.getString("FStockIDName"));

            //产品内码--不用改
            data.put("product_FItem__c",jsonObject2.getString("FItemID"));

            //物料名称--不用改
            data.put("customItem139__c",jsonObject2.getString("FItemIDName"));

            //仓库内码--需要改
            //产品内码拼接仓库代码
            String productName = jsonObject2.getString("FItemIDNumber") + "@"+ type;
            data.put("productName",productName);

            //物料代码--不用改
            data.put("customItem150__c",jsonObject2.getString("FItemIDNumber"));

            //物料型号
            data.put("customItem140__c",jsonObject2.getString("fmodel"));

            //客户机型
            data.put("product_number__c",jsonObject2.getString("fitemidnote"));

            //单位
            data.put("unit",jsonObject2.getString("FUnitIDName"));

            //数量
            if(jsonObject2.getString("FQty")==null){
                data.put("customItem144__c",0);
            }else{
                data.put("customItem144__c",jsonObject2.getDoubleValue("FQty"));
            }

            //生产计划量
            if(jsonObject2.getString("fscjhl")==null){
                data.put("customItem145__c",0);
            }else{
                data.put("customItem145__c",jsonObject2.getDoubleValue("fscjhl"));
            }


            //销售待发量
            if(jsonObject2.getString("fxsjhl")==null){
                data.put("customItem146__c",0);
            }else{
                data.put("customItem146__c",jsonObject2.getDoubleValue("fxsjhl"));
            }



            //实际可用量
            if(jsonObject2.getString("FQty")==null&&jsonObject2.getString("fxsjhl")==null){
                data.put("FQtyKY__c",0);
            }else{
                Double FQtyKY = jsonObject2.getDoubleValue("FQty")-jsonObject2.getDoubleValue("fxsjhl");
                data.put("FQtyKY__c",FQtyKY);
            }





            //销售待审量
            //data.put("customItem147__c",jsonObject2.getDoubleValue("fqtyzzYL"));

            //预计可用量
            //data.put("customItem148__c",jsonObject2.getString("FQtyYJ"));

            datas.add(data);
        }

        //再构造一个标准参数
        JSONObject data2 = new JSONObject();

        data2.put("jobId",object.getString("id"));

        data2.put("datas",datas);

        JSONObject data = new JSONObject();

        data.put("data",data2);

        return data.toString();
    }

    //异步任务更新产品组合参数
    public static  String product_pojo_up(JSONArray data_josn,String parentId,JSONObject bulkId,String type) throws IOException, ApiEntityServiceException {

        //接收一个及时库存的数组
        JSONArray jsonArray = data_josn;

        //接收异步作业id
        JSONObject object = bulkId;

        //定义一个空的对象数组用来存储筛选的数据
        JSONArray datas = new JSONArray();

        //开始对获取的物料信息进行数据筛选
        for(Object object2 : jsonArray){

            JSONObject jsonObject2 = (JSONObject) object2;

            //定义一个临时的对象来存储数据
            JSONObject data = new JSONObject();

            //固定参数--默认业务类型
            //data.put("entityType", "11010000400001");

            //产品目录--这个需要判断的
            //data.put("parentId", parentId);

            //仓库名称--不用改
            data.put("customItem149__c",jsonObject2.getString("FStockIDName"));

            //产品内码--不用改
            data.put("product_FItem__c",jsonObject2.getString("FItemID"));

            //物料名称--不用改
            data.put("customItem139__c",jsonObject2.getString("FItemIDName"));

            //仓库内码--需要改
            //产品更新的id
            String productName = jsonObject2.getString("FItemIDNumber") + "@"+ type;
            //String productid =deal_xsy_data(GetXsy_sql(get_productId(productName)));
            //String productid = GetProductId(productName);
            //使用哈希表快速查找数据
            String productid = productMap.get(productName);
            data.put("id",productid);

            //物料代码--不用改
            data.put("customItem150__c",jsonObject2.getString("FItemIDNumber"));

            //物料型号
            data.put("customItem140__c",jsonObject2.getString("fmodel"));

            //客户机型
            data.put("product_number__c",jsonObject2.getString("fitemidnote"));

            //单位
            data.put("unit",jsonObject2.getString("FUnitIDName"));

            //数量
            if(jsonObject2.getString("FQty")==null){
                data.put("customItem144__c",0);
            }else{
                data.put("customItem144__c",jsonObject2.getDoubleValue("FQty"));
            }

            //生产计划量
            if(jsonObject2.getString("fscjhl")==null){
                data.put("customItem145__c",0);
            }else{
                data.put("customItem145__c",jsonObject2.getDoubleValue("fscjhl"));
            }


            //销售待发量
            if(jsonObject2.getString("fxsjhl")==null){
                data.put("customItem146__c",0);
            }else{
                data.put("customItem146__c",jsonObject2.getDoubleValue("fxsjhl"));
            }



            //实际可用量
            if(jsonObject2.getString("FQty")==null&&jsonObject2.getString("fxsjhl")==null){
                data.put("FQtyKY__c",0);
            }else{
                Double FQtyKY = jsonObject2.getDoubleValue("FQty")-jsonObject2.getDoubleValue("fxsjhl");
                data.put("FQtyKY__c",FQtyKY);
            }





            //销售待审量
            //data.put("customItem147__c",jsonObject2.getDoubleValue("fqtyzzYL"));

            //预计可用量
            //data.put("customItem148__c",jsonObject2.getString("FQtyYJ"));

            datas.add(data);
        }

        //再构造一个标准参数
        JSONObject data2 = new JSONObject();

        data2.put("jobId",object.getString("id"));

        data2.put("datas",datas);

        JSONObject data = new JSONObject();

        data.put("data",data2);

        return data.toString();
    }


    //创建异步任务请求函数
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


    //增加物料半成品，成品，其他,其他样品,电子料，结构料，包装料
    @RestMapping(value = "/yblproduct_cre_cp", method = RequestMethod.GET)
    public  static String deal_data_all() throws IOException {


        //异步作业id
        JSONObject bulkId = deal_job_data();


        //筛选需要的物料请求体--其他样品
        String product6 = ybl_get_product_pojo2("05");

        //ybl请求函数--处理返回函数
        JSONArray qtyp = deal_product(ybl_get_product(product6));

        //加入异步作业队列--其他样品
        batch_job(product_pojo(qtyp,"3473980961380952",bulkId,"05"));



        //筛选需要的物料请求体--半成品
        String product5 = ybl_get_product_pojo2("02");

        //ybl请求函数--处理返回函数
        JSONArray bcp = deal_product(ybl_get_product(product5));

        //加入异步作业队列--半成品
        batch_job(product_pojo(bcp,"3412065693473358",bulkId,"02"));




        //筛选需要的物料请求体--标准成品
        String product2 = ybl_get_product_pojo2("03.01");

        //ybl请求函数--处理返回函数
        JSONArray bz = deal_product(ybl_get_product(product2));

        //加入异步作业队列--标准成品
        batch_job(product_pojo(bz,"3413414517955147",bulkId,"03.01"));


        //筛选需要的物料请求体--国内成品
        String product3 = ybl_get_product_pojo2("03.03");

        //ybl请求函数--处理返回函数
        JSONArray gn = deal_product(ybl_get_product(product3));

        //加入异步作业队列--国内成品
        batch_job(product_pojo(gn,"3413413892201109",bulkId,"03.03"));


        //筛选需要的物料请求体--其他
        String product4 = ybl_get_product_pojo2("03.04");

        //ybl请求函数--处理返回函数
        JSONArray qt = deal_product(ybl_get_product(product4));

        //加入异步作业队列--其他
        batch_job(product_pojo(qt,"3413414559734375",bulkId,"03.04"));



        //筛选需要的物料请求体--辅料
        String product = ybl_get_product_pojo2("04");

        //ybl请求函数--处理返回函数
        JSONArray fl = deal_product(ybl_get_product(product));

        //加入异步作业队列--辅料
        batch_job(product_pojo(fl,"3413414676109910",bulkId,"04"));


        //筛选需要的物料请求体--电子料
        String product8 = ybl_get_product_pojo2("01.01");

        //ybl请求函数--处理返回函数
        JSONArray dzl = deal_product(ybl_get_product(product8));

        //加入异步作业队列--电子料
        batch_job(product_pojo(dzl,"3412065859721834",bulkId,"01.01"));


        //筛选需要的物料请求体--结构料
        String product7 = ybl_get_product_pojo2("01.02");

        //ybl请求函数--处理返回函数
        JSONArray jgl = deal_product(ybl_get_product(product7));

        //加入异步作业队列--结构料
        batch_job(product_pojo(jgl,"3412066271943238",bulkId,"01.02"));


        //筛选需要的物料请求体--包装料
        String product9 = ybl_get_product_pojo2("01.03");

        //ybl请求函数--处理返回函数
        JSONArray bzl = deal_product(ybl_get_product(product9));

        //加入异步作业队列--包装料
        batch_job(product_pojo(bzl,"3412066271943269",bulkId,"01.03"));


        return null;
    }


    //增加物料电子料，结构料，包装料
    //@RestMapping(value = "/yblproduct_cre_dcl", method = RequestMethod.GET)
    public  static String deal_data_ycl() throws IOException {

        //异步作业id
        JSONObject bulkId = deal_job_data();


        //筛选需要的物料请求体--电子料
        String product8 = ybl_get_product_pojo("01.01");

        //ybl请求函数--处理返回函数
        JSONArray dzl = deal_product(ybl_get_product(product8));

        //加入异步作业队列--电子料
        batch_job(product_pojo(dzl,"3412065859721834",bulkId,"01.01"));


        //筛选需要的物料请求体--结构料
        String product7 = ybl_get_product_pojo("01.02");

        //ybl请求函数--处理返回函数
        JSONArray jgl = deal_product(ybl_get_product(product7));

        //加入异步作业队列--结构料
        batch_job(product_pojo(jgl,"3412066271943238",bulkId,"01.02"));


        //筛选需要的物料请求体--包装料
        String product6 = ybl_get_product_pojo("01.03");

        //ybl请求函数--处理返回函数
        JSONArray bzl = deal_product(ybl_get_product(product6));

        //加入异步作业队列--包装料
        batch_job(product_pojo(bzl,"3412066271943269",bulkId,"01.03"));

        return null;

    }


    //更新成品,辅料，半成品,其他样品,电子料，结构料，包装料
    @RestMapping(value = "/yblproduct_up_cp", method = RequestMethod.GET)
    public  static String deal_data_up_all() throws IOException, ApiEntityServiceException {


        //异步作业id
        JSONObject bulkId = deal_job_data_up();

        //调用哈希组合函数
        HashCombine();

        //筛选需要的物料请求体--其他样品
        String product6 = ybl_get_product_pojo("05");

        //ybl请求函数--处理返回函数
        JSONArray qtyp = deal_product(ybl_get_product(product6));

        //加入异步作业队列--其他样品
        batch_job(product_pojo_up(qtyp,"",bulkId,"05"));


        //筛选需要的物料请求体--其他样品
        String product62 = ybl_get_product_pojo2("05");

        //ybl请求函数--处理返回函数
        JSONArray qtyp2 = deal_product(ybl_get_product(product62));

        //加入异步作业队列--其他样品
        batch_job(product_pojo_up(qtyp2,"",bulkId,"05"));


        //筛选需要的物料请求体--半成品
        String product5 = ybl_get_product_pojo("02");

        //ybl请求函数--处理返回函数
        JSONArray bcp = deal_product(ybl_get_product(product5));

        //加入异步作业队列--半成品
        batch_job(product_pojo_up(bcp,"",bulkId,"02"));


        //筛选需要的物料请求体--半成品
        String product52 = ybl_get_product_pojo2("02");

        //ybl请求函数--处理返回函数
        JSONArray bcp2 = deal_product(ybl_get_product(product52));

        //加入异步作业队列--半成品
        batch_job(product_pojo_up(bcp2,"",bulkId,"02"));




        //筛选需要的物料请求体--标准成品
        String product2 = ybl_get_product_pojo("03.01");

        //ybl请求函数--处理返回函数
        JSONArray bz = deal_product(ybl_get_product(product2));

        //加入异步作业队列--标准成品
        batch_job(product_pojo_up(bz,"",bulkId,"03.01"));


        //筛选需要的物料请求体--标准成品
        String product22 = ybl_get_product_pojo2("03.01");

        //ybl请求函数--处理返回函数
        JSONArray bz2 = deal_product(ybl_get_product(product22));

        //加入异步作业队列--标准成品
        batch_job(product_pojo_up(bz2,"",bulkId,"03.01"));


        //筛选需要的物料请求体--国内成品
        String product3 = ybl_get_product_pojo("03.03");

        //ybl请求函数--处理返回函数
        JSONArray gn = deal_product(ybl_get_product(product3));

        //加入异步作业队列--国内成品
        batch_job(product_pojo_up(gn,"",bulkId,"03.03"));


        //筛选需要的物料请求体--国内成品
        String product32 = ybl_get_product_pojo2("03.03");

        //ybl请求函数--处理返回函数
        JSONArray gn2 = deal_product(ybl_get_product(product32));

        //加入异步作业队列--国内成品
        batch_job(product_pojo_up(gn2,"",bulkId,"03.03"));


        //筛选需要的物料请求体--其他
        String product4 = ybl_get_product_pojo("03.04");

        //ybl请求函数--处理返回函数
        JSONArray qt = deal_product(ybl_get_product(product4));

        //加入异步作业队列--其他
        batch_job(product_pojo_up(qt,"",bulkId,"03.04"));


        //筛选需要的物料请求体--其他
        String product42 = ybl_get_product_pojo2("03.04");

        //ybl请求函数--处理返回函数
        JSONArray qt2 = deal_product(ybl_get_product(product42));

        //加入异步作业队列--其他
        batch_job(product_pojo_up(qt2,"",bulkId,"03.04"));



        //筛选需要的物料请求体--辅料
        String product = ybl_get_product_pojo("04");

        //ybl请求函数--处理返回函数
        JSONArray fl = deal_product(ybl_get_product(product));

        //加入异步作业队列--辅料
        batch_job(product_pojo_up(fl,"",bulkId,"04"));


        //筛选需要的物料请求体--辅料
        String product12 = ybl_get_product_pojo2("04");

        //ybl请求函数--处理返回函数
        JSONArray fl2 = deal_product(ybl_get_product(product12));

        //加入异步作业队列--辅料
        batch_job(product_pojo_up(fl2,"",bulkId,"04"));



        //筛选需要的物料请求体--电子料
        String product8 = ybl_get_product_pojo("01.01");

        //ybl请求函数--处理返回函数
        JSONArray dzl = deal_product(ybl_get_product(product8));

        //加入异步作业队列--电子料
        batch_job(product_pojo_up(dzl,"",bulkId,"01.01"));


        //筛选需要的物料请求体--电子料
        String product82 = ybl_get_product_pojo2("01.01");

        //ybl请求函数--处理返回函数
        JSONArray dzl2 = deal_product(ybl_get_product(product82));

        //加入异步作业队列--电子料
        batch_job(product_pojo_up(dzl2,"",bulkId,"01.01"));


        //筛选需要的物料请求体--结构料
        String product7 = ybl_get_product_pojo("01.02");

        //ybl请求函数--处理返回函数
        JSONArray jgl = deal_product(ybl_get_product(product7));

        //加入异步作业队列--结构料
        batch_job(product_pojo_up(jgl,"-",bulkId,"01.02"));


        //筛选需要的物料请求体--结构料
        String product72 = ybl_get_product_pojo2("01.02");

        //ybl请求函数--处理返回函数
        JSONArray jgl2 = deal_product(ybl_get_product(product72));

        //加入异步作业队列--结构料
        batch_job(product_pojo_up(jgl2,"-",bulkId,"01.02"));


        //筛选需要的物料请求体--包装料
        String product9 = ybl_get_product_pojo("01.03");

        //ybl请求函数--处理返回函数
        JSONArray bzl = deal_product(ybl_get_product(product9));

        //加入异步作业队列--包装料
        batch_job(product_pojo_up(bzl,"",bulkId,"01.03"));


        //筛选需要的物料请求体--包装料
        String product92 = ybl_get_product_pojo2("01.03");

        //ybl请求函数--处理返回函数
        JSONArray bzl2 = deal_product(ybl_get_product(product9));

        //加入异步作业队列--包装料
        batch_job(product_pojo_up(bzl2,"",bulkId,"01.03"));


        return null;
    }



    //更新电子料，结构料，包装料
    //@RestMapping(value = "/yblproduct_up_dcl", method = RequestMethod.GET)
    public  static String deal_data_ycl_up() throws IOException, ApiEntityServiceException {

        //异步作业id
        JSONObject bulkId = deal_job_data_up();


        //筛选需要的物料请求体--半成品
        String product5 = ybl_get_product_pojo("02");

        //ybl请求函数--处理返回函数
        JSONArray bcp = deal_product(ybl_get_product(product5));

        //加入异步作业队列--半成品
        batch_job(product_pojo_up(bcp,"",bulkId,"02"));


        //筛选需要的物料请求体--电子料
        String product8 = ybl_get_product_pojo("01.01");

        //ybl请求函数--处理返回函数
        JSONArray dzl = deal_product(ybl_get_product(product8));

        //加入异步作业队列--电子料
        batch_job(product_pojo_up(dzl,"",bulkId,"01.01"));


        //筛选需要的物料请求体--结构料
        String product7 = ybl_get_product_pojo("01.02");

        //ybl请求函数--处理返回函数
        JSONArray jgl = deal_product(ybl_get_product(product7));

        //加入异步作业队列--结构料
        batch_job(product_pojo_up(jgl,"-",bulkId,"01.02"));


        //筛选需要的物料请求体--包装料
        String product9 = ybl_get_product_pojo("01.03");

        //ybl请求函数--处理返回函数
        JSONArray bzl = deal_product(ybl_get_product(product9));

        //加入异步作业队列--包装料
        batch_job(product_pojo_up(bzl,"",bulkId,"01.03"));

        return null;

    }


    //异步作业销售易这个设定很鸡肋只是为了获得90秒的运行时间，配合自定义接口使用
    @RestMapping(value = "/yblProductFuture", method = RequestMethod.GET)
    public static String ProductFutureTask() throws AsyncTaskException {

        String messageId = FutureTaskService
                .instance()
                .addFutureTask(productFutureTask.class, "");
        LOG.info("更新产品的异步任务的事务id:" + messageId);
        return messageId;
    }


    public static void main(String[] args) throws IOException, ApiEntityServiceException {

        deal_data_up_all();
    }


}