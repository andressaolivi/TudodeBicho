package br.sankhya.sjc;



import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.PersistenceException;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.jape.wrapper.fluid.FluidCreateVO;

import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import org.jrobin.core.Util;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.text.Normalizer;

public class CriaParceiro {
    public static BigDecimal criarParceiro  (BigDecimal arquivo, String cpf_cnpj,String tipo   ) throws Exception {
        JapeWrapper parDAO = JapeFactory.dao("Parceiro");
        JapeWrapper cidDAO = JapeFactory.dao("Cidade");
        JapeWrapper endDAO = JapeFactory.dao("Endereco");
        JapeWrapper ufDAO = JapeFactory.dao("UnidadeFederativa");
        JapeWrapper baiDAO = JapeFactory.dao("Bairro");



        String CNPJ_CPF =cpf_cnpj;
        String NOME= null;
        String IE= null;
        String EMAIL= "naotem@gmail.com.br";
        String CEP=null;
        String NOMEEND=null;
        String NUMEND= null;
        String COMPLEMENTO=" ";
        String NOMEBAI=null;
        String NOMECID=null;
        String UF=null;
        BigDecimal parceiro=BigDecimal.ZERO;
        String CADASTRADO="N";

        JdbcWrapper jdbcWrapper = null;
        EntityFacade dwfEntityFacade = EntityFacadeFactory.getDWFFacade();
        jdbcWrapper = dwfEntityFacade.getJdbcWrapper();
        NativeSql sql = new NativeSql(jdbcWrapper);

        if(tipo=="CTE") {

            sql.appendSql("SELECT xml," +
                    " EXTRACTVALUE(xmltype(xml), 'cteProc/CTe/infCte/dest/xNome') as NOME,\n" +
                    " EXTRACTVALUE(xmltype(xml), '/cteProc/CTe/infCte/dest/enderDest/CEP') as NOME,\n" +
                    " EXTRACTVALUE(xmltype(xml), '/cteProc/CTe/infCte/dest/enderDest/xLgr') as NOMEEND,\n" +
                    " EXTRACTVALUE(xmltype(xml), '/cteProc/CTe/infCte/dest/enderDest/nro') as NUMEND,\n" +
                    " EXTRACTVALUE(xmltype(xml), '/cteProc/CTe/infCte/dest/enderDest/xBairro') as NOMEBAI,\n" +
                    " EXTRACTVALUE(xmltype(xml), '/cteProc/CTe/infCte/dest/enderDest/xMun') as NOMECID,\n" +
                    " EXTRACTVALUE(xmltype(xml), '/cteProc/CTe/infCte/dest/enderDest/UF') as UF\n" +
                    " FROM tgfixn\n" +
                    " WHERE NUARQUIVO= :NUARQUIVO " +
                    " and   (select count(*) from TGFPAR WHERE TGFPAR.CGC_CPF =NVL(EXTRACTVALUE(xmltype(xml), '/cteProc/CTe/infCte/dest/CPF'),EXTRACTVALUE(xmltype(xml), '/cteProc/CTe/infCte/dest/CNPJ'))  )=0");
        }else
        {
            sql.appendSql("SELECT xml," +
                    " EXTRACTVALUE(xmltype(xml), 'nfeProc/NFe/infNFe/dest/xNome') AS NOME, \n" +
                    " EXTRACTVALUE(xmltype(xml), 'nfeProc/NFe/infNFe/dest/IE') AS IE, \n" +
                    " EXTRACTVALUE(xmltype(xml), '/nfeProc/NFe/infNFe/dest/enderDest/CEP') AS CEP, \n" +
                    " EXTRACTVALUE(xmltype(xml), '/nfeProc/NFe/infNFe/dest/enderDest/xLgr') AS NOMEEND, \n" +
                    " EXTRACTVALUE(xmltype(xml), '/cteProc/CTe/infCte/dest/enderDest/nro') AS NUMEND, \n" +
                    " EXTRACTVALUE(xmltype(xml), '/nfeProc/NFe/infNFe/dest/enderDest/xBairro') AS NOMEBAI, \n" +
                    " EXTRACTVALUE(xmltype(xml), '/nfeProc/NFe/infNFe/dest/enderDest/xMun') AS NOMECID, \n" +
                    " EXTRACTVALUE(xmltype(xml), '/nfeProc/NFe/infNFe/dest/enderDest/UF') AS UF \n" +
                    " FROM tgfixn\n" +
                    " WHERE NUARQUIVO= :NUARQUIVO" +
                    " and (select count(*) from TGFPAR WHERE TGFPAR.CGC_CPF =NVL(EXTRACTVALUE(xmltype(xml), '/nfeProc/NFe/infNFe/dest/CPF'),EXTRACTVALUE(xmltype(xml), '/nfeProc/NFe/infNFe/dest/CNPJ'))   )=0 ");
        }
        sql.setNamedParameter("NUARQUIVO", arquivo);
        ResultSet rSet = sql.executeQuery();
        while (rSet.next()) {
            NOME = rSet.getString("NOME");
            IE = rSet.getString("IE");
            CEP =  rSet.getString("CEP");
            NOMEEND= rSet.getString("NOMEEND");
            NUMEND = rSet.getString("NUMEND");
            NOMEBAI = rSet.getString("NOMEBAI");
            NOMECID = rSet.getString("NOMECID");
            UF = rSet.getString("UF");

        }
        if(NOME!=null) {


            DynamicVO codUf = ufDAO.findOne("UF = ?", UF);


            DynamicVO cidVO = cidDAO.findOne("UF=? and UPPER(NOMECID)=UPPER(?)", codUf.asBigDecimal("CODUF"), removeAccents(NOMECID));
            if (cidVO == null) {
                cidVO= cidDAO.create().set("NOMECID", NOMECID.toUpperCase())
                .set("UF", codUf.asBigDecimal("CODUF")).save();
            }

            DynamicVO endVO = endDAO.findOne("UPPER(NOMEEND)=UPPER(?)", removeAccents(NOMEEND));
            if (endVO == null) {
                endVO = endDAO.create().set("NOMEEND", NOMEEND.toUpperCase()).save();
            }
            DynamicVO baiVO = baiDAO.findOne("UPPER(NOMEBAI)=UPPER(?)", removeAccents(NOMEBAI));
            if (baiVO == null) {
                baiVO = baiDAO.create().set("NOMEBAI", NOMEBAI.toUpperCase()).save();
            }

            DynamicVO parVO =parDAO.findOne("CGC_CPF like '?'",CNPJ_CPF);
            if(parVO==null &&  "N".equals(CADASTRADO) ) {
                CADASTRADO="S";
                String tipopar;
                if (CNPJ_CPF.length() == 11) {
                    tipopar= "F";
                } else {
                    tipopar= "J";
                }
//                throw new PersistenceException("ERRO NCPJ "+CNPJ_CPF);

                DynamicVO par = parDAO.create()
                .set("CGC_CPF", CNPJ_CPF)
                .set("IDENTINSCESTAD", IE)
                .set("NOMEPARC", NOME.toUpperCase())
                .set("RAZAOSOCIAL", NOME.toUpperCase())
                .set("EMAIL", EMAIL.toUpperCase())
                .set("CEP", CEP)
                 .set("TIPPESSOA", tipopar)
                .set("COMPLEMENTO", COMPLEMENTO.toUpperCase())
                .set("NUMEND", NUMEND)
                .set("CODCID", cidVO.asBigDecimal("CODCID"))
                .set("CODEND", endVO.asBigDecimal("CODEND"))
                .set("CODBAI", baiVO.asBigDecimal("CODBAI"))
                .set("CLIENTE", "S")
                .set("FORNECEDOR", "S")
                .set("ATIVO", "S")
                .save();

                parceiro = par.asBigDecimal("CODPARC");
            }
        }



        return parceiro;

    }

    public static String removeAccents(String str) {
        str = Normalizer.normalize(str, Normalizer.Form.NFD);
        str = str.replaceAll("[^\\p{ASCII}]", "");
        return str;
    }


}
