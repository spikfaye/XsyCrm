ingage.conn.remoteCall({
    url:'/rest/data/v2.0/scripts/api/ybl/ybldeliveryRecord', method:'GET',
    success:function(a){
        console.log(a);
        ingage.noticeSuccess("成功");
        alert(a);
    },
    error:function(a){
        console.log(a);
        ingage.noticeError("失败");
        alert(a);
    }
})
