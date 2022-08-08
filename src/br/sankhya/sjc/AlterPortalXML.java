package br.sankhya.sjc;

import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.util.JapeSessionContext;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import org.jrobin.core.Util;

import java.math.BigDecimal;
import java.sql.ResultSet;

public class AlterPortalXML implements EventoProgramavelJava {
    @Override
    public void beforeInsert(PersistenceEvent persistenceEvent) throws Exception {

    }

    @Override
    public void beforeUpdate(PersistenceEvent persistenceEvent) throws Exception {
        DynamicVO docVO = (DynamicVO) persistenceEvent.getVo();
        JapeWrapper parDAO = JapeFactory.dao("Parceiro");

        Boolean processado = JapeSession.getPropertyAsBoolean("br.com.sankhya.sjc.Alterportalxml", false);

        JdbcWrapper jdbcWrapper = null;
        EntityFacade dwfEntityFacade = EntityFacadeFactory.getDWFFacade();

        jdbcWrapper = dwfEntityFacade.getJdbcWrapper();
        NativeSql qryfull = new NativeSql(jdbcWrapper);

        qryfull.setNamedParameter("NUARQUIVO", docVO.asBigDecimal("NUARQUIVO"));

        qryfull.executeUpdate("        UPDATE tgfixn SET xml =   UPDATEXML(xmltype(xml)," +
                "                '/nfeProc/NFe/infNFe/ide/NFref',to_char('')).getClobVal()" +
                "        where CODTIPOPER in (2109,3206,4,3208) and NUARQUIVO=:NUARQUIVO ");

        String CPF_CNPJ=null;
        BigDecimal CADASTRADO= new BigDecimal(-1);
        String IE=null;
        String xml=null;
        String tipo =null;

        // Buscar parceiro CTE
        JdbcWrapper jdbc = EntityFacadeFactory.getDWFFacade().getJdbcWrapper();
        NativeSql sqlCTE = new NativeSql(jdbc);
        sqlCTE.appendSql("SELECT xml,NVL(EXTRACTVALUE(xmltype(xml), '/cteProc/CTe/infCte/dest/CPF'),EXTRACTVALUE(xmltype(xml), '/cteProc/CTe/infCte/dest/CNPJ'))  AS CPF_CNPFJ\n" +
                "FROM tgfixn\n" +
                "WHERE NUARQUIVO= :NUARQUIVO AND CODTIPOPER=4103\n" +
                "and\n" +
                "    (select count(*) from TGFPAR WHERE TGFPAR.CGC_CPF =NVL(EXTRACTVALUE(xmltype(xml), '/cteProc/CTe/infCte/dest/CPF'),EXTRACTVALUE(xmltype(xml), '/cteProc/CTe/infCte/dest/CNPJ'))  )=0");
        sqlCTE.setNamedParameter("NUARQUIVO", docVO.asBigDecimal("NUARQUIVO"));
        ResultSet rSet = sqlCTE.executeQuery();
        while (rSet.next()) {
            CPF_CNPJ= rSet.getString("CPF_CNPFJ");
            xml=  rSet.getString("xml");
            tipo="CTE";
        }
        // Buscar parceiro nota
        NativeSql sqlNFE= new NativeSql(jdbc);
        sqlNFE.appendSql("SELECT  xml,NVL(EXTRACTVALUE(xmltype(xml), '/nfeProc/NFe/infNFe/dest/CPF'),EXTRACTVALUE(xmltype(xml), '/nfeProc/NFe/infNFe/dest/CNPJ'))  AS CPF_CNPFJ, " +
                "   EXTRACTVALUE(xmltype(xml), 'nfeProc/NFe/infNFe/dest/IE') AS IE, " +
                "  (select count(*) from TGFPAR WHERE TGFPAR.CGC_CPF =NVL(EXTRACTVALUE(xmltype(xml), '/nfeProc/NFe/infNFe/dest/CPF'),EXTRACTVALUE(xmltype(xml), '/nfeProc/NFe/infNFe/dest/CNPJ'))   ) as cadastrado " +
                " FROM tgfixn " +
                " WHERE NUARQUIVO= :NUARQUIVO AND CODTIPOPER in (4103,3206,4,3208)");
        sqlNFE.setNamedParameter("NUARQUIVO", docVO.asBigDecimal("NUARQUIVO"));
        ResultSet rSetNF = sqlNFE.executeQuery();
        while (rSetNF.next()) {
            CPF_CNPJ= rSetNF.getString("CPF_CNPFJ");
            IE= rSetNF.getString("IE");
            CADASTRADO= rSetNF.getBigDecimal("cadastrado");
            xml= rSetNF.getString("xml");
            tipo="NFE";
        }

        BigDecimal parceiro;
        if(CADASTRADO.compareTo(BigDecimal.ZERO)==0 && docVO.asBigDecimalOrZero("CODPARC").compareTo(BigDecimal.ZERO)==0 ){
            JapeSession.putProperty("br.com.sankhya.sjc.Alterportalxml", Boolean.TRUE);

            parceiro = CriaParceiro.criarParceiro(docVO.asBigDecimal("NUARQUIVO"), CPF_CNPJ, tipo);

            jdbcWrapper = dwfEntityFacade.getJdbcWrapper();
            NativeSql qrypar = new NativeSql(jdbcWrapper);

            qrypar.setNamedParameter("NUARQUIVO", docVO.asBigDecimal("NUARQUIVO"));
            qrypar.setNamedParameter("CODPARC", parceiro);

            qrypar.executeUpdate("        UPDATE tgfixn SET CODPARC=:CODPARC WHERE  NUARQUIVO=:NUARQUIVO ");

        }
        if(CADASTRADO.compareTo(BigDecimal.ZERO)==1  ) {
            jdbcWrapper = dwfEntityFacade.getJdbcWrapper();
            NativeSql qrypar = new NativeSql(jdbcWrapper);
            DynamicVO parVO = parDAO.findOne("CGC_CPF=?",CPF_CNPJ);

            qrypar.setNamedParameter("CODPARC", parVO.asBigDecimal("CODPARC"));
            qrypar.setNamedParameter("IE", IE);

            qrypar.executeUpdate("UPDATE TGFPAR SET IDENTINSCESTAD=:IE WHERE  CODPARC=:CODPARC");


        }


        }

