package br.sankhya.sjc;

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.mgefin.model.services.MovimentacaoBancariaSP;
import br.com.sankhya.mgefin.model.services.MovimentacaoBancariaSPBean;
import br.com.sankhya.mgefin.model.services.MovimentacaoBancariaSPHome;
import br.com.sankhya.mgefin.model.utils.CompensacaoFinanceiraHelper;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import br.com.sankhya.modelcore.util.FinanceiroHelper;

import java.math.BigDecimal;
import java.sql.ResultSet;

import static br.sankhya.sjc.CentralNotasUtils.refazerFinanceiro;

public class extornarFinanceiro implements AcaoRotinaJava {
    @Override
    public void doAction(ContextoAcao contextoAcao) throws Exception {
        Registro[] linhas = contextoAcao.getLinhas();
        JapeWrapper finDAO= JapeFactory.dao("Financeiro");


        for (Registro linha : linhas) {
            DynamicVO finVO =finDAO.findOne("NUFIN=?",linha.getCampo("NUFIN"));

            CompensacaoFinanceiraHelper.estornar(finVO);


        }


    }
}
