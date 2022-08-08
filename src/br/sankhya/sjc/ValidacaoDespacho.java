package br.sankhya.sjc;

import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.PersistenceException;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;

public class ValidacaoDespacho implements EventoProgramavelJava {
    @Override
    public void beforeInsert(PersistenceEvent persistenceEvent) throws Exception {

    }

    @Override
    public void beforeUpdate(PersistenceEvent persistenceEvent) throws Exception {
        JapeWrapper aneDAO = JapeFactory.dao("AnexoSistema");
        DynamicVO linhaVO = (DynamicVO) persistenceEvent.getVo();
        DynamicVO linhaOldVO = (DynamicVO) persistenceEvent.getOldVO();
        if ("F".equals(linhaVO.asString("STATUS")) && "A".equals(linhaOldVO.asString("STATUS"))){
           DynamicVO aneVO =aneDAO.findOne("NOMEINSTANCIA='OrdemDespacho' and cast(replace(PKREGISTRO,'_OrdemDespacho','') as int) =?",linhaVO.asBigDecimal("NUODP"));
           if(aneVO==null){
               throw new PersistenceException("Não é permitido Fechar Despacho sem Anexo de Documento Assinado ");

           }
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
