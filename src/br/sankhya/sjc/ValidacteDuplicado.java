package br.sankhya.sjc;

import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.PersistenceException;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;

public class ValidacteDuplicado implements EventoProgramavelJava {
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
        JapeWrapper nctDAO= JapeFactory.dao("NotaConhecimentoTransporte");
        DynamicVO docVO = (DynamicVO) persistenceEvent.getVo();

        DynamicVO nctVO = nctDAO.findOne("CHAVENFE=? AND NUNOTA=?",docVO.asString("CHAVENFE"),docVO.asBigDecimal("NUNOTA"));
        if(nctVO != null ){
                throw new PersistenceException("Ja existe um CTE  Vinculado para esta Nota  Nr. Unico :"+nctVO.asBigDecimal("NUNOTA").toString());


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
