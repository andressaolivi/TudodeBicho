package br.sankhya.sjc;

import br.com.sankhya.extensions.actionbutton.QueryExecutor;
import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.bmp.PersistentLocalEntity;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.util.JapeSessionContext;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.vo.PrePersistEntityState;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.modelcore.comercial.impostos.ImpostosHelpper;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import org.apache.ibatis.exceptions.PersistenceException;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static br.sankhya.sjc.CentralNotasUtils.confirmarNota;

public class AlterCabRegra implements EventoProgramavelJava {

    @Override
    public void beforeInsert(PersistenceEvent persistenceEvent) throws Exception {

    }

    @Override
    public void beforeUpdate(PersistenceEvent persistenceEvent) throws Exception {
        JapeWrapper venDAO= JapeFactory.dao("Vendedor");
        JapeWrapper cabDAO= JapeFactory.dao("CabecalhoNota");
        JapeWrapper parDAO= JapeFactory.dao("Parceiro");



        DynamicVO cabVO = (DynamicVO) persistenceEvent.getVo();
        DynamicVO vendVO = venDAO.findOne("CODVEND=?", cabVO.asBigDecimal("CODVEND"));



        //if ( "S".equals(vendVO.asString("AD_USATRANSP"))  && cabVO.asBigDecimal("CODPARCTRANSP") == BigDecimal.ZERO ) {

            //JdbcWrapper jdbcWrapper = null;
            //EntityFacade dwfEntityFacade = EntityFacadeFactory.getDWFFacade();

            //jdbcWrapper = dwfEntityFacade.getJdbcWrapper();
            //NativeSql qryfull = new NativeSql(jdbcWrapper);

            //String metodo = cabVO.asString("BH_METODO");


            //qryfull.setNamedParameter("NUNOTA", cabVO.asBigDecimal("NUNOTA"));
           // qryfull.setNamedParameter("METODO", metodo);


         //   qryfull.executeUpdate("UPDATE TGFCAB SET  CODPARCTRANSP=NVL((SELECT TGFPAR.CODPARC FROM  TGFPAR WHERE  TGFPAR.AD_METODOSDEENVIO LIKE :METODO ),0) WHERE NUNOTA=:NUNOTA ");

       // }



    }

    @Override
    public void beforeDelete(PersistenceEvent persistenceEvent) throws Exception {

    }

    @Override
    public void afterInsert(PersistenceEvent persistenceEvent) throws Exception {
        JapeWrapper venDAO= JapeFactory.dao("Vendedor");
        JapeWrapper cabDAO= JapeFactory.dao("CabecalhoNota");
        JapeWrapper parDAO= JapeFactory.dao("Parceiro");



        DynamicVO cabVO = (DynamicVO) persistenceEvent.getVo();
            DynamicVO vendVO = venDAO.findOne("CODVEND=?", cabVO.asBigDecimal("CODVEND"));



        if("V".equals(cabVO.asString("TIPMOV")) || "P".equals(cabVO.asString("TIPMOV"))) {


                String observacao = cabVO.asString("OBSERVACAO");
                if (observacao == null) {
                    observacao = " ";
                }
                String BH_CODEMKT = cabVO.asString("BH_CODEMKT");
                if (BH_CODEMKT == null) {
                    BH_CODEMKT = " ";
                }

                JdbcWrapper jdbcWrapper = null;
                EntityFacade dwfEntityFacade = EntityFacadeFactory.getDWFFacade();

                jdbcWrapper = dwfEntityFacade.getJdbcWrapper();
                NativeSql qrycom = new NativeSql(jdbcWrapper);

                qrycom.setNamedParameter("NUNOTA", cabVO.asBigDecimal("NUNOTA"));
                qrycom.setNamedParameter("CODCENCUSPAD", vendVO.asBigDecimal("CODCENCUSPAD"));
                qrycom.setNamedParameter("AD_IDENTREGA", cabVO.asString("BH_CARRIER"));
                qrycom.setNamedParameter("OBSERVACAO", observacao.concat(" ".concat(BH_CODEMKT)));


                qrycom.executeUpdate("UPDATE TGFCAB SET  CODCONTATOENTREGA=CODCONTATO,NUMPEDIDO2=substr(BH_CODMKT,0,15), CODCENCUS=:CODCENCUSPAD , AD_IDENTREGA=:AD_IDENTREGA,OBSERVACAO=:OBSERVACAO ||' Nr. Conferencia '|| NVL(CAST((SELECT max(NUCONF) FROM TGFCON2  WHERE TGFCON2.NUNOTAORIG = :NUNOTA ) AS VARCHAR(50)),' ') " +
                        "  WHERE NUNOTA=:NUNOTA ");




            }


        if ( "FALSE".equals(cabVO.asString("BH_FULFILLMENT")) && "P".equals(cabVO.asString("TIPMOV")) ) {

            JdbcWrapper jdbcWrapper = null;
            EntityFacade dwfEntityFacade = EntityFacadeFactory.getDWFFacade();

            jdbcWrapper = dwfEntityFacade.getJdbcWrapper();
            NativeSql qryfull = new NativeSql(jdbcWrapper);

            qryfull.setNamedParameter("NUNOTA", cabVO.asBigDecimal("NUNOTA"));

            qryfull.executeUpdate("UPDATE TGFCAB SET CODTIPOPER=3102 , DHTIPOPER=(SELECT MAX(DHALTER) FROM TGFTOP WHERE TGFTOP.CODTIPOPER=3102) WHERE NUNOTA=:NUNOTA ");

        }
        if ( "TRUE".equals(cabVO.asString("BH_FULFILLMENT")) && "P".equals(cabVO.asString("TIPMOV")) && cabVO.asBigDecimal("CODVEND").compareTo(new BigDecimal(37))==0)  {

            JdbcWrapper jdbcWrapper = null;
            EntityFacade dwfEntityFacade = EntityFacadeFactory.getDWFFacade();

            jdbcWrapper = dwfEntityFacade.getJdbcWrapper();
            NativeSql qryfull = new NativeSql(jdbcWrapper);

            qryfull.setNamedParameter("NUNOTA", cabVO.asBigDecimal("NUNOTA"));

            qryfull.executeUpdate("UPDATE TGFCAB SET CODTIPOPER=3112 , DHTIPOPER=(SELECT MAX(DHALTER) FROM TGFTOP WHERE TGFTOP.CODTIPOPER=3112),PENDENTE='S' WHERE NUNOTA=:NUNOTA ");

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
