package br.sankhya.sjc;

import java.math.BigDecimal;

import java.sql.CallableStatement;

import java.sql.Connection;

import java.sql.PreparedStatement;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;

import java.util.ArrayList;

import java.util.Collection;

import java.util.HashMap;

import java.util.Map;

import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.util.JapeSessionContext;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import org.cuckoo.core.ScheduledAction;

import org.cuckoo.core.ScheduledActionContext;

import com.sun.jmx.snmp.Timestamp;

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;

import br.com.sankhya.extensions.actionbutton.ContextoAcao;

import br.com.sankhya.jape.dao.JdbcWrapper;

import br.com.sankhya.modelcore.PlatformService;

import br.com.sankhya.modelcore.PlatformServiceFactory;

import br.com.sankhya.modelcore.PlatformServiceFactory.ServiceDescriptor;

import br.com.sankhya.modelcore.util.EntityFacadeFactory;

import static br.sankhya.sjc.CentralNotasUtils.confirmarNota;

public class AlteracaoCabAgenda implements ScheduledAction {

    public void onTime(ScheduledActionContext contexto) {

        try {


            JapeWrapper notaDAO = JapeFactory.dao("CabecalhoNota");
            JapeSessionContext.putProperty("usuario_logado",0);

           JdbcWrapper jdbcWrapper = null;
           EntityFacade dwfEntityFacade = EntityFacadeFactory.getDWFFacade();

                 jdbcWrapper = dwfEntityFacade.getJdbcWrapper();

            NativeSql qryemp = new NativeSql(jdbcWrapper);


            qryemp.executeUpdate("UPDATE  TGFCAB SET CODEMP=5, CODPARCTRANSP=45158 ,BH_METODO=CASE  WHEN  BH_METODO IN ('CORREIOS','CORREIOS PAC','CORREIOS SEDEX') THEN BH_METODO ELSE 'JADLOG PACKAGE' END  WHERE  CODVEND=14 and  CODCIDDESTINO not in (4798, 2475)  and CODTIPOPER = 3102  and PENDENTE='S'  AND (SELECT COUNT(*) FROM TGFVAR WHERE TGFCAB.NUNOTA=TGFVAR.NUNOTAORIG)=0  AND BH_LOJA='TUDO DE BICHO' AND DTNEG >= '02/02/2022' AND CODEMP = 1  AND (SELECT COUNT(*)       FROM TGFITE                LEFT JOIN TGFEST ON TGFITE.CODPROD = TGFEST.CODPROD AND TGFITE.CODLOCALORIG = TGFEST.CODLOCAL AND  TGFEST.CODEMP = 5       WHERE NVL(TGFEST.ESTOQUE - TGFEST.RESERVADO, 0) < TGFITE.QTDNEG         AND TGFITE.NUNOTA = TGFCAB.NUNOTA) =0 AND ROWNUM=1 ");
            jdbcWrapper = dwfEntityFacade.getJdbcWrapper();

            NativeSql qryempITE = new NativeSql(jdbcWrapper);


            qryempITE.executeUpdate("UPDATE TGFITE SET CODEMP=5 WHERE (SELECT COUNT(*) FROM TGFCAB  WHERE CODTIPOPER = 3102  and PENDENTE='S' AND TGFCAB.CODEMP=5 AND  TGFCAB.CODEMP<>TGFITE.CODEMP AND  TGFCAB.NUNOTA=TGFITE.NUNOTA)>0");




            jdbcWrapper = dwfEntityFacade.getJdbcWrapper();



            NativeSql qrycab = new NativeSql(jdbcWrapper);


            qrycab.executeUpdate("UPDATE TGFCAB SET CODTIPOPER=3102 , DHTIPOPER=(SELECT MAX(DHALTER) FROM TGFTOP WHERE TGFTOP.CODTIPOPER=3102) WHERE CODTIPOPER=3105 AND BH_STATUSPED='1'  ");

            jdbcWrapper = null;
            jdbcWrapper = dwfEntityFacade.getJdbcWrapper();
            NativeSql qryite = new NativeSql(jdbcWrapper);


            qryite.executeUpdate("UPDATE TGFITE SET ATUALESTOQUE=1 , RESERVA='S' WHERE   ( SELECT COUNT(*) FROM TGFCAB WHERE dtneg > (sysdate-10) AND TGFCAB.NUNOTA=TGFITE.NUNOTA AND  CODTIPOPER=3102 AND BH_STATUSPED='1')>0  AND ATUALESTOQUE=0 AND PENDENTE='S'  ");


            jdbcWrapper = null;
            jdbcWrapper = dwfEntityFacade.getJdbcWrapper();
            NativeSql qrycabCAN = new NativeSql(jdbcWrapper);


            qrycabCAN.executeUpdate("UPDATE TGFCAB SET CODTIPOPER=3106 , DHTIPOPER=(SELECT MAX(DHALTER) FROM TGFTOP WHERE TGFTOP.CODTIPOPER=3106),PENDENTE='N' WHERE CODTIPOPER=3105 AND  BH_STATUSPED IN ('3','4','5','6','7','8')  ");

            jdbcWrapper = null;
            jdbcWrapper = dwfEntityFacade.getJdbcWrapper();
            NativeSql qryiteCAN = new NativeSql(jdbcWrapper);


            qryiteCAN.executeUpdate("UPDATE TGFITE SET ATUALESTOQUE=0 , RESERVA='N' WHERE   ( SELECT COUNT(*) FROM TGFCAB WHERE dtneg > (sysdate-10) AND TGFCAB.NUNOTA=TGFITE.NUNOTA AND  CODTIPOPER=3106 )>0  AND RESERVA='S'  ");

            jdbcWrapper = null;
            jdbcWrapper = dwfEntityFacade.getJdbcWrapper();
            NativeSql qrymesagDetrator = new NativeSql(jdbcWrapper);


            qrymesagDetrator.executeUpdate("UPDATE TGFITE SET ATUALESTOQUE=0 , RESERVA='N' WHERE   ( SELECT COUNT(*) FROM TGFCAB WHERE dtneg > (sysdate-10) AND TGFCAB.NUNOTA=TGFITE.NUNOTA AND  CODTIPOPER=3106 )>0  AND RESERVA='S'  ");

            jdbcWrapper = null;
            jdbcWrapper = dwfEntityFacade.getJdbcWrapper();
            NativeSql qrycabfullamazon = new NativeSql(jdbcWrapper);


            qrycabfullamazon.executeUpdate("UPDATE TGFITE SET ATUALESTOQUE=1 , RESERVA='S',CODLOCALORIG=1007,PENDENTE='S'  WHERE   ( SELECT COUNT(*) FROM TGFCAB WHERE  dtneg > (sysdate-10) AND TGFCAB.NUNOTA=TGFITE.NUNOTA AND  CODTIPOPER=3112 )>0  AND CODLOCALORIG<>1007 ");

            jdbcWrapper = null;
            jdbcWrapper = dwfEntityFacade.getJdbcWrapper();
            NativeSql qrycabtranspshopee = new NativeSql(jdbcWrapper);


            qrycabtranspshopee.executeUpdate("update tgfcab set CODPARCTRANSP=368934 where CODTIPOPER=3232 and DTNEG>='01/03/2022' and nvl(CODPARCTRANSP,0)=0 ");


        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}