package br.sankhya.sjc;

import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class BuscaBonificado implements EventoProgramavelJava {
    @Override
    public void beforeInsert(PersistenceEvent persistenceEvent) throws Exception {

    }

    @Override
    public void beforeUpdate(PersistenceEvent persistenceEvent) throws Exception {

    }

    @Override
    public void beforeDelete(PersistenceEvent persistenceEvent) throws Exception {

    }

    @Override
    public void afterInsert(PersistenceEvent persistenceEvent) throws Exception {
        DynamicVO varVO = (DynamicVO) persistenceEvent.getVo();
        JapeWrapper cabDAO= JapeFactory.dao("CabecalhoNota");

        DynamicVO cabVO =cabDAO.findOne("NUNOTA=?",varVO.asBigDecimal("NUNOTA"));
        if("C".equals(cabVO.asString("TIPMOV")) ) {
            JdbcWrapper jdbcWrapper = null;
            EntityFacade dwfEntityFacade = EntityFacadeFactory.getDWFFacade();
            jdbcWrapper = dwfEntityFacade.getJdbcWrapper();

            NativeSql qryite = new NativeSql(jdbcWrapper);

            qryite.setNamedParameter("NUNOTA", cabVO.asBigDecimal("NUNOTA"));

            qryite.executeUpdate("UPDATE TGFITE SET AD_QTDBONIF =(SELECT sum(ITE.AD_QTDBONIF) FROM TGFVAR INNER JOIN  TGFITE ITE ON" +
                    " TGFVAR.NUNOTAORIG=ITE.NUNOTA AND TGFVAR.SEQUENCIAORIG=ITE.SEQUENCIA WHERE TGFVAR.NUNOTA=TGFITE.NUNOTA AND" +
                    " TGFVAR.SEQUENCIA=TGFITE.SEQUENCIA),AD_VLRBONIF =(SELECT sum(ITE.AD_VLRBONIF) FROM TGFVAR INNER JOIN  TGFITE ITE" +
                    " ON TGFVAR.NUNOTAORIG=ITE.NUNOTA AND TGFVAR.SEQUENCIAORIG=ITE.SEQUENCIA WHERE TGFVAR.NUNOTA=TGFITE.NUNOTA AND " +
                    "TGFVAR.SEQUENCIA=TGFITE.SEQUENCIA)  where  NUNOTA=:NUNOTA ");


        }
    }

    @Override
    public void afterUpdate(PersistenceEvent persistenceEvent) throws Exception {

    }

    @Override
    public void afterDelete(PersistenceEvent persistenceEvent) throws Exception {

    }

    @Override
    public void beforeCommit(TransactionContext transactionContext) throws Exception {

    }
}
