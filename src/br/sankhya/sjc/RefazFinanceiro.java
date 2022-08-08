package br.sankhya.sjc;

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;

import java.math.BigDecimal;

import static br.sankhya.sjc.CentralNotasUtils.refazerFinanceiro;

public class RefazFinanceiro implements AcaoRotinaJava {
    @Override
    public void doAction(ContextoAcao contextoAcao) throws Exception {

        Registro[] linhas = contextoAcao.getLinhas();

        for (Registro linha : linhas) {
            refazerFinanceiro((BigDecimal) linha.getCampo("NUNOTA"));

        }
    }
}
