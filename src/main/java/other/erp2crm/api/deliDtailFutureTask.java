package other.erp2crm.api;

import com.rkhd.platform.sdk.exception.ScriptBusinessException;
import com.rkhd.platform.sdk.log.Logger;
import com.rkhd.platform.sdk.log.LoggerFactory;
import com.rkhd.platform.sdk.task.FutureTask;

import static other.erp2crm.api.ybldeliveryRecord.deal_deliveryRecordDetail_data;


public class deliDtailFutureTask implements FutureTask {

    private final static Logger LOG = LoggerFactory.getLogger();

    @Override
    public void execute(String param) throws ScriptBusinessException {
        try {
            deal_deliveryRecordDetail_data();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

}