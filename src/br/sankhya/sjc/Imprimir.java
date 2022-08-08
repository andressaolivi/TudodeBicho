package br.sankhya.sjc;

import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.bmp.PersistentLocalEntity;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.vo.PrePersistEntityState;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.modelcore.comercial.ImpressaoNotaHelpper;

import java.math.BigDecimal;

public class Imprimir implements EventoProgramavelJava {
    @Override
    public void beforeInsert(PersistenceEvent persistenceEvent) throws Exception {

    }

    @Override
    public void beforeUpdate(PersistenceEvent persistenceEvent) throws Exception {
        DynamicVO documentoVO = (DynamicVO) persistenceEvent.getVo();
        JapeWrapper cabDAO= JapeFactory.dao("CabecalhoNota");
        DynamicVO cabVO =cabDAO.findOne("NUNOTA=?",documentoVO.asBigDecimal("NUNOTAORIG"));

        if(new BigDecimal(3208).compareTo(cabVO.asBigDecimal("CODTIPOPER")) !=0 && new BigDecimal(3300).compareTo(cabVO.asBigDecimal("CODTIPOPER")) !=0 && "V".equals(cabVO.asString("TIPMOV")) && persistenceEvent.getModifingFields().isModifing("DHFINCONF") && "F".equals(documentoVO.asString("STATUS"))) {
            ImpressaoNotaHelpper h = new ImpressaoNotaHelpper();
            h.inicializaNota(documentoVO.asBigDecimal("NUNOTAORIG"));
            h.imprimirNota();
        }
    }

    @Override
    public void beforeDelete(PersistenceEvent persistenceEvent) throws Exception {

    }

    @Override
    public void afterInsert(PersistenceEvent persistenceEvent) throws Exception {

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
