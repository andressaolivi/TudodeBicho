package br.sankhya.sjc;

import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.bmp.PersistentLocalEntity;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.vo.PrePersistEntityState;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.modelcore.comercial.ContextoRegra;
import br.com.sankhya.modelcore.comercial.ImpressaoNotaHelpper;
import br.com.sankhya.modelcore.comercial.Regra;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

import java.math.BigDecimal;

public class AlterCab implements Regra {

    @Override
    public void beforeInsert(ContextoRegra contextoRegra) throws Exception {

    }

    @Override
    public void beforeUpdate(ContextoRegra contextoRegra) throws Exception {
        String nomeEnty = contextoRegra.getPrePersistEntityState().getDao().getEntityName();
        JapeWrapper venDAO= JapeFactory.dao("Vendedor");
        JapeWrapper cabDAO= JapeFactory.dao("CabecalhoNota");


        if(!"ItemNota".equals(nomeEnty)) {
            Boolean confirmando = (Boolean) JapeSession.getProperty("CabecalhoNota.confirmando.nota");

            PrePersistEntityState state = contextoRegra.getPrePersistEntityState();
            PersistentLocalEntity entityVO = state.getEntity();
            DynamicVO cabVO = state.getNewVO();



            if (confirmando != null && confirmando ) {

                if("V".equals(cabVO.asString("TIPMOV")) || "P".equals(cabVO.asString("TIPMOV"))) {

                    DynamicVO vendVO = venDAO.findOne("CODVEND=?", cabVO.asBigDecimal("CODVEND"));

                    String observacao = cabVO.asString("OBSERVACAO");
                    BigDecimal transportadora =cabVO.asBigDecimal("CODPARCTRANSP");
                    if (observacao == null) {
                        observacao = " ";
                    }
                    String BH_CODEMKT = cabVO.asString("BH_CODEMKT");
                    if (BH_CODEMKT == null) {
                        BH_CODEMKT = " ";
                    }
                    if(new BigDecimal(59).compareTo(cabVO.asBigDecimal("CODVEND"))==0){
                        transportadora =new BigDecimal(368934);
                    }

                    JdbcWrapper jdbcWrapper = null;
                    EntityFacade dwfEntityFacade = EntityFacadeFactory.getDWFFacade();

                    jdbcWrapper = dwfEntityFacade.getJdbcWrapper();
                    NativeSql qrycom = new NativeSql(jdbcWrapper);

                    qrycom.setNamedParameter("NUNOTA", cabVO.asBigDecimal("NUNOTA"));
                    qrycom.setNamedParameter("CODCENCUSPAD", vendVO.asBigDecimal("CODCENCUSPAD"));
                    qrycom.setNamedParameter("AD_IDENTREGA", cabVO.asString("BH_CARRIER"));
                    qrycom.setNamedParameter("TRANSPORTADORA", transportadora);

                    qrycom.executeUpdate("UPDATE TGFCAB SET CODCONTATOENTREGA=CODCONTATO,NUMPEDIDO2=substr(BH_CODMKT,0,15),CODCENCUS=:CODCENCUSPAD , AD_IDENTREGA=:AD_IDENTREGA,OBSERVACAO=:OBSERVACAO,CODPARCTRANSP=:TRANSPORTADORA,CODCONTATOENTREGA=CODCONTATO WHERE NUNOTA=:NUNOTA ");

                  //  if ( "S".equals(vendVO.asString("AD_USATRANSP"))  && cabVO.asBigDecimal("CODPARCTRANSP") == BigDecimal.ZERO ) {


                 //       jdbcWrapper = dwfEntityFacade.getJdbcWrapper();
                 //       NativeSql qryfull = new NativeSql(jdbcWrapper);

                   //     String metodo = cabVO.asString("BH_METODO");


                     //   qryfull.setNamedParameter("NUNOTA", cabVO.asBigDecimal("NUNOTA"));
                     //   qryfull.setNamedParameter("METODO", metodo);


                     //   qryfull.executeUpdate("UPDATE TGFCAB SET  CODPARCTRANSP=NVL((SELECT TGFPAR.CODPARC FROM  TGFPAR WHERE  TGFPAR.AD_METODOSDEENVIO LIKE :METODO ),0) WHERE NUNOTA=:NUNOTA AND CODPARCTRANSP=0 ");

             //       }
                }

            }

        }
    }

    @Override
    public void beforeDelete(ContextoRegra contextoRegra) throws Exception {

    }

    @Override
    public void afterInsert(ContextoRegra contextoRegra) throws Exception {


    }

    @Override
    public void afterUpdate(ContextoRegra contextoRegra) throws Exception {

    }

    @Override
    public void afterDelete(ContextoRegra contextoRegra) throws Exception {

    }
}
