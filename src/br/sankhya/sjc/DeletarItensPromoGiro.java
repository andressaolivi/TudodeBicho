package br.sankhya.sjc;

import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import org.cuckoo.core.ScheduledAction;
import org.cuckoo.core.ScheduledActionContext;

public class DeletarItensPromoGiro implements ScheduledAction {

    public void onTime(ScheduledActionContext contexto) {

        try {
            JdbcWrapper jdbcWrapper = null;
            EntityFacade dwfEntityFacade = EntityFacadeFactory.getDWFFacade();

            jdbcWrapper = dwfEntityFacade.getJdbcWrapper();

            NativeSql qrygir = new NativeSql(jdbcWrapper);


            qrygir.executeUpdate("DELETE from tgfgir1  WHERE (select COUNT(*) from TGFDES D WHERE D.CODPROD=tgfgir1.CODPROD AND  D.CODEMP=tgfgir1.CODEMP AND tgfgir1.DTNEG>=D.DTINICIALAND  tgfgir1.DTNEG<=D.DTFINAL)>0  ");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