    @Override
    public void beforeDelete(PersistenceEvent persistenceEvent) throws Exception {

    }

    @Override
    public void afterInsert(PersistenceEvent persistenceEvent) throws Exception {

        DynamicVO docVO = (DynamicVO) persistenceEvent.getVo();
        JapeWrapper parDAO = JapeFactory.dao("Parceiro");
        JapeWrapper empDAO = JapeFactory.dao("Empresa");

        Boolean processado = JapeSession.getPropertyAsBoolean("br.com.sankhya.sjc.Alterportalxml", false);

        JdbcWrapper jdbcWrapper = null;
        EntityFacade dwfEntityFacade = EntityFacadeFactory.getDWFFacade();

        jdbcWrapper = dwfEntityFacade.getJdbcWrapper();
        NativeSql qryfull = new NativeSql(jdbcWrapper);

        qryfull.setNamedParameter("NUARQUIVO", docVO.asBigDecimal("NUARQUIVO"));

        qryfull.executeUpdate("        UPDATE tgfixn SET xml =   UPDATEXML(xmltype(xml)," +
                "                '/nfeProc/NFe/infNFe/ide/NFref',to_char('')).getClobVal()" +
                "        where CODTIPOPER in (2109,3206,4,3208) and NUARQUIVO=:NUARQUIVO ");

        String CPF_CNPJ=null;
        String CNPJEMP=null;
        BigDecimal CADASTRADO= new BigDecimal(-1);

        String IE=null;
        String xml=null;
        String tipo =null;

        // Buscar parceiro CTE
        JdbcWrapper jdbc = EntityFacadeFactory.getDWFFacade().getJdbcWrapper();
        NativeSql sqlCTE = new NativeSql(jdbc);
        sqlCTE.appendSql("SELECT xml,NVL(EXTRACTVALUE(xmltype(xml), '/cteProc/CTe/infCte/dest/CPF'),EXTRACTVALUE(xmltype(xml), '/cteProc/CTe/infCte/dest/CNPJ'))  AS CPF_CNPFJ\n" +
                "FROM tgfixn\n" +
                "WHERE NUARQUIVO= :NUARQUIVO AND CODTIPOPER IN 4103\n" +
                "and\n" +
                "    (select count(*) from TGFPAR WHERE TGFPAR.CGC_CPF =NVL(EXTRACTVALUE(xmltype(xml), '/cteProc/CTe/infCte/dest/CPF'),EXTRACTVALUE(xmltype(xml), '/cteProc/CTe/infCte/dest/CNPJ'))  )=0");
        sqlCTE.setNamedParameter("NUARQUIVO", docVO.asBigDecimal("NUARQUIVO"));
        ResultSet rSet = sqlCTE.executeQuery();
        while (rSet.next()) {
            CPF_CNPJ= rSet.getString("CPF_CNPFJ");
            xml=  rSet.getString("xml");
            tipo="CTE";
        }
        // Buscar parceiro nota
        NativeSql sqlNFE= new NativeSql(jdbc);
        sqlNFE.appendSql("SELECT  xml,NVL(EXTRACTVALUE(xmltype(xml), '/nfeProc/NFe/infNFe/dest/CPF'),EXTRACTVALUE(xmltype(xml), '/nfeProc/NFe/infNFe/dest/CNPJ'))  AS CPF_CNPFJ, " +
                "  EXTRACTVALUE(xmltype(xml), 'nfeProc/NFe/infNFe/dest/IE') AS IE, " +
                "  EXTRACTVALUE(xmltype(xml), 'nfeProc/NFe/infNFe/emit/CNPJ') AS CNPJEMP, " +
                "  (select count(*) from TGFPAR WHERE TGFPAR.CGC_CPF =NVL(EXTRACTVALUE(xmltype(xml), '/nfeProc/NFe/infNFe/dest/CPF'),EXTRACTVALUE(xmltype(xml), '/nfeProc/NFe/infNFe/dest/CNPJ'))   ) as cadastrado" +
                " FROM tgfixn " +
                " WHERE NUARQUIVO= :NUARQUIVO AND CODTIPOPER in (4103,3206,4,3208)");
        sqlNFE.setNamedParameter("NUARQUIVO", docVO.asBigDecimal("NUARQUIVO"));
        ResultSet rSetNF = sqlNFE.executeQuery();
        while (rSetNF.next()) {
            CPF_CNPJ= rSetNF.getString("CPF_CNPFJ");
            IE= rSetNF.getString("IE");
            CADASTRADO= rSetNF.getBigDecimal("cadastrado");
            xml= rSetNF.getString("xml");
            CNPJEMP=rSetNF.getString("CNPJEMP");
            tipo="NFE";
        }

        BigDecimal parceiro;
        if(CADASTRADO.compareTo(BigDecimal.ZERO)==0 && docVO.asBigDecimalOrZero("CODPARC").compareTo(BigDecimal.ZERO)==0 ){
            JapeSession.putProperty("br.com.sankhya.sjc.Alterportalxml", Boolean.TRUE);

            parceiro = CriaParceiro.criarParceiro(docVO.asBigDecimal("NUARQUIVO"), CPF_CNPJ, tipo);

            jdbcWrapper = dwfEntityFacade.getJdbcWrapper();
            NativeSql qrypar = new NativeSql(jdbcWrapper);

            qrypar.setNamedParameter("NUARQUIVO", docVO.asBigDecimal("NUARQUIVO"));
            qrypar.setNamedParameter("CODPARC", parceiro);

            qrypar.executeUpdate(" UPDATE tgfixn SET CODPARC=:CODPARC WHERE  NUARQUIVO=:NUARQUIVO ");

        }
        if(CADASTRADO.compareTo(BigDecimal.ZERO)==1) {
            jdbcWrapper = dwfEntityFacade.getJdbcWrapper();
            NativeSql qrypar = new NativeSql(jdbcWrapper);
            DynamicVO parVO = parDAO.findOne("CGC_CPF=?", CPF_CNPJ);

            qrypar.setNamedParameter("CODPARC", parVO.asBigDecimal("CODPARC"));
            qrypar.setNamedParameter("IE", IE);

            qrypar.executeUpdate("UPDATE TGFPAR SET IDENTINSCESTAD=:IE WHERE  CODPARC=:CODPARC");


        }
//        DynamicVO empVO=empDAO.findOne("CGC=?",CNPJEMP);
//        if(empVO!=null &&  "N".equals(docVO.asString("TIPNFE"))){
//            jdbcWrapper = dwfEntityFacade.getJdbcWrapper();
//            NativeSql qrypar = new NativeSql(jdbcWrapper);
//
//            qrypar.setNamedParameter("NUARQUIVO", docVO.asBigDecimal("NUARQUIVO"));
//            qrypar.executeUpdate(" UPDATE tgfixn SET TIPNFE='V' WHERE  NUARQUIVO=:NUARQUIVO ");
//
//        }


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
