package br.sankhya.sjc;

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.mgefin.model.utils.CompensacaoFinanceiraHelper;
import org.apache.ibatis.jdbc.Null;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

public class MovBancariaPendencia implements AcaoRotinaJava {
    @Override
    public void doAction(ContextoAcao contextoAcao) throws Exception {
        Registro[] linhas = contextoAcao.getLinhas();
        JapeWrapper penDAO= JapeFactory.dao("IMPMELIPEN");
        JapeWrapper movDAO= JapeFactory.dao("MovimentoBancario");
        JapeWrapper topDAO= JapeFactory.dao("TipoOperacao");

        Calendar c = Calendar.getInstance();
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
        Date dia= (Date) c.getTime();
        String ID=null;

        for (Registro linha : linhas) {

             ID = (String) linha.getCampo("ID");

            }
        BigDecimal Valor=BigDecimal.ZERO;

        Collection<DynamicVO> pend = penDAO.find("ID = ? ",ID);
        Map<String, Object> fieldsItens = new HashMap<>();
        for (DynamicVO pendItem : pend) {
            Valor=Valor.add(pendItem.asBigDecimalOrZero("VALOR"));

        }
        DynamicVO topVO =topDAO.findOne("CODTIPOPER=4000 AND DHALTER=(SELECT MAX(T.DHALTER) FROM TGFTOP T WHERE T.CODTIPOPER=4000)");

        movDAO.create().set("CODCTABCOCONTRA",new BigDecimal(6))
                .set("CODCTABCOINT",new BigDecimal(5))
                .set("CODLANC",new BigDecimal(2))
                .set("CODTIPOPER",new BigDecimal(4000))
                .set("CODUSU",contextoAcao.getUsuarioLogado())
                .set("DHTIPOPER",topVO.asTimestamp("DHALTER"))
                .set("DTALTER",new java.sql.Timestamp(dia.getTime()))
                .set("DTINCLUSAO",new java.sql.Timestamp(dia.getTime()))
                .set("DTLANC",new java.sql.Timestamp(dia.getTime()))
                .set("HISTORICO","Trasferencia para Pendencia" )
                .set("VLRLANC",Valor)
                .set("RECDESP",new BigDecimal(-1))
                .set("ORIGMOV","T")
                .save();

//        movDAO.create()
//                .set("CODCTABCOCONTRA",new BigDecimal(5))
//                .set("CODCTABCOINT",new BigDecimal(6))
//                .set("CODLANC",new BigDecimal(1))
//                .set("CODTIPOPER",new BigDecimal(4000))
//                .set("CODUSU",contextoAcao.getUsuarioLogado())
//                .set("DHTIPOPER",topVO.asTimestamp("DHALTER"))
//                .set("DTALTER",new java.sql.Timestamp(dia.getTime()))
//                .set("DTINCLUSAO",new java.sql.Timestamp(dia.getTime()))
//                .set("DTLANC",new java.sql.Timestamp(dia.getTime()))
//                .set("HISTORICO","Trasferencia para Pendencia" )
//                .set("VLRLANC",Valor)
//                .set("RECDESP",new BigDecimal(1))
//                .set("ORIGMOV","T")
//                .save();

//        DynamicVO movVO =movDAO.findOne("NUBCO=?",movorig.getCampo("NUBCO"));
//        movDAO.prepareToUpdate(movVO).set("CODCTABCOCONTRA",new BigDecimal(6))
//                .set("NUBCOCP", movdest.getCampo("NUBCO")).update();


    }
}
