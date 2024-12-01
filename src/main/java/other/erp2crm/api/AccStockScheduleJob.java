package other.erp2crm.api;

import com.rkhd.platform.sdk.ScheduleJob;
import com.rkhd.platform.sdk.exception.ApiEntityServiceException;
import com.rkhd.platform.sdk.exception.AsyncTaskException;
import com.rkhd.platform.sdk.log.Logger;
import com.rkhd.platform.sdk.log.LoggerFactory;
import com.rkhd.platform.sdk.param.ScheduleJobParam;

import static other.erp2crm.api.Result.ProductFutureTask;


public class AccStockScheduleJob implements ScheduleJob {

        //日志信息
        private static final Logger log = LoggerFactory.getLogger();
        //继承定时函数
        public void execute(ScheduleJobParam arg0) {

            try {
                ProductFutureTask();
            } catch (AsyncTaskException e) {
                throw new RuntimeException(e);
            }

        }

}
